'use client';

import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  ArrowLeft,
  PackageCheck,
  Truck,
  XCircle,
  RotateCcw,
  Star,
  ExternalLink,
  CreditCard,
  MapPin,
  Calendar,
  Copy,
  Check,
  Loader2,
} from 'lucide-react';
import { orderApi } from '@/api/order.api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { toast } from 'sonner';
import type { Order } from '@/types';

const statusMeta: Record<
  string,
  { label: string; variant: 'success' | 'lime' | 'secondary' | 'warning' | 'outline' }
> = {
  PENDING_PAYMENT: { label: 'Chờ thanh toán', variant: 'warning' },
  CONFIRMED: { label: 'Đã xác nhận', variant: 'lime' },
  PROCESSING: { label: 'Đang xử lý', variant: 'lime' },
  SHIPPED: { label: 'Đang giao', variant: 'secondary' },
  DELIVERED: { label: 'Đã giao', variant: 'success' },
  CANCELLED: { label: 'Đã hủy', variant: 'outline' },
  RETURNED: { label: 'Đã trả hàng', variant: 'outline' },
};

function formatCurrency(value?: number | string | null) {
  const numeric = typeof value === 'number' ? value : Number(value || 0);
  return `${numeric.toLocaleString('vi-VN')} ₫`;
}

