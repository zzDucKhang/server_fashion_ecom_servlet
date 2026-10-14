'use client';

import Link from 'next/link';
import { ArrowLeft, Check, Lock } from 'lucide-react';
import { useState, useMemo, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { catalogApi } from '@/api/catalog.api';
import { useRouter, useSearchParams } from 'next/navigation';
import { useForm, FormProvider } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { checkoutSchema, CheckoutFormValues } from '../schemas';
import { useCart } from '@/modules/cart/hooks/use-cart';
import { useCreateOrder } from '@/modules/order/hooks/use-orders';
import { orderApi } from '@/api/order.api';
import { ShippingForm } from './shipping-form';
import { PaymentMethodComponent } from './payment-method';
import { formatCurrency } from '@/lib/utils/format-currency';
import { toast } from 'sonner';
import {
  VoucherInput,
  PointRedeemSlider,
  type VoucherValidationData,
  getPointBalance,
} from '@/modules/loyalty-engage';

export function CheckoutView() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [step, setStep] = useState(1);
  const { data: cartResponse } = useCart();
  const createOrderMutation = useCreateOrder();

  const [qrData, setQrData] = useState<{ qrUrl: string; amount: number; transferContent: string } | null>(null);
  const [createdOrderCode, setCreatedOrderCode] = useState<string | null>(null);
  const [isCreatingPayment, setIsCreatingPayment] = useState(false);

  const [appliedVoucher, setAppliedVoucher] = useState<VoucherValidationData | null>(null);
  const [usedPoints, setUsedPoints] = useState<number>(0);
  const [userPointsBalance, setUserPointsBalance] = useState<number>(0);

  useEffect(() => {
    getPointBalance()
      .then((res) => {
        if (res?.data?.rewardPoints) {
          setUserPointsBalance(res.data.rewardPoints);
        }
      })
      .catch(() => {});
  }, []);

    const isDirectBuy = searchParams.get('directBuy') === 'true';
  const directProductId = Number(searchParams.get('productId'));
  const directVariantId = Number(searchParams.get('variantId'));
  const directQuantity = Number(searchParams.get('quantity')) || 1;

  const { data: directProductRes } = useQuery({
    queryKey: ['direct-buy-product', directProductId],
    queryFn: () => catalogApi.getProductById(directProductId),
    enabled: isDirectBuy && !!directProductId,
  });

  const itemsParam = searchParams.get('items');
  const selectedItemIds = useMemo(() => {
    if (!itemsParam) return null;
    return itemsParam.split(',').map(Number).filter((id) => !isNaN(id) && id > 0);
  }, [itemsParam]);

  const allCartItems = cartResponse?.data?.cartItems || [];
  
  const checkoutItems = useMemo(() => {
    if (isDirectBuy && directProductRes?.data) {
      const variant = directProductRes.data.variants?.find((v: any) => v.id === directVariantId);
      if (variant) {
        return [{
          id: -1,
          cartId: -1,
          variantId: variant.id,
          quantity: directQuantity,
          variant: {
            ...variant,
            product: directProductRes.data
          }
        }];
      }
    }
    if (!selectedItemIds || selectedItemIds.length === 0) return allCartItems;
    return allCartItems.filter((item) => selectedItemIds.includes(item.id));
  }, [allCartItems, selectedItemIds, isDirectBuy, directProductRes, directVariantId, directQuantity]);

  const methods = useForm<CheckoutFormValues>({
    resolver: zodResolver(checkoutSchema),
    defaultValues: { shippingMethod: 'STANDARD', paymentMethod: 'COD' },
    mode: 'onTouched',
  });

  const subTotal = checkoutItems.reduce((sum, item) => {
    const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
      if (!fsi.campaign || !fsi.campaign.isActive) return false;
      const now = new Date().getTime();
      const start = new Date(fsi.campaign.startTime).getTime();
      const end = new Date(fsi.campaign.endTime).getTime();
      const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
      return now >= start && now <= end && remainingQty >= item.quantity;
    });

    const price = activeFlashSale
      ? Number(activeFlashSale.flashSalePrice)
      : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
      
    return sum + price * item.quantity;
  }, 0);

  const voucherDiscount = appliedVoucher ? appliedVoucher.discountAmount : 0;
  const remainingAfterVoucher = Math.max(0, subTotal - voucherDiscount);
  const pointDiscount = Math.min(usedPoints, remainingAfterVoucher);
  const finalTotal = Math.max(0, subTotal - voucherDiscount - pointDiscount);

  const onSubmit = (data: CheckoutFormValues) => {
    const orderPayload: any = {
      shippingAddress: data.shippingAddress,
      paymentMethod: data.paymentMethod,
      shippingMethod: data.shippingMethod,
      customerNote: data.customerNote,
      voucherCode: data.voucherCode,
      pointUsed: data.pointUsed,
    };

    if (isDirectBuy) {
      orderPayload.directBuy = {
        variantId: directVariantId,
        quantity: directQuantity,
      };
    } else {
      orderPayload.cartItemIds = selectedItemIds && selectedItemIds.length > 0 ? selectedItemIds : undefined;
    }

    createOrderMutation.mutate(orderPayload, {
      onSuccess: async (res) => {
        const orderData = res.data;
        if (data.paymentMethod !== 'COD') {
          setIsCreatingPayment(true);
          try {
            const paymentRes = await orderApi.createPayment(orderData.id);
            setQrData(paymentRes.data);
            setCreatedOrderCode(orderData.orderCode);
            setStep(3);
            toast.success('Đặt hàng thành công! Vui lòng quét mã thanh toán.');
          } catch (err) {
            toast.error('Không thể tải mã QR lúc này, bạn có thể thanh toán sau trong chi tiết đơn hàng.');
            router.push(`/order-success/${orderData.orderCode}`);
          } finally {
            setIsCreatingPayment(false);
          }
        } else {
          toast.success('Đặt hàng thành công!');
          router.push(`/order-success/${orderData.orderCode}`);
        }
      }
    });
  };

  if (step === 3 && qrData) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
        <div className="max-w-xl mx-auto bg-white rounded-3xl border border-[#84EA53] p-8 sm:p-10 text-center shadow-lg animate-in zoom-in-95 duration-500">
          <h2 className="font-display text-3xl text-neutral-900 mb-2">Thanh toán đơn hàng</h2>
          <p className="text-sm text-neutral-600 mb-8">
            Mã đơn hàng: <strong className="text-neutral-900 font-mono">#{createdOrderCode}</strong><br />
            Vui lòng mở ứng dụng ngân hàng hoặc ví điện tử và quét mã QR dưới đây.
          </p>
          
          <div className="flex justify-center p-4 bg-neutral-50 rounded-2xl border border-[#EAE6DF] max-w-xs mx-auto mb-6 shadow-xs">
            <img src={qrData.qrUrl} alt="Mã QR Thanh toán" className="w-full h-auto object-contain mix-blend-multiply" />
          </div>

          <div className="bg-amber-50 border border-amber-200 p-5 rounded-2xl max-w-sm mx-auto text-left space-y-3 mb-8">
            <div className="flex justify-between items-center border-b border-amber-200/50 pb-3">
              <span className="text-xs text-amber-800">Số tiền cần chuyển:</span>
              <span className="text-lg font-black text-amber-900">{formatCurrency(qrData.amount)}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-xs text-amber-800">Nội dung CK:</span>
              <span className="font-mono font-bold text-sm text-amber-900 bg-amber-200/50 px-2 py-1 rounded select-all">{qrData.transferContent}</span>
            </div>
          </div>

          <button
            type="button"
            onClick={() => router.push(`/order-success/${createdOrderCode}`)}
            className="w-full max-w-sm mx-auto rounded-xl bg-[#1D1F20] hover:bg-black py-4 text-sm font-bold !text-white [&_*]:!text-white transition-all shadow-sm active:scale-[0.99] block"
          >
            Tôi đã thanh toán thành công
          </button>
        </div>
      </main>
    );
  }

  if (checkoutItems.length === 0) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-20 sm:px-8 text-center">
        <h1 className="font-display text-3xl mb-4 text-neutral-900">Không có sản phẩm nào được chọn</h1>
        <Link href="/cart" className="text-emerald-600 underline font-semibold hover:text-emerald-700">
          Quay lại giỏ hàng
        </Link>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
      <div className="mb-10 flex items-end justify-between">
        <div>
          <p className="eyebrow">XÁC NHẬN MUA HÀNG</p>
          <h1 className="section-title mt-2">Thanh toán</h1>
        </div>
        <Link href="/cart" className="flex items-center gap-1 text-xs text-[#74736e] hover:text-neutral-900 transition">
          <ArrowLeft size={14} /> Quay lại giỏ hàng
        </Link>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)} className="grid gap-10 lg:grid-cols-[1fr_360px]">
          <div className="space-y-4">
            <section
              className={`rounded-2xl bg-white p-6 border transition-all ${
                step === 1 ? 'border-[#84EA53] shadow-xs' : 'border-[#EAE6DF]'
              }`}
            >
              <div className="flex items-center justify-between cursor-pointer" onClick={() => setStep(1)}>
                <h2 className="font-display text-3xl text-neutral-900">01 · Địa chỉ nhận hàng</h2>
                {step > 1 && <Check size={18} color="#61bc39" />}
              </div>
              {step === 1 && <ShippingForm onNext={() => setStep(2)} />}
            </section>

            <section
              className={`rounded-2xl bg-white p-6 border transition-all ${
                step === 2
                  ? 'border-[#84EA53] shadow-xs'
                  : 'border-[#EAE6DF] opacity-60 pointer-events-none'
              }`}
            >
              <h2 className="font-display text-3xl text-neutral-900">02 · Phương thức thanh toán</h2>
              {step === 2 && (
                <div className="mt-6 space-y-6">
                  <VoucherInput
                    orderTotal={subTotal}
                    onValidate={(v) => {
                      methods.setValue('voucherCode', v.code);
                      setAppliedVoucher(v);
                    }}
                    onRemove={() => {
                      methods.setValue('voucherCode', undefined);
                      setAppliedVoucher(null);
                    }}
                  />
                  {userPointsBalance > 0 && (
                    <PointRedeemSlider
                      maxPoints={userPointsBalance}
                      orderTotal={remainingAfterVoucher}
                      onRedeem={(pts) => {
                        methods.setValue('pointUsed', pts);
                        setUsedPoints(pts);
                      }}
                    />
                  )}
                  <PaymentMethodComponent
                    isSubmitting={createOrderMutation.isPending || isCreatingPayment}
                    totalAmount={finalTotal}
                  />
                </div>
              )}
            </section>
          </div>

          <aside className="h-fit rounded-2xl bg-[#1D1F20] p-6 !text-white [&_*]:!text-white lg:sticky lg:top-36 shadow-lg">
            <div className="flex items-center gap-2 text-sm font-semibold">
              <Lock size={15} color="#84EA53" /> Thanh toán an toàn
            </div>
            <div className="mt-6 divide-y divide-white/10">
              {checkoutItems.map((item) => {
                // Tính lại giá cho từng Item hiển thị
                const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
                  if (!fsi.campaign || !fsi.campaign.isActive) return false;
                  const now = new Date().getTime();
                  const start = new Date(fsi.campaign.startTime).getTime();
                  const end = new Date(fsi.campaign.endTime).getTime();
                  const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
                  return now >= start && now <= end && remainingQty >= item.quantity;
                });
                const currentPrice = activeFlashSale ? Number(activeFlashSale.flashSalePrice) : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);

                return (
                  <div key={item.id} className="py-3 flex justify-between text-xs">
                    <span className="!text-white [&_*]:!text-white/70 line-clamp-1 w-2/3">
                      {item.quantity}x {item.variant.product.defaultName}
                    </span>
                    <span className="font-medium">
                      {/* Hiển thị giá nhân với số lượng */}
                      {formatCurrency(currentPrice * item.quantity)}
                    </span>
                  </div>
                );
              })}
            </div>

            <div className="mt-6 space-y-2 border-t border-white/15 pt-5 text-xs !text-white [&_*]:!text-white/70">
              <div className="flex justify-between">
                <span>Tạm tính</span>
                <span className="!text-white [&_*]:!text-white">{formatCurrency(subTotal)}</span>
              </div>
              {voucherDiscount > 0 && (
                <div className="flex justify-between text-[#84EA53]">
                  <span>Voucher giảm giá ({appliedVoucher?.code})</span>
                  <span>-{formatCurrency(voucherDiscount)}</span>
                </div>
              )}
              {pointDiscount > 0 && (
                <div className="flex justify-between text-[#84EA53]">
                  <span>Điểm tích lũy</span>
                  <span>-{formatCurrency(pointDiscount)}</span>
                </div>
              )}
            </div>

            <div className="mt-4 flex justify-between border-t border-white/15 pt-4 text-sm">
              <span className="!text-white [&_*]:!text-white/70 font-semibold">Tổng thanh toán</span>
              <strong className="text-lg text-[#84EA53]">{formatCurrency(finalTotal)}</strong>
            </div>
          </aside>
        </form>
      </FormProvider>
    </main>
  );
}