export default function OrderDetailPage() {
  const params = useParams();
  const router = useRouter();
  const queryClient = useQueryClient();

  // Nhận diện orderId linh hoạt theo tên thư mục [orderId] hoặc [ordersId]
  const orderId = Number(params?.orderId ?? params?.ordersId);

  const [qrModalData, setQrModalData] = useState<{
    qrUrl: string;
    amount?: number;
    transferContent?: string;
  } | null>(null);
  const [isCopied, setIsCopied] = useState(false);
  const [isFetchingPayment, setIsFetchingPayment] = useState(false);

  // Lấy chi tiết đơn hàng
  const { data, isLoading } = useQuery({
    queryKey: ['order-detail', orderId],
    queryFn: () => orderApi.getOrderById(orderId),
    enabled: !isNaN(orderId) && orderId > 0,
  });

  const order = (data?.data ?? null) as Order | null;

  // Xử lý hủy đơn hàng
  const cancelMutation = useMutation({
    mutationFn: () => orderApi.cancelOrder(orderId),
    onSuccess: () => {
      toast.success('Hủy đơn hàng thành công');
      queryClient.invalidateQueries({ queryKey: ['order-detail', orderId] });
      queryClient.invalidateQueries({ queryKey: ['account-orders'] });
    },
    onError: (err: any) => {
      toast.error(err?.response?.data?.message || 'Không thể hủy đơn hàng lúc này');
    },
  });

  const handleCopy = (text: string) => {
    navigator.clipboard.writeText(text);
    setIsCopied(true);
    toast.success('Đã sao chép nội dung chuyển khoản');
    setTimeout(() => setIsCopied(false), 2000);
  };

  const handleGetPaymentQR = async () => {
    setIsFetchingPayment(true);
    try {
      const res = await orderApi.getPaymentDetails(orderId);
      if (res?.data?.qrUrl) {
        setQrModalData({
          qrUrl: res.data.qrUrl,
          amount: res.data.amount ?? (res.data.totalAmount != null ? Number(res.data.totalAmount) : (order?.totalAmount != null ? Number(order.totalAmount) : undefined)),
          transferContent: res.data.transferContent ?? res.data.orderCode ?? order?.orderCode,
        });
      } else {
        toast.error('Chưa có thông tin thanh toán cho đơn hàng này');
      }
    } catch (err: any) {
      toast.error(err?.response?.data?.message || 'Không thể tải mã QR thanh toán');
    } finally {
      setIsFetchingPayment(false);
    }
  };

  if (isLoading) {
    return (
      <div className="mx-auto max-w-5xl space-y-6">
        <div className="h-28 rounded-2xl border border-[#EAE6DF] bg-white p-6 animate-pulse" />
        <div className="grid gap-6 lg:grid-cols-[1.3fr_0.7fr]">
          <div className="h-80 rounded-2xl border border-[#EAE6DF] bg-white p-6 animate-pulse" />
          <div className="h-80 rounded-2xl border border-[#EAE6DF] bg-white p-6 animate-pulse" />
        </div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="mx-auto max-w-md rounded-2xl border border-[#EAE6DF] bg-white p-8 text-center shadow-xs">
        <h3 className="text-base font-semibold text-neutral-900">Không tìm thấy đơn hàng</h3>
        <p className="mt-1 text-xs text-neutral-500">Đơn hàng không tồn tại hoặc bạn không có quyền xem.</p>
        <Link href="/orders" className="mt-5 inline-block">
          <Button variant="outline" className="rounded-xl border-[#EAE6DF]">
            <ArrowLeft className="mr-2 size-4" /> Quay lại danh sách đơn hàng
          </Button>
        </Link>
      </div>
    );
  }

  const meta = statusMeta[order.status] ?? { label: order.status, variant: 'outline' as const };

  return (
    <div className="mx-auto max-w-5xl space-y-6">
      {/* Header trạng thái */}
      <div className="rounded-2xl border border-[#EAE6DF] bg-white p-6 sm:p-8 shadow-xs">
        <div className="flex flex-col gap-4 border-b border-[#EAE6DF] pb-6 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-3">
            {/* Đường dẫn chuẩn /orders */}
            <Link
              href="/orders"
              className="inline-flex h-10 w-10 items-center justify-center rounded-xl border border-[#EAE6DF] text-neutral-600 hover:bg-neutral-50 transition"
              title="Quay lại danh sách đơn mua"
            >
              <ArrowLeft className="size-4" />
            </Link>
            <div>
              <p className="text-xs uppercase tracking-[0.2em] text-neutral-400">Chi tiết đơn hàng</p>
              <h2 className="mt-1 text-xl font-bold text-neutral-900">#{order.orderCode}</h2>
            </div>
          </div>

          <Badge variant={meta.variant} className="self-start sm:self-auto text-xs px-3 py-1">
            {meta.label}
          </Badge>
        </div>

        {/* Thông tin đơn */}
        <div className="mt-6 grid gap-4 sm:grid-cols-3">
          <div className="rounded-xl border border-[#EAE6DF] bg-neutral-50/40 p-4">
            <div className="flex items-center gap-2 text-neutral-400 text-xs">
              <Calendar className="size-3.5" />
              <span className="uppercase tracking-wider">Ngày đặt hàng</span>
            </div>
            <p className="mt-2 text-sm font-semibold text-neutral-900">
              {order.orderDate ? new Date(order.orderDate).toLocaleDateString('vi-VN') : '—'}
            </p>
          </div>

          <div className="rounded-xl border border-[#EAE6DF] bg-neutral-50/40 p-4">
            <div className="flex items-center gap-2 text-neutral-400 text-xs">
              <CreditCard className="size-3.5" />
              <span className="uppercase tracking-wider">Phương thức</span>
            </div>
            <p className="mt-2 text-sm font-semibold text-neutral-900">
              {order.paymentMethod === 'COD' ? 'Thanh toán khi nhận hàng (COD)' : 'Chuyển khoản SePay'}
            </p>
          </div>

          <div className="rounded-xl border border-[#EAE6DF] bg-neutral-50/40 p-4">
            <div className="flex items-center gap-2 text-neutral-400 text-xs">
              <span className="size-2 rounded-full bg-emerald-500 inline-block" />
              <span className="uppercase tracking-wider">Thanh toán</span>
            </div>
            <p className="mt-2 text-sm font-semibold text-neutral-900">
              {order.paymentStatus === 'SUCCESS' ? 'Đã thanh toán' : 'Chưa thanh toán'}
            </p>
          </div>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1.3fr_0.7fr]">
        {/* Cột trái: Sản phẩm */}
        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-6 shadow-xs">
          <div className="flex items-center gap-2 border-b border-[#EAE6DF] pb-4">
            <PackageCheck className="size-4 text-neutral-700" />
            <h3 className="text-sm font-bold text-neutral-900">
              Sản phẩm trong đơn ({order.orderItems?.length || 0})
            </h3>
          </div>

          <div className="mt-4 divide-y divide-[#EAE6DF]">
            {order.orderItems?.map((item: any) => {
              // 1. Lấy đúng productId từ item hoặc từ quan hệ variant -> product
              const resolvedProductId =
                item.productId ??
                item.variant?.productId ??
                item.variant?.product?.id;

              // 2. Tạo đường link:
              // - Nếu muốn xem trang chi tiết sản phẩm trên giao diện web (Client):
              const productHref = resolvedProductId ? `/products/${resolvedProductId}` : '#';

              // - HOẶC nếu bạn muốn mở API JSON server (http://localhost:3000/api/v1/catalog/products/:id):
              // const productHref = resolvedProductId ? `http://localhost:3000/api/v1/catalog/products/${resolvedProductId}` : '#';

              return (
                <div
                  key={item.id}
                  className="py-4.5 first:pt-2 last:pb-2 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div className="flex items-start gap-3 min-w-0">
                    {/* Hiển thị ảnh thực tế của sản phẩm thay vì icon trống */}
                    <div className="size-12 rounded-xl bg-neutral-100 border border-[#EAE6DF] flex items-center justify-center shrink-0 overflow-hidden">
                      {item.productImageUrl || item.variant?.imageUrl || item.variant?.product?.thumbnailUrl ? (
                        <img
                          src={item.productImageUrl || item.variant?.imageUrl || item.variant?.product?.thumbnailUrl}
                          alt={item.productName}
                          className="size-full object-cover"
                        />
                      ) : (
                        <PackageCheck className="size-5 text-neutral-400" />
                      )}
                    </div>

                    <div className="min-w-0">
                      <Link
                        href={productHref}
                        className="group inline-flex items-center gap-1.5 text-sm font-semibold text-neutral-900 hover:text-black hover:underline transition"
                        title="Bấm để xem sản phẩm"
                      >
                        <span className="line-clamp-1">{item.productName}</span>
                        <ExternalLink className="size-3.5 opacity-0 group-hover:opacity-100 text-neutral-400 shrink-0 transition-opacity" />
                      </Link>

                      <p className="mt-1 text-xs text-neutral-500">
                        {item.variantValue ? `Phân loại: ${item.variantValue}` : 'Tiêu chuẩn'} • Số lượng:{' '}
                        <strong className="text-neutral-800">{item.quantity}</strong>
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center justify-between sm:flex-col sm:items-end gap-2 shrink-0">
                    <p className="text-sm font-bold text-neutral-900">
                      {formatCurrency(Number(item.unitPrice) * item.quantity)}
                    </p>
                    <span className="text-[11px] text-neutral-400">
                      {formatCurrency(item.unitPrice)} / món
                    </span>

                    {order.status === 'DELIVERED' && (
                      <Link
                        href={`/orders/${order.id}/review/${resolvedProductId ?? item.variantId ?? item.id}`}
                        className="mt-1"
                      >
                        <Button variant="outline" size="sm" className="rounded-xl border-[#EAE6DF] text-xs h-8">
                          <Star className="mr-1 size-3 text-amber-500 fill-amber-500" />
                          Đánh giá
                        </Button>
                      </Link>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Cột phải: Thông tin giao nhận, Thanh toán, Nút hành động */}
        <div className="space-y-6">
          <div className="rounded-2xl border border-[#EAE6DF] bg-white p-6 shadow-xs">
            <div className="flex items-center gap-2 border-b border-[#EAE6DF] pb-4">
              <MapPin className="size-4 text-neutral-700" />
              <h3 className="text-sm font-bold text-neutral-900">Địa chỉ giao hàng</h3>
            </div>
            <div className="mt-4 text-xs text-neutral-600 leading-relaxed">
              <p className="whitespace-pre-wrap">{order.shippingAddress || 'Chưa có thông tin địa chỉ.'}</p>
            </div>
          </div>

          <div className="rounded-2xl border border-[#EAE6DF] bg-white p-6 shadow-xs">
            <h3 className="text-sm font-bold text-neutral-900 border-b border-[#EAE6DF] pb-4">
              Chi tiết thanh toán
            </h3>
            <div className="mt-4 space-y-2.5 text-xs text-neutral-600">
              <div className="flex justify-between">
                <span>Tạm tính</span>
                <span className="font-medium text-neutral-800">{formatCurrency(order.subTotal)}</span>
              </div>
              <div className="flex justify-between">
                <span>Phí vận chuyển</span>
                <span className="font-medium text-neutral-800">{formatCurrency(order.shippingFee)}</span>
              </div>

              {(Number(order.voucherDiscount || 0) > 0 || Number(order.pointDiscount || 0) > 0) && (
                <div className="flex justify-between text-emerald-600">
                  <span>Giảm giá (Voucher & Điểm)</span>
                  <span className="font-semibold">
                    - {formatCurrency(Number(order.voucherDiscount || 0) + Number(order.pointDiscount || 0))}
                  </span>
                </div>
              )}

              <div className="mt-3 flex items-center justify-between border-t border-[#EAE6DF] pt-3 text-sm font-bold text-neutral-900">
                <span>Tổng thanh toán</span>
                <span className="text-base font-extrabold text-[#1D1F20]">{formatCurrency(order.totalAmount)}</span>
              </div>
            </div>
          </div>

          {/* Các nút xử lý thao tác đơn hàng */}
          <div className="flex flex-col gap-2.5">
            {order.status === 'PENDING_PAYMENT' && (
              <>
                {order.paymentMethod !== 'COD' && (
                  <Button
                    onClick={handleGetPaymentQR}
                    disabled={isFetchingPayment}
                    className="w-full rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-black py-5 text-sm font-bold"
                  >
                    {isFetchingPayment ? (
                      <>
                        <Loader2 className="mr-2 size-4 animate-spin" />
                        Đang lấy mã thanh toán...
                      </>
                    ) : (
                      'Thanh toán ngay (Mã QR)'
                    )}
                  </Button>
                )}

                <Button
                  onClick={() => {
                    if (confirm('Bạn có chắc chắn muốn hủy đơn hàng này không?')) {
                      cancelMutation.mutate();
                    }
                  }}
                  variant="destructive"
                  className="w-full rounded-xl py-5 text-sm font-semibold"
                  disabled={cancelMutation.isPending}
                >
                  <XCircle className="mr-2 size-4" />
                  {cancelMutation.isPending ? 'Đang hủy đơn...' : 'Hủy đơn hàng'}
                </Button>
              </>
            )}

            {order.status === 'DELIVERED' && (
              <Link href={`/orders/${order.id}/return`}>
                <Button variant="outline" className="w-full rounded-xl border-[#EAE6DF] py-5 text-sm font-semibold">
                  <RotateCcw className="mr-2 size-4" />
                  Yêu cầu trả hàng / Hoàn tiền
                </Button>
              </Link>
            )}
          </div>
        </div>
      </div>

      {/* Modal Popup QR SePay */}
      {qrModalData && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-xs p-4 animate-in fade-in duration-200">
          <div className="w-full max-w-sm rounded-3xl bg-white p-6 sm:p-7 shadow-2xl relative text-center border border-[#84EA53] animate-in zoom-in-95 duration-200">
            <button
              onClick={() => setQrModalData(null)}
              className="absolute right-4 top-4 rounded-full p-1.5 text-neutral-400 hover:bg-neutral-100 hover:text-neutral-700 transition cursor-pointer"
            >
              <XCircle className="size-5" />
            </button>

            <h3 className="text-lg font-bold text-neutral-900">Quét mã để thanh toán</h3>
            <p className="mt-1 text-xs text-neutral-500">
              Sử dụng ứng dụng ngân hàng hoặc ví điện tử để quét mã QR bên dưới.
            </p>

            <div className="my-5 flex justify-center rounded-2xl bg-neutral-50 p-3 border border-[#EAE6DF]">
              <img
                src={qrModalData.qrUrl}
                alt="Mã QR thanh toán"
                className="w-full max-w-[220px] h-auto object-contain mix-blend-multiply"
              />
            </div>

            <div className="rounded-xl bg-amber-50 border border-amber-200/80 p-3.5 text-left space-y-2 text-xs mb-5">
              <div className="flex justify-between items-center">
                <span className="text-amber-800">Số tiền:</span>
                <span className="font-bold text-sm text-amber-900">
                  {formatCurrency(qrModalData.amount)}
                </span>
              </div>
              <div className="flex justify-between items-center gap-2">
                <span className="text-amber-800 shrink-0">Nội dung CK:</span>
                <button
                  type="button"
                  onClick={() => handleCopy(qrModalData.transferContent || '')}
                  className="flex items-center gap-1 font-mono font-bold text-amber-900 bg-amber-200/60 hover:bg-amber-200 px-2 py-0.5 rounded transition cursor-pointer"
                  title="Bấm để sao chép"
                >
                  <span>{qrModalData.transferContent}</span>
                  {isCopied ? <Check className="size-3 text-emerald-600" /> : <Copy className="size-3" />}
                </button>
              </div>
            </div>

            <Button
              onClick={() => {
                setQrModalData(null);
                queryClient.invalidateQueries({ queryKey: ['order-detail', orderId] });
              }}
              className="w-full rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-black font-semibold"
            >
              Tôi đã chuyển khoản thành công
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}