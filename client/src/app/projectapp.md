# Project Dump

**Root:** `C:\Programing\cnpm\fashion-ecom\client\src\app`

**Generated:** 2026-10-14 11:37:45.362098

**Files:** 48 | **Total size:** 477,667 bytes

---

# (account)\addresses\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\addresses\page.tsx` |
| Size | 463 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.856354 |

```tsx
import { Metadata } from 'next';
import { AddressList } from '@/modules/user/components/address-list';

export const metadata: Metadata = {
  title: 'Sổ địa chỉ — Fashion Store',
  description: 'Quản lý các địa chỉ nhận hàng của bạn.',
};

export default function AddressesPage() {
  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <AddressList />
    </div>
  );
}


```

# (account)\layout.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\layout.tsx` |
| Size | 5,149 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.927858 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  User,
  Shield,
  MapPin,
  Package,
  Coins,
  LogOut,
  LayoutDashboard,
} from 'lucide-react';
import { SiteHeader } from '@/components/layouts/site-header';
import { SiteFooter } from '@/components/layouts/site-footer';
import { useAuth } from '@/components/providers/auth-provider';
import { Avatar } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

const navItems = [
  { href: '/profile', label: 'Thông tin cá nhân', icon: User },
  { href: '/points', label: 'Lịch sử tích điểm', icon: Coins },
  { href: '/addresses', label: 'Sổ địa chỉ', icon: MapPin },
  { href: '/security', label: 'Bảo mật & Mật khẩu', icon: Shield },
  { href: '/orders', label: 'Đơn mua', icon: Package },
];

export default function AccountLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();
  const { user, isAdmin, logout } = useAuth();

  return (
    <div className="min-h-screen flex flex-col bg-[#FBF9F4]">
      <SiteHeader />

      <main className="flex-1 mx-auto w-full max-w-[1440px] px-4 sm:px-8 lg:px-12 py-8 sm:py-12">
        <div className="mb-8">
          <span className="eyebrow block mb-2">QUẢN LÝ TÀI KHOẢN</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Tài khoản của tôi
          </h1>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          {/* Sidebar */}
          <aside className="lg:col-span-1">
            <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs sticky top-28 space-y-6">
              {/* User overview */}
              <div className="flex items-center gap-3.5 pb-6 border-b border-[#EAE6DF]">
                <Avatar
                  src={user?.avatarUrl}
                  alt={user?.fullName || 'User'}
                  size="lg"
                  className="size-12 ring-2 ring-[#84EA53]/30"
                />
                <div className="min-w-0 flex-1">
                  <h3 className="text-sm font-semibold text-neutral-900 truncate">
                    {user?.fullName || 'Người dùng'}
                  </h3>
                  <p className="text-xs text-neutral-500 truncate">
                    {user?.email}
                  </p>
                  <div className="mt-1">
                    <Badge
                      variant={isAdmin ? 'lime' : 'secondary'}
                      className="text-[10px] py-0 px-2"
                    >
                      {user?.role?.name || (isAdmin ? 'Admin' : 'Khách hàng')}
                    </Badge>
                  </div>
                </div>
              </div>

              {/* Navigation Menu */}
              <nav className="space-y-1">
                {navItems.map((item) => {
                  const Icon = item.icon;
                  const isActive = pathname === item.href;
                  return (
                    <Link
                      key={item.href}
                      href={item.href}
                      className={cn(
                        'flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-medium transition',
                        isActive
                          ? 'bg-[#1D1F20] !text-white [&_*]:!text-white shadow-xs'
                          : 'text-neutral-600 hover:text-neutral-900 hover:bg-neutral-100',
                      )}
                    >
                      <Icon className="size-4 shrink-0" />
                      <span>{item.label}</span>
                    </Link>
                  );
                })}

                {isAdmin && (
                  <Link
                    href="/admin/dashboard"
                    className="flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold text-emerald-800 bg-emerald-50 hover:bg-emerald-100 transition mt-2"
                  >
                    <LayoutDashboard className="size-4 shrink-0 text-emerald-700" />
                    <span>Trang quản trị (Admin)</span>
                  </Link>
                )}
              </nav>

              {/* Logout Button */}
              <div className="pt-4 border-t border-[#EAE6DF]">
                <button
                  type="button"
                  onClick={logout}
                  className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-medium text-rose-600 hover:bg-rose-50 transition"
                >
                  <LogOut className="size-4 shrink-0" />
                  <span>Đăng xuất</span>
                </button>
              </div>
            </div>
          </aside>

          {/* Content Area */}
          <section className="lg:col-span-3">{children}</section>
        </div>
      </main>

      <SiteFooter />
    </div>
  );
}

```

# (account)\orders\[orderId]\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\orders\[orderId]\page.tsx` |
| Size | 19,977 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 17:56:40.338684 |

```tsx
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
```

# (account)\orders\[orderId]\return\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\orders\[orderId]\return\page.tsx` |
| Size | 13,861 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 01:44:07.126170 |

```tsx
'use client';

import { useMemo, useState } from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useMutation, useQuery } from '@tanstack/react-query';
import {
  ArrowLeft,
  Image as ImageIcon,
  Loader2,
  Plus,
  RotateCcw,
  Trash2,
} from 'lucide-react';
import { toast } from 'sonner';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { orderApi } from '@/api/order.api';
import { Button } from '@/components/ui/button';
import type { Order } from '@/types';

export default function OrderReturnPage() {
  const params = useParams<{ orderId: string }>();
  const router = useRouter();
  const orderId = Number(params.orderId);

  const { data, isLoading } = useQuery({
    queryKey: ['order-return', orderId],
    queryFn: () => orderApi.getOrderById(orderId),
    enabled: !!orderId,
  });

  const order = (data?.data ?? null) as Order | null;
  const orderItems = order?.orderItems ?? [];

  const [selectedItemId, setSelectedItemId] = useState<number | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [reason, setReason] = useState('Sản phẩm bị lỗi hoặc không đúng mô tả');
  const [description, setDescription] = useState('');
  const [mediaList, setMediaList] = useState<
    Array<{ mediaUrl: string; mediaType: string }>
  >([]);
  const [inputUrl, setInputUrl] = useState('');
  const [isUploading, setIsUploading] = useState(false);

  useMemo(() => {
    if (!selectedItemId && orderItems[0]) {
      setSelectedItemId(orderItems[0].id);
      setQuantity(orderItems[0].quantity || 1);
    }
  }, [orderItems, selectedItemId]);

  const selectedItem =
    orderItems.find((item) => item.id === selectedItemId) ??
    orderItems[0] ??
    null;

  const handleAddUrl = () => {
    const trimmed = inputUrl.trim();
    if (!trimmed) return;
    try {
      new URL(trimmed);
      setMediaList((prev) => [
        ...prev,
        { mediaUrl: trimmed, mediaType: 'image' },
      ]);
      setInputUrl('');
      toast.success('Đã thêm ảnh minh chứng');
    } catch {
      toast.error('URL không hợp lệ');
    }
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];

    // FIX 1: Reset value
    e.target.value = '';

    if (!file) return;

    if (file.size > 10 * 1024 * 1024) {
      toast.error('Kích thước tập tin tối đa là 10MB');
      return;
    }

    setIsUploading(true);
    const reader = new FileReader();
    reader.onload = async () => {
      try {
        const base64 = reader.result as string;
        const res = await apiClient.post<any>(API_ENDPOINTS.COMMON.UPLOAD, {
          fileName: file.name,
          fileType: file.type,
          base64,
          folder: 'returns', // Đừng quên giữ lại params folder đã fix ở bước trước
        });

        const uploaded = res.data?.data;
        if (uploaded?.url) {
          setMediaList((prev) => [
            ...prev,
            {
              mediaUrl: uploaded.url,
              mediaType: uploaded.type || 'image',
            },
          ]);
          toast.success('Tải ảnh minh chứng thành công!');
        }
      } catch (err: any) {
        toast.error(
          'Không thể tải ảnh: ' +
            (err?.response?.data?.message || err?.message),
        );
      } finally {
        setIsUploading(false);
      }
    };

    reader.onerror = () => {
      setIsUploading(false);
      toast.error('Lỗi đọc tập tin');
    };

    reader.readAsDataURL(file);
  };

  const handleRemoveMedia = (index: number) => {
    setMediaList((prev) => prev.filter((_, i) => i !== index));
  };

  const returnMutation = useMutation({
    mutationFn: async () => {
      if (!selectedItem) {
        throw new Error('Vui lòng chọn sản phẩm cần trả hàng');
      }

      const payload = {
        orderItemId: Number(selectedItem.id),
        quantity: Number(quantity),
        reason,
        description: description || undefined,
        medias: mediaList.length > 0 ? mediaList : undefined,
      };

      const res = await apiClient.post(
        API_ENDPOINTS.FULFILLMENT.RETURNS,
        payload,
      );
      return res.data;
    },
    onSuccess: () => {
      toast.success('Yêu cầu trả hàng đã được gửi thành công');
      router.push(`/orders/${orderId}`);
    },
    onError: (error: any) => {
      toast.error(
        error?.response?.data?.message || 'Không thể gửi yêu cầu trả hàng',
      );
    },
  });

  if (isLoading) {
    return (
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
        <div className="h-10 w-48 rounded-xl bg-neutral-100 animate-pulse" />
        <div className="mt-6 h-52 rounded-2xl bg-neutral-100 animate-pulse" />
      </div>
    );
  }

  if (!order) {
    return (
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs text-center">
        <p className="text-sm text-neutral-600">
          Không tìm thấy đơn hàng để yêu cầu trả hàng.
        </p>
        <Link href="/orders" className="mt-4 inline-block">
          <Button variant="outline" className="rounded-xl border-[#EAE6DF]">
            Quay lại
          </Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="flex items-center gap-3 border-b border-[#EAE6DF] pb-6">
        <Link
          href={`/orders/${orderId}`}
          className="inline-flex h-10 w-10 items-center justify-center rounded-xl border border-[#EAE6DF] text-neutral-600 hover:bg-neutral-50"
        >
          <ArrowLeft className="size-4" />
        </Link>
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-neutral-400">
            Trả hàng
          </p>
          <h2 className="mt-1 text-xl font-semibold text-neutral-900">
            Yêu cầu đổi / trả hàng
          </h2>
        </div>
      </div>

      <div className="mt-6 space-y-6">
        <div className="space-y-3">
          <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
            Sản phẩm muốn trả
          </label>
          <select
            value={selectedItem?.id ?? ''}
            onChange={(e) => {
              const itemId = Number(e.target.value);
              const current = orderItems.find((item) => item.id === itemId);
              setSelectedItemId(itemId);
              setQuantity(
                current?.quantity ? Math.min(current.quantity, 1) : 1,
              );
            }}
            className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900"
          >
            {orderItems.map((item) => (
              <option key={item.id} value={item.id}>
                {item.productName} ({item.quantity} sản phẩm)
              </option>
            ))}
          </select>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Số lượng
            </label>
            <input
              type="number"
              min={1}
              max={selectedItem?.quantity ?? 1}
              value={quantity}
              onChange={(e) =>
                setQuantity(
                  Math.max(
                    1,
                    Math.min(
                      Number(e.target.value || 1),
                      selectedItem?.quantity ?? 1,
                    ),
                  ),
                )
              }
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900"
            />
          </div>

          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Lý do
            </label>
            <select
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900"
            >
              <option value="Sản phẩm bị lỗi hoặc không đúng mô tả">
                Sản phẩm bị lỗi hoặc không đúng mô tả
              </option>
              <option value="Sản phẩm không phù hợp">
                Sản phẩm không phù hợp
              </option>
              <option value="Giao sai sản phẩm">Giao sai sản phẩm</option>
              <option value="Khác">Khác</option>
            </select>
          </div>
        </div>

        <div className="space-y-2">
          <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
            Mô tả chi tiết
          </label>
          <textarea
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Mô tả rõ tình trạng sản phẩm để hỗ trợ kiểm tra nhanh hơn..."
            className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-3 text-sm outline-none focus:border-neutral-900"
          />
        </div>

        {/* PHẦN ĐÍNH KÈM HÌNH ẢNH MINH CHỨNG */}
        <div className="space-y-3 rounded-xl border border-[#EAE6DF] bg-neutral-50/50 p-4">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-neutral-700 flex items-center gap-2">
              <ImageIcon className="size-4 text-neutral-500" />
              Hình ảnh minh chứng lỗi ({mediaList.length}/5)
            </span>
            <label className="inline-flex cursor-pointer items-center gap-1.5 rounded-lg border border-[#EAE6DF] bg-white px-3 py-1.5 text-xs font-medium text-neutral-700 shadow-2xs hover:bg-neutral-100">
              {isUploading ? (
                <>
                  <Loader2 className="size-3.5 animate-spin" /> Đang tải...
                </>
              ) : (
                <>
                  <Plus className="size-3.5" /> Thêm ảnh từ máy
                </>
              )}
              <input
                type="file"
                accept="image/*"
                onChange={handleFileUpload}
                disabled={isUploading || mediaList.length >= 5}
                className="hidden"
              />
            </label>
          </div>

          <div className="flex gap-2">
            <input
              type="url"
              placeholder="Hoặc dán liên kết ảnh minh chứng (https://...)"
              value={inputUrl}
              onChange={(e) => setInputUrl(e.target.value)}
              className="flex-1 rounded-lg border border-[#EAE6DF] bg-white px-3 py-1.5 text-xs outline-none focus:border-neutral-900"
            />
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={handleAddUrl}
              disabled={!inputUrl.trim() || mediaList.length >= 5}
              className="rounded-lg text-xs"
            >
              Thêm URL
            </Button>
          </div>

          {mediaList.length > 0 && (
            <div className="grid grid-cols-2 gap-3 pt-2 sm:grid-cols-4 md:grid-cols-5">
              {mediaList.map((m, idx) => (
                <div
                  key={idx}
                  className="group relative aspect-square overflow-hidden rounded-xl border border-[#EAE6DF] bg-black/5"
                >
                  <img
                    src={m.mediaUrl}
                    alt={`Evidence ${idx + 1}`}
                    className="h-full w-full object-cover"
                  />
                  <button
                    type="button"
                    onClick={() => handleRemoveMedia(idx)}
                    className="absolute right-1 top-1 rounded-md bg-black/70 p-1 text-white opacity-0 transition-opacity hover:bg-red-600 group-hover:opacity-100"
                    title="Xóa"
                  >
                    <Trash2 className="size-3.5" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="flex items-center justify-end gap-3 pt-2">
          <Link href={`/orders/${orderId}`}>
            <Button variant="outline" className="rounded-xl border-[#EAE6DF]">
              Hủy
            </Button>
          </Link>
          <Button
            onClick={() => returnMutation.mutate()}
            disabled={returnMutation.isPending || !selectedItem}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white"
          >
            {returnMutation.isPending ? (
              <>
                <Loader2 className="mr-2 size-4 animate-spin" />
                Đang gửi...
              </>
            ) : (
              <>
                <RotateCcw className="mr-2 size-4" />
                Gửi yêu cầu
              </>
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}

```

# (account)\orders\[orderId]\review\[productId]\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\orders\[orderId]\review\[productId]\page.tsx` |
| Size | 12,031 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 16:58:16.026413 |

```tsx
'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useMutation, useQuery } from '@tanstack/react-query';
import {
  ArrowLeft,
  Image as ImageIcon,
  Loader2,
  Plus,
  Send,
  Star,
  Trash2,
  Video,
} from 'lucide-react';
import { toast } from 'sonner';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { orderApi } from '@/api/order.api';
import { Button } from '@/components/ui/button';
import type { Order } from '@/types';

export default function ProductReviewPage() {
  const params = useParams<{ orderId: string; productId: string }>();
  const router = useRouter();
  const orderId = Number(params.orderId);
  const productId = Number(params.productId);

  const { data, isLoading } = useQuery({
    queryKey: ['order-review', orderId],
    queryFn: () => orderApi.getOrderById(orderId),
    enabled: !!orderId,
  });

  const order = (data?.data ?? null) as Order | null;
  const [rating, setRating] = useState(5);
  const [content, setContent] = useState('');
  const [mediaList, setMediaList] = useState<
    Array<{ url: string; type: string }>
  >([]);
  const [inputUrl, setInputUrl] = useState('');
  const [isUploading, setIsUploading] = useState(false);

  const handleAddUrl = () => {
    const trimmed = inputUrl.trim();
    if (!trimmed) return;
    try {
      new URL(trimmed);
      const isVideo = trimmed.endsWith('.mp4') || trimmed.includes('video');
      setMediaList((prev) => [
        ...prev,
        { url: trimmed, type: isVideo ? 'video' : 'image' },
      ]);
      setInputUrl('');
      toast.success('Đã thêm liên kết hình ảnh / video');
    } catch {
      toast.error('URL không hợp lệ. Vui lòng nhập URL dạng https://...');
    }
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];

    // FIX 1: Reset value của input để cho phép chọn lại cùng 1 file
    e.target.value = '';

    if (!file) return;

    if (file.size > 10 * 1024 * 1024) {
      toast.error('Kích thước tập tin tối đa là 10MB');
      return;
    }

    setIsUploading(true);
    const reader = new FileReader();
    reader.onload = async () => {
      try {
        const base64 = reader.result as string;
        const res = await apiClient.post<any>(API_ENDPOINTS.COMMON.UPLOAD, {
          fileName: file.name,
          fileType: file.type,
          base64,
          folder: 'reviews',
        });

        const uploaded = res.data?.data;
        if (uploaded?.url) {
          setMediaList((prev) => [
            ...prev,
            {
              url: uploaded.url,
              type:
                uploaded.type ||
                (file.type.startsWith('video') ? 'video' : 'image'),
            },
          ]);
          toast.success('Tải lên tập tin thành công!');
        }
      } catch (err: any) {
        toast.error(
          'Không thể tải tập tin lên: ' +
            (err?.response?.data?.message || err?.message),
        );
      } finally {
        setIsUploading(false);
      }
    };

    reader.onerror = () => {
      setIsUploading(false);
      toast.error('Lỗi đọc tập tin');
    };

    reader.readAsDataURL(file);
  };

  const handleRemoveMedia = (index: number) => {
    setMediaList((prev) => prev.filter((_, i) => i !== index));
  };

  const reviewMutation = useMutation({
    mutationFn: async () => {
      if (!orderId || !productId) {
        throw new Error('Thiếu thông tin đơn hàng hoặc sản phẩm');
      }

      const payload = {
        productId,
        orderId,
        rating,
        content: content.trim(),
        mediaUrls: mediaList,
      };

      const res = await apiClient.post(
        API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS,
        payload,
      );
      return res.data;
    },
    onSuccess: () => {
      toast.success('Cảm ơn bạn đã đánh giá sản phẩm!');
      router.push(`/orders/${orderId}`);
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Không thể gửi đánh giá');
    },
  });

  if (isLoading) {
    return (
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
        <div className="h-10 w-48 rounded-xl bg-neutral-100 animate-pulse" />
        <div className="mt-6 h-52 rounded-2xl bg-neutral-100 animate-pulse" />
      </div>
    );
  }

  if (!order) {
    return (
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs text-center">
        <p className="text-sm text-neutral-600">
          Không tìm thấy đơn hàng để đánh giá.
        </p>
        <Link href="/orders" className="mt-4 inline-block">
          <Button variant="outline" className="rounded-xl border-[#EAE6DF]">
            Quay lại
          </Button>
        </Link>
      </div>
    );
  }

  const productName =
    order.orderItems.find(
      (item) => Number(item.variantId ?? item.id) === productId,
    )?.productName || 'Sản phẩm';

  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="flex items-center gap-3 border-b border-[#EAE6DF] pb-6">
        <Link
          href={`/orders/${orderId}`}
          className="inline-flex h-10 w-10 items-center justify-center rounded-xl border border-[#EAE6DF] text-neutral-600 hover:bg-neutral-50"
        >
          <ArrowLeft className="size-4" />
        </Link>
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-neutral-400">
            Đánh giá sản phẩm
          </p>
          <h2 className="mt-1 text-xl font-semibold text-neutral-900">
            {productName}
          </h2>
        </div>
      </div>

      <div className="mt-6 space-y-6">
        <div>
          <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
            Đánh giá của bạn
          </label>
          <div className="mt-3 flex items-center gap-2">
            {[1, 2, 3, 4, 5].map((value) => (
              <button
                key={value}
                type="button"
                onClick={() => setRating(value)}
                className="transition hover:scale-110"
                aria-label={`Rating ${value}`}
              >
                <Star
                  className={`size-8 ${value <= rating ? 'fill-amber-400 text-amber-400' : 'text-neutral-300'}`}
                />
              </button>
            ))}
          </div>
        </div>

        <div className="space-y-2">
          <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
            Nội dung đánh giá
          </label>
          <textarea
            rows={5}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Chia sẻ cảm nhận của bạn về chất lượng, kiểu dáng, độ bền và trải nghiệm mua hàng..."
            className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-3 text-sm outline-none focus:border-neutral-900"
          />
        </div>

        {/* PHẦN ĐÍNH KÈM HÌNH ẢNH / VIDEO */}
        <div className="space-y-3 rounded-xl border border-[#EAE6DF] bg-neutral-50/50 p-4">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-neutral-700 flex items-center gap-2">
              <ImageIcon className="size-4 text-neutral-500" />
              Hình ảnh & Video thực tế ({mediaList.length}/10)
            </span>
            <label className="inline-flex cursor-pointer items-center gap-1.5 rounded-lg border border-[#EAE6DF] bg-white px-3 py-1.5 text-xs font-medium text-neutral-700 shadow-2xs hover:bg-neutral-100">
              {isUploading ? (
                <>
                  <Loader2 className="size-3.5 animate-spin" /> Đang tải...
                </>
              ) : (
                <>
                  <Plus className="size-3.5" /> Tải ảnh / video từ máy
                </>
              )}
              <input
                type="file"
                accept="image/*,video/*"
                onChange={handleFileUpload}
                disabled={isUploading || mediaList.length >= 10}
                className="hidden"
              />
            </label>
          </div>

          <div className="flex gap-2">
            <input
              type="url"
              placeholder="Hoặc dán liên kết ảnh / video (https://...)"
              value={inputUrl}
              onChange={(e) => setInputUrl(e.target.value)}
              className="flex-1 rounded-lg border border-[#EAE6DF] bg-white px-3 py-1.5 text-xs outline-none focus:border-neutral-900"
            />
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={handleAddUrl}
              disabled={!inputUrl.trim() || mediaList.length >= 10}
              className="rounded-lg text-xs"
            >
              Thêm URL
            </Button>
          </div>

          {mediaList.length > 0 && (
            <div className="grid grid-cols-2 gap-3 pt-2 sm:grid-cols-4 md:grid-cols-5">
              {mediaList.map((m, idx) => (
                <div
                  key={idx}
                  className="group relative aspect-square overflow-hidden rounded-xl border border-[#EAE6DF] bg-black/5"
                >
                  {m.type === 'video' ? (
                    <div className="flex h-full w-full items-center justify-center bg-neutral-900 text-white">
                      <Video className="size-8 opacity-70" />
                    </div>
                  ) : (
                    <img
                      src={m.url}
                      alt={`Media ${idx + 1}`}
                      className="h-full w-full object-cover"
                    />
                  )}
                  <button
                    type="button"
                    onClick={() => handleRemoveMedia(idx)}
                    className="absolute right-1 top-1 rounded-md bg-black/70 p-1 text-white opacity-0 transition-opacity hover:bg-red-600 group-hover:opacity-100"
                    title="Xóa"
                  >
                    <Trash2 className="size-3.5" />
                  </button>
                  <span className="absolute bottom-1 left-1 rounded bg-black/60 px-1 py-0.5 text-[9px] uppercase tracking-wider text-white">
                    {m.type}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="flex justify-end gap-3 pt-2">
          <Link href={`/orders/${orderId}`}>
            <Button variant="outline" className="rounded-xl border-[#EAE6DF]">
              Hủy
            </Button>
          </Link>

          <Button
            onClick={() => reviewMutation.mutate()}
            disabled={reviewMutation.isPending || !content.trim()}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white"
          >
            {reviewMutation.isPending ? (
              <>
                <Loader2 className="mr-2 size-4 animate-spin" />
                Đang gửi...
              </>
            ) : (
              <>
                <Send className="mr-2 size-4" />
                Gửi đánh giá
              </>
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}

```

# (account)\orders\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\orders\page.tsx` |
| Size | 9,286 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 16:58:16.020409 |

```tsx
'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { PackageSearch, ArrowUpRight, ShoppingBag } from 'lucide-react';
import { orderApi } from '@/api/order.api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import type { Order } from '@/types';
import { cn } from '@/lib/utils';

const statusMeta: Record<
  string,
  {
    label: string;
    variant: 'success' | 'lime' | 'secondary' | 'warning' | 'outline';
  }
> = {
  PENDING_PAYMENT: { label: 'Chờ thanh toán', variant: 'warning' },
  CONFIRMED: { label: 'Đã xác nhận', variant: 'lime' },
  PROCESSING: { label: 'Đang xử lý', variant: 'lime' },
  SHIPPED: { label: 'Đang giao', variant: 'secondary' },
  DELIVERED: { label: 'Đã giao', variant: 'success' },
  CANCELLED: { label: 'Đã hủy', variant: 'outline' },
  RETURNED: { label: 'Đã trả hàng', variant: 'outline' },
};

const TABS = [
  { id: 'ALL', label: 'Tất cả' },
  { id: 'PENDING_PAYMENT', label: 'Chờ thanh toán' },
  { id: 'PROCESSING', label: 'Đang xử lý' },
  { id: 'SHIPPED', label: 'Đang giao' },
  { id: 'DELIVERED', label: 'Hoàn thành' },
  { id: 'CANCELLED', label: 'Đã hủy' },
  { id: 'RETURNED', label: 'Trả hàng' },
];

function formatCurrency(value: number | string) {
  const numeric = typeof value === 'number' ? value : Number(value || 0);
  return `${numeric.toLocaleString('vi-VN')} ₫`;
}

export default function OrdersPage() {
  const [activeTab, setActiveTab] = useState('ALL');

  const { data, isLoading } = useQuery({
    queryKey: ['account-orders', activeTab],
    queryFn: () =>
      orderApi.getOrders({
        page: 1,
        limit: 20,
        ...(activeTab !== 'ALL' ? { status: activeTab } : {}),
      }),
  });

  const orders = (data?.data?.data ?? data?.data ?? []) as Order[];

  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4">
        <div>
          <h2 className="text-xl font-semibold text-neutral-900">Đơn mua</h2>
          <p className="text-xs text-neutral-500 mt-1">
            Theo dõi tiến độ, kiểm tra chi tiết và quản lý các đơn hàng của bạn.
          </p>
        </div>

        <div className="flex items-center gap-2 rounded-xl bg-neutral-100 px-3 py-2 text-xs font-medium text-neutral-600">
          <ShoppingBag className="size-4" />
          <span>{orders.length} đơn hàng</span>
        </div>
      </div>

      <div className="flex overflow-x-auto border-b border-[#EAE6DF] mb-6 scrollbar-hide pb-1">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={cn(
              'whitespace-nowrap px-4 py-3 text-sm font-medium transition-colors border-b-2',
              activeTab === tab.id
                ? 'border-neutral-900 text-neutral-900'
                : 'border-transparent text-neutral-500 hover:text-neutral-700',
            )}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((item) => (
            <div
              key={item}
              className="h-32 rounded-2xl border border-[#EAE6DF] bg-neutral-50 animate-pulse"
            />
          ))}
        </div>
      ) : orders.length === 0 ? (
        <div className="mt-8 rounded-2xl border border-dashed border-[#EAE6DF] bg-neutral-50/40 px-6 py-12 text-center">
          <PackageSearch className="mx-auto size-8 text-neutral-400" />
          <h3 className="mt-4 text-sm font-semibold text-neutral-800">
            Bạn chưa có đơn hàng nào
          </h3>
          <p className="mt-1 text-xs text-neutral-500">
            Hãy mua sắm và các đơn hàng của bạn sẽ hiển thị tại đây.
          </p>
          <Link href="/" className="mt-5 inline-block">
            <Button className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white">
              Tiếp tục mua sắm
            </Button>
          </Link>
        </div>
      ) : (
        <div className="mt-6 space-y-4">
          {orders.map((order) => {
            const meta = statusMeta[order.status] ?? {
              label: order.status,
              variant: 'outline',
            };
            const firstItem = order.orderItems?.[0];

            return (
              <div
                key={order.id}
                className="rounded-2xl border border-[#EAE6DF] bg-neutral-50/30 p-5 transition hover:border-neutral-300"
              >
                <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                  <div>
                    <div className="flex flex-wrap items-center gap-2">
                      <p className="text-sm font-semibold text-neutral-900">
                        {order.orderCode}
                      </p>
                      <Badge variant={meta.variant}>{meta.label}</Badge>
                    </div>
                    <p className="mt-1 text-[11px] text-neutral-500">
                      Đặt ngày{' '}
                      {order.orderDate
                        ? new Date(order.orderDate).toLocaleDateString('vi-VN')
                        : '—'}
                    </p>
                  </div>

                  <div className="flex items-center gap-2 md:justify-end">
                    <div className="text-right">
                      <p className="text-[11px] text-neutral-500">
                        Tổng thanh toán
                      </p>
                      <p className="text-sm font-semibold text-neutral-900">
                        {formatCurrency(order.totalAmount)}
                      </p>
                    </div>
                    <Link href={`/orders/${order.id}`}>
                      <Button
                        variant="outline"
                        size="sm"
                        className="rounded-xl border-[#EAE6DF]"
                      >
                        Xem chi tiết
                        <ArrowUpRight className="ml-2 size-3.5" />
                      </Button>
                    </Link>
                  </div>
                </div>

                {/* Phần thông tin sản phẩm trong từng thẻ đơn hàng */}
                <div className="mt-4 flex flex-col gap-3 border-t border-[#EAE6DF] pt-4 md:flex-row md:items-center md:justify-between">
                  <div className="min-w-0 flex-1">
                    <p className="text-xs text-neutral-500 mb-1.5">Sản phẩm</p>
                    <div className="flex flex-col gap-1.5">
                      {order.orderItems?.length ? (
                        order.orderItems.map((item) => {
                          // Lấy slug của sản phẩm (hoặc fallback về productId nếu backend chưa trả slug)
                          const productHref = item.productSlug
                            ? `/products/${item.productSlug}`
                            : item.productId
                              ? `/products/${item.productId}`
                              : '#';

                          return (
                            <div
                              key={item.id}
                              className="flex items-center gap-2"
                            >
                              <Link
                                href={productHref}
                                className="text-sm font-medium text-neutral-800 hover:text-black hover:underline line-clamp-1 transition"
                                title="Bấm để xem chi tiết sản phẩm"
                              >
                                {item.productName}
                              </Link>
                              <span className="text-xs text-neutral-400">
                                x{item.quantity}
                              </span>
                            </div>
                          );
                        })
                      ) : (
                        <p className="text-sm text-neutral-500">
                          Không có thông tin sản phẩm
                        </p>
                      )}
                    </div>
                  </div>

                  <div className="text-xs text-neutral-500 md:text-right shrink-0">
                    {firstItem ? (
                      <span>
                        {firstItem.quantity} sản phẩm ·{' '}
                        {formatCurrency(firstItem.unitPrice)} / món
                      </span>
                    ) : (
                      <span>Đang cập nhật</span>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

```

# (account)\points\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\points\page.tsx` |
| Size | 10,216 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.929859 |

```tsx
'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import {
  Coins,
  ArrowUpRight,
  ArrowDownLeft,
  RefreshCw,
  Gift,
  HelpCircle,
  ShoppingBag,
  Clock,
  Sparkles,
} from 'lucide-react';
import { getPointBalance, getPointHistory } from '@/modules/loyalty-engage/api';
import { useAuth } from '@/components/providers/auth-provider';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { formatDate } from '@/lib/utils/format-date';
import type { PointTransaction } from '@/types';

const transTypeMeta: Record<
  string,
  { label: string; isCredit: boolean; variant: 'success' | 'destructive' | 'secondary' | 'warning' }
> = {
  EARN: { label: 'Tích điểm đơn hàng', isCredit: true, variant: 'success' },
  REDEEM: { label: 'Tiêu điểm thanh toán', isCredit: false, variant: 'destructive' },
  REFUND: { label: 'Hoàn trả điểm', isCredit: true, variant: 'warning' },
  EXPIRE: { label: 'Điểm hết hạn', isCredit: false, variant: 'secondary' },
};

export default function AccountPointsPage() {
  const { user } = useAuth();
  const [page, setPage] = React.useState(1);

  // Fetch balance
  const { data: balanceData } = useQuery({
    queryKey: ['user-point-balance'],
    queryFn: () => getPointBalance(),
  });

  // Fetch history
  const { data: historyData, isLoading } = useQuery({
    queryKey: ['user-point-history', page],
    queryFn: () => getPointHistory({ page, limit: 10 }),
  });

  const currentPoints =
    balanceData?.data?.rewardPoints ?? user?.rewardPoints ?? 0;

  const transactions: PointTransaction[] = (historyData?.data as any) || [];
  const pagination = historyData?.pagination || { page: 1, limit: 10, total: 0, totalPages: 1 };

  return (
    <div className="space-y-8">
      {/* Header card: Point balance banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-[#1D1F20] to-[#2D3032] p-8 text-white shadow-xl">
        <div className="absolute right-0 top-0 -mr-10 -mt-10 size-60 rounded-full bg-[#84EA53]/10 blur-3xl" />
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="flex size-7 items-center justify-center rounded-full bg-[#84EA53]/20 text-[#84EA53]">
                <Coins className="size-4" />
              </span>
              <span className="text-xs font-semibold uppercase tracking-wider text-[#84EA53]">
                COMMON LOYALTY REWARDS
              </span>
            </div>
            <h2 className="text-sm text-neutral-400 font-medium">Số dư điểm hiện tại của bạn</h2>
            <div className="flex items-baseline gap-2 mt-1">
              <span className="font-display text-4xl sm:text-5xl font-bold tracking-tight text-white">
                {currentPoints.toLocaleString('vi-VN')}
              </span>
              <span className="text-base text-neutral-400 font-medium">điểm</span>
            </div>
            <p className="text-xs text-neutral-400 mt-2 max-w-md leading-relaxed">
              Tương đương{' '}
              <strong className="text-white font-semibold">
                {currentPoints.toLocaleString('vi-VN')} VNĐ
              </strong>{' '}
              có thể khấu trừ trực tiếp khi thanh toán đơn hàng tiếp theo.
            </p>
          </div>

          <div className="flex flex-col sm:flex-row gap-3">
            <Link
              href="/products"
              className="inline-flex items-center justify-center rounded-xl bg-[#84EA53] text-[#1D1F20] hover:bg-[#72d244] font-semibold text-xs px-5 py-2.5 transition shadow-xs"
            >
              <ShoppingBag className="size-4 mr-2" /> Dùng điểm mua sắm
            </Link>
          </div>
        </div>

        {/* Perks ribbon */}
        <div className="mt-8 pt-6 border-t border-white/10 grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
          <div className="flex items-center gap-2.5 text-neutral-300">
            <Sparkles className="size-4 text-[#84EA53] shrink-0" />
            <span>Tích 1% giá trị mỗi đơn hàng hoàn tất</span>
          </div>
          <div className="flex items-center gap-2.5 text-neutral-300">
            <Coins className="size-4 text-[#84EA53] shrink-0" />
            <span>1 điểm thưởng = 1 VNĐ giảm trực tiếp</span>
          </div>
          <div className="flex items-center gap-2.5 text-neutral-300">
            <Gift className="size-4 text-[#84EA53] shrink-0" />
            <span>Áp dụng đồng thời cùng Voucher</span>
          </div>
        </div>
      </div>

      {/* History table card */}
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-semibold text-base text-neutral-900">Lịch sử tích & tiêu điểm</h3>
            <p className="text-xs text-neutral-500">
              Chi tiết các giao dịch cộng điểm từ đơn hàng và trừ điểm khi mua sắm.
            </p>
          </div>
        </div>

        {isLoading ? (
          <div className="py-12 text-center text-xs text-neutral-400">Đang tải lịch sử điểm...</div>
        ) : transactions.length === 0 ? (
          <div className="py-12 text-center text-xs text-neutral-500">
            <Coins className="size-8 text-neutral-300 mx-auto mb-2" />
            Bạn chưa có giao dịch tích điểm nào. Hãy mua sắm để nhận điểm thưởng ngay!
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-[#EAE6DF] text-neutral-400 font-semibold uppercase text-[10px]">
                  <th className="pb-3 pl-2">Loại giao dịch</th>
                  <th className="pb-3">Mô tả</th>
                  <th className="pb-3 text-right">Biến động điểm</th>
                  <th className="pb-3 text-right">Số dư sau GD</th>
                  <th className="pb-3 text-right pr-2">Thời gian</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#EAE6DF]/60">
                {transactions.map((t) => {
                  const meta = transTypeMeta[t.transactionType] || {
                    label: t.transactionType,
                    isCredit: t.pointChange > 0,
                    variant: 'secondary' as const,
                  };
                  const isPositive = t.pointChange > 0;

                  return (
                    <tr key={t.id} className="hover:bg-neutral-50/60 transition">
                      <td className="py-3.5 pl-2 font-medium">
                        <Badge variant={meta.variant} className="text-[10px]">
                          {meta.label}
                        </Badge>
                      </td>
                      <td className="py-3.5 text-neutral-800">
                        <p className="font-medium">{t.description}</p>
                        {t.orderId && (
                          <Link
                            href={`/orders/${t.orderId}`}
                            className="text-[11px] text-neutral-500 font-mono hover:underline"
                          >
                            Đơn hàng #{t.orderId}
                          </Link>
                        )}
                      </td>
                      <td className="py-3.5 text-right font-mono font-bold">
                        <span
                          className={`inline-flex items-center gap-0.5 ${
                            isPositive ? 'text-emerald-600' : 'text-rose-600'
                          }`}
                        >
                          {isPositive ? (
                            <ArrowUpRight className="size-3.5" />
                          ) : (
                            <ArrowDownLeft className="size-3.5" />
                          )}
                          {isPositive ? `+${t.pointChange}` : t.pointChange}
                        </span>
                      </td>
                      <td className="py-3.5 text-right font-mono text-neutral-600">
                        {t.currentPoint?.toLocaleString('vi-VN') || '---'}
                      </td>
                      <td className="py-3.5 text-right pr-2 text-neutral-400">
                        {formatDate(t.createdAt)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {pagination.totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-[#EAE6DF] pt-4 text-xs text-neutral-500">
            <span>
              Trang {pagination.page} / {pagination.totalPages} (Tổng {pagination.total} giao dịch)
            </span>
            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                disabled={page <= 1}
                className="h-8 px-3 rounded-xl border-[#EAE6DF]"
              >
                Trước
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage((p) => Math.min(pagination.totalPages, p + 1))}
                disabled={page >= pagination.totalPages}
                className="h-8 px-3 rounded-xl border-[#EAE6DF]"
              >
                Sau
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

```

# (account)\profile\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\profile\page.tsx` |
| Size | 848 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.858479 |

```tsx
import { Metadata } from 'next';
import { ProfileForm } from '@/modules/user/components/profile-form';

export const metadata: Metadata = {
  title: 'Hồ sơ cá nhân — Fashion Store',
  description: 'Quản lý thông tin cá nhân và điểm thưởng tài khoản của bạn.',
};

export default function ProfilePage() {
  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="pb-6 border-b border-[#EAE6DF] mb-6">
        <h2 className="text-xl font-semibold text-neutral-900">
          Thông tin cá nhân
        </h2>
        <p className="text-xs text-neutral-500 mt-1">
          Cập nhật thông tin chi tiết để hỗ trợ giao hàng và liên hệ nhanh chóng.
        </p>
      </div>

      <ProfileForm />
    </div>
  );
}


```

# (account)\security\page.tsx

| Property | Value |
|----------|-------|
| Path | `(account)\security\page.tsx` |
| Size | 4,831 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.401932 |

```tsx
'use client';

import * as React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Eye, EyeOff, Loader2, ShieldCheck } from 'lucide-react';
import { changePasswordSchema, type ChangePasswordFormData } from '@/modules/auth/schemas';
import { useAuth } from '@/modules/auth/hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

export default function SecurityPage() {
  const [showOld, setShowOld] = React.useState(false);
  const [showNew, setShowNew] = React.useState(false);
  const [showConfirm, setShowConfirm] = React.useState(false);
  const { changePasswordMutation } = useAuth();

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ChangePasswordFormData>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: {
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
  });

  const onSubmit = (data: ChangePasswordFormData) => {
    changePasswordMutation.mutate(data, {
      onSuccess: () => {
        reset();
      },
    });
  };

  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="pb-6 border-b border-[#EAE6DF] mb-6 flex items-start justify-between">
        <div>
          <h2 className="text-xl font-semibold text-neutral-900">
            Bảo mật & Đổi mật khẩu
          </h2>
          <p className="text-xs text-neutral-500 mt-1">
            Định kỳ thay đổi mật khẩu để bảo vệ an toàn cho tài khoản và thông tin mua hàng.
          </p>
        </div>
        <div className="p-2.5 rounded-xl bg-neutral-100 text-neutral-800 hidden sm:block">
          <ShieldCheck className="size-5" />
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 max-w-lg">
        <div className="relative">
          <FormField
            control={control}
            name="oldPassword"
            label="Mật khẩu hiện tại"
            type={showOld ? 'text' : 'password'}
            placeholder="••••••••"
            error={errors.oldPassword?.message}
          />
          <button
            type="button"
            onClick={() => setShowOld(!showOld)}
            className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
            tabIndex={-1}
          >
            {showOld ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        </div>

        <div className="relative">
          <FormField
            control={control}
            name="newPassword"
            label="Mật khẩu mới"
            type={showNew ? 'text' : 'password'}
            placeholder="Tối thiểu 8 ký tự (hoa, thường, số, ký tự)"
            error={errors.newPassword?.message}
          />
          <button
            type="button"
            onClick={() => setShowNew(!showNew)}
            className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
            tabIndex={-1}
          >
            {showNew ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        </div>

        <div className="relative">
          <FormField
            control={control}
            name="confirmPassword"
            label="Xác nhận mật khẩu mới"
            type={showConfirm ? 'text' : 'password'}
            placeholder="Nhập lại mật khẩu mới"
            error={errors.confirmPassword?.message}
          />
          <button
            type="button"
            onClick={() => setShowConfirm(!showConfirm)}
            className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
            tabIndex={-1}
          >
            {showConfirm ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        </div>

        <div className="pt-4">
          <Button
            type="submit"
            disabled={changePasswordMutation.isPending}
            className="h-11 px-8 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99]"
          >
            {changePasswordMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-2" />
                Đang cập nhật mật khẩu...
              </>
            ) : (
              'Cập nhật mật khẩu'
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}


```

# (auth)\forgot-password\page.tsx

| Property | Value |
|----------|-------|
| Path | `(auth)\forgot-password\page.tsx` |
| Size | 1,567 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.862351 |

```tsx
import { Metadata } from 'next';
import Link from 'next/link';
import { ForgotPasswordForm } from '@/modules/auth/components/forgot-password-form';

export const metadata: Metadata = {
  title: 'Quên mật khẩu — Fashion Store',
  description: 'Khôi phục mật khẩu tài khoản của bạn thông qua mã xác thực gửi về email.',
};

export default function ForgotPasswordPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Quên mật khẩu
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Nhập email của bạn để nhận mã xác thực khôi phục mật khẩu.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <ForgotPasswordForm />
        </div>

        <div className="text-center mt-6">
          <Link
            href="/"
            className="text-xs text-neutral-500 hover:text-neutral-900 transition underline-offset-4 hover:underline"
          >
            ← Quay lại trang chủ
          </Link>
        </div>
      </div>
    </main>
  );
}


```

# (auth)\login\page.tsx

| Property | Value |
|----------|-------|
| Path | `(auth)\login\page.tsx` |
| Size | 1,528 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.865370 |

```tsx
import { Metadata } from 'next';
import Link from 'next/link';
import { LoginForm } from '@/modules/auth/components/login-form';

export const metadata: Metadata = {
  title: 'Đăng nhập — Fashion Store',
  description: 'Đăng nhập vào tài khoản của bạn để trải nghiệm mua sắm và quản lý đơn hàng.',
};

export default function LoginPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Đăng nhập
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Chào mừng bạn quay trở lại. Hãy điền thông tin để tiếp tục.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <LoginForm />
        </div>

        <div className="text-center mt-6">
          <Link
            href="/"
            className="text-xs text-neutral-500 hover:text-neutral-900 transition underline-offset-4 hover:underline"
          >
            ← Quay lại trang chủ
          </Link>
        </div>
      </div>
    </main>
  );
}


```

# (auth)\register\page.tsx

| Property | Value |
|----------|-------|
| Path | `(auth)\register\page.tsx` |
| Size | 1,710 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-12 22:18:00.158124 |

```tsx
import { Metadata } from 'next';
import { Suspense } from 'react';
import Link from 'next/link';
import { RegisterForm } from '@/modules/auth/components/register-form';

export const metadata: Metadata = {
  title: 'Đăng ký tài khoản — Fashion Store',
  description: 'Tạo tài khoản mới để nhận ưu đãi thành viên và theo dõi đơn hàng tiện lợi.',
};

export default function RegisterPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md my-8">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Đăng ký
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Đăng ký tài khoản để bắt đầu trải nghiệm mua sắm độc quyền.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <Suspense fallback={<div className="h-48 animate-pulse rounded-xl bg-neutral-50" />}>
            <RegisterForm />
          </Suspense>
        </div>

        <div className="text-center mt-6">
          <Link
            href="/"
            className="text-xs text-neutral-500 hover:text-neutral-900 transition underline-offset-4 hover:underline"
          >
            ← Quay lại trang chủ
          </Link>
        </div>
      </div>
    </main>
  );
}


```

# (auth)\reset-password\page.tsx

| Property | Value |
|----------|-------|
| Path | `(auth)\reset-password\page.tsx` |
| Size | 5,418 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.405961 |

```tsx
'use client';

import * as React from 'react';
import { Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { Eye, EyeOff, Loader2 } from 'lucide-react';
import { resetPasswordSchema, type ResetPasswordFormData } from '@/modules/auth/schemas';
import { useAuth } from '@/modules/auth/hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

function ResetPasswordFormContent() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token') || '';
  const [showPassword, setShowPassword] = React.useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = React.useState(false);
  const { resetPasswordMutation } = useAuth();

  const form = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: {
      token,
      newPassword: '',
      confirmPassword: '',
    },
  });

  React.useEffect(() => {
    if (token) {
      form.setValue('token', token);
    }
  }, [token, form]);

  const onSubmit = (data: ResetPasswordFormData) => {
    resetPasswordMutation.mutate(data);
  };

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
      <input type="hidden" {...form.register('token')} />

      {!token && (
        <div className="p-3 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-xs">
          Không tìm thấy mã xác thực khôi phục mật khẩu. Vui lòng bắt đầu lại từ bước Quên mật khẩu.
        </div>
      )}

      <div className="relative">
        <FormField
          control={form.control}
          name="newPassword"
          label="Mật khẩu mới"
          type={showPassword ? 'text' : 'password'}
          placeholder="Tối thiểu 8 ký tự (hoa, thường, số, ký tự)"
          error={form.formState.errors.newPassword?.message}
        />
        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
          tabIndex={-1}
        >
          {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
        </button>
      </div>

      <div className="relative">
        <FormField
          control={form.control}
          name="confirmPassword"
          label="Xác nhận mật khẩu mới"
          type={showConfirmPassword ? 'text' : 'password'}
          placeholder="Nhập lại mật khẩu mới"
          error={form.formState.errors.confirmPassword?.message}
        />
        <button
          type="button"
          onClick={() => setShowConfirmPassword(!showConfirmPassword)}
          className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
          tabIndex={-1}
        >
          {showConfirmPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
        </button>
      </div>

      <Button
        type="submit"
        disabled={resetPasswordMutation.isPending || !token}
        className="w-full h-11 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99] mt-2"
      >
        {resetPasswordMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin mr-2" />
            Đang cập nhật mật khẩu...
          </>
        ) : (
          'Lưu mật khẩu mới'
        )}
      </Button>

      <div className="text-center pt-2">
        <Link
          href="/login"
          className="text-xs text-neutral-500 hover:text-neutral-900 transition underline underline-offset-4"
        >
          ← Quay về đăng nhập
        </Link>
      </div>
    </form>
  );
}

export default function ResetPasswordPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Đặt lại mật khẩu
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Nhập mật khẩu mới an toàn cho tài khoản của bạn.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <Suspense fallback={<div className="text-center py-6 text-sm text-neutral-500">Đang tải...</div>}>
            <ResetPasswordFormContent />
          </Suspense>
        </div>

        <div className="text-center mt-6">
          <Link
            href="/"
            className="text-xs text-neutral-500 hover:text-neutral-900 transition underline-offset-4 hover:underline"
          >
            ← Quay lại trang chủ
          </Link>
        </div>
      </div>
    </main>
  );
}


```

# (shop)\cart\page.tsx

| Property | Value |
|----------|-------|
| Path | `(shop)\cart\page.tsx` |
| Size | 352 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-06 00:40:44.072759 |

```tsx
import { Metadata } from 'next';
import { CartView } from '@/modules/cart/components/cart-view';

export const metadata: Metadata = {
  title: 'Giỏ hàng của bạn — Fashion Store',
  description: 'Xem lại các sản phẩm đã chọn và tiến hành đặt hàng.',
};

export default function CartPage() {
  return <CartView />;
}
```

# (shop)\checkout\page.tsx

| Property | Value |
|----------|-------|
| Path | `(shop)\checkout\page.tsx` |
| Size | 566 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-09 10:38:13.108385 |

```tsx
import { Suspense } from 'react';
import { Metadata } from 'next';
import { CheckoutView } from '@/modules/checkout/components/checkout-view';

export const metadata: Metadata = {
  title: 'Thanh toán đơn hàng — Fashion Store',
  description: 'Xác nhận thông tin giao nhận và thanh toán đơn hàng an toàn.',
};

export default function CheckoutPage() {
  return (
    <Suspense fallback={<div className="py-20 text-center text-sm text-neutral-500">Đang tải trang thanh toán...</div>}>
      <CheckoutView />
    </Suspense>
  );
}
```

# (shop)\flash-sale\page.tsx

| Property | Value |
|----------|-------|
| Path | `(shop)\flash-sale\page.tsx` |
| Size | 10,762 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.409001 |

```tsx
'use client';

import { useEffect, useState } from 'react';
import { Flame, ShieldCheck, Zap, RefreshCw, ShoppingBag, Clock, Search } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { FlashSaleCountdown } from '@/modules/flash-sale/components/countdown';
import { FlashSaleCard } from '@/modules/flash-sale/components/flash-sale-card';
import { Button } from '@/components/ui/button';
import { useFlashSaleStore } from '@/modules/flash-sale/store';
import { FlashSaleProvider, useFlashSaleContext } from '@/modules/flash-sale/context';

// Sub-component sử dụng Context
function FlashSaleItemList({ imageByVariantId }: { imageByVariantId: Record<number, string> }) {
  const { filteredItems } = useFlashSaleContext();

  if (filteredItems.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center rounded-3xl border border-dashed border-zinc-300 bg-white p-12 text-center dark:border-zinc-800 dark:bg-zinc-900">
        <div className="flex size-16 items-center justify-center rounded-full bg-red-100 text-red-600 dark:bg-red-950 dark:text-red-400">
          <Flame className="size-8" />
        </div>
        <h3 className="mt-4 text-lg font-bold text-zinc-900 dark:text-zinc-100">
          Không tìm thấy sản phẩm Flash Sale nào
        </h3>
        <p className="mt-1 text-sm text-zinc-500 max-w-sm">
          Thử tìm kiếm với từ khóa khác hoặc quay lại sau!
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
      {filteredItems.map((item) => (
        <FlashSaleCard
          key={item.id || item.variantId}
          item={item}
          imageUrl={imageByVariantId[item.variantId]}
        />
      ))}
    </div>
  );
}

// Sub-component sử dụng Context cho Toolbar Tìm kiếm
function FlashSaleToolbar({ onRefresh }: { onRefresh: () => void }) {
  const { searchQuery, setSearchQuery, filteredItems } = useFlashSaleContext();

  return (
    <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between rounded-2xl bg-white p-4 shadow-xs dark:bg-zinc-900 border border-zinc-200/80 dark:border-zinc-800">
      <div className="flex items-center gap-2">
        <ShoppingBag className="size-5 text-red-600" />
        <h2 className="text-base font-bold text-zinc-900 dark:text-zinc-100">
          Sản phẩm Flash Sale ({filteredItems.length})
        </h2>
      </div>

      <div className="flex items-center gap-3">
        <div className="relative w-full sm:w-64">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-zinc-400" />
          <input
            type="text"
            placeholder="Tìm kiếm sản phẩm trong sale..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 rounded-xl border border-zinc-300 bg-zinc-50 px-3.5 py-1.5 text-sm focus:border-red-500 focus:outline-none dark:border-zinc-700 dark:bg-zinc-800"
          />
        </div>
        <Button onClick={onRefresh} variant="outline" size="sm" className="gap-1.5">
          <RefreshCw className="size-3.5" />
          Làm mới
        </Button>
      </div>
    </div>
  );
}

export default function FlashSaleShopPage() {
  // 1. GLOAL STATE (Zustand Store)
  const {
    activeCampaigns,
    selectedCampaignId,
    loading,
    fetchActiveCampaigns,
    setSelectedCampaignId,
  } = useFlashSaleStore();

  // 2. LOCAL STATE (useState) cho UI trợ lý đếm ngược / tab local
  const [refreshing, setRefreshing] = useState(false);
  const [imageByVariantId, setImageByVariantId] = useState<Record<number, string>>({});

  useEffect(() => {
    fetchActiveCampaigns();
  }, [fetchActiveCampaigns]);

  useEffect(() => {
    if (activeCampaigns.length === 0) return;

    let cancelled = false;

    async function fetchProductImages() {
      try {
        const firstPage = await catalogApi.getProducts({
          page: 1,
          limit: 100,
          status: 'PUBLISHED',
        });
        const remainingPages = await Promise.all(
          Array.from({ length: Math.max(firstPage.totalPages - 1, 0) }, (_, index) =>
            catalogApi.getProducts({
              page: index + 2,
              limit: 100,
              status: 'PUBLISHED',
            })
          )
        );
        const products = [firstPage, ...remainingPages].flatMap((page) => page.data);
        const imageMap: Record<number, string> = {};

        for (const product of products) {
          const imageUrl = product.images?.[0]?.imageUrl || product.thumbnailUrl;
          if (!imageUrl) continue;

          for (const variant of product.variants || []) {
            imageMap[variant.id] = imageUrl;
          }
        }

        if (!cancelled) setImageByVariantId(imageMap);
      } catch (error) {
        console.error('Failed to fetch Flash Sale product images:', error);
      }
    }

    void fetchProductImages();

    return () => {
      cancelled = true;
    };
  }, [activeCampaigns]);

  const activeCampaign =
    activeCampaigns.find((campaign) => campaign.id === selectedCampaignId) ??
    activeCampaigns[0];

  const handleRefresh = async () => {
    setRefreshing(true);
    await fetchActiveCampaigns();
    setRefreshing(false);
  };

  return (
    <main className="min-h-screen bg-zinc-50 dark:bg-zinc-950 pb-20">
      {/* Hero Banner Section */}
      <section className="relative overflow-hidden bg-zinc-950 !text-white [&_*]:!text-white py-12 px-4 sm:px-8 lg:px-12">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_30%_30%,rgba(239,68,68,0.25),transparent_60%)]" />
        <div className="absolute right-0 top-0 h-full w-1/2 bg-[radial-gradient(circle_at_80%_20%,rgba(245,158,11,0.2),transparent_50%)]" />

        <div className="relative z-10 mx-auto max-w-[1440px]">
          <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
            <div className="max-w-2xl">
              <div className="inline-flex items-center gap-2 rounded-full border border-red-500/30 bg-red-500/10 px-3.5 py-1 text-xs font-bold text-red-400 backdrop-blur-md">
                <Flame className="size-4 fill-red-500 text-red-500 animate-pulse" />
                <span>CHƯƠNG TRÌNH KHUYẾN MÃI GIỜ VÀNG</span>
              </div>
              <h1 className="mt-4 font-display text-4xl font-black tracking-tight sm:text-6xl !text-white [&_*]:!text-white">
                FLASH SALE SĂN DEAL KHỦNG
              </h1>
              <p className="mt-3 text-sm sm:text-base text-zinc-300">
                Sở hữu ngay những sản phẩm thời trang cao cấp với mức giá giảm tới 70%. Số lượng có hạn theo từng khung giờ vàng!
              </p>

              {/* Guarantees */}
              <div className="mt-6 flex flex-wrap gap-4 text-xs font-semibold text-zinc-300">
                <div className="flex items-center gap-1.5 rounded-lg bg-zinc-900/80 px-3 py-1.5 border border-zinc-800">
                  <ShieldCheck className="size-4 text-emerald-400" />
                  <span>Sản phẩm chính hãng 100%</span>
                </div>
                <div className="flex items-center gap-1.5 rounded-lg bg-zinc-900/80 px-3 py-1.5 border border-zinc-800">
                  <Zap className="size-4 text-amber-400" />
                  <span>Giao hàng thần tốc</span>
                </div>
                <div className="flex items-center gap-1.5 rounded-lg bg-zinc-900/80 px-3 py-1.5 border border-zinc-800">
                  <Clock className="size-4 text-red-400" />
                  <span>Cập nhật theo khung giờ</span>
                </div>
              </div>
            </div>

            {/* Countdown Box */}
            {activeCampaign && (
              <div className="flex flex-col items-start md:items-end rounded-2xl border border-red-500/20 bg-zinc-900/90 p-6 backdrop-blur-md shadow-2xl">
                <span className="text-xs font-bold text-zinc-400 uppercase tracking-widest">
                  Thời gian diễn ra
                </span>
                <h3 className="mt-1 text-lg font-bold !text-white [&_*]:!text-white mb-3">
                  {activeCampaign.name}
                </h3>
                <FlashSaleCountdown
                  targetTime={activeCampaign.endTime}
                  startTime={activeCampaign.startTime}
                  size="lg"
                />
              </div>
            )}
          </div>
        </div>
      </section>

      {/* Main Container */}
      <div className="mx-auto max-w-[1440px] px-4 pt-8 sm:px-8 lg:px-12">
        {/* Campaign Tabs */}
        {activeCampaigns.length > 1 && (
          <div className="mb-8 flex gap-3 overflow-x-auto pb-2 scrollbar-none">
            {activeCampaigns.map((c) => {
              const isSelected = c.id === activeCampaign?.id;
              return (
                <button
                  key={c.id}
                  onClick={() => setSelectedCampaignId(c.id)}
                  className={`flex shrink-0 items-center gap-2 rounded-2xl px-6 py-3.5 text-sm font-bold transition-all shadow-xs ${isSelected
                      ? 'bg-red-600 !text-white [&_*]:!text-white shadow-lg shadow-red-600/30 scale-105'
                      : 'bg-white text-zinc-700 hover:bg-zinc-100 dark:bg-zinc-900 dark:text-zinc-300 dark:hover:bg-zinc-800'
                    }`}
                >
                  <Flame className={`size-4 ${isSelected ? 'fill-amber-300 text-amber-300' : 'text-zinc-400'}`} />
                  <span>{c.name}</span>
                </button>
              );
            })}
          </div>
        )}

        {/* 3. CONTEXT PROVIDER bao bọc danh sách sản phẩm & toolbar */}
        <FlashSaleProvider campaign={activeCampaign || null}>
          <FlashSaleToolbar onRefresh={handleRefresh} />

          {loading || refreshing ? (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
              {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="h-80 animate-pulse rounded-2xl bg-zinc-200 dark:bg-zinc-800" />
              ))}
            </div>
          ) : (
            <FlashSaleItemList imageByVariantId={imageByVariantId} />
          )}
        </FlashSaleProvider>
      </div>
    </main>
  );
}

```

# (shop)\layout.tsx

| Property | Value |
|----------|-------|
| Path | `(shop)\layout.tsx` |
| Size | 431 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-06 00:08:13.656305 |

```tsx
import { ReactNode } from 'react';
import { SiteHeader } from '@/components/layouts/site-header';
import { SiteFooter } from '@/components/layouts/site-footer';

export default function ShopLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col bg-[#FBF9F4]">
      <SiteHeader />
      <div className="flex-1">{children}</div>
      <SiteFooter />
    </div>
  );
}

```

# (shop)\order-success\[orderCode]\page.tsx

| Property | Value |
|----------|-------|
| Path | `(shop)\order-success\[orderCode]\page.tsx` |
| Size | 2,126 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.412002 |

```tsx
import Link from 'next/link';
import { CheckCircle2, ArrowRight, ShoppingBag } from 'lucide-react';

interface PageProps {
  params: Promise<{ orderCode: string }>;
}

export default async function OrderSuccessPage({ params }: PageProps) {
  // Await params theo chuẩn Next.js 15
  const { orderCode } = await params;

  return (
    <main className="min-h-[75vh] flex items-center justify-center p-4 bg-[#FBF9F4]">
      <div className="max-w-md w-full bg-white rounded-3xl border border-[#EAE6DF] p-8 sm:p-10 text-center shadow-xs">
        <div className="mx-auto size-16 bg-emerald-50 text-emerald-500 rounded-full flex items-center justify-center mb-6 ring-8 ring-emerald-50/50">
          <CheckCircle2 size={32} />
        </div>
        
        <h1 className="text-2xl font-bold text-neutral-900 mb-2">Đặt hàng thành công!</h1>
        <p className="text-sm text-neutral-500 mb-6">
          Cảm ơn bạn đã mua sắm. Đơn hàng của bạn đang được xử lý. Mã đơn hàng là:
        </p>
        
        <div className="inline-block bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl px-6 py-3 mb-8">
          <span className="font-mono font-bold text-xl tracking-wider text-neutral-900">
            {orderCode || '---'}
          </span>
        </div>
        
        <div className="flex flex-col gap-3">
          <Link 
            href="/orders" 
            className="w-full flex items-center justify-center gap-2 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white px-4 py-3.5 text-sm font-semibold hover:bg-neutral-800 transition active:scale-[0.99]"
          >
            <ShoppingBag size={16} /> Theo dõi đơn hàng
          </Link>
          <Link 
            href="/products" 
            className="w-full flex items-center justify-center gap-2 rounded-xl border border-[#EAE6DF] text-neutral-700 px-4 py-3.5 text-sm font-semibold hover:bg-neutral-50 transition active:scale-[0.99]"
          >
            Tiếp tục mua sắm <ArrowRight size={16} />
          </Link>
        </div>
      </div>
    </main>
  );
}
```

# (shop)\products\[productId]\page.tsx

| Property | Value |
|----------|-------|
| Path | `(shop)\products\[productId]\page.tsx` |
| Size | 10,425 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 19:50:22.992833 |

```tsx
'use client';

import * as React from 'react';
// 1. Thêm import useSearchParams
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import Image from 'next/image';
import Link from 'next/link';
import { ChevronLeft, Plus, Minus, ShoppingBag, Loader2, Flame } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { useAddToCart } from '@/modules/cart/hooks/use-cart';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';
import { ProductReviewList } from '@/modules/loyalty-engage';

export default function ProductDetailPage() {
  const params = useParams();
  const searchParams = useSearchParams();
  const router = useRouter();
  
  const productId = Number(params.productId);
  // 2. Lấy variantId từ URL truyền qua
  const urlVariantId = Number(searchParams.get('variantId'));

  const [selectedVariantIndex, setSelectedVariantIndex] = React.useState(0);
  const [quantity, setQuantity] = React.useState(1);

  const { data: res, isLoading } = useQuery({
    queryKey: ['product-detail', productId],
    queryFn: () => catalogApi.getProductById(productId),
    enabled: !!productId,
  });

  const addToCartMutation = useAddToCart();
  const product = res?.data;
  const variants = product?.variants || [];

  // 3. Tự động chọn đúng Variant nếu có truyền từ trang Flash Sale
  React.useEffect(() => {
    if (variants.length > 0 && urlVariantId) {
      const index = variants.findIndex((v: any) => v.id === urlVariantId);
      if (index !== -1) {
        setSelectedVariantIndex(index);
      }
    }
  }, [variants, urlVariantId]);

  const selectedVariant = variants[selectedVariantIndex] || variants[0];

  // Logic Flash Sale
  const activeFlashSale = React.useMemo(() => {
    if (!selectedVariant?.flashSaleItems?.length) return null;
    const now = new Date().getTime();
    return selectedVariant.flashSaleItems.find((fsi: any) => {
      if (!fsi.campaign || !fsi.campaign.isActive) return false;
      const start = new Date(fsi.campaign.startTime).getTime();
      const end = new Date(fsi.campaign.endTime).getTime();
      const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
      return now >= start && now <= end && remainingQty > 0;
    });
  }, [selectedVariant]);

  const currentPrice = activeFlashSale 
    ? Number(activeFlashSale.flashSalePrice)
    : Number(selectedVariant?.sellingPrice || selectedVariant?.originalPrice || 0);

  const displayOriginalPrice = activeFlashSale
    ? Number(selectedVariant?.sellingPrice || selectedVariant?.originalPrice || 0)
    : Number(selectedVariant?.originalPrice || 0);

  const handleAddToCart = () => {
    if (!selectedVariant) return;
    addToCartMutation.mutate({
      variantId: selectedVariant.id,
      quantity,
    });
  };

  const handleBuyNow = () => {
    if (!selectedVariant) return;
    router.push(`/checkout?directBuy=true&productId=${product?.id}&variantId=${selectedVariant.id}&quantity=${quantity}`);
  };

  if (isLoading) {
    return (
      <div className="mx-auto max-w-[1280px] px-4 py-16 text-center text-sm text-neutral-500">
        <Loader2 className="mx-auto size-6 animate-spin mb-2" />
        Đang tải thông tin sản phẩm...
      </div>
    );
  }

  if (!product) {
    return (
      <div className="mx-auto max-w-[1280px] px-4 py-16 text-center">
        <p className="text-sm text-neutral-500 mb-4">Không tìm thấy sản phẩm này.</p>
        <Button onClick={() => router.push('/products')} variant="outline">
          Quay lại danh sách
        </Button>
      </div>
    );
  }

  return (
    <main className="mx-auto max-w-[1280px] px-4 py-6 sm:px-8 lg:py-12">
      <Link
        href="/products"
        className="mb-6 inline-flex items-center gap-1 text-xs text-[#74736e] hover:text-neutral-900 transition"
      >
        <ChevronLeft size={14} /> Quay lại danh mục
      </Link>

      <div className="grid gap-10 lg:grid-cols-2 lg:gap-16">
        <div className="relative aspect-square overflow-hidden rounded-2xl bg-white border border-[#EAE6DF]">
          <Image
            src={selectedVariant?.imageUrl || product.thumbnailUrl || '/placeholder.png'}
            alt={product.defaultName}
            fill
            priority
            className="object-cover"
          />
        </div>

        <div className="flex flex-col justify-center rounded-2xl bg-white p-6 sm:p-10 border border-[#EAE6DF] shadow-xs relative overflow-hidden">
          {activeFlashSale && (
            <div className="absolute top-0 right-0 bg-red-600 text-white px-4 py-1.5 rounded-bl-xl font-black text-xs uppercase tracking-wider flex items-center gap-1.5 shadow-md">
              <Flame className="size-3.5 fill-amber-300 text-amber-300 animate-pulse" />
              Đang Flash Sale
            </div>
          )}

          <div className="mb-4 mt-2">
            <span className="eyebrow block mb-2">{product.brand?.defaultName || 'FASHION STORE'}</span>
            <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20]">{product.defaultName}</h1>
          </div>

          <div className="flex items-baseline gap-3 pb-6 border-b border-[#EAE6DF]">
            <span className={activeFlashSale ? "text-3xl font-black text-red-600" : "text-2xl font-bold text-neutral-900"}>
              {formatCurrency(currentPrice)}
            </span>
            {displayOriginalPrice > currentPrice && (
              <span className="text-sm text-neutral-400 line-through">{formatCurrency(displayOriginalPrice)}</span>
            )}
            <Badge variant="lime" className="ml-auto text-[10px]">Chính hãng 100%</Badge>
          </div>

          <p className="py-6 text-xs text-neutral-600 leading-relaxed">
            {(product as any)?.translations?.[0]?.shortDesc ||
              'Sản phẩm được gia công từ chất liệu cao cấp mang lại cảm giác thoáng mát và tôn dáng.'}
          </p>

          {/* 4. LUÔN HIỂN THỊ CHỌN PHÂN LOẠI: Xóa điều kiện variants.length > 1 đi */}
          {variants.length > 0 && (
            <div className="space-y-3 mb-6">
              <span className="text-xs font-semibold text-neutral-800">
                Phân loại sản phẩm (Chọn để xem giá ưu đãi)
              </span>
              <div className="flex flex-wrap gap-2">
                {variants.map((v: any, idx: number) => {
                  const isSelected = selectedVariantIndex === idx;
                  const outOfStock = v.stockQuantity <= 0;
                  
                  // Kiểm tra xem phân loại này có đang được sale không để tô màu nổi bật
                  const hasSale = v.flashSaleItems?.some((fsi: any) => fsi.campaign?.isActive);
                  
                  return (
                    <button
                      key={v.id}
                      onClick={() => setSelectedVariantIndex(idx)}
                      disabled={outOfStock}
                      className={`rounded-xl border px-3.5 py-2 text-xs font-medium transition flex items-center gap-1.5 ${
                        isSelected
                          ? 'border-neutral-900 bg-neutral-900 !text-white [&_*]:!text-white shadow-sm'
                          : outOfStock 
                          ? 'border-[#EAE6DF] bg-neutral-50 text-neutral-400 cursor-not-allowed opacity-60'
                          : hasSale
                          ? 'border-red-200 bg-red-50 text-red-700 hover:border-red-400'
                          : 'border-[#EAE6DF] bg-white text-neutral-700 hover:border-neutral-400'
                      }`}
                    >
                      {v.variantValue || v.sku} 
                      {hasSale && !isSelected && <Flame className="size-3" />}
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          <div className="flex items-center gap-4 pt-4 border-t border-[#EAE6DF]">
            <div className="flex items-center rounded-xl border border-[#EAE6DF] bg-white">
              <button
                type="button"
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                className="p-3 text-neutral-600 hover:text-neutral-900"
              >
                <Minus size={14} />
              </button>
              <span className="w-8 text-center text-xs font-semibold">{quantity}</span>
              <button
                type="button"
                onClick={() => setQuantity(quantity + 1)}
                className="p-3 text-neutral-600 hover:text-neutral-900"
              >
                <Plus size={14} />
              </button>
            </div>

            <Button
              onClick={handleAddToCart}
              variant="outline"
              disabled={addToCartMutation.isPending || !selectedVariant || selectedVariant.stockQuantity <= 0}
              className="flex-1 h-12 rounded-xl border-[#EAE6DF] hover:bg-neutral-50 font-medium gap-2 text-neutral-800"
            >
              {addToCartMutation.isPending ? (
                <>
                  <Loader2 className="size-4 animate-spin" /> Đang thêm...
                </>
              ) : (
                <>
                  <ShoppingBag size={16} /> Thêm vào giỏ
                </>
              )}
            </Button>
            <Button
              onClick={handleBuyNow}
              disabled={!selectedVariant || selectedVariant.stockQuantity <= 0}
              className={`flex-1 h-12 rounded-xl !text-white [&_*]:!text-white font-bold gap-2 ${
                activeFlashSale ? 'bg-red-600 hover:bg-red-700' : 'bg-[#1D1F20] hover:bg-neutral-800'
              }`}
            >
              Mua ngay ({formatCurrency(currentPrice * quantity)})
            </Button>
          </div>
        </div>
      </div>

      <div className="mt-12 sm:mt-16">
        <ProductReviewList productId={product.id} />
      </div>
    </main>
  );
}
```

# (shop)\products\page.tsx

| Property | Value |
|----------|-------|
| Path | `(shop)\products\page.tsx` |
| Size | 4,160 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 19:21:22.527193 |

```tsx
'use client';

import * as React from 'react';
import { Suspense } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'next/navigation';
import { catalogApi } from '@/api/catalog.api';
import { ProductCard } from '@/modules/catalog/components/product-card';
import { SearchInput } from '@/components/ui/search-input';
import { Skeleton } from '@/components/ui/skeleton';
import { ShoppingBag } from 'lucide-react';
import type { Product } from '@/modules/catalog/mock';

function ProductsContent() {
  const searchParams = useSearchParams();
  const initialSearch = searchParams.get('search') || '';
  const [search, setSearch] = React.useState(initialSearch);

  const { data: response, isLoading } = useQuery({
    queryKey: ['catalog-products-list', search],
    queryFn: () => catalogApi.getProducts({ search: search || undefined, limit: 20 }),
  });

  const apiProducts = (response?.data as any) || [];

  // Map dữ liệu phẳng sang Model hiển thị của ProductCard
  const productList: Product[] = apiProducts.map((p: any) => ({
    slug: String(p.id),
    name: p.defaultName,
    brand: p.brand?.defaultName || 'Fashion Studio',
    category: p.category?.defaultName || 'Thời trang',
    price: Number(p.variants?.[0]?.sellingPrice || p.variants?.[0]?.originalPrice || 0),
    originalPrice: Number(p.variants?.[0]?.originalPrice || 0),
    rating: 5.0, // Chỗ này tương lai có thể call API Review để lấy
    reviews: 12,
    images: p.images?.length > 0
      ? p.images.map((img: any) => img.imageUrl)
      : [p.thumbnailUrl || 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80'],
    colors: ['#1D1F20', '#ded5c8'],
    sizes: ['S', 'M', 'L'],
    description: p.defaultName || 'Sản phẩm thời trang cao cấp.',
  }));

  return (
    <div className="mx-auto max-w-[1440px] px-4 py-8 sm:px-8 lg:px-12">
      <div className="mb-8 flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#EAE6DF] pb-6">
        <div>
          <span className="eyebrow block mb-1">BỘ SƯU TẬP</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            {initialSearch ? `Kết quả cho "${initialSearch}"` : 'Tất cả sản phẩm'}
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Khám phá những thiết kế thời trang tối giản và hiện đại.
          </p>
        </div>

        <div className="w-full sm:w-80">
          <SearchInput
            value={search}
            onChange={setSearch}
            placeholder="Tìm kiếm sản phẩm..."
          />
        </div>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-2 gap-x-4 gap-y-8 sm:grid-cols-3 lg:grid-cols-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <div key={i} className="space-y-3">
              <Skeleton className="aspect-[.88] w-full rounded-xl" />
              <Skeleton className="h-4 w-3/4 rounded" />
              <Skeleton className="h-4 w-1/2 rounded" />
            </div>
          ))}
        </div>
      ) : productList.length === 0 ? (
        <div className="py-20 text-center">
          <ShoppingBag className="mx-auto size-12 text-neutral-300 mb-3" />
          <h3 className="text-sm font-semibold text-neutral-800">Không tìm thấy sản phẩm</h3>
          <p className="text-xs text-neutral-500 mt-1">Vui lòng thử tìm kiếm với từ khóa khác.</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-x-4 gap-y-8 sm:grid-cols-3 lg:grid-cols-4">
          {productList.map((product) => (
            <ProductCard key={product.slug} product={product} />
          ))}
        </div>
      )}
    </div>
  );
}

export default function ProductsCatalogPage() {
  return (
    <Suspense fallback={<div className="p-20 text-center text-sm text-neutral-500">Đang tải sản phẩm...</div>}>
      <ProductsContent />
    </Suspense>
  );
}
```

# admin\brands\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\brands\page.tsx` |
| Size | 12,744 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.951858 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { Plus, Edit3, Trash2, Sparkles, Image as ImageIcon } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import type { Brand } from '@/types';

export default function AdminBrandsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingBrand, setEditingBrand] = React.useState<Brand | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Brand | null>(null);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      defaultName: '',
      logo: '',
    },
  });

  const { data, isLoading } = useQuery({
    queryKey: ['admin-brands', page, search],
    queryFn: () => catalogApi.getBrands({ page, limit: 10, search: search || undefined }),
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: any) => {
      return catalogApi.createBrand({
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || undefined,
      });
    },
    onSuccess: () => {
      toast.success('Thêm thương hiệu mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-brands'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thêm thương hiệu thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, values }: { id: number; values: any }) => {
      return catalogApi.updateBrand(id, {
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || undefined,
      });
    },
    onSuccess: () => {
      toast.success('Cập nhật thương hiệu thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-brands'] });
      setEditingBrand(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật thương hiệu thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      return catalogApi.deleteBrand(id);
    },
    onSuccess: () => {
      toast.success('Đã xóa thương hiệu!');
      queryClient.invalidateQueries({ queryKey: ['admin-brands'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa thương hiệu thất bại');
    },
  });

  const handleOpenEdit = (brand: Brand) => {
    setEditingBrand(brand);
    setValue('defaultName', brand.defaultName);
    setValue('logo', (brand as any).logo || brand.logoUrl || '');
  };

  const columns: Column<Brand>[] = [
    {
      header: 'ID',
      className: 'w-16 text-xs text-neutral-400 font-mono',
      cell: (brand) => `#${brand.id}`,
    },
    {
      header: 'Thương hiệu',
      cell: (brand) => {
        const logoUrl = (brand as any).logo || brand.logoUrl;
        return (
          <div className="flex items-center gap-3">
            {logoUrl ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img
                src={logoUrl}
                alt={brand.defaultName}
                className="size-10 rounded-lg object-contain border border-[#EAE6DF] bg-white p-1"
              />
            ) : (
              <div className="size-10 rounded-lg bg-neutral-100 flex items-center justify-center text-neutral-400">
                <ImageIcon className="size-4" />
              </div>
            )}
            <div>
              <span className="font-semibold text-sm text-neutral-900">{brand.defaultName}</span>
              {logoUrl && (
                <span className="block text-[10px] text-neutral-400 truncate max-w-xs">
                  {logoUrl}
                </span>
              )}
            </div>
          </div>
        );
      },
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (brand) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(brand)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa thương hiệu"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(brand)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa thương hiệu"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">CATALOG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Thương hiệu</h1>
          <p className="text-xs text-neutral-500 mt-1">Quản lý danh sách các thương hiệu sản phẩm đang kinh doanh.</p>
        </div>
        <Button
          onClick={() => {
            reset({ defaultName: '', logo: '' });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" /> Thêm Thương hiệu
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex justify-between items-center">
        <SearchInput 
          value={search} 
          onChange={(val) => { setSearch(val); setPage(1); }} 
          placeholder="Tìm kiếm thương hiệu..." 
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={(data?.data as any) || []}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy thương hiệu nào."
      />

      {/* Modal Thêm Thương hiệu */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Plus className="size-5 text-neutral-700" />
              Thêm thương hiệu mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Tên thương hiệu *
              </label>
              <input
                type="text"
                {...register('defaultName', { required: true })}
                placeholder="VD: Nike, Adidas, Zara, Uniqlo..."
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Logo URL (Tùy chọn)
              </label>
              <input
                type="text"
                {...register('logo')}
                placeholder="https://example.com/logo.png"
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
              />
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsCreateOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {createMutation.isPending ? 'Đang thêm...' : 'Thêm thương hiệu'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Thương hiệu */}
      <Dialog open={!!editingBrand} onOpenChange={(open) => !open && setEditingBrand(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa thương hiệu #{editingBrand?.id}
            </DialogTitle>
          </DialogHeader>

          {editingBrand && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({ id: editingBrand.id, values })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tên thương hiệu *
                </label>
                <input
                  type="text"
                  {...register('defaultName', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Logo URL (Tùy chọn)
                </label>
                <input
                  type="text"
                  {...register('logo')}
                  placeholder="https://example.com/logo.png"
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingBrand(null)}
                  className="rounded-xl border-[#EAE6DF]"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={updateMutation.isPending}
                  className="rounded-xl bg-[#1D1F20] text-white"
                >
                  {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </Button>
              </DialogFooter>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title={`Xóa thương hiệu "${deleteTarget?.defaultName}"?`}
        description="Bạn có chắc chắn muốn xóa thương hiệu này? Các sản phẩm thuộc thương hiệu có thể bị ảnh hưởng."
        confirmText="Xóa thương hiệu"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
      />
    </div>
  );
}
```

# admin\categories\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\categories\page.tsx` |
| Size | 15,298 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.953859 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { Plus, Edit3, Trash2, Layers, CheckCircle2, EyeOff, FolderTree } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import type { Category } from '@/types';

export default function AdminCategoriesPage() {
  const queryClient = useQueryClient();
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingCategory, setEditingCategory] = React.useState<Category | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Category | null>(null);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      defaultName: '',
      logo: '',
      parentCategoryId: '',
    },
  });

  const { data, isLoading } = useQuery({
    queryKey: ['admin-categories'],
    queryFn: () => catalogApi.getCategories(),
  });

  const allCategories: Category[] = data?.data ?? [];

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: any) => {
      return catalogApi.createCategory({
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || null,
        parentCategoryId: values.parentCategoryId ? Number(values.parentCategoryId) : null,
      });
    },
    onSuccess: () => {
      toast.success('Thêm danh mục mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-categories'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thêm danh mục thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, values }: { id: number; values: any }) => {
      return catalogApi.updateCategory(id, {
        defaultName: values.defaultName.trim(),
        logo: values.logo?.trim() || null,
        parentCategoryId: values.parentCategoryId ? Number(values.parentCategoryId) : null,
      });
    },
    onSuccess: () => {
      toast.success('Cập nhật danh mục thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-categories'] });
      setEditingCategory(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật danh mục thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      return catalogApi.deleteCategory(id);
    },
    onSuccess: () => {
      toast.success('Đã xóa danh mục!');
      queryClient.invalidateQueries({ queryKey: ['admin-categories'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa danh mục thất bại');
    },
  });

  const handleOpenEdit = (cat: Category) => {
    setEditingCategory(cat);
    setValue('defaultName', cat.defaultName);
    setValue('logo', (cat as any).logo || cat.iconUrl || '');
    setValue('parentCategoryId', (cat.parentId || (cat as any).parentCategoryId || '').toString());
  };

  const filteredCategories = React.useMemo(() => {
    if (!search.trim()) return allCategories;
    const q = search.toLowerCase();
    return allCategories.filter((c) => c.defaultName.toLowerCase().includes(q));
  }, [allCategories, search]);

  const columns: Column<Category>[] = [
    {
      header: 'ID',
      className: 'w-16 text-xs text-neutral-400 font-mono',
      cell: (cat) => `#${cat.id}`,
    },
    {
      header: 'Tên danh mục',
      cell: (cat) => {
        const iconUrl = (cat as any).logo || cat.iconUrl;
        const isImageUrl = !!iconUrl && /^https?:\/\//i.test(iconUrl);

        return (
          <div className="flex items-center gap-3">
            <div className="size-8 shrink-0 overflow-hidden rounded-lg bg-neutral-100 flex items-center justify-center text-neutral-400">
              {isImageUrl && iconUrl ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={iconUrl} alt="" loading="lazy" className="size-full object-cover" />
              ) : iconUrl ? (
                iconUrl
              ) : (
                <Layers className="size-4" />
              )}
            </div>
            <div>
              <span className="font-semibold text-sm text-neutral-900">{cat.defaultName}</span>
              {(cat as any).logo && (
                <span className="block text-[10px] text-neutral-400 truncate max-w-xs">
                  {(cat as any).logo}
                </span>
              )}
            </div>
          </div>
        );
      },
    },
    {
      header: 'Danh mục cha',
      cell: (cat) => {
        const parentId = cat.parentId || (cat as any).parentCategoryId;
        const parent = allCategories.find((c) => c.id === parentId);
        return (
          <span className="text-xs text-neutral-600">
            {parent ? (
              <span className="font-medium text-neutral-800">{parent.defaultName}</span>
            ) : parentId ? (
              `ID: #${parentId}`
            ) : (
              <span className="text-neutral-400 italic">Gốc (Root)</span>
            )}
          </span>
        );
      },
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (cat) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(cat)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa danh mục"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(cat)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa danh mục"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">CATALOG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Danh mục</h1>
          <p className="text-xs text-neutral-500 mt-1">Cấu trúc phân loại sản phẩm hiển thị trên Website.</p>
        </div>
        <Button
          onClick={() => {
            reset({ defaultName: '', logo: '', parentCategoryId: '' });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" /> Thêm Danh mục
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <SearchInput
          value={search}
          onChange={setSearch}
          placeholder="Tìm kiếm danh mục theo tên..."
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={filteredCategories}
        isLoading={isLoading}
        emptyMessage="Không có danh mục nào."
      />

      {/* Modal Thêm Danh mục */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Plus className="size-5 text-neutral-700" />
              Thêm danh mục mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Tên danh mục *
              </label>
              <input
                type="text"
                {...register('defaultName', { required: true })}
                placeholder="VD: Áo sơ mi nam, Quần jeans..."
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Logo / Icon URL (Tùy chọn)
              </label>
              <input
                type="text"
                {...register('logo')}
                placeholder="https://example.com/category-icon.png"
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
              />
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Danh mục cha (Parent Category)
              </label>
              <select
                {...register('parentCategoryId')}
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
              >
                <option value="">Không có (Danh mục gốc - Root)</option>
                {allCategories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.defaultName} (ID: #{c.id})
                  </option>
                ))}
              </select>
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsCreateOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {createMutation.isPending ? 'Đang thêm...' : 'Thêm danh mục'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Danh mục */}
      <Dialog open={!!editingCategory} onOpenChange={(open) => !open && setEditingCategory(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa danh mục #{editingCategory?.id}
            </DialogTitle>
          </DialogHeader>

          {editingCategory && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({ id: editingCategory.id, values })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tên danh mục *
                </label>
                <input
                  type="text"
                  {...register('defaultName', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Logo / Icon URL (Tùy chọn)
                </label>
                <input
                  type="text"
                  {...register('logo')}
                  placeholder="https://example.com/icon.png"
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Danh mục cha (Parent Category)
                </label>
                <select
                  {...register('parentCategoryId')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                >
                  <option value="">Không có (Danh mục gốc - Root)</option>
                  {allCategories
                    .filter((c) => c.id !== editingCategory.id)
                    .map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.defaultName} (ID: #{c.id})
                      </option>
                    ))}
                </select>
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingCategory(null)}
                  className="rounded-xl border-[#EAE6DF]"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={updateMutation.isPending}
                  className="rounded-xl bg-[#1D1F20] text-white"
                >
                  {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </Button>
              </DialogFooter>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title={`Xóa danh mục "${deleteTarget?.defaultName}"?`}
        description="Bạn có chắc chắn muốn xóa danh mục này? Các sản phẩm thuộc danh mục có thể bị ảnh hưởng."
        confirmText="Xóa danh mục"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
      />
    </div>
  );
}
```

# admin\chat\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\chat\page.tsx` |
| Size | 24,258 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.955859 |

```tsx
'use client';

import { FormEvent, useCallback, useEffect, useMemo, useState } from 'react';
import {
  Circle,
  Search,
  Send,
  User,
  X,
  Laptop,
  Smartphone,
  ExternalLink,
  ShieldCheck,
  Clock,
} from 'lucide-react';
import { useAuth } from '@/components/providers/auth-provider';
import { useSocket } from '@/components/providers/socket-provider';
import { chatApi } from '@/modules/loyalty-engage/api';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import type { ChatMessage, ChatSession } from '@/types/loyalty-engage';

function appendMessage(messages: ChatMessage[], message: ChatMessage) {
  if (messages.some((existing) => existing.id === message.id)) return messages;
  return [...messages, message].sort(
    (left, right) => new Date(left.sendDate).getTime() - new Date(right.sendDate).getTime()
  );
}

function formatTime(value?: string | Date) {
  if (!value) return '';
  return new Date(value).toLocaleString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    day: '2-digit',
    month: '2-digit',
  });
}

export default function AdminChatPage() {
  const { user } = useAuth();
  const { socket, isConnected } = useSocket();
  const [conversations, setConversations] = useState<ChatSession[]>([]);
  const [selectedChatId, setSelectedChatId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [search, setSearch] = useState('');
  const [input, setInput] = useState('');
  const [loadingConversations, setLoadingConversations] = useState(true);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Online statuses tracking for customers
  const [onlineUsersMap, setOnlineUsersMap] = useState<Record<number, boolean>>({});
  const [selectedUserSessions, setSelectedUserSessions] = useState<
    Array<{ socketId: string; userId: number; connectedAt: string }>
  >([]);
  const [isSessionsDialogOpen, setIsSessionsDialogOpen] = useState(false);

  const selectedConversation =
    conversations.find((conversation) => conversation.id === selectedChatId) ?? null;

  const filteredConversations = useMemo(() => {
    const query = search.trim().toLocaleLowerCase();
    if (!query) return conversations;
    return conversations.filter((conversation) =>
      conversation.user?.fullName.toLocaleLowerCase().includes(query)
    );
  }, [conversations, search]);

  // Load online status for user IDs
  const refreshOnlineStatuses = useCallback(async (userIds: number[]) => {
    if (!userIds.length) return;
    try {
      const results = await Promise.all(
        userIds.map(async (uid) => {
          try {
            const res = await chatApi.getUserOnlineStatus(uid);
            return { userId: uid, isOnline: !!res?.data?.isOnline };
          } catch {
            return { userId: uid, isOnline: false };
          }
        })
      );
      setOnlineUsersMap((prev) => {
        const next = { ...prev };
        results.forEach((r) => {
          next[r.userId] = r.isOnline;
        });
        return next;
      });
    } catch {
      // ignore
    }
  }, []);

  const loadConversations = useCallback(async () => {
    setLoadingConversations(true);
    try {
      const response = await chatApi.getConversations();
      setConversations(response.data);
      setSelectedChatId((current) => {
        if (current && response.data.some((conversation) => conversation.id === current))
          return current;
        return response.data[0]?.id ?? null;
      });
      setError(null);

      // Check online statuses for all conversation participants
      const userIds = Array.from(new Set(response.data.map((c) => c.userId)));
      void refreshOnlineStatuses(userIds);
    } catch (cause) {
      console.error('Không thể tải danh sách hội thoại:', cause);
      setError('Không thể tải danh sách hội thoại.');
    } finally {
      setLoadingConversations(false);
    }
  }, [refreshOnlineStatuses]);

  useEffect(() => {
    void Promise.resolve().then(loadConversations);
  }, [loadConversations]);

  // Periodic polling of online status every 15 seconds
  useEffect(() => {
    if (!conversations.length) return;
    const interval = setInterval(() => {
      const userIds = Array.from(new Set(conversations.map((c) => c.userId)));
      void refreshOnlineStatuses(userIds);
    }, 15000);
    return () => clearInterval(interval);
  }, [conversations, refreshOnlineStatuses]);

  // When selected conversation changes, fetch its sessions
  useEffect(() => {
    if (!selectedConversation?.userId) {
      setSelectedUserSessions([]);
      return;
    }

    const userId = selectedConversation.userId;
    chatApi
      .getUserSessions(userId)
      .then((res) => {
        const sessions = res?.data || [];
        setSelectedUserSessions(sessions);
        setOnlineUsersMap((prev) => ({ ...prev, [userId]: sessions.length > 0 }));
      })
      .catch(() => {
        setSelectedUserSessions([]);
      });
  }, [selectedConversation?.userId]);

  useEffect(() => {
    if (!socket) return;

    const joinShop = () => socket.emit('join_shop');
    const handleNewMessage = (message: ChatMessage) => {
      const conversation = conversations.find((item) => item.id === message.chatId);
      if (!conversation) {
        void loadConversations();
        return;
      }
      setConversations((current) => [
        { ...conversation, messages: [message] },
        ...current.filter((item) => item.id !== message.chatId),
      ]);

      if (message.chatId === selectedChatId) {
        setMessages((current) => appendMessage(current, message));
        if (message.senderId !== user?.id) {
          void chatApi.markAsRead(message.chatId).catch((cause: unknown) => {
            console.error('Không thể đánh dấu tin nhắn đã đọc:', cause);
          });
        }
      }
    };

    socket.on('connect', joinShop);
    socket.on('new_message', handleNewMessage);
    if (socket.connected) joinShop();

    return () => {
      socket.off('connect', joinShop);
      socket.off('new_message', handleNewMessage);
    };
  }, [socket, selectedChatId, user?.id, loadConversations, conversations]);

  useEffect(() => {
    if (selectedChatId === null) {
      return;
    }

    let cancelled = false;
    void Promise.resolve()
      .then(() => {
        if (!cancelled) setLoadingMessages(true);
        return chatApi.getHistory(selectedChatId);
      })
      .then(async (response) => {
        if (!cancelled) {
          setMessages(response.data);
          await chatApi.markAsRead(selectedChatId);
        }
      })
      .catch((cause: unknown) => {
        console.error('Không thể tải lịch sử hội thoại:', cause);
        if (!cancelled) setError('Không thể tải tin nhắn cuộc trò chuyện này.');
      })
      .finally(() => {
        if (!cancelled) setLoadingMessages(false);
      });

    return () => {
      cancelled = true;
    };
  }, [selectedChatId]);

  const handleSend = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const content = input.trim();
    if (
      !content ||
      selectedChatId === null ||
      sending ||
      selectedConversation?.status === 'CLOSED'
    )
      return;

    setSending(true);
    try {
      const response = await chatApi.sendMessage(selectedChatId, content);
      setMessages((current) => appendMessage(current, response.data));
      setConversations((current) =>
        current.map((conversation) =>
          conversation.id === selectedChatId
            ? { ...conversation, messages: [response.data] }
            : conversation
        )
      );
      setInput('');
      setError(null);
    } catch (cause) {
      console.error('Không thể gửi phản hồi:', cause);
      setError('Gửi tin nhắn thất bại. Vui lòng thử lại.');
    } finally {
      setSending(false);
    }
  };

  const handleClose = async () => {
    if (selectedChatId === null) return;
    try {
      await chatApi.closeConversation(selectedChatId);
      await loadConversations();
      setMessages([]);
    } catch (cause) {
      console.error('Không thể đóng cuộc trò chuyện:', cause);
      setError('Không thể đóng cuộc trò chuyện.');
    }
  };

  const isSelectedUserOnline = selectedConversation
    ? !!onlineUsersMap[selectedConversation.userId] || selectedUserSessions.length > 0
    : false;

  return (
    <div className="flex h-[calc(100vh-8rem)] flex-col">
      <div className="mb-4 shrink-0">
        <span className="eyebrow mb-1 block">CSKH & TRỰC TUYẾN</span>
        <h1 className="font-display text-3xl tracking-tight text-[#1D1F20] sm:text-4xl">
          Hỗ trợ Khách hàng Trực tuyến
        </h1>
      </div>

      {error && (
        <p role="alert" className="mb-3 rounded-xl bg-red-50 px-4 py-2 text-sm text-red-700">
          {error}
        </p>
      )}

      <div className="flex min-h-0 flex-1 overflow-hidden rounded-2xl border border-[#EAE6DF] bg-white shadow-xs">
        {/* Conversation List Sidebar */}
        <aside className="flex w-72 shrink-0 flex-col border-r border-[#EAE6DF] bg-neutral-50/30 sm:w-80">
          <div className="border-b border-[#EAE6DF] p-4">
            <label className="relative block">
              <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-neutral-400" />
              <input
                type="search"
                name="conversationSearch"
                aria-label="Tìm kiếm hội thoại"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Tìm kiếm hội thoại..."
                className="h-10 w-full rounded-xl border border-[#EAE6DF] bg-white pl-9 pr-3 text-sm outline-none focus:border-neutral-900"
              />
            </label>
          </div>

          <div className="flex-1 overflow-y-auto">
            {loadingConversations ? (
              <p className="p-5 text-center text-sm text-neutral-500">Đang tải hội thoại...</p>
            ) : filteredConversations.length === 0 ? (
              <p className="p-5 text-center text-sm text-neutral-500">Chưa có hội thoại nào.</p>
            ) : (
              filteredConversations.map((conversation) => {
                const lastMessage = conversation.messages?.[0];
                const unread =
                  !!lastMessage && !lastMessage.isRead && lastMessage.senderId !== user?.id;
                const isOnline = !!onlineUsersMap[conversation.userId];

                return (
                  <button
                    type="button"
                    key={conversation.id}
                    onClick={() => setSelectedChatId(conversation.id)}
                    className={`flex w-full items-start gap-3 border-b border-[#EAE6DF]/60 border-l-4 p-4 text-left transition hover:bg-white ${
                      selectedChatId === conversation.id
                        ? 'border-l-[#1D1F20] bg-white'
                        : 'border-l-transparent'
                    }`}
                  >
                    <div className="relative shrink-0">
                      <span className="flex size-10 items-center justify-center rounded-full bg-neutral-200 text-neutral-600">
                        <User size={20} />
                      </span>
                      {/* Online dot indicator */}
                      <span
                        className={`absolute bottom-0 right-0 size-3 rounded-full ring-2 ring-white ${
                          isOnline ? 'bg-emerald-500 animate-pulse' : 'bg-neutral-300'
                        }`}
                        title={isOnline ? 'Đang online' : 'Ngoại tuyến'}
                      />
                    </div>

                    <span className="min-w-0 flex-1">
                      <span className="mb-1 flex items-center justify-between gap-2">
                        <span className="truncate text-sm font-semibold text-neutral-900">
                          {conversation.user?.fullName || `Khách hàng #${conversation.userId}`}
                        </span>
                        <span className="shrink-0 text-[10px] text-neutral-400">
                          {formatTime(lastMessage?.sendDate || conversation.updatedAt)}
                        </span>
                      </span>

                      <span className="flex items-center gap-1.5 mb-0.5">
                        <span
                          className={`size-1.5 rounded-full ${
                            isOnline ? 'bg-emerald-500' : 'bg-neutral-300'
                          }`}
                        />
                        <span
                          className={`text-[10px] font-medium ${
                            isOnline ? 'text-emerald-600' : 'text-neutral-400'
                          }`}
                        >
                          {isOnline ? 'Trực tuyến' : 'Ngoại tuyến'}
                        </span>
                      </span>

                      <span className="block truncate text-xs text-neutral-500">
                        {lastMessage?.content || 'Chưa có tin nhắn'}
                      </span>
                    </span>

                    {unread && <span className="mt-1 size-2 shrink-0 rounded-full bg-rose-500" />}
                  </button>
                );
              })
            )}
          </div>
        </aside>

        {/* Conversation Chat Detail */}
        <section className="flex min-w-0 flex-1 flex-col bg-[#FBF9F4]/50">
          {selectedConversation ? (
            <>
              {/* Header with real Customer Online Status & Device sessions */}
              <header className="flex h-16 shrink-0 items-center justify-between gap-3 border-b border-[#EAE6DF] bg-white px-4 sm:px-6">
                <div className="flex min-w-0 items-center gap-3">
                  <div className="relative shrink-0">
                    <span className="flex size-10 items-center justify-center rounded-full bg-neutral-200 text-neutral-600">
                      <User size={20} />
                    </span>
                    <span
                      className={`absolute bottom-0 right-0 size-3 rounded-full ring-2 ring-white ${
                        isSelectedUserOnline ? 'bg-emerald-500 animate-pulse' : 'bg-neutral-300'
                      }`}
                    />
                  </div>

                  <div className="min-w-0">
                    <div className="flex items-center gap-2">
                      <h2 className="truncate text-sm font-semibold text-neutral-900">
                        {selectedConversation.user?.fullName ||
                          `Khách hàng #${selectedConversation.userId}`}
                      </h2>

                      {isSelectedUserOnline ? (
                        <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-50 text-emerald-700 border border-emerald-200">
                          <span className="size-1.5 rounded-full bg-emerald-500 animate-pulse" />
                          Trực tuyến
                          {selectedUserSessions.length > 0 &&
                            ` (${selectedUserSessions.length} tab/thiết bị)`}
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-neutral-100 text-neutral-500 border border-neutral-200">
                          <span className="size-1.5 rounded-full bg-neutral-400" />
                          Ngoại tuyến
                        </span>
                      )}
                    </div>

                    <div className="flex items-center gap-3 text-[11px] text-neutral-500">
                      <span>Mã KH: #{selectedConversation.userId}</span>
                      {selectedUserSessions.length > 0 && (
                        <button
                          type="button"
                          onClick={() => setIsSessionsDialogOpen(true)}
                          className="text-[#1D1F20] font-medium underline underline-offset-2 hover:text-emerald-700 flex items-center gap-1"
                        >
                          <Laptop className="size-3" /> Xem chi tiết thiết bị & tab
                        </button>
                      )}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {selectedConversation.status !== 'CLOSED' && (
                    <button
                      type="button"
                      onClick={handleClose}
                      aria-label="Đóng cuộc trò chuyện"
                      title="Đóng cuộc trò chuyện"
                      className="rounded-lg p-2 text-neutral-400 transition hover:bg-red-50 hover:text-red-600"
                    >
                      <X size={18} />
                    </button>
                  )}
                </div>
              </header>

              {/* Messages Area */}
              <div className="flex-1 space-y-4 overflow-y-auto p-4 sm:p-6">
                {loadingMessages ? (
                  <p className="pt-10 text-center text-sm text-neutral-500">Đang tải tin nhắn...</p>
                ) : messages.length === 0 ? (
                  <p className="pt-10 text-center text-sm text-neutral-500">Chưa có tin nhắn.</p>
                ) : (
                  messages.map((message) => {
                    const isMe = message.senderId === user?.id;
                    return (
                      <div
                        key={message.id}
                        className={`flex ${isMe ? 'justify-end' : 'justify-start'}`}
                      >
                        <div
                          className={`max-w-[80%] rounded-2xl p-3 text-sm shadow-xs ${
                            isMe
                              ? 'rounded-br-sm bg-[#1D1F20] text-white'
                              : 'rounded-bl-sm border border-[#EAE6DF] bg-white text-neutral-800'
                          }`}
                        >
                          <p className="whitespace-pre-wrap break-words">{message.content}</p>
                          <time
                            className={`mt-1 block text-[10px] ${
                              isMe ? 'text-white/60' : 'text-neutral-400'
                            }`}
                          >
                            {formatTime(message.sendDate)}
                          </time>
                        </div>
                      </div>
                    );
                  })
                )}
              </div>

              {/* Input Form */}
              <form
                onSubmit={handleSend}
                className="flex shrink-0 items-center gap-2 border-t border-[#EAE6DF] bg-white p-4"
              >
                <input
                  type="text"
                  name="message"
                  aria-label="Nội dung phản hồi"
                  value={input}
                  onChange={(event) => setInput(event.target.value)}
                  placeholder={
                    selectedConversation.status === 'CLOSED'
                      ? 'Cuộc trò chuyện đã kết thúc'
                      : 'Nhập phản hồi của bạn...'
                  }
                  maxLength={5000}
                  disabled={sending || selectedConversation.status === 'CLOSED'}
                  className="h-11 min-w-0 flex-1 rounded-xl border border-[#EAE6DF] bg-[#FBF9F4] px-4 text-sm outline-none transition focus:border-neutral-900 disabled:opacity-60"
                />
                <button
                  type="submit"
                  aria-label="Gửi phản hồi"
                  disabled={!input.trim() || sending || selectedConversation.status === 'CLOSED'}
                  className="flex h-11 shrink-0 items-center gap-2 rounded-xl bg-[#1D1F20] px-4 font-medium text-white transition hover:bg-neutral-800 disabled:cursor-not-allowed disabled:opacity-40"
                >
                  Gửi <Send size={16} />
                </button>
              </form>
            </>
          ) : (
            <div className="flex flex-1 items-center justify-center p-6 text-center text-sm text-neutral-500">
              Chọn một cuộc trò chuyện để xem tin nhắn.
            </div>
          )}
        </section>
      </div>

      {/* Modal chi tiết Thiết bị & Socket Sessions của khách hàng */}
      <Dialog open={isSessionsDialogOpen} onOpenChange={setIsSessionsDialogOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Laptop className="size-5 text-neutral-700" />
              Thiết bị & Phiên truy cập của khách hàng
            </DialogTitle>
          </DialogHeader>

          <div className="space-y-3 my-2 text-xs">
            <p className="text-neutral-500">
              Danh sách các kết nối WebSocket (Tabs / Thiết bị) đang duy trì trực tuyến theo thời gian thực:
            </p>

            {selectedUserSessions.length === 0 ? (
              <p className="p-4 text-center text-neutral-400 bg-neutral-50 rounded-xl">
                Không tìm thấy phiên kết nối nào đang hoạt động.
              </p>
            ) : (
              <div className="space-y-2 max-h-60 overflow-y-auto">
                {selectedUserSessions.map((s, idx) => (
                  <div
                    key={s.socketId || idx}
                    className="p-3 bg-neutral-50 rounded-xl border border-neutral-100 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-2.5">
                      <span className="size-2 rounded-full bg-emerald-500 animate-pulse shrink-0" />
                      <div>
                        <p className="font-mono font-semibold text-neutral-900 truncate max-w-[200px]">
                          Socket: {s.socketId}
                        </p>
                        <p className="text-[10px] text-neutral-400 flex items-center gap-1 mt-0.5">
                          <Clock className="size-3" /> Kết nối: {formatTime(s.connectedAt)}
                        </p>
                      </div>
                    </div>
                    <Badge variant="lime" className="text-[9px]">
                      Hoạt động
                    </Badge>
                  </div>
                ))}
              </div>
            )}
          </div>

          <DialogFooter className="mt-4 pt-2 border-t border-[#EAE6DF]">
            <Button
              type="button"
              variant="outline"
              onClick={() => setIsSessionsDialogOpen(false)}
              className="rounded-xl border-[#EAE6DF]"
            >
              Đóng
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

```

# admin\dashboard\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\dashboard\page.tsx` |
| Size | 13,004 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.451004 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import {
  Users,
  ShoppingBag,
  TrendingUp,
  PackageCheck,
  ArrowUpRight,
  Shield,
  Layers,
  Clock,
  PackageX,
} from 'lucide-react';
import { adminApi } from '@/modules/admin/api';
import { orderApi } from '@/api/order.api';
import { DashboardCard } from '@/modules/admin/components/dashboard-card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { formatCurrency } from '@/lib/utils/format-currency';
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';
import type { Order } from '@/types';

function formatRelativeTime(dateInput?: string | Date): string {
  if (!dateInput) return 'Gần đây';
  const diffSec = Math.floor((Date.now() - new Date(dateInput).getTime()) / 1000);
  if (diffSec < 60) return 'Vừa xong';
  if (diffSec < 3600) return `${Math.floor(diffSec / 60)} phút trước`;
  if (diffSec < 86400) return `${Math.floor(diffSec / 3600)} giờ trước`;
  return `${Math.floor(diffSec / 86400)} ngày trước`;
}

export default function AdminDashboardPage() {
  // 1. Lấy dữ liệu KPI thống kê từ server
  const { data: statsData, isLoading: isLoadingStats } = useQuery({
    queryKey: ['admin-dashboard-stats'],
    queryFn: () => adminApi.getDashboardStats(),
  });

  // 2. Lấy danh sách 5 đơn hàng mới nhất từ server
  const { data: ordersData, isLoading: isLoadingOrders } = useQuery({
    queryKey: ['admin-recent-orders'],
    queryFn: () => orderApi.getOrders({ limit: 5, all: true }),
  });

  const stats = statsData?.data;

  // Trích xuất danh sách đơn hàng thực tế
  const rawOrders = ordersData?.data as unknown;
  const recentOrders: Order[] = Array.isArray(rawOrders)
    ? (rawOrders as Order[])
    : Array.isArray((rawOrders as { data?: Order[] })?.data)
    ? ((rawOrders as { data: Order[] }).data)
    : [];

  const getOrderStatusBadge = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return <Badge variant="success">Hoàn thành</Badge>;
      case 'CONFIRMED':
        return <Badge variant="lime">Đã xác nhận</Badge>;
      case 'PROCESSING':
        return <Badge variant="lime">Đang xử lý</Badge>;
      case 'SHIPPED':
        return <Badge variant="secondary">Đang giao</Badge>;
      case 'PENDING_PAYMENT':
        return <Badge variant="warning">Chờ thanh toán</Badge>;
      case 'CANCELLED':
        return <Badge variant="destructive">Đã hủy</Badge>;
      case 'RETURNED':
        return <Badge variant="outline">Trả hàng</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  return (
    <div className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">TỔNG QUAN HỆ THỐNG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Dashboard
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Theo dõi các chỉ số tăng trưởng, đơn hàng và hoạt động người dùng trong thời gian thực.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Link href="/admin/users">
            <Button variant="outline" className="rounded-xl border-[#EAE6DF] text-xs h-10">
              <Users className="size-4 mr-2" /> Quản lý Users
            </Button>
          </Link>
          <Link href="/admin/roles">
            <Button className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white text-xs h-10">
              <Shield className="size-4 mr-2" /> Phân quyền Roles
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <DashboardCard
          title="Tổng doanh thu"
          value={isLoadingStats ? '...' : formatCurrency(stats?.totalRevenue || 0)}
          icon={TrendingUp}
          change="+18.4%"
          trend="up"
          description="so với tháng trước"
        />
        <DashboardCard
          title="Tổng đơn hàng"
          value={isLoadingStats ? '...' : (stats?.totalOrders || 0).toLocaleString('vi-VN')}
          icon={PackageCheck}
          change={`${stats?.ordersPending || 0} đang chờ`}
          trend="neutral"
          description="tất cả giao dịch"
        />
        <DashboardCard
          title="Người dùng đăng ký"
          value={isLoadingStats ? '...' : (stats?.totalUsers || 0).toLocaleString('vi-VN')}
          icon={Users}
          change={`+${stats?.newUsersThisMonth || 0} mới`}
          trend="up"
          description="trong tháng hiện tại"
        />
        <DashboardCard
          title="Sản phẩm đang bán"
          value={isLoadingStats ? '...' : (stats?.totalProducts || 0).toLocaleString('vi-VN')}
          icon={ShoppingBag}
          change="Đang hoạt động"
          trend="up"
          description="danh mục toàn sàn"
        />
      </div>

      {/* Quick shortcuts & Status view */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs lg:col-span-1 space-y-4">
          <div className="flex items-center justify-between pb-4 border-b border-[#EAE6DF]">
            <h3 className="text-sm font-semibold text-neutral-900">
              Phím tắt nhanh
            </h3>
            <span className="text-[10px] text-neutral-400">Điều hướng</span>
          </div>

          <div className="space-y-2">
            <Link
              href="/admin/users"
              className="flex items-center justify-between p-3 rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition group"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-neutral-100 text-neutral-700">
                  <Users className="size-4" />
                </div>
                <div>
                  <p className="text-xs font-semibold text-neutral-800">Danh sách người dùng</p>
                  <p className="text-[10px] text-neutral-400">Xem, khóa hoặc cập nhật tài khoản</p>
                </div>
              </div>
              <ArrowUpRight className="size-4 text-neutral-400 group-hover:text-neutral-900 transition" />
            </Link>

            <Link
              href="/admin/roles"
              className="flex items-center justify-between p-3 rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition group"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-neutral-100 text-neutral-700">
                  <Shield className="size-4" />
                </div>
                <div>
                  <p className="text-xs font-semibold text-neutral-800">Quản lý vai trò & quyền</p>
                  <p className="text-[10px] text-neutral-400">Thiết lập RBAC Admin, Staff, Customer</p>
                </div>
              </div>
              <ArrowUpRight className="size-4 text-neutral-400 group-hover:text-neutral-900 transition" />
            </Link>

            <Link
              href="/admin/products"
              className="flex items-center justify-between p-3 rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition group"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-neutral-100 text-neutral-700">
                  <Layers className="size-4" />
                </div>
                <div>
                  <p className="text-xs font-semibold text-neutral-800">Quản lý sản phẩm</p>
                  <p className="text-[10px] text-neutral-400">Kho hàng, thuộc tính và biến thể</p>
                </div>
              </div>
              <ArrowUpRight className="size-4 text-neutral-400 group-hover:text-neutral-900 transition" />
            </Link>
          </div>
        </div>

        {/* Recent orders table */}
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between pb-4 border-b border-[#EAE6DF]">
            <div>
              <h3 className="text-sm font-semibold text-neutral-900">
                Đơn hàng gần đây
              </h3>
              <p className="text-xs text-neutral-500 mt-0.5">
                Các giao dịch thực tế vừa phát sinh trên hệ thống
              </p>
            </div>
            <Link href="/admin/orders">
              <Button variant="ghost" size="sm" className="text-xs text-neutral-600 hover:text-neutral-900">
                Xem tất cả đơn →
              </Button>
            </Link>
          </div>

          <div className="overflow-hidden rounded-xl border border-[#EAE6DF]">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã đơn</TableHead>
                  <TableHead>Khách hàng</TableHead>
                  <TableHead>Tổng tiền</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead className="text-right">Thời gian</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoadingOrders ? (
                  Array.from({ length: 5 }).map((_, i) => (
                    <TableRow key={i}>
                      <TableCell><Skeleton className="h-4 w-20" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-32" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-24" /></TableCell>
                      <TableCell><Skeleton className="h-5 w-20 rounded-full" /></TableCell>
                      <TableCell className="text-right"><Skeleton className="h-4 w-16 ml-auto" /></TableCell>
                    </TableRow>
                  ))
                ) : recentOrders.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} className="py-10 text-center">
                      <PackageX className="mx-auto size-8 text-neutral-300 mb-2" />
                      <p className="text-xs text-neutral-500 font-medium">
                        Chưa có đơn hàng nào phát sinh trên hệ thống.
                      </p>
                    </TableCell>
                  </TableRow>
                ) : (
                  recentOrders.map((ord) => {
                    const itemsSummary =
                      ord.orderItems && ord.orderItems.length > 0
                        ? ord.orderItems.map((item) => `${item.productName} x${item.quantity}`).join(', ')
                        : '1 sản phẩm';

                    return (
                      <TableRow key={ord.id}>
                        <TableCell className="font-semibold text-neutral-900 text-xs">
                          {ord.orderCode || `ORD-${ord.id}`}
                        </TableCell>
                        <TableCell className="text-xs">
                          <p className="font-medium text-neutral-800">{ord.receiverName || 'Khách hàng'}</p>
                          <p className="text-[11px] text-neutral-400 truncate max-w-[200px]" title={itemsSummary}>
                            {itemsSummary}
                          </p>
                        </TableCell>
                        <TableCell className="font-medium text-xs text-neutral-900">
                          {formatCurrency(ord.totalAmount)}
                        </TableCell>
                        <TableCell>{getOrderStatusBadge(ord.status)}</TableCell>
                        <TableCell className="text-right text-xs text-neutral-400">
                          <span className="inline-flex items-center gap-1">
                            <Clock className="size-3" />
                            {formatRelativeTime(ord.createdAt || ord.orderDate)}
                          </span>
                        </TableCell>
                      </TableRow>
                    );
                  })
                )}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
    </div>
  );
}

```

# admin\flash-sales\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\flash-sales\page.tsx` |
| Size | 43,684 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 13:50:05.788043 |

```tsx
'use client';

import * as React from 'react';
import { useEffect, useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Flame,
  Plus,
  Pencil,
  Trash2,
  Calendar,
  Layers,
  CheckCircle2,
  Clock,
  Search,
  RefreshCcw,
  AlertCircle,
  Eye,
  X,
  Sparkles,
  Loader2,
  Package,
} from 'lucide-react';
import {
  FlashSaleCampaign,
  FlashSaleItemPayload,
  CreateFlashSaleInput,
  UpdateFlashSaleInput,
} from '@/types/inventory-sale';
import { flashSaleApi } from '@/api/flash-sale.api';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';

export default function AdminFlashSalesPage() {
  const [campaigns, setCampaigns] = useState<FlashSaleCampaign[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalItems, setTotalItems] = useState(0);
  const [page, setPage] = useState(1);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState<'all' | 'active' | 'inactive'>('all');

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingCampaign, setEditingCampaign] = useState<FlashSaleCampaign | null>(null);
  const [detailCampaign, setDetailCampaign] = useState<FlashSaleCampaign | null>(null);
  const [deleteConfirmId, setDeleteConfirmId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Form states
  const [formData, setFormData] = useState<{
    name: string;
    bannerUrl: string;
    startTime: string;
    endTime: string;
    isActive: boolean;
    items: FlashSaleItemPayload[];
  }>({
    name: '',
    bannerUrl: '',
    startTime: '',
    endTime: '',
    isActive: true,
    items: [{ variantId: 0, flashSalePrice: 100000, promoQuantity: 10 }],
  });

  // 1. Tải danh sách biến thể sản phẩm từ Catalog để đưa vào dropdown lựa chọn
  const { data: prodRes, isLoading: isLoadingProds } = useQuery({
    queryKey: ['admin-products-variants-select'],
    queryFn: () => catalogApi.getProducts({ limit: 100 }),
  });

  const products = (prodRes?.data as any) || [];

  // Tạo danh sách phẳng biến thể với đầy đủ thông tin: Tên, Phân loại, SKU, Giá, Tồn kho
  const availableVariants = useMemo(() => {
    const list: Array<{
      id: number;
      productName: string;
      variantValue: string;
      sku: string;
      stockQuantity: number;
      sellingPrice: number;
      originalPrice: number;
      label: string;
    }> = [];

    products.forEach((p: any) => {
      p.variants?.forEach((v: any) => {
        const sellPrice = Number(v.sellingPrice || v.originalPrice || 0);
        list.push({
          id: v.id,
          productName: p.defaultName,
          variantValue: v.variantValue || 'Mặc định',
          sku: v.sku,
          stockQuantity: v.stockQuantity,
          sellingPrice: sellPrice,
          originalPrice: Number(v.originalPrice || 0),
          label: `${p.defaultName} — [${v.variantValue || v.sku}] (Tồn: ${v.stockQuantity} | Giá gốc: ${formatCurrency(sellPrice)})`,
        });
      });
    });
    return list;
  }, [products]);

  // Map nhanh ID biến thể sang Object để tiện hiển thị
  const variantMap = useMemo(() => {
    const map = new Map<number, typeof availableVariants[0]>();
    availableVariants.forEach((v) => map.set(v.id, v));
    return map;
  }, [availableVariants]);

  // 2. Tải danh sách chiến dịch Flash Sale
  const fetchCampaigns = async () => {
    setLoading(true);
    try {
      const res = await flashSaleApi.getCampaigns({
        page,
        limit: 10,
        isActive: activeFilter === 'all' ? undefined : activeFilter === 'active',
      });
      setCampaigns(res.data || []);
      setTotalItems(res.totalItems || 0);
    } catch (err: any) {
      console.error('Lỗi khi tải danh sách Flash Sale:', err);
      setErrorMessage(err?.response?.data?.message || 'Không thể kết nối đến máy chủ');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCampaigns();
  }, [page, activeFilter]);

  // Chuyển đổi định dạng ngày giờ sang YYYY-MM-DDTHH:mm không bị lệch múi giờ
  const toLocalInputString = (d: Date | string) => {
    try {
      const date = typeof d === 'string' ? new Date(d) : d;
      if (isNaN(date.getTime())) return '';
      const pad = (n: number) => n.toString().padStart(2, '0');
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    } catch {
      return '';
    }
  };

  // Mở modal tạo mới
  const handleOpenCreate = () => {
    setEditingCampaign(null);
    const now = new Date();
    const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);

    const firstVariant = availableVariants[0];
    const initialPrice = firstVariant ? Math.round(firstVariant.sellingPrice * 0.8) : 100000;
    const initialQty = firstVariant ? Math.min(10, firstVariant.stockQuantity || 10) : 10;

    setFormData({
      name: `Chiến Dịch Flash Sale Hot ${campaigns.length + 1}`,
      bannerUrl: '',
      startTime: toLocalInputString(now),
      endTime: toLocalInputString(tomorrow),
      isActive: true,
      items: [{
        variantId: firstVariant ? firstVariant.id : 0,
        flashSalePrice: initialPrice,
        promoQuantity: initialQty,
      }],
    });
    setErrorMessage(null);
    setIsFormOpen(true);
  };

  // Mở modal chỉnh sửa
  const handleOpenEdit = (campaign: FlashSaleCampaign) => {
    setEditingCampaign(campaign);

    setFormData({
      name: campaign.name,
      bannerUrl: campaign.bannerUrl || '',
      startTime: toLocalInputString(campaign.startTime),
      endTime: toLocalInputString(campaign.endTime),
      isActive: campaign.isActive,
      items:
        campaign.items && campaign.items.length > 0
          ? campaign.items.map((i) => ({
              variantId: i.variantId,
              flashSalePrice: Number(i.flashSalePrice),
              promoQuantity: i.promoQuantity,
            }))
          : [{ variantId: availableVariants[0]?.id || 0, flashSalePrice: 100000, promoQuantity: 10 }],
    });
    setErrorMessage(null);
    setIsFormOpen(true);
  };

  // Thêm dòng sản phẩm mới vào chiến dịch
  const handleAddItemRow = () => {
    const selectedIds = new Set(formData.items.map((i) => i.variantId));
    const nextVariant = availableVariants.find((v) => !selectedIds.has(v.id)) || availableVariants[0];

    setFormData((prev) => ({
      ...prev,
      items: [
        ...prev.items,
        {
          variantId: nextVariant ? nextVariant.id : 0,
          flashSalePrice: nextVariant ? Math.round(nextVariant.sellingPrice * 0.8) : 100000,
          promoQuantity: nextVariant ? Math.min(10, nextVariant.stockQuantity || 10) : 10,
        },
      ],
    }));
  };

  // Xóa dòng sản phẩm
  const handleRemoveItemRow = (index: number) => {
    if (formData.items.length <= 1) {
      setErrorMessage('Chiến dịch Flash Sale phải có ít nhất 1 sản phẩm tham gia');
      return;
    }
    setFormData((prev) => ({
      ...prev,
      items: prev.items.filter((_, i) => i !== index),
    }));
  };

  // Cập nhật khi chọn biến thể từ dropdown
  const handleVariantChange = (index: number, newVariantId: number) => {
    const variant = variantMap.get(newVariantId);
    setFormData((prev) => {
      const updated = [...prev.items];
      const basePrice = variant ? variant.sellingPrice : 100000;
      updated[index] = {
        ...updated[index],
        variantId: newVariantId,
        flashSalePrice: Math.round(basePrice * 0.8), // Gợi ý giảm 20%
        promoQuantity: variant ? Math.min(10, variant.stockQuantity || 10) : 10,
      };
      return { ...prev, items: updated };
    });
  };

  // Cập nhật giá bán hoặc số lượng
  const handleItemFieldChange = (index: number, field: 'flashSalePrice' | 'promoQuantity', value: number) => {
    setFormData((prev) => {
      const updated = [...prev.items];
      updated[index] = { ...updated[index], [field]: value };
      return { ...prev, items: updated };
    });
  };

  // Submit Form Tạo / Sửa
  const handleSubmitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!formData.name.trim()) {
      setErrorMessage('Tên chiến dịch không được để trống');
      return;
    }
    if (!formData.startTime || !formData.endTime) {
      setErrorMessage('Vui lòng chọn thời gian bắt đầu và kết thúc');
      return;
    }
    if (new Date(formData.startTime) >= new Date(formData.endTime)) {
      setErrorMessage('Thời gian kết thúc phải sau thời gian bắt đầu');
      return;
    }

    // Kiểm tra chưa chọn biến thể
    const hasUnselected = formData.items.some((i) => !i.variantId || i.variantId === 0);
    if (hasUnselected) {
      setErrorMessage('Vui lòng chọn sản phẩm cụ thể cho tất cả các dòng');
      return;
    }

    // Kiểm tra trùng lặp sản phẩm
    const variantIds = formData.items.map((i) => i.variantId);
    if (new Set(variantIds).size !== variantIds.length) {
      setErrorMessage('Không được có sản phẩm trùng lặp trong cùng một chiến dịch');
      return;
    }

    // Kiểm tra logic giá và tồn kho
    for (const item of formData.items) {
      const v = variantMap.get(item.variantId);
      if (v) {
        if (item.flashSalePrice >= v.sellingPrice) {
          setErrorMessage(`Giá Flash Sale của "${v.productName} (${v.sku})" phải thấp hơn giá bán hiện tại (${formatCurrency(v.sellingPrice)})`);
          return;
        }
        if (item.promoQuantity > v.stockQuantity) {
          setErrorMessage(`Số lượng khuyến mãi của "${v.sku}" vượt quá tồn kho thực tế (${v.stockQuantity})`);
          return;
        }
      }
    }

    setSubmitting(true);

    try {
      const payload: CreateFlashSaleInput | UpdateFlashSaleInput = {
        name: formData.name.trim(),
        bannerUrl: formData.bannerUrl || undefined,
        startTime: new Date(formData.startTime).toISOString(),
        endTime: new Date(formData.endTime).toISOString(),
        isActive: formData.isActive,
        items: formData.items,
      };

      if (editingCampaign) {
        await flashSaleApi.updateCampaign(editingCampaign.id, payload);
        setSuccessMessage('Cập nhật chiến dịch Flash Sale thành công!');
      } else {
        await flashSaleApi.createCampaign(payload as CreateFlashSaleInput);
        setSuccessMessage('Tạo chiến dịch Flash Sale thành công!');
      }

      setIsFormOpen(false);
      fetchCampaigns();
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err: any) {
      console.error('Submit error:', err);
      setErrorMessage(err?.response?.data?.message || err?.message || 'Có lỗi xảy ra khi lưu chiến dịch');
    } finally {
      setSubmitting(false);
    }
  };

  // Xóa chiến dịch
  const handleDelete = async (id: number) => {
    setSubmitting(true);
    try {
      await flashSaleApi.deleteCampaign(id);
      setSuccessMessage('Xóa chiến dịch thành công!');
      setDeleteConfirmId(null);
      fetchCampaigns();
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err: any) {
      setErrorMessage(err?.response?.data?.message || 'Xóa chiến dịch thất bại');
    } finally {
      setSubmitting(false);
    }
  };

  const getCampaignStatus = (c: FlashSaleCampaign) => {
    if (!c.isActive) {
      return { label: 'Đã tắt', variant: 'outline' as const };
    }
    const now = new Date().getTime();
    const start = new Date(c.startTime).getTime();
    const end = new Date(c.endTime).getTime();

    if (now < start) {
      return { label: 'Sắp diễn ra', variant: 'warning' as const };
    }
    if (now > end) {
      return { label: 'Đã kết thúc', variant: 'secondary' as const };
    }
    return { label: 'Đang diễn ra', variant: 'success' as const };
  };

  const filteredCampaigns = campaigns.filter((c) =>
    c.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const formatDateTime = (str: string) => {
    try {
      return new Date(str).toLocaleString('vi-VN', {
        hour: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
      });
    } catch {
      return str;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between border-b border-[#EAE6DF] pb-5">
        <div>
          <span className="eyebrow block mb-1">MODULE INVENTORY SALE</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Quản Lý Flash Sale
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Tạo và cấu hình các chiến dịch giảm giá theo khung giờ chớp nhoáng.
          </p>
        </div>

        <Button
          onClick={handleOpenCreate}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2 shrink-0 shadow-xs"
        >
          <Plus className="size-4" />
          <span>Tạo chiến dịch mới</span>
        </Button>
      </div>

      {/* Notifications */}
      {successMessage && (
        <div className="flex items-center gap-2 rounded-xl bg-emerald-50 border border-emerald-200 p-3.5 text-xs font-semibold text-emerald-800">
          <CheckCircle2 className="size-4 text-emerald-600" />
          <span>{successMessage}</span>
        </div>
      )}

      {errorMessage && (
        <div className="flex items-center gap-2 rounded-xl bg-rose-50 border border-rose-200 p-3.5 text-xs font-semibold text-rose-800">
          <AlertCircle className="size-4 text-rose-600" />
          <span>{errorMessage}</span>
        </div>
      )}

      {/* Quick Stats Grid */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Tổng chiến dịch</span>
            <Layers className="size-4 text-neutral-700" />
          </div>
          <div className="mt-2 text-2xl font-bold text-neutral-900 font-display">
            {totalItems}
          </div>
        </div>

        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Đang hoạt động</span>
            <Sparkles className="size-4 text-emerald-600" />
          </div>
          <div className="mt-2 text-2xl font-bold text-emerald-600 font-display">
            {campaigns.filter((c) => c.isActive).length}
          </div>
        </div>

        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Tổng SP Sale</span>
            <Flame className="size-4 text-red-500" />
          </div>
          <div className="mt-2 text-2xl font-bold text-neutral-900 font-display">
            {campaigns.reduce((acc, c) => acc + (c.items?.length || 0), 0)}
          </div>
        </div>

        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Đã bán</span>
            <CheckCircle2 className="size-4 text-neutral-700" />
          </div>
          <div className="mt-2 text-2xl font-bold text-neutral-900 font-display">
            {campaigns.reduce(
              (acc, c) => acc + (c.items?.reduce((iAcc, item) => iAcc + (item.soldQuantity || 0), 0) || 0),
              0
            )}
          </div>
        </div>
      </div>

      {/* Toolbar */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between rounded-2xl bg-white p-4 shadow-xs border border-[#EAE6DF]">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 size-4 text-neutral-400" />
          <input
            type="text"
            placeholder="Tìm theo tên chiến dịch..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-sm rounded-xl border border-[#EAE6DF] bg-white outline-none focus:border-neutral-900 text-neutral-900"
          />
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1 rounded-xl bg-neutral-100 p-1">
            <button
              onClick={() => setActiveFilter('all')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                activeFilter === 'all' ? 'bg-white text-neutral-900 shadow-xs' : 'text-neutral-500'
              }`}
            >
              Tất cả
            </button>
            <button
              onClick={() => setActiveFilter('active')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                activeFilter === 'active' ? 'bg-white text-neutral-900 shadow-xs' : 'text-neutral-500'
              }`}
            >
              Hoạt động
            </button>
            <button
              onClick={() => setActiveFilter('inactive')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                activeFilter === 'inactive' ? 'bg-white text-neutral-900 shadow-xs' : 'text-neutral-500'
              }`}
            >
              Đã tắt
            </button>
          </div>

          <Button onClick={fetchCampaigns} variant="outline" size="sm" className="gap-1.5 rounded-xl border-[#EAE6DF]">
            <RefreshCcw className="size-3.5" />
          </Button>
        </div>
      </div>

      {/* Campaign Table */}
      <div className="overflow-hidden rounded-2xl border border-[#EAE6DF] bg-white shadow-xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 border-b border-[#EAE6DF] text-[11px] font-bold uppercase tracking-wider text-neutral-500">
              <tr>
                <th className="px-6 py-4">Mã</th>
                <th className="px-6 py-4">Tên chiến dịch</th>
                <th className="px-6 py-4">Thời gian</th>
                <th className="px-6 py-4">Sản phẩm</th>
                <th className="px-6 py-4">Trạng thái</th>
                <th className="px-6 py-4 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#EAE6DF]">
              {loading ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-neutral-500">
                    <Loader2 className="mx-auto size-5 animate-spin mb-1 text-neutral-400" />
                    Đang tải dữ liệu chiến dịch Flash Sale...
                  </td>
                </tr>
              ) : filteredCampaigns.length === 0 ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-neutral-500">
                    Chưa có chiến dịch Flash Sale nào phù hợp.
                  </td>
                </tr>
              ) : (
                filteredCampaigns.map((c) => {
                  const status = getCampaignStatus(c);
                  return (
                    <tr key={c.id} className="hover:bg-neutral-50/70 transition-colors">
                      <td className="px-6 py-4 font-mono font-bold text-neutral-500">#{c.id}</td>
                      <td className="px-6 py-4 font-semibold text-neutral-900 text-sm">
                        {c.name}
                      </td>
                      <td className="px-6 py-4 text-xs text-neutral-500">
                        <div className="flex items-center gap-1.5 font-medium text-neutral-700">
                          <Calendar className="size-3.5 text-neutral-400" />
                          <span>{formatDateTime(c.startTime)}</span>
                        </div>
                        <div className="flex items-center gap-1.5 mt-1 text-neutral-400">
                          <Clock className="size-3.5" />
                          <span>đến {formatDateTime(c.endTime)}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="inline-flex items-center gap-1 rounded-full bg-neutral-100 px-2.5 py-1 text-xs font-semibold text-neutral-800">
                          <Flame className="size-3 text-red-500" />
                          {c.items?.length || 0} biến thể
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <Badge variant={status.variant} className="text-[10px]">
                          {status.label}
                        </Badge>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            onClick={() => setDetailCampaign(c)}
                            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
                            title="Xem chi tiết"
                          >
                            <Eye className="size-4" />
                          </button>
                          <button
                            onClick={() => handleOpenEdit(c)}
                            className="p-1.5 text-neutral-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition"
                            title="Chỉnh sửa chiến dịch"
                          >
                            <Pencil className="size-4" />
                          </button>
                          <button
                            onClick={() => setDeleteConfirmId(c.id)}
                            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                            title="Xóa chiến dịch"
                          >
                            <Trash2 className="size-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* ============================================================== */}
      {/* MODAL TẠO & CHỈNH SỬA CHIẾN DỊCH (GIAO DIỆN SÁNG, RÕ NÉT, DỄ DÙNG) */}
      {/* ============================================================== */}
      {isFormOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="relative w-full max-w-4xl max-h-[90vh] overflow-y-auto rounded-3xl bg-white p-6 md:p-8 shadow-2xl border border-[#EAE6DF] text-neutral-900 animate-in zoom-in-95 duration-200">
            {/* Header Modal */}
            <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-4">
              <h2 className="text-xl font-bold text-neutral-900 flex items-center gap-2">
                <Flame className="size-5 text-red-600" />
                <span>{editingCampaign ? 'Chỉnh Sửa Flash Sale' : 'Tạo Chiến Dịch Flash Sale'}</span>
              </h2>
              <button
                type="button"
                onClick={() => setIsFormOpen(false)}
                className="rounded-full p-1.5 text-neutral-400 hover:bg-neutral-100 hover:text-neutral-700 transition cursor-pointer"
              >
                <X className="size-5" />
              </button>
            </div>

            <form onSubmit={handleSubmitForm} className="mt-6 space-y-6">
              {errorMessage && (
                <div className="rounded-xl bg-rose-50 p-3.5 text-xs font-semibold text-rose-700 border border-rose-200 flex items-center gap-2">
                  <AlertCircle className="size-4 shrink-0 text-rose-600" />
                  <span>{errorMessage}</span>
                </div>
              )}

              {/* Thông tin chung */}
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="sm:col-span-2 space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Tên chiến dịch *
                  </label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="VD: Flash Sale Giờ Vàng 12h-14h"
                    required
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 font-medium outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Thời gian bắt đầu *
                  </label>
                  <input
                    type="datetime-local"
                    value={formData.startTime}
                    onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                    required
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Thời gian kết thúc *
                  </label>
                  <input
                    type="datetime-local"
                    value={formData.endTime}
                    onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                    required
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="sm:col-span-2 space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Banner URL (Tùy chọn)
                  </label>
                  <input
                    type="text"
                    value={formData.bannerUrl}
                    onChange={(e) => setFormData({ ...formData, bannerUrl: e.target.value })}
                    placeholder="https://images.unsplash.com/... hoặc đường dẫn ảnh banner"
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="sm:col-span-2 flex items-center gap-2.5 pt-1">
                  <input
                    type="checkbox"
                    id="isActive"
                    checked={formData.isActive}
                    onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
                    className="size-4 rounded border-[#EAE6DF] accent-[#1D1F20] cursor-pointer"
                  />
                  <label htmlFor="isActive" className="text-xs font-semibold text-neutral-800 cursor-pointer">
                    Kích hoạt chiến dịch ngay lập tức
                  </label>
                </div>
              </div>

              {/* Danh sách biến thể tham gia Sale */}
              <div className="border-t border-[#EAE6DF] pt-6 space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-sm font-bold uppercase tracking-wider text-neutral-900 flex items-center gap-1.5">
                      <Package className="size-4 text-neutral-700" />
                      Danh sách sản phẩm tham gia sale ({formData.items.length})
                    </h3>
                    <p className="text-xs text-neutral-500 mt-0.5">
                      Chọn sản phẩm và cấu hình giá ưu đãi cùng số lượng xuất kho cho sự kiện.
                    </p>
                  </div>

                  <Button
                    type="button"
                    onClick={handleAddItemRow}
                    className="h-9 px-3.5 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white text-xs font-semibold flex items-center gap-1.5 shadow-xs"
                  >
                    <Plus className="size-3.5" />
                    <span>Thêm sản phẩm</span>
                  </Button>
                </div>

                {isLoadingProds ? (
                  <div className="py-8 text-center text-xs text-neutral-400">
                    <Loader2 className="mx-auto size-4 animate-spin mb-1" />
                    Đang tải danh sách sản phẩm từ kho...
                  </div>
                ) : (
                  <div className="space-y-3">
                    {formData.items.map((item, idx) => {
                      const currentVariant = variantMap.get(item.variantId);
                      const basePrice = currentVariant ? currentVariant.sellingPrice : 0;
                      const discountPercent = basePrice > item.flashSalePrice
                        ? Math.round(((basePrice - item.flashSalePrice) / basePrice) * 100)
                        : 0;

                      return (
                        <div
                          key={idx}
                          className="rounded-2xl border border-[#EAE6DF] bg-neutral-50/50 p-4 space-y-3 transition hover:border-neutral-300"
                        >
                          <div className="flex items-start justify-between gap-3">
                            {/* Chọn biến thể từ Dropdown */}
                            <div className="flex-1 space-y-1">
                              <label className="text-[11px] font-bold uppercase tracking-wider text-neutral-600">
                                Sản phẩm & Phân loại SKU *
                              </label>
                              <select
                                value={item.variantId}
                                onChange={(e) => handleVariantChange(idx, Number(e.target.value))}
                                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2.5 text-xs font-medium text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                                required
                              >
                                <option value={0}>-- Chọn sản phẩm tham gia --</option>
                                {availableVariants.map((v) => (
                                  <option key={v.id} value={v.id}>
                                    {v.label}
                                  </option>
                                ))}
                              </select>
                            </div>

                            {/* Nút xóa dòng */}
                            <button
                              type="button"
                              onClick={() => handleRemoveItemRow(idx)}
                              className="p-2 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition mt-5 shrink-0"
                              title="Xóa sản phẩm này khỏi Flash Sale"
                            >
                              <Trash2 className="size-4" />
                            </button>
                          </div>

                          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                            {/* Giá Flash Sale */}
                            <div className="space-y-1">
                              <div className="flex items-center justify-between">
                                <label className="text-[11px] font-bold uppercase tracking-wider text-neutral-600">
                                  Giá Flash Sale (VNĐ) *
                                </label>
                                {discountPercent > 0 && (
                                  <span className="text-[10px] font-bold text-red-600 bg-red-50 px-1.5 py-0.5 rounded border border-red-200">
                                    Giảm {discountPercent}%
                                  </span>
                                )}
                              </div>
                              <input
                                type="number"
                                min={1000}
                                step={1000}
                                value={item.flashSalePrice}
                                onChange={(e) => handleItemFieldChange(idx, 'flashSalePrice', Number(e.target.value))}
                                required
                                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2 text-xs font-bold text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                              />
                              {basePrice > 0 && (
                                <p className="text-[10px] text-neutral-500">
                                  Giá bán gốc hiện tại: <strong className="text-neutral-700">{formatCurrency(basePrice)}</strong>
                                </p>
                              )}
                            </div>

                            {/* Số lượng KM */}
                            <div className="space-y-1">
                              <label className="text-[11px] font-bold uppercase tracking-wider text-neutral-600">
                                Số lượng khuyến mãi *
                              </label>
                              <input
                                type="number"
                                min={1}
                                max={currentVariant?.stockQuantity || 9999}
                                value={item.promoQuantity}
                                onChange={(e) => handleItemFieldChange(idx, 'promoQuantity', Number(e.target.value))}
                                required
                                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2 text-xs font-mono font-semibold text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                              />
                              {currentVariant && (
                                <p className="text-[10px] text-neutral-500">
                                  Tồn kho sẵn có: <strong className="text-neutral-700">{currentVariant.stockQuantity} sản phẩm</strong>
                                </p>
                              )}
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>

              {/* Action Buttons */}
              <div className="flex items-center justify-end gap-3 border-t border-[#EAE6DF] pt-5">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setIsFormOpen(false)}
                  className="rounded-xl border-[#EAE6DF] text-xs h-10 px-5"
                >
                  Hủy bỏ
                </Button>
                <Button
                  type="submit"
                  disabled={submitting}
                  className="rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-semibold text-xs h-10 px-6 shadow-xs"
                >
                  {submitting ? (
                    <>
                      <Loader2 className="size-4 animate-spin mr-1.5" />
                      Đang lưu...
                    </>
                  ) : editingCampaign ? (
                    'Lưu thay đổi chiến dịch'
                  ) : (
                    'Khởi tạo chiến dịch'
                  )}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Detail Modal */}
      {detailCampaign && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="relative w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-3xl bg-white p-6 md:p-8 shadow-2xl border border-[#EAE6DF] text-neutral-900">
            <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-4">
              <h2 className="text-lg font-bold text-neutral-900 flex items-center gap-2">
                <Flame className="size-5 text-red-600" />
                <span>Chi tiết chiến dịch: {detailCampaign.name}</span>
              </h2>
              <button
                onClick={() => setDetailCampaign(null)}
                className="rounded-full p-1.5 text-neutral-400 hover:bg-neutral-100 transition cursor-pointer"
              >
                <X className="size-5" />
              </button>
            </div>

            <div className="mt-6 space-y-4 text-xs">
              <div className="grid grid-cols-2 gap-4 rounded-xl bg-neutral-50 p-4 border border-[#EAE6DF]">
                <div>
                  <span className="text-neutral-500 block mb-1">Thời gian bắt đầu:</span>
                  <span className="font-semibold text-neutral-900">
                    {formatDateTime(detailCampaign.startTime)}
                  </span>
                </div>
                <div>
                  <span className="text-neutral-500 block mb-1">Thời gian kết thúc:</span>
                  <span className="font-semibold text-neutral-900">
                    {formatDateTime(detailCampaign.endTime)}
                  </span>
                </div>
              </div>

              <h4 className="font-bold uppercase tracking-wider text-neutral-700 pt-2">
                Danh sách sản phẩm trong sale ({detailCampaign.items?.length || 0})
              </h4>

              <div className="space-y-2.5 max-h-64 overflow-y-auto pr-1">
                {detailCampaign.items?.map((item) => (
                  <div
                    key={item.id || item.variantId}
                    className="flex items-center justify-between rounded-xl border border-[#EAE6DF] p-3.5 bg-white"
                  >
                    <div>
                      <p className="font-semibold text-neutral-900">
                        {item.variant?.product?.defaultName || `Biến thể #${item.variantId}`}
                      </p>
                      <p className="text-[11px] text-neutral-400 font-mono mt-0.5">
                        SKU: {item.variant?.sku || item.variantId}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-red-600 text-sm">
                        {formatCurrency(Number(item.flashSalePrice))}
                      </p>
                      <p className="text-[11px] text-neutral-500 mt-0.5">
                        Đã bán: <strong className="text-neutral-800">{item.soldQuantity}</strong> / {item.promoQuantity}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="mt-6 flex justify-end border-t border-[#EAE6DF] pt-4">
              <Button onClick={() => setDetailCampaign(null)} variant="outline" className="rounded-xl border-[#EAE6DF] text-xs">
                Đóng
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirm Modal */}
      {deleteConfirmId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl border border-[#EAE6DF] text-neutral-900">
            <h3 className="text-base font-bold text-neutral-900">Xác nhận xóa chiến dịch</h3>
            <p className="mt-2 text-xs text-neutral-500 leading-relaxed">
              Bạn có chắc chắn muốn xóa chiến dịch Flash Sale này không? Thao tác này sẽ ngừng áp dụng toàn bộ giá ưu đãi cho các sản phẩm liên quan.
            </p>
            <div className="mt-6 flex justify-end gap-2.5">
              <Button variant="outline" size="sm" onClick={() => setDeleteConfirmId(null)} className="rounded-xl border-[#EAE6DF] text-xs">
                Hủy
              </Button>
              <Button
                size="sm"
                onClick={() => handleDelete(deleteConfirmId)}
                disabled={submitting}
                className="rounded-xl bg-rose-600 hover:bg-rose-700 !text-white [&_*]:!text-white font-semibold text-xs"
              >
                {submitting ? 'Đang xóa...' : 'Xóa vĩnh viễn'}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
```

# admin\inventory\import\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\inventory\import\page.tsx` |
| Size | 10,298 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.459005 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm, useFieldArray } from 'react-hook-form';
import { toast } from 'sonner';
import { ArrowLeft, Plus, Trash2, Loader2, Save, Boxes } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { formatCurrency } from '@/lib/utils/format-currency';

interface ImportItem {
  variantId: number;
  quantityChange: number;
  importPrice: number;
}

interface ImportFormValues {
  supplier: string;
  note?: string;
  items: ImportItem[];
}

export default function AdminInventoryImportPage() {
  const router = useRouter();
  const queryClient = useQueryClient();

  // Lấy danh sách sản phẩm và biến thể để chọn
  const { data: prodRes, isLoading: isLoadingProds } = useQuery({
    queryKey: ['admin-products-variants-select'],
    queryFn: () => catalogApi.getProducts({ limit: 100 }),
  });

  const products = (prodRes?.data as any) || [];

  // Tạo danh sách phẳng tất cả SKU để đưa vào dropdown
  const availableVariants = React.useMemo(() => {
    const list: Array<{ id: number; label: string; currentStock: number }> = [];
    products.forEach((p: any) => {
      p.variants?.forEach((v: any) => {
        list.push({
          id: v.id,
          label: `${p.defaultName} — [${v.variantValue || 'Mặc định'}] (${v.sku})`,
          currentStock: v.stockQuantity,
        });
      });
    });
    return list;
  }, [products]);

  const { register, control, handleSubmit, watch, formState: { errors } } = useForm<ImportFormValues>({
    defaultValues: {
      supplier: '',
      note: '',
      items: [{ variantId: 0, quantityChange: 10, importPrice: 100000 }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  const watchedItems = watch('items');
  const totalCost = watchedItems.reduce(
    (sum, item) => sum + (Number(item.quantityChange || 0) * Number(item.importPrice || 0)),
    0
  );

  const importMutation = useMutation({
    mutationFn: async (values: ImportFormValues) => {
      // Cập nhật tồn kho từng biến thể thông qua catalogApi
      const updatePromises = values.items.map((item) =>
        catalogApi.updateVariantStock(Number(item.variantId), Number(item.quantityChange))
      );
      return Promise.all(updatePromises);
    },
    onSuccess: () => {
      toast.success('Nhập kho thành công! Số lượng tồn đã được cập nhật.');
      queryClient.invalidateQueries({ queryKey: ['admin-inventory'] });
      queryClient.invalidateQueries({ queryKey: ['admin-products'] });
      router.push('/admin/inventory');
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi nhập kho');
    },
  });

  const onSubmit = (data: ImportFormValues) => {
    const hasInvalid = data.items.some((i) => !i.variantId || Number(i.variantId) === 0);
    if (hasInvalid) {
      toast.error('Vui lòng chọn biến thể sản phẩm cho tất cả các dòng');
      return;
    }
    importMutation.mutate(data);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4 border-b border-[#EAE6DF] pb-4">
        <Link href="/admin/inventory" className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition">
          <ArrowLeft className="size-4 text-neutral-600" />
        </Link>
        <div>
          <span className="eyebrow block mb-0.5">VẬN HÀNH KHO</span>
          <h1 className="font-display text-2xl text-[#1D1F20] tracking-tight">Tạo phiếu nhập kho</h1>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
          <h3 className="text-sm font-bold text-neutral-900 border-b border-[#EAE6DF] pb-3">
            Thông tin nhà cung cấp & chứng từ
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Nhà cung cấp / Đối tác *
              </label>
              <input
                {...register('supplier', { required: 'Vui lòng nhập tên nhà cung cấp' })}
                placeholder="VD: Xưởng dệt may Hà Nội"
                className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              />
              {errors.supplier && <p className="text-xs text-rose-500">{errors.supplier.message}</p>}
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Ghi chú phiếu nhập
              </label>
              <input
                {...register('note')}
                placeholder="VD: Nhập bổ sung đợt đầu tháng 10"
                className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-3">
            <h3 className="text-sm font-bold text-neutral-900">Danh sách sản phẩm nhập</h3>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => append({ variantId: 0, quantityChange: 10, importPrice: 100000 })}
              className="rounded-xl border-[#EAE6DF] text-xs"
            >
              <Plus className="size-3.5 mr-1" /> Thêm dòng
            </Button>
          </div>

          {isLoadingProds ? (
            <div className="py-8 text-center text-xs text-neutral-400">Đang tải danh sách SKU...</div>
          ) : (
            <div className="space-y-3">
              {fields.map((field, idx) => (
                <div key={field.id} className="flex flex-col sm:flex-row gap-3 items-end bg-neutral-50/50 p-3.5 rounded-xl border border-[#EAE6DF]">
                  <div className="flex-1 space-y-1 w-full">
                    <label className="text-[11px] font-semibold text-neutral-500 uppercase">Sản phẩm / SKU *</label>
                    <select
                      {...register(`items.${idx}.variantId` as const, { required: true })}
                      className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs outline-none focus:border-neutral-900"
                    >
                      <option value={0}>-- Chọn SKU --</option>
                      {availableVariants.map((v) => (
                        <option key={v.id} value={v.id}>
                          {v.label} [Hiện có: {v.currentStock}]
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="w-full sm:w-28 space-y-1">
                    <label className="text-[11px] font-semibold text-neutral-500 uppercase">Số lượng</label>
                    <input
                      type="number"
                      min={1}
                      {...register(`items.${idx}.quantityChange` as const, { required: true, valueAsNumber: true })}
                      className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs font-mono outline-none"
                    />
                  </div>

                  <div className="w-full sm:w-36 space-y-1">
                    <label className="text-[11px] font-semibold text-neutral-500 uppercase">Giá nhập (₫)</label>
                    <input
                      type="number"
                      min={0}
                      step={1000}
                      {...register(`items.${idx}.importPrice` as const, { required: true, valueAsNumber: true })}
                      className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs font-mono outline-none"
                    />
                  </div>

                  <button
                    type="button"
                    onClick={() => remove(idx)}
                    className="p-2 text-neutral-400 hover:text-rose-600 transition self-center sm:self-end mb-1"
                    title="Xóa dòng"
                  >
                    <Trash2 className="size-4" />
                  </button>
                </div>
              ))}
            </div>
          )}

          <div className="flex justify-between items-center pt-3 border-t border-[#EAE6DF] text-sm">
            <span className="font-semibold text-neutral-700">Tổng giá trị phiếu nhập:</span>
            <span className="font-black text-lg text-neutral-900">{formatCurrency(totalCost)}</span>
          </div>
        </div>

        <div className="flex justify-end gap-3">
          <Link href="/admin/inventory">
            <Button type="button" variant="outline" className="rounded-xl border-[#EAE6DF]">Hủy</Button>
          </Link>
          <Button
            type="submit"
            disabled={importMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800"
          >
            {importMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-1.5" /> Đang cập nhật kho...
              </>
            ) : (
              <>
                <Save className="size-4 mr-1.5" /> Xác nhận nhập kho
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}
```

# admin\inventory\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\inventory\page.tsx` |
| Size | 4,757 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.460002 |

```tsx
'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { Boxes, AlertTriangle, ArrowRightLeft, FileDown } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import type { Product } from '@/types';

export default function AdminInventoryPage() {
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-inventory', page, search],
    queryFn: () => catalogApi.getProducts({ page, limit: 10, search: search || undefined }),
  });

  const columns: Column<Product>[] = [
    {
      header: 'Sản phẩm',
      cell: (product) => (
        <div className="flex items-center gap-3">
          <div className="min-w-0 max-w-[200px]">
            <p className="font-semibold text-sm text-neutral-900 truncate" title={product.defaultName}>{product.defaultName}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Biến thể (SKU)',
      cell: (product) => (
        <div className="space-y-1">
          {product.variants?.slice(0, 3).map(v => (
            <div key={v.id} className="text-[11px] font-mono text-neutral-600 bg-neutral-50 px-2 py-0.5 rounded border border-[#EAE6DF] inline-block mr-1">
              {v.sku}
            </div>
          ))}
          {(product.variants?.length || 0) > 3 && <span className="text-[10px] text-neutral-400">+{product.variants!.length - 3}</span>}
        </div>
      ),
    },
    {
      header: 'Tổng Tồn kho',
      cell: (product) => {
        const total = product.variants?.reduce((acc, v) => acc + v.stockQuantity, 0) || 0;
        return (
          <span className={`font-mono text-sm font-bold ${total <= 5 ? 'text-rose-600' : 'text-neutral-900'}`}>
            {total.toLocaleString()}
          </span>
        );
      },
    },
    {
      header: 'Cảnh báo',
      cell: (product) => {
        const total = product.variants?.reduce((acc, v) => acc + v.stockQuantity, 0) || 0;
        if (total === 0) return <Badge variant="destructive" className="text-[10px]"><AlertTriangle className="size-3 mr-1"/> Hết hàng</Badge>;
        if (total <= 10) return <Badge variant="warning" className="text-[10px]"><AlertTriangle className="size-3 mr-1"/> Sắp hết</Badge>;
        return <Badge variant="success" className="text-[10px]">Đủ hàng</Badge>;
      },
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (product) => (
        <Link href={`/admin/products/${product.id}/variants`}>
          <Button variant="outline" size="sm" className="h-8 text-xs border-[#EAE6DF]">
            <ArrowRightLeft className="size-3 mr-1.5" /> Điều chỉnh
          </Button>
        </Link>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">VẬN HÀNH</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Kho hàng</h1>
          <p className="text-xs text-neutral-500 mt-1">Kiểm soát số lượng tồn kho và cảnh báo hết hàng.</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" className="h-10 px-4 rounded-xl border-[#EAE6DF]">
            <FileDown className="size-4 mr-2" /> Xuất báo cáo
          </Button>
          <Link href="/admin/inventory/import">
            <Button className="h-10 px-4 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white">
              Nhập kho
            </Button>
          </Link>
        </div>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <SearchInput 
          value={search} 
          onChange={(val) => { setSearch(val); setPage(1); }} 
          placeholder="Tìm theo tên sản phẩm hoặc mã SKU..." 
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={(data?.data as any) || []}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy dữ liệu kho hàng."
      />
    </div>
  );
}
```

# admin\layout.tsx

| Property | Value |
|----------|-------|
| Path | `admin\layout.tsx` |
| Size | 4,420 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.463001 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { AdminSidebar } from '@/modules/admin/components/sidebar';
import { useAuth } from '@/components/providers/auth-provider';
import { Avatar } from '@/components/ui/avatar';
import { Bell, Search, ShieldAlert, ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { user, isAuthenticated, isAdmin } = useAuth();
  const router = useRouter();
  const pathname = usePathname();
  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#FBF9F4]">
        <div className="animate-pulse text-sm text-neutral-500">Đang khởi tạo quyền quản trị...</div>
      </div>
    );
  }

  const canAccessStaffChat =
    isAuthenticated && user?.role?.code === 'STAFF' && pathname === '/admin/chat';

  if (canAccessStaffChat) {
    return (
      <div className="flex min-h-screen bg-[#FBF9F4]">
        <AdminSidebar />
        <main className="min-w-0 flex-1 p-4 sm:p-8">{children}</main>
      </div>
    );
  }

  if (!isAuthenticated || !isAdmin) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-[#FBF9F4] p-6 text-center">
        <div className="size-14 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center mb-4">
          <ShieldAlert className="size-7" />
        </div>
        <h2 className="text-xl font-semibold text-neutral-900 mb-2">
          Truy cập bị từ chối
        </h2>
        <p className="text-xs text-neutral-500 max-w-sm mb-6 leading-relaxed">
          Khu vực này chỉ dành cho tài khoản có quyền Quản trị viên (Admin). Bạn không có quyền truy cập vào nội dung này.
        </p>
        <div className="flex gap-3">
          <Button
            variant="outline"
            onClick={() => router.push('/')}
            className="rounded-xl border-[#EAE6DF]"
          >
            <ArrowLeft className="size-4 mr-2" /> Về trang chủ
          </Button>
          <Button
            onClick={() => router.push('/login')}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white"
          >
            Đăng nhập tài khoản khác
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex bg-[#FBF9F4]">
      {/* Sidebar */}
      <AdminSidebar />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Topbar */}
        <header className="h-16 bg-white/80 backdrop-blur border-b border-[#EAE6DF] px-6 sm:px-8 flex items-center justify-between sticky top-0 z-30">
          <div className="flex items-center gap-3">
            <span className="eyebrow text-[#74736E]">ADMINISTRATION CONSOLE</span>
          </div>

          <div className="flex items-center gap-4">
            <button
              type="button"
              className="p-2 text-neutral-400 hover:text-neutral-700 hover:bg-neutral-100 rounded-xl transition relative"
            >
              <Bell className="size-4" />
              <span className="absolute top-1.5 right-1.5 size-2 bg-emerald-500 rounded-full ring-2 ring-white" />
            </button>

            <div className="h-6 w-px bg-[#EAE6DF]" />

            <Link
              href="/profile"
              className="flex items-center gap-2.5 p-1 rounded-xl hover:bg-neutral-50 transition"
            >
              <Avatar
                src={user?.avatarUrl}
                alt={user?.fullName || 'Admin'}
                size="sm"
                className="size-7"
              />
              <span className="text-xs font-semibold text-neutral-800 hidden sm:inline">
                {user?.fullName}
              </span>
            </Link>
          </div>
        </header>

        {/* Dynamic page content */}
        <main className="flex-1 p-6 sm:p-8 max-w-[1440px] w-full mx-auto">
          {children}
        </main>
      </div>
    </div>
  );
}

```

# admin\orders\[orderId]\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\orders\[orderId]\page.tsx` |
| Size | 11,906 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.465531 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { 
  ArrowLeft, Package, Truck, CreditCard, Clock, 
  MapPin, User, FileText, CheckCircle2, Loader2 
} from 'lucide-react';
import { orderApi } from '@/api/order.api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { Order, OrderStatus } from '@/types';

const statusMap: Record<string, { label: string; variant: "default" | "secondary" | "destructive" | "outline" | "success" | "warning" | "lime" }> = {
  PENDING_PAYMENT: { label: 'Chờ thanh toán', variant: 'warning' },
  CONFIRMED: { label: 'Đã xác nhận', variant: 'lime' },
  PROCESSING: { label: 'Đang xử lý', variant: 'lime' },
  SHIPPED: { label: 'Đang giao hàng', variant: 'secondary' },
  DELIVERED: { label: 'Đã giao thành công', variant: 'success' },
  CANCELLED: { label: 'Đã hủy', variant: 'destructive' },
  RETURNED: { label: 'Đã hoàn trả', variant: 'outline' },
};

const NEXT_STATUS_TRANSITIONS: Record<string, { target: OrderStatus; label: string; actionText: string }[]> = {
  PENDING_PAYMENT: [
    { target: 'CONFIRMED', label: 'Xác nhận thanh toán & Duyệt đơn', actionText: 'Duyệt đơn' },
    { target: 'CANCELLED', label: 'Hủy đơn hàng', actionText: 'Hủy đơn' }
  ],
  CONFIRMED: [
    { target: 'PROCESSING', label: 'Bắt đầu xử lý đóng gói', actionText: 'Xử lý đóng gói' }
  ],
  PROCESSING: [
    { target: 'SHIPPED', label: 'Bàn giao cho đơn vị vận chuyển', actionText: 'Giao hàng' }
  ],
  SHIPPED: [
    { target: 'DELIVERED', label: 'Xác nhận giao thành công', actionText: 'Hoàn thành' },
    { target: 'RETURNED', label: 'Khách trả hàng / Giao thất bại', actionText: 'Hoàn trả' }
  ],
};

export default function AdminOrderDetailPage() {
  const params = useParams<{ orderId: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();
  const orderId = Number(params.orderId);

  const [confirmStatus, setConfirmStatus] = React.useState<{ target: OrderStatus; label: string } | null>(null);

  const { data: res, isLoading } = useQuery({
    queryKey: ['admin-order-detail', orderId],
    queryFn: () => orderApi.getOrderById(orderId),
    enabled: !!orderId,
  });

  const updateStatusMutation = useMutation({
    mutationFn: (status: OrderStatus) => orderApi.updateOrderStatus(orderId, { status }),
    onSuccess: () => {
      toast.success('Cập nhật trạng thái đơn hàng thành công');
      queryClient.invalidateQueries({ queryKey: ['admin-order-detail', orderId] });
      setConfirmStatus(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật trạng thái thất bại');
    }
  });

  const order = res?.data;

  if (isLoading) {
    return <div className="flex justify-center py-20"><Loader2 className="animate-spin text-neutral-400 size-8" /></div>;
  }

  if (!order) {
    return (
      <div className="text-center py-20">
        <p className="text-neutral-500">Không tìm thấy đơn hàng #{orderId}</p>
        <Button onClick={() => router.push('/admin/orders')} variant="outline" className="mt-4">Quay lại danh sách</Button>
      </div>
    );
  }

  const currentStatusMeta = statusMap[order.status] || { label: order.status, variant: 'default' };
  const availableTransitions = NEXT_STATUS_TRANSITIONS[order.status] || [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-4">
        <div className="flex items-center gap-4">
          <Link href="/admin/orders" className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition">
            <ArrowLeft className="size-4 text-neutral-600" />
          </Link>
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold text-neutral-900">{order.orderCode}</h1>
              <Badge variant={currentStatusMeta.variant}>{currentStatusMeta.label}</Badge>
            </div>
            <p className="text-xs text-neutral-500 flex items-center gap-1 mt-1">
              <Clock className="size-3" /> Đặt lúc {formatDate(order.createdAt || order.orderDate)}
            </p>
          </div>
        </div>

        {/* Action Buttons based on Status */}
        <div className="flex gap-2">
          {availableTransitions.map((transition) => (
            <Button
              key={transition.target}
              variant={transition.target === 'CANCELLED' ? 'outline' : 'default'}
              className={transition.target === 'CANCELLED' ? 'text-rose-600 border-rose-200 hover:bg-rose-50' : 'bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800'}
              onClick={() => setConfirmStatus({ target: transition.target, label: transition.label })}
            >
              {transition.actionText}
            </Button>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Order Items & Payment Info */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs">
            <h3 className="text-sm font-bold text-neutral-900 flex items-center gap-2 mb-4">
              <Package className="size-4" /> Danh sách sản phẩm
            </h3>
            
            <div className="divide-y divide-[#EAE6DF]">
              {order.orderItems?.map((item) => (
                <div key={item.id} className="py-4 flex gap-4 items-center">
                  {item.productImageUrl ? (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img src={item.productImageUrl} alt={item.productName} className="size-16 object-cover rounded-lg border border-[#EAE6DF]" />
                  ) : (
                    <div className="size-16 bg-neutral-100 rounded-lg flex items-center justify-center text-xs text-neutral-400">IMG</div>
                  )}
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-sm text-neutral-900 truncate">{item.productName}</p>
                    <p className="text-xs text-neutral-500 mt-1">Phân loại: {item.variantValue} • SKU: {item.skuCode}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-neutral-900">{formatCurrency(item.unitPrice)}</p>
                    <p className="text-xs text-neutral-500 mt-1">x {item.quantity}</p>
                  </div>
                </div>
              ))}
            </div>

            <div className="border-t border-[#EAE6DF] mt-2 pt-4 space-y-2 text-sm">
              <div className="flex justify-between text-neutral-600">
                <span>Tạm tính</span>
                <span>{formatCurrency(order.subTotal)}</span>
              </div>
              <div className="flex justify-between text-neutral-600">
                <span>Phí vận chuyển</span>
                <span>{formatCurrency(order.shippingFee)}</span>
              </div>
              <div className="flex justify-between text-emerald-600">
                <span>Giảm giá (Voucher + Điểm)</span>
                <span>- {formatCurrency(Number(order.voucherDiscount || 0) + Number(order.pointDiscount || 0))}</span>
              </div>
              <div className="flex justify-between items-center pt-3 mt-3 border-t border-[#EAE6DF]">
                <span className="font-bold text-neutral-900 text-base">Tổng cộng</span>
                <span className="font-black text-[#1D1F20] text-xl">{formatCurrency(order.totalAmount)}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column: Customer & Shipping */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
            <h3 className="text-sm font-bold text-neutral-900 flex items-center gap-2 border-b border-[#EAE6DF] pb-3">
              <User className="size-4" /> Thông tin Khách hàng
            </h3>
            <div className="space-y-3 text-sm">
              <div>
                <p className="text-xs text-neutral-500">Người nhận</p>
                <p className="font-semibold text-neutral-900 mt-0.5">{order.shippingAddress?.split('|')[0]?.trim() || 'N/A'}</p>
              </div>
              <div>
                <p className="text-xs text-neutral-500">Số điện thoại</p>
                <p className="font-medium text-neutral-900 mt-0.5">{order.shippingAddress?.split('|')[1]?.trim() || 'N/A'}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
            <h3 className="text-sm font-bold text-neutral-900 flex items-center gap-2 border-b border-[#EAE6DF] pb-3">
              <MapPin className="size-4" /> Địa chỉ Giao hàng
            </h3>
            <p className="text-sm text-neutral-700 leading-relaxed whitespace-pre-wrap">
              {order.shippingAddress}
            </p>
            {order.customerNote && (
              <div className="mt-4 p-3 bg-amber-50 border border-amber-200 rounded-xl">
                <p className="text-xs font-semibold text-amber-800 flex items-center gap-1.5 mb-1">
                  <FileText className="size-3" /> Ghi chú của khách
                </p>
                <p className="text-xs text-amber-900">{order.customerNote}</p>
              </div>
            )}
          </div>

          <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
            <h3 className="text-sm font-bold text-neutral-900 flex items-center gap-2 border-b border-[#EAE6DF] pb-3">
              <CreditCard className="size-4" /> Thanh toán
            </h3>
            <div className="space-y-3 text-sm">
              <div className="flex justify-between items-center">
                <span className="text-xs text-neutral-500">Phương thức</span>
                <Badge variant="secondary" className="font-mono text-[10px]">{order.paymentMethod}</Badge>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-xs text-neutral-500">Trạng thái TT</span>
                <Badge variant={order.paymentStatus === 'SUCCESS' ? 'success' : 'warning'} className="text-[10px]">
                  {order.paymentStatus}
                </Badge>
              </div>
            </div>
          </div>
        </div>
      </div>

      <ConfirmDialog
        open={!!confirmStatus}
        onOpenChange={(open) => !open && setConfirmStatus(null)}
        title="Xác nhận cập nhật trạng thái"
        description={`Bạn có chắc chắn muốn chuyển trạng thái đơn hàng này thành: ${confirmStatus?.label}?`}
        confirmText="Xác nhận"
        isLoading={updateStatusMutation.isPending}
        onConfirm={() => {
          if (confirmStatus) updateStatusMutation.mutate(confirmStatus.target);
        }}
      />
    </div>
  );
}
```

# admin\orders\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\orders\page.tsx` |
| Size | 5,576 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-10 16:39:14.802557 |

```tsx
'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { Eye, PackageSearch } from 'lucide-react';
import { orderApi } from '@/api/order.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { Order } from '@/types';

// Định nghĩa màu sắc cho từng trạng thái đơn hàng
const statusMap: Record<string, { label: string; variant: "default" | "secondary" | "destructive" | "outline" | "success" | "warning" | "lime" }> = {
  PENDING_PAYMENT: { label: 'Chờ thanh toán', variant: 'warning' },
  CONFIRMED: { label: 'Đã xác nhận', variant: 'lime' },
  PROCESSING: { label: 'Đang xử lý', variant: 'lime' },
  SHIPPED: { label: 'Đang giao', variant: 'secondary' },
  DELIVERED: { label: 'Đã giao', variant: 'success' },
  CANCELLED: { label: 'Đã hủy', variant: 'destructive' },
  RETURNED: { label: 'Đã trả hàng', variant: 'outline' },
};

export default function AdminOrdersPage() {
  const [page, setPage] = React.useState(1);
  const [status, setStatus] = React.useState<string>('ALL');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-orders', page, status],
    queryFn: () => orderApi.getOrders({ 
      page, 
      limit: 10, 
      all: true,
      ...(status !== 'ALL' ? { status } : {}) 
    }),
  });

  const columns: Column<Order>[] = [
    {
      header: 'Mã đơn',
      className: 'font-mono font-bold text-xs text-neutral-600',
      cell: (order) => order.orderCode,
    },
    {
      header: 'Thông tin Khách hàng',
      cell: (order) => (
        <div>
          <p className="font-semibold text-sm text-neutral-900">{order.shippingAddress?.split('|')[0]?.trim() || 'N/A'}</p>
          <p className="text-xs text-neutral-500 font-mono mt-0.5">{order.shippingAddress?.split('|')[1]?.trim() || ''}</p>
        </div>
      ),
    },
    {
      header: 'Tổng tiền',
      cell: (order) => (
        <span className="font-bold text-[#1D1F20]">
          {formatCurrency(order.totalAmount)}
        </span>
      ),
    },
    {
      header: 'Thanh toán',
      cell: (order) => (
        <span className="text-[11px] font-semibold text-neutral-600 bg-neutral-100 px-2 py-1 rounded-md">
          {order.paymentMethod}
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (order) => {
        const s = statusMap[order.status] || { label: order.status, variant: 'outline' };
        return <Badge variant={s.variant}>{s.label}</Badge>;
      },
    },
    {
      header: 'Ngày đặt',
      cell: (order) => (
        <span className="text-xs text-neutral-500">
          {formatDate(order.createdAt || order.orderDate)}
        </span>
      ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (order) => (
        <div className="flex justify-end">
          <Link href={`/admin/orders/${order.id}`}>
            <button className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition" title="Xem chi tiết">
              <Eye size={18} />
            </button>
          </Link>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">VẬN HÀNH</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Quản lý Đơn hàng</h1>
          <p className="text-xs text-neutral-500 mt-1">Theo dõi, duyệt và xử lý các đơn hàng trên toàn hệ thống.</p>
        </div>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex flex-col sm:flex-row gap-4 justify-between items-center">
        <div className="flex items-center gap-2 w-full max-w-sm">
          <label className="text-xs font-semibold text-neutral-500 w-24">Trạng thái:</label>
          <select 
            value={status} 
            onChange={(e) => { setStatus(e.target.value); setPage(1); }}
            className="w-full text-sm rounded-xl border border-[#EAE6DF] px-3 py-2 bg-white focus:outline-none focus:ring-1 focus:ring-black"
          >
            <option value="ALL">Tất cả trạng thái</option>
            <option value="PENDING_PAYMENT">Chờ thanh toán</option>
            <option value="CONFIRMED">Đã xác nhận</option>
            <option value="PROCESSING">Đang xử lý</option>
            <option value="SHIPPED">Đang giao</option>
            <option value="DELIVERED">Đã giao</option>
            <option value="CANCELLED">Đã hủy</option>
            <option value="RETURNED">Đã trả hàng</option>
          </select>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={(data?.data?.data as any) || []}
        isLoading={isLoading}
        page={page}
        totalPages={data?.data?.totalPages || 1}
        totalItems={data?.data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy đơn hàng nào phù hợp."
      />
    </div>
  );
}
```

# admin\payments\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\payments\page.tsx` |
| Size | 3,588 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-09 10:38:13.134974 |

```tsx
'use client';

import * as React from 'react';
import { useQuery } from '@tanstack/react-query';
import { CreditCard, CheckCircle2, Clock, XCircle, RotateCcw } from 'lucide-react';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { PaymentTransaction, PaymentStatus } from '@/types';

const paymentStatusMeta: Record<PaymentStatus, { label: string; variant: 'warning' | 'success' | 'destructive' | 'outline' }> = {
  PENDING: { label: 'Chờ thanh toán', variant: 'warning' },
  SUCCESS: { label: 'Thành công', variant: 'success' },
  FAILED: { label: 'Thất bại', variant: 'destructive' },
  REFUNDED: { label: 'Đã hoàn tiền', variant: 'outline' },
};

export default function AdminPaymentsPage() {
  const [page, setPage] = React.useState(1);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-payments', page],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.FULFILLMENT.PAYMENTS, {
        params: { page, limit: 10 },
      });
      return res.data;
    },
  });

  const transactions: PaymentTransaction[] = (data?.data as any) || [];

  const columns: Column<PaymentTransaction>[] = [
    {
      header: 'Mã GD Cổng',
      cell: (p) => (
        <span className="font-mono text-xs font-semibold text-neutral-800 bg-neutral-100 px-2.5 py-1 rounded-md">
          {p.providerTransId || `TXN-${p.id}`}
        </span>
      ),
    },
    {
      header: 'Cổng thanh toán',
      cell: (p) => (
        <div className="flex items-center gap-1.5 font-bold text-xs text-neutral-700">
          <CreditCard className="size-3.5 text-neutral-400" />
          <span>{p.provider}</span>
        </div>
      ),
    },
    {
      header: 'Đơn hàng',
      cell: (p) => <span className="font-mono text-xs font-semibold text-neutral-900">{p.order?.orderCode || `Đơn #${p.orderId}`}</span>,
    },
    {
      header: 'Số tiền thanh toán',
      cell: (p) => <span className="font-bold text-xs text-emerald-600">{formatCurrency(p.amount)}</span>,
    },
    {
      header: 'Trạng thái',
      cell: (p) => {
        const meta = paymentStatusMeta[p.status] || { label: p.status, variant: 'warning' as const };
        return <Badge variant={meta.variant} className="text-[10px]">{meta.label}</Badge>;
      },
    },
    {
      header: 'Thời gian giao dịch',
      cell: (p) => <span className="text-xs text-neutral-400">{formatDate(p.createdAt)}</span>,
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <span className="eyebrow block mb-1">TÀI CHÍNH & VẬN HÀNH</span>
        <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Giao dịch Thanh toán</h1>
        <p className="text-xs text-neutral-500 mt-1">Lịch sử thu tiền qua QR Ngân hàng (SePay), Thẻ ATM, Ví điện tử và COD.</p>
      </div>

      <DataTable
        columns={columns}
        data={transactions}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Chưa có giao dịch thanh toán nào phát sinh."
      />
    </div>
  );
}
```

# admin\points\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\points\page.tsx` |
| Size | 10,839 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.958857 |

```tsx
'use client';

import * as React from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  Coins,
  Plus,
  Sparkles,
  CheckCircle2,
  Info,
  Package,
  ShieldCheck,
  User,
  ArrowRight,
} from 'lucide-react';
import { earnPoints } from '@/modules/loyalty-engage/api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';

export default function AdminPointsPage() {
  const queryClient = useQueryClient();
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [lastResult, setLastResult] = React.useState<{
    message: string;
    currentPoint: number;
    transaction: any;
  } | null>(null);

  const { register, handleSubmit, reset } = useForm({
    defaultValues: {
      orderId: '',
      description: 'Cộng điểm thưởng từ đơn hàng hoàn tất',
    },
  });

  const earnMutation = useMutation({
    mutationFn: async (values: { orderId: string; description: string }) => {
      return earnPoints({
        orderId: Number(values.orderId),
        description: values.description.trim() || undefined,
      });
    },
    onSuccess: (res) => {
      toast.success(res.message || 'Cộng điểm thưởng thành công!');
      setLastResult(res);
      setIsDialogOpen(false);
      reset({ orderId: '', description: 'Cộng điểm thưởng từ đơn hàng hoàn tất' });
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cộng điểm thất bại');
    },
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">MARKETING & CSKH</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Chính sách & Điểm thưởng
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Quản trị quy tắc tích lũy điểm và thao tác cộng điểm thưởng thủ công cho đơn hàng.
          </p>
        </div>
        <Button
          onClick={() => setIsDialogOpen(true)}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white flex items-center gap-2"
        >
          <Coins className="size-4 text-[#84EA53]" /> Cộng điểm cho đơn hàng
        </Button>
      </div>

      {/* Highlights & Rules Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        <div className="bg-white p-6 rounded-2xl border border-[#EAE6DF] shadow-xs space-y-3">
          <div className="size-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
            <Coins className="size-5" />
          </div>
          <h3 className="font-semibold text-sm text-neutral-900">Tỷ lệ quy đổi điểm</h3>
          <p className="text-xs text-neutral-500 leading-relaxed">
            <strong className="text-neutral-800">1 Điểm = 1 VNĐ</strong>. Khách hàng có thể sử dụng
            điểm để giảm trực tiếp vào tổng tiền thanh toán ở bước Checkout mà không bị giới hạn số
            lượng tối thiểu.
          </p>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-[#EAE6DF] shadow-xs space-y-3">
          <div className="size-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <Sparkles className="size-5" />
          </div>
          <h3 className="font-semibold text-sm text-neutral-900">Quy tắc tích điểm 1%</h3>
          <p className="text-xs text-neutral-500 leading-relaxed">
            Hệ thống hỗ trợ tự động cộng <strong className="text-neutral-800">1%</strong> giá trị đơn
            hàng khi đơn đã chuyển trạng thái <strong className="text-emerald-700">DELIVERED</strong>.
            Cơ chế khóa giao dịch Atomic Idempotent chống cộng trùng lặp.
          </p>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-[#EAE6DF] shadow-xs space-y-3">
          <div className="size-10 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
            <ShieldCheck className="size-5" />
          </div>
          <h3 className="font-semibold text-sm text-neutral-900">Hoàn trả & Đền bù</h3>
          <p className="text-xs text-neutral-500 leading-relaxed">
            Khi đơn hàng bị hủy hoặc hoàn trả (Return / Refund), số điểm đã dùng sẽ được tự động hoàn
            về ví người dùng, đồng thời điểm thưởng phát sinh từ đơn sẽ được thu hồi.
          </p>
        </div>
      </div>

      {/* Manual Grant Section */}
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-4">
        <div className="flex items-center gap-3">
          <div className="size-9 rounded-xl bg-neutral-100 flex items-center justify-center text-neutral-700">
            <Package className="size-5" />
          </div>
          <div>
            <h3 className="font-semibold text-sm text-neutral-900">Cộng điểm theo đơn hàng</h3>
            <p className="text-xs text-neutral-500">
              Nhập mã đơn hàng đã giao thành công để hệ thống kiểm tra và kích hoạt cộng điểm.
            </p>
          </div>
        </div>

        <form
          onSubmit={handleSubmit((values) => earnMutation.mutate(values))}
          className="max-w-xl space-y-4 pt-2 text-xs"
        >
          <div>
            <label className="font-bold text-neutral-700 uppercase block mb-1">
              Mã đơn hàng (Order ID) *
            </label>
            <input
              type="number"
              {...register('orderId', { required: true, min: 1 })}
              placeholder="Nhập mã đơn, ví dụ: 45"
              className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2.5 text-sm outline-none focus:border-neutral-900 font-mono"
            />
          </div>

          <div>
            <label className="font-bold text-neutral-700 uppercase block mb-1">
              Mô tả giao dịch cộng điểm
            </label>
            <input
              type="text"
              {...register('description')}
              placeholder="VD: Cộng điểm thưởng cho đơn hàng giao thành công"
              className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2 text-xs outline-none focus:border-neutral-900"
            />
          </div>

          <div className="flex items-center gap-2 p-3 bg-neutral-50 rounded-xl border border-neutral-100 text-neutral-600">
            <Info className="size-4 shrink-0 text-neutral-400" />
            <span>
              Lưu ý: Đơn hàng bắt buộc phải ở trạng thái <strong>DELIVERED (Giao thành công)</strong>.
              Hệ thống sẽ lấy ID chủ sở hữu đơn hàng để tự động cộng điểm.
            </span>
          </div>

          <Button
            type="submit"
            disabled={earnMutation.isPending}
            className="rounded-xl bg-[#1D1F20] text-white px-5"
          >
            {earnMutation.isPending ? 'Đang xử lý...' : 'Thực hiện cộng điểm'}
          </Button>
        </form>

        {lastResult && (
          <div className="mt-4 p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs">
            <div className="flex items-center gap-2 font-semibold">
              <CheckCircle2 className="size-4 text-emerald-600" />
              {lastResult.message}
            </div>
            <p className="mt-1 text-emerald-700">
              Số dư điểm mới của khách hàng:{' '}
              <strong className="font-mono">{lastResult.currentPoint.toLocaleString('vi-VN')} điểm</strong>
            </p>
          </div>
        )}
      </div>

      {/* Modal Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Coins className="size-5 text-amber-500" />
              Cộng điểm đơn hàng
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => earnMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Mã đơn hàng (Order ID) *
              </label>
              <input
                type="number"
                {...register('orderId', { required: true, min: 1 })}
                placeholder="VD: 12"
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900 font-mono"
              />
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Mô tả (Tùy chọn)
              </label>
              <input
                type="text"
                {...register('description')}
                placeholder="Cộng điểm thưởng đơn hàng"
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
              />
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsDialogOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={earnMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {earnMutation.isPending ? 'Đang thực hiện...' : 'Cộng điểm ngay'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

```

# admin\products\[productId]\edit\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\products\[productId]\edit\page.tsx` |
| Size | 18,198 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 22:28:02.026789 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  ArrowLeft,
  Loader2,
  Save,
  Layers,
  Tag as TagIcon,
  X,
  Search,
  ChevronDown,
} from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import type { Tag } from '@/types';

interface EditProductFormValues {
  defaultName: string;
  categoryId: number | string;
  brandId: number | string;
  thumbnailUrl: string;
  status: 'DRAFT' | 'PUBLISHED' | 'OUT_OF_STOCK' | 'ARCHIVED';
  tagIds: number[];
}

export default function AdminEditProductPage() {
  const params = useParams<{ productId: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();
  const productId = Number(params.productId);

  const [isDropdownOpen, setIsDropdownOpen] = React.useState(false);
  const [tagSearch, setTagSearch] = React.useState('');
  const dropdownRef = React.useRef<HTMLDivElement>(null);

  // 1. Chi tiết sản phẩm
  const { data: prodRes, isLoading: isLoadingProd } = useQuery({
    queryKey: ['admin-product-edit', productId],
    queryFn: () => catalogApi.getProductById(productId),
    enabled: !!productId,
  });

  // 2. Danh mục
  const { data: catRes } = useQuery({
    queryKey: ['admin-categories-select'],
    queryFn: () => catalogApi.getCategories(),
  });

  // 3. Thương hiệu
  const { data: brandRes } = useQuery({
    queryKey: ['admin-brands-select'],
    queryFn: () => catalogApi.getBrands({ limit: 100 }),
  });

  // 4. Tags
  const { data: tagRes } = useQuery({
    queryKey: ['admin-tags-select'],
    queryFn: () => catalogApi.getTags(),
  });

  const product = prodRes?.data as any;
  const categories = (catRes?.data as any) || [];
  const brands = (brandRes?.data as any) || [];
  const allTags: Tag[] = (tagRes?.data as any) || [];

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<EditProductFormValues>({
    defaultValues: {
      defaultName: '',
      categoryId: '',
      brandId: '',
      thumbnailUrl: '',
      status: 'PUBLISHED',
      tagIds: [],
    },
  });

  const selectedTagIds = watch('tagIds') || [];

  // =========================================================================
  // BỘ NẠP DỮ LIỆU ĐA TƯƠNG THÍCH (CHO CẢ NODE.JS LẪN JAVA SERVLET)
  // =========================================================================
  React.useEffect(() => {
    if (!product) return;

    // 1. Chuẩn hóa CategoryId:
    // - Node.js: product.categoryId
    // - Java: product.category?.id hoặc product.category (nếu là số)
    const rawCatId =
      product.categoryId ??
      product.category?.id ??
      (typeof product.category === 'number' ? product.category : null);
    const resolvedCategoryId = rawCatId !== null && rawCatId !== undefined ? Number(rawCatId) : '';

    // 2. Chuẩn hóa BrandId:
    // - Node.js: product.brandId
    // - Java: product.brand?.id hoặc product.brand (nếu là số)
    const rawBrandId =
      product.brandId ??
      product.brand?.id ??
      (typeof product.brand === 'number' ? product.brand : null);
    const resolvedBrandId = rawBrandId !== null && rawBrandId !== undefined ? Number(rawBrandId) : '';

    // 3. Chuẩn hóa TagIds:
    // - Hỗ trợ cả: [{ id: 1 }], [{ tag: { id: 1 } }], [1, 2], [{ tagId: 1 }]
    let resolvedTagIds: number[] = [];
    if (Array.isArray(product.tags)) {
      resolvedTagIds = product.tags
        .map((t: any) => {
          if (typeof t === 'number') return t;
          return t?.id ?? t?.tag?.id ?? t?.tagId ?? null;
        })
        .filter((id: any) => typeof id === 'number' && !isNaN(id));
    } else if (Array.isArray(product.tagIds)) {
      resolvedTagIds = product.tagIds.map(Number).filter((id: number) => !isNaN(id));
    }

    reset({
      defaultName: product.defaultName || '',
      categoryId: resolvedCategoryId,
      brandId: resolvedBrandId,
      thumbnailUrl: product.thumbnailUrl || '',
      status: product.status || 'PUBLISHED',
      tagIds: resolvedTagIds,
    });
  }, [product, categories.length, brands.length, reset]);

  // Click outside đóng Dropdown Tag
  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Lọc tag hiển thị
  const selectedTags = React.useMemo(() => {
    return allTags.filter((t) => selectedTagIds.includes(t.id));
  }, [allTags, selectedTagIds]);

  const filteredAvailableTags = React.useMemo(() => {
    return allTags
      .filter((t) => !selectedTagIds.includes(t.id))
      .filter((t) => t.name.toLowerCase().includes(tagSearch.toLowerCase().trim()));
  }, [allTags, selectedTagIds, tagSearch]);

  const addTag = (id: number) => {
    setValue('tagIds', [...selectedTagIds, id], { shouldDirty: true });
    setTagSearch('');
  };

  const removeTag = (id: number) => {
    setValue(
      'tagIds',
      selectedTagIds.filter((tId) => tId !== id),
      { shouldDirty: true }
    );
  };

  // =========================================================================
  // GỬI DỮ LIỆU ĐA TƯƠNG THÍCH (AN TOÀN CHO CẢ 2 SERVER)
  // =========================================================================
  const updateMutation = useMutation({
    mutationFn: (values: EditProductFormValues) => {
      // Ép kiểu chuẩn:
      // - Java không nhận "" cho kiểu Integer -> chuyển thành null
      // - Node.js Prisma nhận null để disconnect quan hệ
      const payload = {
        defaultName: values.defaultName.trim(),
        categoryId: Number(values.categoryId),
        brandId: values.brandId !== '' && values.brandId !== undefined ? Number(values.brandId) : null,
        thumbnailUrl: values.thumbnailUrl?.trim() || null,
        status: values.status,
        tagIds: values.tagIds || [],
      };

      return catalogApi.updateProduct(productId, payload as any);
    },
    onSuccess: () => {
      toast.success('Cập nhật thông tin sản phẩm thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-product-edit', productId] });
      queryClient.invalidateQueries({ queryKey: ['admin-products'] });
      router.push('/admin/products');
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật sản phẩm thất bại');
    },
  });

  if (isLoadingProd) {
    return (
      <div className="flex justify-center py-20">
        <Loader2 className="size-8 animate-spin text-neutral-400" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-4">
        <div className="flex items-center gap-4">
          <Link
            href="/admin/products"
            className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition"
          >
            <ArrowLeft className="size-4 text-neutral-600" />
          </Link>
          <div>
            <span className="eyebrow block mb-0.5">CATALOG</span>
            <h1 className="font-display text-2xl text-[#1D1F20] tracking-tight">
              Chỉnh sửa sản phẩm #{productId}
            </h1>
          </div>
        </div>

        <Link href={`/admin/products/${productId}/variants`}>
          <Button variant="outline" className="rounded-xl border-[#EAE6DF] text-xs">
            <Layers className="size-4 mr-1.5 text-emerald-600" /> Quản lý SKU Biến thể
          </Button>
        </Link>
      </div>

      <form onSubmit={handleSubmit((d) => updateMutation.mutate(d))} className="space-y-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-5">
          {/* Tên sản phẩm */}
          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Tên sản phẩm *
            </label>
            <input
              {...register('defaultName', { required: 'Tên sản phẩm là bắt buộc' })}
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
            {errors.defaultName && (
              <p className="text-xs text-rose-500 mt-1">{errors.defaultName.message}</p>
            )}
          </div>

          {/* Danh mục & Thương hiệu */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Danh mục *
              </label>
              <select
                {...register('categoryId', { required: 'Vui lòng chọn danh mục' })}
                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              >
                <option value="">-- Chọn danh mục --</option>
                {categories.map((c: any) => (
                  <option key={c.id} value={c.id}>
                    {c.defaultName}
                  </option>
                ))}
              </select>
              {errors.categoryId && (
                <p className="text-xs text-rose-500 mt-1">{errors.categoryId.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Thương hiệu
              </label>
              <select
                {...register('brandId')}
                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              >
                <option value="">-- Không thương hiệu --</option>
                {brands.map((b: any) => (
                  <option key={b.id} value={b.id}>
                    {b.defaultName}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Quản lý Thẻ Tag (Dropdown chọn tự động nhảy lên hàng trên) */}
          <div className="space-y-2.5 pt-1 border-t border-[#EAE6DF]" ref={dropdownRef}>
            <div className="flex items-center justify-between">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600 flex items-center gap-1.5">
                <TagIcon className="size-3.5 text-neutral-500" />
                Thẻ Tag sản phẩm (Tags)
              </label>
              {selectedTagIds.length > 0 && (
                <button
                  type="button"
                  onClick={() => setValue('tagIds', [], { shouldDirty: true })}
                  className="text-[11px] font-semibold text-rose-500 hover:text-rose-700 transition"
                >
                  Bỏ chọn tất cả ({selectedTagIds.length})
                </button>
              )}
            </div>

            {/* Hàng chứa các tag đã chọn */}
            <div className="flex flex-wrap gap-1.5 min-h-8 rounded-xl border border-dashed border-[#EAE6DF] bg-neutral-50/50 p-2">
              {selectedTags.length === 0 ? (
                <span className="text-xs text-neutral-400 italic px-1.5 py-0.5">
                  Chưa chọn thẻ tag nào cho sản phẩm...
                </span>
              ) : (
                selectedTags.map((tag) => (
                  <span
                    key={tag.id}
                    className="inline-flex items-center gap-1 bg-[#1D1F20] text-white text-[11px] font-semibold pl-2.5 pr-1.5 py-1 rounded-lg animate-in zoom-in-95 duration-150"
                  >
                    <span>{tag.name}</span>
                    <button
                      type="button"
                      onClick={() => removeTag(tag.id)}
                      className="p-0.5 hover:bg-white/20 rounded text-[#84EA53] transition cursor-pointer"
                    >
                      <X className="size-3" />
                    </button>
                  </span>
                ))
              )}
            </div>

            {/* Ô tìm kiếm & Dropdown chọn tag */}
            <div className="relative">
              <div
                onClick={() => setIsDropdownOpen(true)}
                className={cn(
                  'flex items-center gap-2 w-full rounded-xl border bg-white pl-3.5 pr-4 py-2 text-sm shadow-xs cursor-pointer transition',
                  isDropdownOpen ? 'border-neutral-900 ring-1 ring-neutral-900' : 'border-[#EAE6DF]'
                )}
              >
                <Search className="size-4 text-neutral-400 shrink-0" />
                <input
                  type="text"
                  placeholder="Tìm kiếm hoặc Chọn thẻ tag..."
                  value={tagSearch}
                  onChange={(e) => {
                    setTagSearch(e.target.value);
                    setIsDropdownOpen(true);
                  }}
                  onFocus={() => setIsDropdownOpen(true)}
                  className="w-full bg-transparent outline-none text-xs text-neutral-900 font-medium placeholder:text-neutral-400"
                />
                <ChevronDown
                  className={cn(
                    'size-4 text-neutral-400 transition-transform duration-200 shrink-0',
                    isDropdownOpen && 'rotate-180'
                  )}
                />
              </div>

              {isDropdownOpen && (
                <div className="absolute left-0 mt-1.5 w-full max-h-60 overflow-y-auto rounded-xl border border-[#EAE6DF] bg-white shadow-lg p-1.5 z-40 animate-in fade-in slide-in-from-top-1 duration-150">
                  {filteredAvailableTags.length === 0 ? (
                    <div className="px-3.5 py-2.5 text-xs text-neutral-400 italic">
                      {tagSearch.trim() ? 'Không tìm thấy tag phù hợp' : 'Tất cả các thẻ tag đã được thêm'}
                    </div>
                  ) : (
                    filteredAvailableTags.map((tag) => (
                      <button
                        key={tag.id}
                        type="button"
                        onClick={() => addTag(tag.id)}
                        className="flex items-center justify-between w-full px-3 py-2 text-xs font-semibold text-neutral-700 hover:text-neutral-900 hover:bg-neutral-50 rounded-lg transition text-left cursor-pointer"
                      >
                        <span>{tag.name}</span>
                        <span className="text-[10px] text-neutral-400 font-mono font-normal">
                          (id: {tag.id})
                        </span>
                      </button>
                    ))
                  )}
                </div>
              )}
            </div>
          </div>

          {/* Thumbnail */}
          <div className="space-y-1.5 pt-1 border-t border-[#EAE6DF]">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Ảnh đại diện Thumbnail (URL)
            </label>
            <input
              {...register('thumbnailUrl')}
              placeholder="https://images.unsplash.com/... hoặc đường dẫn ảnh"
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
          </div>

          {/* Trạng thái */}
          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Trạng thái sản phẩm
            </label>
            <select
              {...register('status')}
              className="w-full sm:w-60 rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            >
              <option value="PUBLISHED">Công khai (PUBLISHED)</option>
              <option value="DRAFT">Lưu nháp (DRAFT)</option>
              <option value="OUT_OF_STOCK">Hết hàng (OUT_OF_STOCK)</option>
              <option value="ARCHIVED">Lưu trữ (ARCHIVED)</option>
            </select>
          </div>
        </div>

        <div className="flex justify-end gap-3">
          <Link href="/admin/products">
            <Button type="button" variant="outline" className="rounded-xl border-[#EAE6DF]">
              Hủy
            </Button>
          </Link>
          <Button
            type="submit"
            disabled={updateMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800"
          >
            {updateMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-1.5" /> Đang cập nhật...
              </>
            ) : (
              <>
                <Save className="size-4 mr-1.5" /> Lưu thay đổi
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}
```

# admin\products\[productId]\variants\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\products\[productId]\variants\page.tsx` |
| Size | 9,750 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.470050 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { ArrowLeft, Plus, Trash2, Layers, Loader2, Save } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { formatCurrency } from '@/lib/utils/format-currency';

interface VariantRow {
  id?: number | null;
  sku: string;
  variant: string;
  variantValue: string;
  originalPrice: number;
  sellingPrice: number;
  stockQuantity: number;
  weightGram: number;
}

export default function AdminProductVariantsPage() {
  const params = useParams<{ productId: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();
  const productId = Number(params.productId);

  const { data: prodRes, isLoading } = useQuery({
    queryKey: ['admin-product-detail-variants', productId],
    queryFn: () => catalogApi.getProductById(productId),
    enabled: !!productId,
  });

  const product = prodRes?.data;
  const [variants, setVariants] = React.useState<VariantRow[]>([]);

  React.useEffect(() => {
    if (product?.variants && product.variants.length > 0) {
      setVariants(
        product.variants.map((v) => ({
          id: v.id,
          sku: v.sku,
          variant: v.variant || 'Màu sắc - Kích cỡ',
          variantValue: v.variantValue || 'Mặc định',
          originalPrice: Number(v.originalPrice || 0),
          sellingPrice: Number(v.sellingPrice || 0),
          stockQuantity: Number(v.stockQuantity || 0),
          weightGram: Number(v.weightGram || 200),
        }))
      );
    } else if (product) {
      setVariants([
        {
          id: null,
          sku: `SKU-${productId}-01`,
          variant: 'Kích cỡ',
          variantValue: 'Tiêu chuẩn',
          originalPrice: 200000,
          sellingPrice: 180000,
          stockQuantity: 50,
          weightGram: 200,
        },
      ]);
    }
  }, [product, productId]);

  const syncMutation = useMutation({
    mutationFn: (rows: VariantRow[]) => catalogApi.syncVariants({
      productId,
      variants: rows.map(r => ({
        ...r,
        id: r.id || undefined,
      })),
    }),
    onSuccess: () => {
      toast.success('Đồng bộ ma trận biến thể thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-product-detail-variants', productId] });
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Lỗi khi lưu biến thể');
    },
  });

  const handleAddRow = () => {
    setVariants((prev) => [
      ...prev,
      {
        id: null,
        sku: `SKU-${productId}-0${prev.length + 1}`,
        variant: 'Màu sắc - Kích cỡ',
        variantValue: 'Đen - L',
        originalPrice: 200000,
        sellingPrice: 180000,
        stockQuantity: 10,
        weightGram: 200,
      },
    ]);
  };

  const handleRemoveRow = (idx: number) => {
    if (variants.length <= 1) {
      toast.error('Sản phẩm phải có ít nhất một biến thể (SKU)');
      return;
    }
    setVariants((prev) => prev.filter((_, i) => i !== idx));
  };

  const handleFieldChange = (idx: number, field: keyof VariantRow, value: any) => {
    setVariants((prev) => {
      const updated = [...prev];
      updated[idx] = { ...updated[idx], [field]: value };
      return updated;
    });
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Loader2 className="size-8 animate-spin text-neutral-400" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#EAE6DF] pb-4">
        <div className="flex items-center gap-4">
          <Link href="/admin/products" className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition">
            <ArrowLeft className="size-4 text-neutral-600" />
          </Link>
          <div>
            <span className="eyebrow block mb-0.5">QUẢN LÝ BIẾN THỂ SKU</span>
            <h1 className="text-xl sm:text-2xl font-bold text-neutral-900 truncate max-w-lg">
              {product?.defaultName}
            </h1>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Button onClick={handleAddRow} variant="outline" className="rounded-xl border-[#EAE6DF] text-xs">
            <Plus className="size-4 mr-1.5" /> Thêm SKU
          </Button>
          <Button
            onClick={() => syncMutation.mutate(variants)}
            disabled={syncMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800 text-xs"
          >
            {syncMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-1.5" /> Đang lưu...
              </>
            ) : (
              <>
                <Save className="size-4 mr-1.5" /> Lưu danh sách biến thể
              </>
            )}
          </Button>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-[#EAE6DF] shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 border-b border-[#EAE6DF] text-[11px] font-bold uppercase tracking-wider text-neutral-500">
              <tr>
                <th className="px-4 py-3.5">Mã SKU *</th>
                <th className="px-4 py-3.5">Nhóm thuộc tính</th>
                <th className="px-4 py-3.5">Giá trị phân loại *</th>
                <th className="px-4 py-3.5">Giá gốc (₫)</th>
                <th className="px-4 py-3.5">Giá bán (₫) *</th>
                <th className="px-4 py-3.5">Tồn kho *</th>
                <th className="px-4 py-3.5 text-right">Xóa</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#EAE6DF]">
              {variants.map((row, idx) => (
                <tr key={idx} className="hover:bg-neutral-50/50">
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={row.sku}
                      onChange={(e) => handleFieldChange(idx, 'sku', e.target.value)}
                      className="w-32 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 font-mono text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={row.variant}
                      placeholder="VD: Màu sắc"
                      onChange={(e) => handleFieldChange(idx, 'variant', e.target.value)}
                      className="w-32 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={row.variantValue}
                      placeholder="VD: Trắng - M"
                      onChange={(e) => handleFieldChange(idx, 'variantValue', e.target.value)}
                      className="w-32 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 font-semibold text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      value={row.originalPrice}
                      onChange={(e) => handleFieldChange(idx, 'originalPrice', Number(e.target.value))}
                      className="w-24 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      value={row.sellingPrice}
                      onChange={(e) => handleFieldChange(idx, 'sellingPrice', Number(e.target.value))}
                      className="w-24 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 font-bold text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      value={row.stockQuantity}
                      onChange={(e) => handleFieldChange(idx, 'stockQuantity', Number(e.target.value))}
                      className="w-20 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 text-xs focus:border-neutral-900 outline-none font-mono"
                    />
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => handleRemoveRow(idx)}
                      className="p-1.5 text-neutral-400 hover:text-rose-600 rounded-lg hover:bg-rose-50 transition"
                    >
                      <Trash2 className="size-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
```

# admin\products\new\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\products\new\page.tsx` |
| Size | 7,272 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.473051 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { ArrowLeft, Loader2, Plus, Sparkles, Upload } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';

interface CreateProductFormValues {
  defaultName: string;
  categoryId: number;
  brandId?: number;
  thumbnailUrl?: string;
  status: 'DRAFT' | 'PUBLISHED';
}

export default function AdminNewProductPage() {
  const router = useRouter();

  const { data: catRes } = useQuery({
    queryKey: ['admin-categories-select'],
    queryFn: () => catalogApi.getCategories(),
  });

  const { data: brandRes } = useQuery({
    queryKey: ['admin-brands-select'],
    queryFn: () => catalogApi.getBrands({ limit: 100 }),
  });

  const categories = (catRes?.data as any) || [];
  const brands = (brandRes?.data as any) || [];

  const { register, handleSubmit, formState: { errors } } = useForm<CreateProductFormValues>({
    defaultValues: {
      defaultName: '',
      status: 'PUBLISHED',
      thumbnailUrl: '',
    },
  });

  const createMutation = useMutation({
    mutationFn: (values: CreateProductFormValues) => catalogApi.createProduct({
      ...values,
      categoryId: Number(values.categoryId),
      brandId: values.brandId ? Number(values.brandId) : undefined,
    }),
    onSuccess: (res: any) => {
      const createdId = res?.data?.id || res?.id;
      toast.success('Khởi tạo sản phẩm thành công! Hãy tiếp tục thiết lập các biến thể SKU.');
      if (createdId) {
        router.push(`/admin/products/${createdId}/variants`);
      } else {
        router.push('/admin/products');
      }
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Không thể tạo sản phẩm mới');
    },
  });

  const onSubmit = (data: CreateProductFormValues) => {
    createMutation.mutate(data);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-4 border-b border-[#EAE6DF] pb-4">
        <Link href="/admin/products" className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition">
          <ArrowLeft className="size-4 text-neutral-600" />
        </Link>
        <div>
          <span className="eyebrow block mb-1">CATALOG</span>
          <h1 className="font-display text-3xl text-[#1D1F20] tracking-tight">Thêm sản phẩm mới</h1>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs space-y-5">
          <h2 className="text-sm font-bold text-neutral-900 border-b border-[#EAE6DF] pb-3">
            1. Thông tin chung
          </h2>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Tên sản phẩm *
            </label>
            <input
              {...register('defaultName', { required: 'Tên sản phẩm là bắt buộc' })}
              placeholder="VD: Áo Thun Oversize Minimalist Cotton 100%"
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
            {errors.defaultName && <p className="text-xs text-rose-500 mt-1">{errors.defaultName.message}</p>}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Danh mục *
              </label>
              <select
                {...register('categoryId', { required: 'Vui lòng chọn danh mục' })}
                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              >
                <option value="">-- Chọn danh mục --</option>
                {categories.map((c: any) => (
                  <option key={c.id} value={c.id}>{c.defaultName}</option>
                ))}
              </select>
              {errors.categoryId && <p className="text-xs text-rose-500 mt-1">{errors.categoryId.message}</p>}
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                Thương hiệu (Tùy chọn)
              </label>
              <select
                {...register('brandId')}
                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
              >
                <option value="">-- Không thương hiệu --</option>
                {brands.map((b: any) => (
                  <option key={b.id} value={b.id}>{b.defaultName}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Ảnh đại diện Thumbnail (URL)
            </label>
            <input
              {...register('thumbnailUrl')}
              placeholder="https://images.unsplash.com/... hoặc đường dẫn ảnh"
              className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
              Trạng thái mở bán
            </label>
            <select
              {...register('status')}
              className="w-full sm:w-60 rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm outline-none focus:border-neutral-900 shadow-xs"
            >
              <option value="PUBLISHED">Công khai (PUBLISHED)</option>
              <option value="DRAFT">Lưu nháp (DRAFT)</option>
            </select>
          </div>
        </div>

        <div className="flex justify-end gap-3">
          <Link href="/admin/products">
            <Button type="button" variant="outline" className="rounded-xl border-[#EAE6DF]">Hủy</Button>
          </Link>
          <Button
            type="submit"
            disabled={createMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800"
          >
            {createMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-2" /> Đang tạo...
              </>
            ) : (
              'Lưu & Thiết lập Biến thể SKU →'
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}
```

# admin\products\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\products\page.tsx` |
| Size | 6,897 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.475049 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { Plus, Edit3, Trash2, Layers, Search, EyeOff, CheckCircle2 } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { formatCurrency } from '@/lib/utils/format-currency';
import type { Product } from '@/types';

export default function AdminProductsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [deleteTargetId, setDeleteTargetId] = React.useState<number | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-products', page, search],
    queryFn: () => catalogApi.getProducts({ page, limit: 10, search: search || undefined }),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => catalogApi.deleteProduct(id),
    onSuccess: () => {
      toast.success('Xóa sản phẩm thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-products'] });
      setDeleteTargetId(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Không thể xóa sản phẩm này');
    }
  });

  const columns: Column<Product>[] = [
    {
      header: 'Sản phẩm',
      cell: (product) => (
        <div className="flex items-center gap-3">
          {product.thumbnailUrl ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img src={product.thumbnailUrl} alt={product.defaultName} className="size-12 rounded-lg object-cover border border-[#EAE6DF]" />
          ) : (
            <div className="size-12 rounded-lg bg-neutral-100 flex items-center justify-center text-[10px] text-neutral-400">IMG</div>
          )}
          <div className="min-w-0 max-w-[250px]">
            <p className="font-semibold text-sm text-neutral-900 truncate" title={product.defaultName}>{product.defaultName}</p>
            <p className="text-[11px] text-neutral-500 mt-1">{product.brand?.defaultName || 'No Brand'}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Danh mục',
      cell: (product) => <span className="text-xs text-neutral-600">{product.category?.defaultName || '---'}</span>,
    },
    {
      header: 'Giá bán cơ bản',
      cell: (product) => {
        const basePrice = product.variants?.[0]?.sellingPrice || product.variants?.[0]?.originalPrice || 0;
        return <span className="font-bold text-[#1D1F20] text-xs">{formatCurrency(basePrice as number)}</span>;
      },
    },
    {
      header: 'Kho',
      cell: (product) => {
        const totalStock = product.variants?.reduce((acc, v) => acc + v.stockQuantity, 0) || 0;
        return <span className="text-xs font-mono font-medium">{totalStock}</span>;
      },
    },
    {
      header: 'Trạng thái',
      cell: (product) => (
        <Badge variant={product.status === 'PUBLISHED' ? 'success' : 'secondary'} className="text-[10px]">
          {product.status === 'PUBLISHED' ? <CheckCircle2 className="size-3 mr-1" /> : <EyeOff className="size-3 mr-1" />}
          {product.status}
        </Badge>
      ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (product) => (
        <div className="flex justify-end gap-1">
          <Link href={`/admin/products/${product.id}/variants`}>
            <button className="p-1.5 text-neutral-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition" title="Quản lý biến thể (Variants)">
              <Layers className="size-4" />
            </button>
          </Link>
          <Link href={`/admin/products/${product.id}/edit`}>
            <button className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition" title="Chỉnh sửa sản phẩm">
              <Edit3 className="size-4" />
            </button>
          </Link>
          <button 
            onClick={() => setDeleteTargetId(product.id)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition" 
            title="Xóa sản phẩm"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">CATALOG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">Danh sách Sản phẩm</h1>
          <p className="text-xs text-neutral-500 mt-1">Quản lý mặt hàng, tồn kho và các biến thể phân loại (SKU).</p>
        </div>
        <Link href="/admin/products/new">
          <Button className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2 shrink-0">
            <Plus className="size-4" />
            <span>Thêm sản phẩm mới</span>
          </Button>
        </Link>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex justify-between items-center">
        <SearchInput 
          value={search} 
          onChange={(val) => { setSearch(val); setPage(1); }} 
          placeholder="Tìm theo tên sản phẩm hoặc mã SKU..." 
          className="w-full max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={(data?.data as any) || []}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không có sản phẩm nào trong kho."
      />

      <ConfirmDialog
        open={deleteTargetId !== null}
        onOpenChange={(open) => !open && setDeleteTargetId(null)}
        title="Xóa sản phẩm"
        description="Bạn có chắc chắn muốn xóa sản phẩm này? Mọi biến thể (Variants) và tồn kho liên quan cũng sẽ bị xóa vĩnh viễn."
        variant="destructive"
        confirmText="Xóa vĩnh viễn"
        isLoading={deleteMutation.isPending}
        onConfirm={() => {
          if (deleteTargetId) deleteMutation.mutate(deleteTargetId);
        }}
      />
    </div>
  );
}
```

# admin\returns\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\returns\page.tsx` |
| Size | 18,342 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.960858 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import {
  RotateCcw,
  CheckCircle2,
  XCircle,
  Clock,
  Eye,
  Package,
  AlertCircle,
  FileText,
  Filter,
} from 'lucide-react';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { formatDate } from '@/lib/utils/format-date';
import type { ReturnRequest, ReturnStatus } from '@/types';

const statusMeta: Record<
  ReturnStatus,
  { label: string; variant: 'warning' | 'success' | 'destructive' | 'secondary' }
> = {
  PENDING: { label: 'Chờ duyệt', variant: 'warning' },
  APPROVED: { label: 'Đã duyệt', variant: 'success' },
  REJECTED: { label: 'Từ chối', variant: 'destructive' },
  COMPLETED: { label: 'Hoàn tất', variant: 'secondary' },
};

export default function AdminReturnsPage() {
  const queryClient = useQueryClient();
  const [selectedStatus, setSelectedStatus] = React.useState<ReturnStatus | 'ALL'>('ALL');
  const [search, setSearch] = React.useState('');
  const [page, setPage] = React.useState(1);
  const [viewingReturn, setViewingReturn] = React.useState<ReturnRequest | null>(null);
  const [rejectTarget, setRejectTarget] = React.useState<ReturnRequest | null>(null);

  // Fetch returns
  const { data, isLoading } = useQuery({
    queryKey: ['admin-returns', selectedStatus],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.FULFILLMENT.RETURNS, {
        params: {
          status: selectedStatus === 'ALL' ? undefined : selectedStatus,
        },
      });
      return res.data;
    },
  });

  // Mutation to update return status
  const updateStatusMutation = useMutation({
    mutationFn: async ({ returnId, status }: { returnId: number; status: ReturnStatus }) => {
      const res = await apiClient.put(
        `${API_ENDPOINTS.FULFILLMENT.RETURNS}/${returnId}`,
        { status }
      );
      return res.data;
    },
    onSuccess: (_, variables) => {
      const label = statusMeta[variables.status]?.label || variables.status;
      toast.success(`Đã cập nhật yêu cầu sang: "${label}"!`);
      queryClient.invalidateQueries({ queryKey: ['admin-returns'] });
      setViewingReturn(null);
      setRejectTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật trạng thái thất bại');
    },
  });

  const rawReturns: ReturnRequest[] = (data?.data as any) || [];

  // Filter returns by search query client-side if needed
  const filteredReturns = React.useMemo(() => {
    if (!search.trim()) return rawReturns;
    const q = search.toLowerCase();
    return rawReturns.filter(
      (r) =>
        r.id.toString().includes(q) ||
        r.orderItem?.orderId?.toString().includes(q) ||
        r.orderItem?.productName?.toLowerCase().includes(q) ||
        r.reason?.toLowerCase().includes(q) ||
        r.description?.toLowerCase().includes(q)
    );
  }, [rawReturns, search]);

  const columns: Column<ReturnRequest>[] = [
    {
      header: 'Mã yêu cầu',
      className: 'w-24',
      cell: (r) => (
        <span className="font-mono text-xs font-bold text-neutral-900 bg-neutral-100 px-2 py-0.5 rounded">
          #{r.id}
        </span>
      ),
    },
    {
      header: 'Sản phẩm hoàn trả',
      cell: (r) => (
        <div className="max-w-xs">
          <div className="flex items-center gap-1.5">
            <span className="font-mono text-[11px] font-semibold text-neutral-500">
              Đơn #{r.orderItem?.orderId || 'N/A'}
            </span>
          </div>
          <p className="font-semibold text-xs text-neutral-900 line-clamp-1 mt-0.5">
            {r.orderItem?.productName || `Item #${r.orderItemId}`}
          </p>
          {r.orderItem?.variantValue && (
            <p className="text-[11px] text-neutral-500">
              Phân loại: <span className="font-medium text-neutral-700">{r.orderItem.variantValue}</span>
            </p>
          )}
        </div>
      ),
    },
    {
      header: 'Số lượng',
      className: 'text-center w-20',
      cell: (r) => (
        <span className="font-semibold text-xs text-neutral-800">
          {r.quantity} {r.orderItem ? `/ ${r.orderItem.quantity}` : ''}
        </span>
      ),
    },
    {
      header: 'Lý do đổi trả',
      cell: (r) => (
        <div className="max-w-sm">
          <p className="text-xs font-semibold text-neutral-800 line-clamp-1">{r.reason}</p>
          {r.description && (
            <p className="text-[11px] text-neutral-500 line-clamp-1 mt-0.5">{r.description}</p>
          )}
          {r.medias && r.medias.length > 0 && (
            <div className="flex gap-1.5 mt-1.5">
              {r.medias.map((m, idx) => {
                const isVideo =
                  m.mediaType?.includes('video') ||
                  m.mediaUrl.endsWith('.mp4') ||
                  m.mediaUrl.includes('video');
                return isVideo ? (
                  <span
                    key={m.id || idx}
                    className="text-[10px] bg-neutral-100 text-neutral-600 px-1.5 py-0.5 rounded border border-neutral-200"
                  >
                    Video ({idx + 1})
                  </span>
                ) : (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img
                    key={m.id || idx}
                    src={m.mediaUrl}
                    alt="Chứng từ đổi trả"
                    className="size-8 rounded object-cover border border-neutral-200 cursor-pointer hover:opacity-80"
                    onClick={() => setViewingReturn(r)}
                  />
                );
              })}
            </div>
          )}
        </div>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (r) => {
        const meta = statusMeta[r.status] || { label: r.status, variant: 'secondary' as const };
        return <Badge variant={meta.variant} className="text-[10px]">{meta.label}</Badge>;
      },
    },
    {
      header: 'Ngày yêu cầu',
      cell: (r) => <span className="text-xs text-neutral-400">{formatDate(r.createdAt)}</span>,
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (r) => (
        <div className="flex justify-end items-center gap-1.5">
          <button
            type="button"
            onClick={() => setViewingReturn(r)}
            className="p-1.5 text-neutral-500 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Xem chi tiết"
          >
            <Eye className="size-4" />
          </button>

          {r.status === 'PENDING' && (
            <>
              <Button
                size="sm"
                variant="outline"
                onClick={() => updateStatusMutation.mutate({ returnId: r.id, status: 'APPROVED' })}
                disabled={updateStatusMutation.isPending}
                className="h-8 px-2.5 text-xs text-emerald-700 hover:text-emerald-800 hover:bg-emerald-50 border-emerald-200 rounded-lg"
              >
                <CheckCircle2 className="size-3.5 mr-1" /> Duyệt
              </Button>
              <Button
                size="sm"
                variant="outline"
                onClick={() => setRejectTarget(r)}
                disabled={updateStatusMutation.isPending}
                className="h-8 px-2.5 text-xs text-rose-700 hover:text-rose-800 hover:bg-rose-50 border-rose-200 rounded-lg"
              >
                <XCircle className="size-3.5 mr-1" /> Từ chối
              </Button>
            </>
          )}

          {r.status === 'APPROVED' && (
            <Button
              size="sm"
              variant="outline"
              onClick={() => updateStatusMutation.mutate({ returnId: r.id, status: 'COMPLETED' })}
              disabled={updateStatusMutation.isPending}
              className="h-8 px-2.5 text-xs text-neutral-700 hover:bg-neutral-100 border-neutral-300 rounded-lg"
            >
              Hoàn tất
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">GIAO VẬN & ĐỔI TRẢ</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Yêu cầu Đổi trả hàng
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Xem xét và phê duyệt hoặc từ chối các yêu cầu hoàn trả sản phẩm từ khách hàng.
          </p>
        </div>
      </div>

      {/* Filter Tabs & Search */}
      <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <div className="flex items-center gap-1 overflow-x-auto pb-1 md:pb-0">
          {(
            [
              { key: 'ALL', label: 'Tất cả' },
              { key: 'PENDING', label: 'Chờ duyệt' },
              { key: 'APPROVED', label: 'Đã duyệt' },
              { key: 'REJECTED', label: 'Từ chối' },
              { key: 'COMPLETED', label: 'Hoàn tất' },
            ] as const
          ).map((tab) => (
            <button
              key={tab.key}
              type="button"
              onClick={() => {
                setSelectedStatus(tab.key);
                setPage(1);
              }}
              className={`px-3 py-1.5 text-xs font-medium rounded-xl transition shrink-0 ${
                selectedStatus === tab.key
                  ? 'bg-[#1D1F20] text-white shadow-xs'
                  : 'text-neutral-600 hover:text-neutral-900 hover:bg-neutral-100'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo mã đơn, mã yêu cầu, lý do..."
          className="max-w-md w-full"
        />
      </div>

      <DataTable
        columns={columns}
        data={filteredReturns}
        isLoading={isLoading}
        page={page}
        totalPages={1}
        totalItems={filteredReturns.length}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy yêu cầu đổi trả nào."
      />

      {/* Detail Dialog */}
      <Dialog open={!!viewingReturn} onOpenChange={(open) => !open && setViewingReturn(null)}>
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <RotateCcw className="size-5 text-neutral-700" />
              Chi tiết Yêu cầu Đổi trả #{viewingReturn?.id}
            </DialogTitle>
          </DialogHeader>

          {viewingReturn && (
            <div className="space-y-4 my-2 text-xs text-neutral-700">
              <div className="grid grid-cols-2 gap-3 bg-neutral-50 p-3 rounded-xl border border-neutral-100">
                <div>
                  <span className="text-[11px] text-neutral-400 block">Đơn hàng</span>
                  <span className="font-mono font-bold text-neutral-900">
                    #{viewingReturn.orderItem?.orderId || 'N/A'}
                  </span>
                </div>
                <div>
                  <span className="text-[11px] text-neutral-400 block">Trạng thái</span>
                  <Badge
                    variant={statusMeta[viewingReturn.status]?.variant || 'secondary'}
                    className="text-[10px] mt-0.5"
                  >
                    {statusMeta[viewingReturn.status]?.label || viewingReturn.status}
                  </Badge>
                </div>
                <div>
                  <span className="text-[11px] text-neutral-400 block">Sản phẩm</span>
                  <span className="font-semibold text-neutral-900">
                    {viewingReturn.orderItem?.productName || `Item #${viewingReturn.orderItemId}`}
                  </span>
                </div>
                <div>
                  <span className="text-[11px] text-neutral-400 block">Số lượng hoàn trả</span>
                  <span className="font-bold text-neutral-900">{viewingReturn.quantity} món</span>
                </div>
              </div>

              <div>
                <span className="text-[11px] font-bold text-neutral-500 uppercase block mb-1">
                  Lý do từ khách hàng
                </span>
                <p className="p-3 bg-neutral-50 rounded-xl border border-neutral-100 font-medium text-neutral-900">
                  {viewingReturn.reason}
                </p>
              </div>

              {viewingReturn.description && (
                <div>
                  <span className="text-[11px] font-bold text-neutral-500 uppercase block mb-1">
                    Mô tả chi tiết
                  </span>
                  <p className="p-3 bg-neutral-50 rounded-xl border border-neutral-100 text-neutral-700 whitespace-pre-wrap">
                    {viewingReturn.description}
                  </p>
                </div>
              )}

              {viewingReturn.medias && viewingReturn.medias.length > 0 && (
                <div>
                  <span className="text-[11px] font-bold text-neutral-500 uppercase block mb-1.5">
                    Ảnh / Video bằng chứng ({viewingReturn.medias.length})
                  </span>
                  <div className="grid grid-cols-3 gap-2">
                    {viewingReturn.medias.map((m, idx) => {
                      const isVideo =
                        m.mediaType?.includes('video') ||
                        m.mediaUrl.endsWith('.mp4') ||
                        m.mediaUrl.includes('video');
                      return isVideo ? (
                        <video
                          key={m.id || idx}
                          src={m.mediaUrl}
                          controls
                          className="w-full h-24 object-cover rounded-xl border border-neutral-200"
                        />
                      ) : (
                        <a
                          key={m.id || idx}
                          href={m.mediaUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="block group relative"
                        >
                          {/* eslint-disable-next-line @next/next/no-img-element */}
                          <img
                            src={m.mediaUrl}
                            alt="Hình ảnh bằng chứng"
                            className="w-full h-24 object-cover rounded-xl border border-neutral-200 group-hover:opacity-90 transition"
                          />
                        </a>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          )}

          <DialogFooter className="mt-4 flex justify-end gap-2 border-t border-[#EAE6DF] pt-3">
            <Button
              type="button"
              variant="outline"
              onClick={() => setViewingReturn(null)}
              className="rounded-xl border-[#EAE6DF]"
            >
              Đóng
            </Button>
            {viewingReturn?.status === 'PENDING' && (
              <>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setRejectTarget(viewingReturn)}
                  className="rounded-xl text-rose-700 hover:bg-rose-50 border-rose-200"
                >
                  <XCircle className="size-4 mr-1.5" /> Từ chối
                </Button>
                <Button
                  type="button"
                  onClick={() =>
                    updateStatusMutation.mutate({
                      returnId: viewingReturn.id,
                      status: 'APPROVED',
                    })
                  }
                  disabled={updateStatusMutation.isPending}
                  className="rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white"
                >
                  <CheckCircle2 className="size-4 mr-1.5" /> Phê duyệt
                </Button>
              </>
            )}
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Confirm Reject Dialog */}
      <ConfirmDialog
        open={!!rejectTarget}
        onOpenChange={(open) => !open && setRejectTarget(null)}
        title={`Từ chối yêu cầu đổi trả #${rejectTarget?.id}?`}
        description="Bạn có chắc chắn muốn từ chối yêu cầu đổi trả này? Hành động này sẽ thông báo cho khách hàng."
        confirmText="Xác nhận từ chối"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (rejectTarget) {
            updateStatusMutation.mutate({ returnId: rejectTarget.id, status: 'REJECTED' });
          }
        }}
      />
    </div>
  );
}
```

# admin\reviews\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\reviews\page.tsx` |
| Size | 12,065 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.962857 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import {
  Star,
  Eye,
  EyeOff,
  MessageSquare,
  CornerDownRight,
  Send,
  Image as ImageIcon,
} from 'lucide-react';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { replyReview, hideReview } from '@/modules/loyalty-engage/api';
import { formatDate } from '@/lib/utils/format-date';
import type { ProductReview } from '@/types';

export default function AdminReviewsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [replyingReview, setReplyingReview] = React.useState<ProductReview | null>(null);
  const [replyContent, setReplyContent] = React.useState('');
  const [previewMediaUrl, setPreviewMediaUrl] = React.useState<string | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-reviews', page],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS, {
        params: { page, limit: 10 },
      });
      return res.data;
    },
  });

  // Reply mutation
  const replyMutation = useMutation({
    mutationFn: async ({ reviewId, shopReply }: { reviewId: number; shopReply: string }) => {
      return replyReview(reviewId, shopReply);
    },
    onSuccess: () => {
      toast.success('Đã gửi phản hồi đánh giá thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-reviews'] });
      setReplyingReview(null);
      setReplyContent('');
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Gửi phản hồi thất bại');
    },
  });

  // Hide/Unhide mutation
  const hideMutation = useMutation({
    mutationFn: async ({ reviewId, isHidden }: { reviewId: number; isHidden: boolean }) => {
      return hideReview(reviewId, isHidden);
    },
    onSuccess: (_, variables) => {
      toast.success(variables.isHidden ? 'Đã ẩn đánh giá khỏi cửa hàng' : 'Đã hiện lại đánh giá');
      queryClient.invalidateQueries({ queryKey: ['admin-reviews'] });
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật trạng thái hiển thị thất bại');
    },
  });

  const handleOpenReply = (r: ProductReview) => {
    setReplyingReview(r);
    setReplyContent(r.shopReply || '');
  };

  const reviews: ProductReview[] = (data?.data as any) || [];

  const columns: Column<ProductReview>[] = [
    {
      header: 'Khách hàng',
      cell: (r) => (
        <div>
          <p className="font-semibold text-xs text-neutral-900">{r.user?.fullName || 'Khách hàng'}</p>
          <p className="text-[10px] text-neutral-400">{r.user?.email}</p>
        </div>
      ),
    },
    {
      header: 'Đánh giá',
      cell: (r) => (
        <div className="flex items-center gap-1">
          <Star className="size-3.5 fill-amber-400 text-amber-400" />
          <span className="font-bold text-xs text-neutral-800">{r.rating}/5</span>
        </div>
      ),
    },
    {
      header: 'Nội dung nhận xét',
      cell: (r) => (
        <div className="max-w-md">
          <p className="text-xs text-neutral-700">{r.content}</p>
          {r.medias && r.medias.length > 0 && (
            <div className="flex gap-1.5 mt-2 flex-wrap">
              {r.medias.map((m: any, idx: number) => {
                const url = m.mediaUrl || m.url;
                const isVideo =
                  m.mediaType === 'video' ||
                  m.type === 'video' ||
                  (typeof url === 'string' && (url.endsWith('.mp4') || url.includes('video')));
                return isVideo ? (
                  <video
                    key={m.id || idx}
                    src={url}
                    controls
                    className="w-14 h-10 object-cover rounded-md border border-neutral-200"
                  />
                ) : (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img
                    key={m.id || idx}
                    src={url}
                    alt="Ảnh đánh giá"
                    className="w-10 h-10 object-cover rounded-md border border-neutral-200 cursor-pointer hover:opacity-80 transition"
                    onClick={() => setPreviewMediaUrl(url)}
                    title="Nhấn để xem ảnh phóng to"
                  />
                );
              })}
            </div>
          )}
          {r.shopReply && (
            <div className="bg-emerald-50/70 border border-emerald-100 rounded-lg p-2 mt-2">
              <p className="text-[11px] text-emerald-800 flex items-start gap-1 font-medium">
                <CornerDownRight className="size-3.5 mt-0.5 shrink-0 text-emerald-600" />
                <span>
                  <strong className="text-emerald-900 font-semibold">Cửa hàng phản hồi:</strong>{' '}
                  {r.shopReply}
                </span>
              </p>
            </div>
          )}
        </div>
      ),
    },
    {
      header: 'Thời gian',
      cell: (r) => <span className="text-xs text-neutral-400">{formatDate(r.createdAt)}</span>,
    },
    {
      header: 'Trạng thái',
      cell: (r) => (
        <Badge variant={r.isHidden ? 'secondary' : 'success'} className="text-[10px]">
          {r.isHidden ? 'Đã ẩn vi phạm' : 'Đang hiển thị'}
        </Badge>
      ),
    },
    {
      header: 'Thao tác',
      className: 'text-right',
      cell: (r) => (
        <div className="flex justify-end items-center gap-1.5">
          <Button
            size="sm"
            variant="outline"
            onClick={() => handleOpenReply(r)}
            className="h-8 px-2.5 text-xs text-neutral-700 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg border-neutral-200"
            title={r.shopReply ? 'Chỉnh sửa phản hồi' : 'Phản hồi đánh giá'}
          >
            <MessageSquare className="size-3.5 mr-1 text-neutral-500" />
            {r.shopReply ? 'Sửa phản hồi' : 'Phản hồi'}
          </Button>

          <Button
            size="sm"
            variant="outline"
            onClick={() => hideMutation.mutate({ reviewId: r.id, isHidden: !r.isHidden })}
            disabled={hideMutation.isPending}
            className={`h-8 px-2.5 text-xs rounded-lg transition ${
              r.isHidden
                ? 'text-emerald-700 hover:bg-emerald-50 border-emerald-200'
                : 'text-rose-700 hover:bg-rose-50 border-rose-200'
            }`}
            title={r.isHidden ? 'Bỏ ẩn đánh giá' : 'Ẩn đánh giá vi phạm'}
          >
            {r.isHidden ? (
              <>
                <Eye className="size-3.5 mr-1" /> Hiện lại
              </>
            ) : (
              <>
                <EyeOff className="size-3.5 mr-1" /> Ẩn đi
              </>
            )}
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <span className="eyebrow block mb-1">CSKH & UY TÍN</span>
        <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
          Đánh giá sản phẩm
        </h1>
        <p className="text-xs text-neutral-500 mt-1">
          Quản lý các phản hồi thực tế từ người mua hàng, trả lời thắc mắc và ẩn các nội dung vi phạm.
        </p>
      </div>

      <DataTable
        columns={columns}
        data={reviews}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Chưa có đánh giá nào."
      />

      {/* Modal Phản hồi Đánh giá */}
      <Dialog open={!!replyingReview} onOpenChange={(open) => !open && setReplyingReview(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <MessageSquare className="size-5 text-neutral-700" />
              Phản hồi đánh giá của {replyingReview?.user?.fullName || 'khách hàng'}
            </DialogTitle>
          </DialogHeader>

          {replyingReview && (
            <div className="space-y-4 my-2 text-xs">
              <div className="bg-neutral-50 p-3 rounded-xl border border-neutral-100 text-neutral-700">
                <div className="flex items-center gap-1 mb-1">
                  <Star className="size-3.5 fill-amber-400 text-amber-400" />
                  <span className="font-bold text-neutral-900">{replyingReview.rating}/5 sao</span>
                </div>
                <p className="italic">{replyingReview.content}</p>
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Nội dung phản hồi từ Shop *
                </label>
                <textarea
                  rows={4}
                  value={replyContent}
                  onChange={(e) => setReplyContent(e.target.value)}
                  placeholder="Cảm ơn bạn đã tin tưởng và ủng hộ sản phẩm của Shop..."
                  maxLength={1000}
                  className="w-full rounded-xl border border-[#EAE6DF] p-3 text-xs outline-none focus:border-neutral-900"
                />
                <span className="text-[10px] text-neutral-400 block text-right mt-1">
                  {replyContent.length}/1000 ký tự
                </span>
              </div>
            </div>
          )}

          <DialogFooter className="mt-4 flex justify-end gap-2 border-t border-[#EAE6DF] pt-3">
            <Button
              type="button"
              variant="outline"
              onClick={() => setReplyingReview(null)}
              className="rounded-xl border-[#EAE6DF]"
            >
              Hủy
            </Button>
            <Button
              type="button"
              onClick={() => {
                if (replyingReview && replyContent.trim()) {
                  replyMutation.mutate({
                    reviewId: replyingReview.id,
                    shopReply: replyContent.trim(),
                  });
                }
              }}
              disabled={!replyContent.trim() || replyMutation.isPending}
              className="rounded-xl bg-[#1D1F20] text-white flex items-center gap-1.5"
            >
              <Send className="size-3.5" />
              {replyMutation.isPending ? 'Đang gửi...' : 'Gửi phản hồi'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Image Preview Modal */}
      <Dialog open={!!previewMediaUrl} onOpenChange={(open) => !open && setPreviewMediaUrl(null)}>
        <DialogContent className="sm:max-w-xl bg-white border border-[#EAE6DF] rounded-2xl p-4">
          {previewMediaUrl && (
            // eslint-disable-next-line @next/next/no-img-element
            <img
              src={previewMediaUrl}
              alt="Ảnh phóng to"
              className="w-full max-h-[80vh] object-contain rounded-xl"
            />
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
```

# admin\roles\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\roles\page.tsx` |
| Size | 17,979 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.480049 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  Shield,
  Plus,
  Edit3,
  Trash2,
  CheckCircle2,
  XCircle,
  KeyRound,
  Loader2,
  Lock,
} from 'lucide-react';
import { adminApi } from '@/modules/admin/api';
import { SearchInput } from '@/components/ui/search-input';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import type { Role, Permission } from '@/types';

interface RoleFormData {
  name: string;
  code: string;
  isActive: boolean;
  permissionIds: number[];
}

export default function AdminRolesPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [editingRole, setEditingRole] = React.useState<Role | null>(null);
  const [deleteTargetRole, setDeleteTargetRole] = React.useState<Role | null>(null);

  // Fetch Roles
  const { data: rolesData, isLoading } = useQuery({
    queryKey: ['admin-roles', page, search],
    queryFn: () =>
      adminApi.getRoles({
        page,
        limit: 10,
        search: search || undefined,
      }),
  });

  // Fetch Permissions
  const { data: permissionsData } = useQuery({
    queryKey: ['admin-permissions'],
    queryFn: () => adminApi.getPermissions({ limit: 100 }),
  });

  const permissions = Array.isArray(permissionsData?.data) ? permissionsData.data : [];

  // Group permissions by module
  const permissionsByModule = React.useMemo(() => {
    const groups: Record<string, Permission[]> = {};
    if (Array.isArray(permissions)) {
      permissions.forEach((perm) => {
        const mod = perm.module || 'CHUNG';
        if (!groups[mod]) groups[mod] = [];
        groups[mod].push(perm);
      });
    }
    return groups;
  }, [permissions]);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    reset,
    formState: { errors },
  } = useForm<RoleFormData>({
    defaultValues: {
      name: '',
      code: '',
      isActive: true,
      permissionIds: [],
    },
  });

  const selectedPermissionIds = watch('permissionIds') || [];
  const isActiveWatched = watch('isActive');

  const openCreateDialog = () => {
    setEditingRole(null);
    reset({
      name: '',
      code: '',
      isActive: true,
      permissionIds: [],
    });
    setIsDialogOpen(true);
  };

  const openEditDialog = (role: Role) => {
    setEditingRole(role);
    reset({
      name: role.name,
      code: role.code,
      isActive: role.isActive ?? true,
      permissionIds: role.permissions?.map((p) => p.id) || [],
    });
    setIsDialogOpen(true);
  };

  const togglePermission = (id: number) => {
    const current = new Set(selectedPermissionIds);
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    setValue('permissionIds', Array.from(current));
  };

  const toggleAllInModule = (modulePermissions: Permission[]) => {
    const current = new Set(selectedPermissionIds);
    const allModuleSelected = modulePermissions.every((p) => current.has(p.id));

    if (allModuleSelected) {
      modulePermissions.forEach((p) => current.delete(p.id));
    } else {
      modulePermissions.forEach((p) => current.add(p.id));
    }
    setValue('permissionIds', Array.from(current));
  };

  // Mutation save role
  const saveRoleMutation = useMutation({
    mutationFn: async (data: RoleFormData) => {
      if (editingRole) {
        return adminApi.updateRole(editingRole.id, {
          name: data.name,
          isActive: data.isActive,
          permissionIds: data.permissionIds,
        });
      } else {
        return adminApi.createRole({
          name: data.name,
          code: data.code.toUpperCase(),
          permissionIds: data.permissionIds,
        });
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-roles'] });
      toast.success(editingRole ? 'Cập nhật vai trò thành công!' : 'Tạo vai trò mới thành công!');
      setIsDialogOpen(false);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Lưu vai trò thất bại');
    },
  });

  // Mutation delete role
  const deleteRoleMutation = useMutation({
    mutationFn: (roleId: number) => adminApi.deleteRole(roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-roles'] });
      toast.success('Xóa vai trò thành công!');
      setDeleteTargetRole(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa vai trò thất bại');
    },
  });

  const onSubmit = (data: RoleFormData) => {
    saveRoleMutation.mutate(data);
  };

  const isCoreRole = (code: string) => {
    return ['ADMIN', 'CUSTOMER'].includes(code?.toUpperCase());
  };

  const columns: Column<Role>[] = [
    {
      header: 'ID',
      className: 'w-16 text-center text-xs text-neutral-400 font-mono',
      cell: (role) => `#${role.id}`,
    },
    {
      header: 'Tên vai trò',
      cell: (role) => (
        <div className="flex items-center gap-2.5">
          <div className="size-8 rounded-xl bg-neutral-100 text-neutral-700 flex items-center justify-center">
            <Shield className="size-4" />
          </div>
          <div>
            <p className="font-semibold text-xs text-neutral-900">{role.name}</p>
            <p className="text-[11px] text-neutral-400 font-mono">{role.code}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Số quyền hạn',
      cell: (role) => (
        <span className="text-xs font-semibold text-neutral-700 flex items-center gap-1.5">
          <KeyRound className="size-3.5 text-neutral-400" />
          {role.permissions?.length || 0} quyền
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (role) =>
        role.isActive ? (
          <Badge variant="success" className="gap-1 text-[11px]">
            <CheckCircle2 className="size-3" /> Hoạt động
          </Badge>
        ) : (
          <Badge variant="destructive" className="gap-1 text-[11px]">
            <XCircle className="size-3" /> Vô hiệu
          </Badge>
        ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (role) => (
        <div className="flex items-center justify-end gap-1">
          <button
            type="button"
            onClick={() => openEditDialog(role)}
            title="Chỉnh sửa vai trò"
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 transition"
          >
            <Edit3 className="size-4" />
          </button>

          {!isCoreRole(role.code) ? (
            <button
              type="button"
              onClick={() => setDeleteTargetRole(role)}
              title="Xóa vai trò"
              className="p-1.5 rounded-lg text-neutral-400 hover:text-rose-600 hover:bg-rose-50 transition"
            >
              <Trash2 className="size-4" />
            </button>
          ) : (
            <span
              title="Vai trò hệ thống cốt lõi không thể xóa"
              className="p-1.5 text-neutral-300 cursor-not-allowed"
            >
              <Lock className="size-4" />
            </span>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">PHÂN QUYỀN HỆ THỐNG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Vai trò & Quyền hạn
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Thiết lập danh mục vai trò người dùng (Roles) và ma trận phân quyền tính năng (RBAC).
          </p>
        </div>

        <Button
          onClick={openCreateDialog}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" />
          <span>Thêm vai trò mới</span>
        </Button>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-4 flex items-center justify-between shadow-xs">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm kiếm theo tên hoặc mã vai trò..."
          className="max-w-md"
        />
      </div>

      {/* Roles Data Table */}
      <DataTable
        columns={columns}
        data={Array.isArray(rolesData?.data) ? rolesData.data : []}
        isLoading={isLoading}
        page={page}
        totalPages={rolesData?.totalPages || 1}
        totalItems={rolesData?.totalItems}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy vai trò nào."
      />

      {/* Create / Edit Role Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-2xl bg-white border border-[#EAE6DF] rounded-2xl p-6 max-h-[90vh] flex flex-col">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900">
              {editingRole ? `Chỉnh sửa vai trò "${editingRole.name}"` : 'Tạo vai trò mới'}
            </DialogTitle>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 mt-2 flex-1 overflow-y-auto pr-1">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                  Tên vai trò
                </label>
                <input
                  {...register('name', { required: 'Tên vai trò là bắt buộc' })}
                  placeholder="Ví dụ: Quản trị viên chi nhánh"
                  className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2 text-sm text-neutral-800 outline-none focus:border-neutral-900 shadow-xs"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                  Mã vai trò (Code)
                </label>
                <input
                  {...register('code', { required: 'Mã vai trò là bắt buộc' })}
                  disabled={!!editingRole}
                  placeholder="Ví dụ: BRANCH_MANAGER"
                  className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2 text-sm text-neutral-800 outline-none focus:border-neutral-900 shadow-xs uppercase font-mono disabled:bg-neutral-100 disabled:text-neutral-500"
                />
              </div>
            </div>

            <label className="flex items-center gap-2 cursor-pointer pt-1">
              <input
                type="checkbox"
                checked={isActiveWatched}
                onChange={(e) => setValue('isActive', e.target.checked)}
                className="rounded border-[#EAE6DF] accent-neutral-900 size-4"
              />
              <span className="text-xs font-medium text-neutral-700">Kích hoạt vai trò này</span>
            </label>

            {/* Matrix Permissions By Module */}
            <div className="space-y-3 pt-2 border-t border-[#EAE6DF]">
              <div className="flex items-center justify-between">
                <h4 className="text-xs font-bold uppercase tracking-wider text-neutral-700 flex items-center gap-1.5">
                  <KeyRound className="size-3.5" />
                  Danh mục quyền hạn ({selectedPermissionIds.length} đã chọn)
                </h4>
              </div>

              {Object.keys(permissionsByModule).length === 0 ? (
                <div className="text-center py-6 text-xs text-neutral-400">
                  Đang tải danh sách quyền hạn...
                </div>
              ) : (
                <div className="space-y-4 max-h-[300px] overflow-y-auto pr-2">
                  {Object.entries(permissionsByModule).map(([mod, perms]) => {
                    const allSelected = perms.every((p) =>
                      selectedPermissionIds.includes(p.id)
                    );
                    return (
                      <div
                        key={mod}
                        className="rounded-xl border border-[#EAE6DF] p-3.5 bg-neutral-50/50 space-y-2.5"
                      >
                        <div className="flex items-center justify-between pb-2 border-b border-[#EAE6DF]">
                          <span className="text-xs font-bold text-neutral-900 uppercase tracking-wide">
                            {mod}
                          </span>
                          <button
                            type="button"
                            onClick={() => toggleAllInModule(perms)}
                            className="text-[11px] text-neutral-500 hover:text-neutral-900 font-medium"
                          >
                            {allSelected ? 'Bỏ chọn module' : 'Chọn tất cả module'}
                          </button>
                        </div>

                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                          {perms.map((p) => {
                            const isChecked = selectedPermissionIds.includes(p.id);
                            return (
                              <label
                                key={p.id}
                                className={`flex items-start gap-2 p-2 rounded-lg border transition cursor-pointer text-xs ${
                                  isChecked
                                    ? 'border-neutral-900 bg-white font-medium'
                                    : 'border-[#EAE6DF] bg-white/70 hover:border-neutral-300'
                                }`}
                              >
                                <input
                                  type="checkbox"
                                  checked={isChecked}
                                  onChange={() => togglePermission(p.id)}
                                  className="rounded border-[#EAE6DF] accent-neutral-900 size-3.5 mt-0.5"
                                />
                                <div className="min-w-0">
                                  <p className="text-neutral-900 truncate font-mono text-[11px]">
                                    {p.code}
                                  </p>
                                  {p.description && (
                                    <p className="text-[10px] text-neutral-500 line-clamp-1">
                                      {p.description}
                                    </p>
                                  )}
                                </div>
                              </label>
                            );
                          })}
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>

            <DialogFooter className="mt-6 flex flex-row justify-end gap-2.5 pt-3 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsDialogOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={saveRoleMutation.isPending}
                className="rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium"
              >
                {saveRoleMutation.isPending ? (
                  <>
                    <Loader2 className="size-4 animate-spin mr-2" />
                    Đang lưu...
                  </>
                ) : (
                  'Lưu vai trò'
                )}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Role */}
      <ConfirmDialog
        open={!!deleteTargetRole}
        onOpenChange={(open) => !open && setDeleteTargetRole(null)}
        title="Xóa vai trò"
        description={`Bạn có chắc chắn muốn xóa vai trò "${deleteTargetRole?.name}" (${deleteTargetRole?.code})? Hành động này sẽ loại bỏ quyền hạn của những người dùng đang giữ vai trò này.`}
        variant="destructive"
        confirmText="Xóa vai trò"
        isLoading={deleteRoleMutation.isPending}
        onConfirm={() => {
          if (deleteTargetRole) deleteRoleMutation.mutate(deleteTargetRole.id);
        }}
      />
    </div>
  );
}


```

# admin\shipments\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\shipments\page.tsx` |
| Size | 22,384 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.964859 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  Truck,
  Plus,
  Edit3,
  Trash2,
  Calendar,
  CheckCircle2,
  XCircle,
  Clock,
  ArrowRight,
  Package,
} from 'lucide-react';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { Shipment, ShipmentStatus } from '@/types';

const shipmentStatusMeta: Record<
  ShipmentStatus,
  { label: string; variant: 'warning' | 'lime' | 'secondary' | 'success' | 'destructive' }
> = {
  PENDING: { label: 'Chờ lấy hàng', variant: 'warning' },
  PICKED_UP: { label: 'Đã lấy hàng', variant: 'lime' },
  IN_TRANSIT: { label: 'Đang vận chuyển', variant: 'secondary' },
  DELIVERED: { label: 'Giao thành công', variant: 'success' },
  FAILED: { label: 'Giao thất bại', variant: 'destructive' },
};

const carrierOptions = [
  'Giao Hàng Nhanh (GHN)',
  'Giao Hàng Tiết Kiệm (GHTK)',
  'Viettel Post',
  'VNPost',
  'J&T Express',
  'Shopee Xpress (SPX)',
  'Ninja Van',
  'Hỏa tốc nội thành',
];

export default function AdminShipmentsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [statusFilter, setStatusFilter] = React.useState<ShipmentStatus | ''>('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingShipment, setEditingShipment] = React.useState<Shipment | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Shipment | null>(null);

  // Form for create/edit
  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      orderId: '',
      carrier: 'Giao Hàng Nhanh (GHN)',
      trackingCode: '',
      actualShippingFee: 30000,
      status: 'PENDING' as ShipmentStatus,
      estimatedDelivery: '',
    },
  });

  // Query shipments
  const { data, isLoading } = useQuery({
    queryKey: ['admin-shipments', page, search, statusFilter],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.FULFILLMENT.SHIPMENTS, {
        params: {
          page,
          limit: 10,
          search: search || undefined,
          status: statusFilter || undefined,
        },
      });
      return res.data;
    },
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: any) => {
      const res = await apiClient.post(API_ENDPOINTS.FULFILLMENT.SHIPMENTS, {
        orderId: Number(values.orderId),
        carrier: values.carrier,
        trackingCode: values.trackingCode.trim(),
        actualShippingFee: Number(values.actualShippingFee),
        status: values.status || 'PENDING',
        estimatedDelivery: values.estimatedDelivery ? new Date(values.estimatedDelivery).toISOString() : undefined,
      });
      return res.data;
    },
    onSuccess: () => {
      toast.success('Tạo vận đơn mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-shipments'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Tạo vận đơn thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({
      shipmentId,
      values,
    }: {
      shipmentId: number;
      values: Partial<Shipment>;
    }) => {
      const res = await apiClient.put(
        `${API_ENDPOINTS.FULFILLMENT.SHIPMENTS}/${shipmentId}`,
        values
      );
      return res.data;
    },
    onSuccess: () => {
      toast.success('Cập nhật vận đơn thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-shipments'] });
      setEditingShipment(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật vận đơn thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (shipmentId: number) => {
      const res = await apiClient.delete(
        `${API_ENDPOINTS.FULFILLMENT.SHIPMENTS}/${shipmentId}`
      );
      return res.data;
    },
    onSuccess: () => {
      toast.success('Đã xóa vận đơn!');
      queryClient.invalidateQueries({ queryKey: ['admin-shipments'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa vận đơn thất bại');
    },
  });

  const handleOpenEdit = (s: Shipment) => {
    setEditingShipment(s);
    setValue('orderId', s.orderId.toString());
    setValue('carrier', s.carrier);
    setValue('trackingCode', s.trackingCode);
    setValue('actualShippingFee', Number(s.actualShippingFee));
    setValue('status', s.status);
    setValue(
      'estimatedDelivery',
      s.estimatedDelivery ? new Date(s.estimatedDelivery).toISOString().slice(0, 10) : ''
    );
  };

  const handleStatusChange = (shipmentId: number, newStatus: ShipmentStatus) => {
    updateMutation.mutate({ shipmentId, values: { status: newStatus } });
  };

  const shipments: Shipment[] = (data?.data as any) || [];

  const columns: Column<Shipment>[] = [
    {
      header: 'Mã vận đơn',
      cell: (s) => (
        <div>
          <div className="flex items-center gap-2">
            <Truck className="size-4 text-neutral-500" />
            <span className="font-mono font-bold text-xs text-neutral-900 bg-neutral-100 px-2 py-0.5 rounded">
              {s.trackingCode}
            </span>
          </div>
          <p className="text-[11px] text-neutral-400 font-mono mt-0.5">
            Đơn hàng #{s.orderId}
          </p>
        </div>
      ),
    },
    {
      header: 'Đơn vị vận chuyển',
      cell: (s) => <span className="font-semibold text-xs text-neutral-800">{s.carrier}</span>,
    },
    {
      header: 'Người nhận',
      cell: (s) => (
        <div>
          <p className="text-xs font-semibold text-neutral-900">{s.order?.receiverName || 'Khách hàng'}</p>
          <p className="text-[11px] text-neutral-500 font-mono">{s.order?.receiverPhone || '---'}</p>
        </div>
      ),
    },
    {
      header: 'Cước vận chuyển',
      cell: (s) => (
        <span className="text-xs font-semibold text-neutral-900">
          {formatCurrency(s.actualShippingFee)}
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (s) => {
        const meta = shipmentStatusMeta[s.status] || {
          label: s.status,
          variant: 'secondary' as const,
        };
        return (
          <div className="flex items-center gap-1.5">
            <Badge variant={meta.variant} className="text-[10px]">
              {meta.label}
            </Badge>
          </div>
        );
      },
    },
    {
      header: 'Cập nhật trạng thái',
      cell: (s) => (
        <select
          value={s.status}
          onChange={(e) => handleStatusChange(s.id, e.target.value as ShipmentStatus)}
          disabled={updateMutation.isPending}
          className="text-xs rounded-lg border border-[#EAE6DF] bg-white px-2 py-1 text-neutral-700 outline-none focus:border-neutral-900"
        >
          {Object.entries(shipmentStatusMeta).map(([stKey, stVal]) => (
            <option key={stKey} value={stKey}>
              {stVal.label}
            </option>
          ))}
        </select>
      ),
    },
    {
      header: 'Thời gian tạo',
      cell: (s) => <span className="text-xs text-neutral-400">{formatDate(s.createdAt)}</span>,
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (s) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(s)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Chỉnh sửa thông tin"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(s)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa vận đơn"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">VẬN HÀNH</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Vận đơn & Giao hàng
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Tạo mã vận đơn, theo dõi hành trình và cập nhật trạng thái giao hàng thủ công.
          </p>
        </div>
        <Button
          onClick={() => {
            reset({
              orderId: '',
              carrier: 'Giao Hàng Nhanh (GHN)',
              trackingCode: `TRK${Date.now().toString().slice(-8)}`,
              actualShippingFee: 30000,
              status: 'PENDING',
              estimatedDelivery: '',
            });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white flex items-center gap-2"
        >
          <Plus className="size-4" /> Tạo vận đơn mới
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo mã vận đơn hoặc số điện thoại..."
          className="max-w-md w-full"
        />

        <div className="flex items-center gap-2">
          <span className="text-xs text-neutral-500 shrink-0">Lọc theo trạng thái:</span>
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value as ShipmentStatus | '');
              setPage(1);
            }}
            className="text-xs rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-neutral-700 outline-none"
          >
            <option value="">Tất cả trạng thái</option>
            {Object.entries(shipmentStatusMeta).map(([k, v]) => (
              <option key={k} value={k}>
                {v.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={shipments}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Chưa có vận đơn nào."
      />

      {/* Modal Tạo Vận Đơn */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Truck className="size-5 text-neutral-700" />
              Tạo vận đơn giao hàng mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Mã đơn hàng (Order ID) *
              </label>
              <input
                type="number"
                {...register('orderId', { required: true, min: 1 })}
                placeholder="VD: 101"
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Đơn vị vận chuyển *
                </label>
                <select
                  {...register('carrier', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                >
                  {carrierOptions.map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Mã tracking vận đơn *
                </label>
                <input
                  type="text"
                  {...register('trackingCode', { required: true })}
                  placeholder="VD: GHN123456VN"
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 font-mono uppercase text-xs outline-none focus:border-neutral-900"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Cước thực tế (VNĐ) *
                </label>
                <input
                  type="number"
                  {...register('actualShippingFee', { required: true, min: 0 })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Trạng thái ban đầu
                </label>
                <select
                  {...register('status')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                >
                  {Object.entries(shipmentStatusMeta).map(([k, v]) => (
                    <option key={k} value={k}>
                      {v.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Dự kiến giao hàng
              </label>
              <input
                type="date"
                {...register('estimatedDelivery')}
                className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
              />
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsCreateOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {createMutation.isPending ? 'Đang tạo...' : 'Tạo vận đơn'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Chỉnh sửa Vận Đơn */}
      <Dialog
        open={!!editingShipment}
        onOpenChange={(open) => !open && setEditingShipment(null)}
      >
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Chỉnh sửa Vận đơn #{editingShipment?.trackingCode}
            </DialogTitle>
          </DialogHeader>

          {editingShipment && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({
                  shipmentId: editingShipment.id,
                  values: {
                    carrier: values.carrier,
                    trackingCode: values.trackingCode.trim(),
                    actualShippingFee: Number(values.actualShippingFee),
                    status: values.status as ShipmentStatus,
                    estimatedDelivery: values.estimatedDelivery
                      ? new Date(values.estimatedDelivery).toISOString()
                      : undefined,
                  },
                })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Đơn vị vận chuyển *
                  </label>
                  <select
                    {...register('carrier', { required: true })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                  >
                    {carrierOptions.map((c) => (
                      <option key={c} value={c}>
                        {c}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Mã tracking *
                  </label>
                  <input
                    type="text"
                    {...register('trackingCode', { required: true })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 font-mono uppercase text-xs outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Cước thực tế (VNĐ) *
                  </label>
                  <input
                    type="number"
                    {...register('actualShippingFee', { required: true, min: 0 })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>

                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Trạng thái
                  </label>
                  <select
                    {...register('status')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none bg-white"
                  >
                    {Object.entries(shipmentStatusMeta).map(([k, v]) => (
                      <option key={k} value={k}>
                        {v.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Dự kiến giao hàng
                </label>
                <input
                  type="date"
                  {...register('estimatedDelivery')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                />
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingShipment(null)}
                  className="rounded-xl border-[#EAE6DF]"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={updateMutation.isPending}
                  className="rounded-xl bg-[#1D1F20] text-white"
                >
                  {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </Button>
              </DialogFooter>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title={`Xóa vận đơn #${deleteTarget?.trackingCode}?`}
        description="Bạn có chắc chắn muốn xóa vận đơn này? Hành động này không thể hoàn tác."
        confirmText="Xóa vận đơn"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
      />
    </div>
  );
}
```

# admin\tags\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\tags\page.tsx` |
| Size | 10,782 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.966907 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { Tag as TagIcon, Plus, Edit3, Trash2, Hash } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import type { Tag } from '@/types';

export default function AdminTagsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingTag, setEditingTag] = React.useState<Tag | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Tag | null>(null);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: {
      name: '',
    },
  });

  const { data, isLoading } = useQuery({
    queryKey: ['admin-tags', page, search],
    queryFn: () => catalogApi.getTags({ page, limit: 10, search: search || undefined }),
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: async (values: { name: string }) => {
      return catalogApi.createTag({ name: values.name.trim() });
    },
    onSuccess: () => {
      toast.success('Thêm thẻ (tag) mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-tags'] });
      setIsCreateOpen(false);
      reset();
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thêm tag thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, name }: { id: number; name: string }) => {
      return catalogApi.updateTag(id, { name: name.trim() });
    },
    onSuccess: () => {
      toast.success('Cập nhật thẻ tag thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-tags'] });
      setEditingTag(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật tag thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      return catalogApi.deleteTag(id);
    },
    onSuccess: () => {
      toast.success('Đã xóa thẻ tag!');
      queryClient.invalidateQueries({ queryKey: ['admin-tags'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa tag thất bại');
    },
  });

  const handleOpenEdit = (t: Tag) => {
    setEditingTag(t);
    setValue('name', t.name);
  };

  const tags: Tag[] = (data?.data as any) || [];

  const columns: Column<Tag>[] = [
    {
      header: 'ID',
      className: 'w-16 text-xs text-neutral-400 font-mono',
      cell: (t) => `#${t.id}`,
    },
    {
      header: 'Tên thẻ (Tag)',
      cell: (t) => (
        <div className="flex items-center gap-2">
          <span className="flex size-7 items-center justify-center rounded-lg bg-neutral-100 text-neutral-600">
            <Hash className="size-3.5" />
          </span>
          <span className="font-semibold text-xs text-neutral-900 bg-neutral-100/80 px-2.5 py-1 rounded-md border border-[#EAE6DF]">
            {t.name}
          </span>
        </div>
      ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (t) => (
        <div className="flex justify-end gap-1">
          <button
            type="button"
            onClick={() => handleOpenEdit(t)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa tag"
          >
            <Edit3 className="size-4" />
          </button>
          <button
            type="button"
            onClick={() => setDeleteTarget(t)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa tag"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">CATALOG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Thẻ sản phẩm (Tags)
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Quản lý các hashtag, nhãn tìm kiếm giúp phân loại và lọc sản phẩm nhanh chóng.
          </p>
        </div>
        <Button
          onClick={() => {
            reset({ name: '' });
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" /> Thêm Tag mới
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs flex justify-between items-center">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm kiếm thẻ tag..."
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={tags}
        isLoading={isLoading}
        page={page}
        totalPages={data?.totalPages || 1}
        totalItems={data?.totalItems || 0}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy thẻ tag nào."
      />

      {/* Modal Thêm Tag */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Plus className="size-5 text-neutral-700" />
              Thêm thẻ tag mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((values) => createMutation.mutate(values))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">
                Tên thẻ tag *
              </label>
              <input
                type="text"
                {...register('name', { required: true })}
                placeholder="VD: Trending, Summer, Bestseller, Eco-friendly..."
                className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsCreateOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {createMutation.isPending ? 'Đang thêm...' : 'Thêm Tag'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Tag */}
      <Dialog open={!!editingTag} onOpenChange={(open) => !open && setEditingTag(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa thẻ tag #{editingTag?.id}
            </DialogTitle>
          </DialogHeader>

          {editingTag && (
            <form
              onSubmit={handleSubmit((values) =>
                updateMutation.mutate({ id: editingTag.id, name: values.name })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tên thẻ tag *
                </label>
                <input
                  type="text"
                  {...register('name', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3.5 py-2 text-sm outline-none focus:border-neutral-900"
                />
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingTag(null)}
                  className="rounded-xl border-[#EAE6DF]"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={updateMutation.isPending}
                  className="rounded-xl bg-[#1D1F20] text-white"
                >
                  {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </Button>
              </DialogFooter>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title={`Xóa thẻ tag "${deleteTarget?.name}"?`}
        description="Bạn có chắc chắn muốn xóa thẻ tag này? Các sản phẩm được gắn thẻ này sẽ không còn hiển thị tag này nữa."
        confirmText="Xóa tag"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
      />
    </div>
  );
}

```

# admin\users\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\users\page.tsx` |
| Size | 18,424 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.966907 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import {
  Users,
  Search,
  Filter,
  MoreVertical,
  ShieldAlert,
  ShieldCheck,
  Ban,
  Trash2,
  Eye,
  CheckCircle2,
  Clock,
  Sparkles,
  Phone,
  Mail,
  UserCheck,
  Coins,
  Laptop,
  Circle,
} from 'lucide-react';
import { adminApi } from '@/modules/admin/api';
import { chatApi, earnPoints } from '@/modules/loyalty-engage/api';
import { SearchInput } from '@/components/ui/search-input';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { Badge } from '@/components/ui/badge';
import { Avatar } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import type { UserProfile, UserStatus } from '@/types';

export default function AdminUsersPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [statusFilter, setStatusFilter] = React.useState<UserStatus | ''>('');
  const [selectedUser, setSelectedUser] = React.useState<UserProfile | null>(null);
  const [statusChangeTarget, setStatusChangeTarget] = React.useState<{
    user: UserProfile;
    newStatus: UserStatus;
  } | null>(null);
  const [deleteTargetUser, setDeleteTargetUser] = React.useState<UserProfile | null>(null);
  const [onlineStatusMap, setOnlineStatusMap] = React.useState<Record<number, boolean>>({});
  const [selectedUserSessions, setSelectedUserSessions] = React.useState<
    Array<{ socketId: string; userId: number; connectedAt: string }>
  >([]);

  // Fetch Users
  const { data: usersData, isLoading } = useQuery({
    queryKey: ['admin-users', page, search, statusFilter],
    queryFn: () =>
      adminApi.getUsers({
        page,
        limit: 10,
        search: search || undefined,
        status: statusFilter || undefined,
      }),
  });

  // Check online status for users on the current page
  React.useEffect(() => {
    const list = usersData?.data || [];
    if (!list.length) return;

    const userIds = list.map((u) => u.id);
    Promise.all(
      userIds.map(async (uid) => {
        try {
          const res = await chatApi.getUserOnlineStatus(uid);
          return { userId: uid, isOnline: !!res?.data?.isOnline };
        } catch {
          return { userId: uid, isOnline: false };
        }
      })
    ).then((results) => {
      setOnlineStatusMap((prev) => {
        const next = { ...prev };
        results.forEach((r) => {
          next[r.userId] = r.isOnline;
        });
        return next;
      });
    });
  }, [usersData]);

  // When selectedUser changes, fetch their active socket sessions
  React.useEffect(() => {
    if (!selectedUser?.id) {
      setSelectedUserSessions([]);
      return;
    }
    chatApi
      .getUserSessions(selectedUser.id)
      .then((res) => {
        setSelectedUserSessions(res?.data || []);
      })
      .catch(() => {
        setSelectedUserSessions([]);
      });
  }, [selectedUser?.id]);

  // Mutation update status
  const updateStatusMutation = useMutation({
    mutationFn: ({ userId, status }: { userId: number; status: UserStatus }) =>
      adminApi.updateUserStatus(userId, { status }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      toast.success('Cập nhật trạng thái người dùng thành công!');
      setStatusChangeTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật trạng thái thất bại');
    },
  });

  // Mutation delete user
  const deleteUserMutation = useMutation({
    mutationFn: (userId: number) => adminApi.deleteUser(userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      toast.success('Đã xóa tài khoản người dùng!');
      setDeleteTargetUser(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa người dùng thất bại');
    },
  });

  const getStatusBadge = (status: UserStatus) => {
    switch (status) {
      case 'ACTIVE':
        return <Badge variant="success">Hoạt động</Badge>;
      case 'BLOCKED':
        return <Badge variant="destructive">Bị khóa</Badge>;
      case 'INACTIVE':
        return <Badge variant="warning">Chưa kích hoạt</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  const columns: Column<UserProfile>[] = [
    {
      header: 'ID',
      className: 'w-16 text-center text-xs text-neutral-400 font-mono',
      cell: (user) => `#${user.id}`,
    },
    {
      header: 'Người dùng',
      cell: (user) => {
        const isOnline = !!onlineStatusMap[user.id];
        return (
          <div className="flex items-center gap-3">
            <div className="relative shrink-0">
              <Avatar
                src={user.avatarUrl}
                alt={user.fullName}
                size="sm"
                className="size-8"
              />
              <span
                className={`absolute bottom-0 right-0 size-2.5 rounded-full ring-2 ring-white ${
                  isOnline ? 'bg-emerald-500 animate-pulse' : 'bg-neutral-300'
                }`}
                title={isOnline ? 'Đang online' : 'Ngoại tuyến'}
              />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <p className="font-semibold text-xs text-neutral-900">{user.fullName}</p>
                {isOnline && (
                  <span className="size-1.5 rounded-full bg-emerald-500 shrink-0" />
                )}
              </div>
              <p className="text-[11px] text-neutral-400">{user.email}</p>
            </div>
          </div>
        );
      },
    },
    {
      header: 'Số điện thoại',
      cell: (user) => (
        <span className="text-xs text-neutral-600 font-mono">
          {user.phoneNumber || '—'}
        </span>
      ),
    },
    {
      header: 'Vai trò',
      cell: (user) => (
        <Badge
          variant={user.role?.code === 'ADMIN' ? 'lime' : 'secondary'}
          className="text-[11px]"
        >
          {user.role?.name || user.role?.code || 'Customer'}
        </Badge>
      ),
    },
    {
      header: 'Điểm thưởng',
      cell: (user) => (
        <span className="text-xs font-semibold text-neutral-800 flex items-center gap-1">
          <Sparkles className="size-3 text-amber-500 fill-amber-500" />
          {(user.rewardPoints || 0).toLocaleString('vi-VN')}
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (user) => getStatusBadge(user.status),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (user) => (
        <div className="flex items-center justify-end gap-1">
          <button
            type="button"
            onClick={() => setSelectedUser(user)}
            title="Xem chi tiết"
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 transition"
          >
            <Eye className="size-4" />
          </button>

          {user.status === 'ACTIVE' ? (
            <button
              type="button"
              onClick={() =>
                setStatusChangeTarget({ user, newStatus: 'BLOCKED' })
              }
              title="Khóa tài khoản"
              className="p-1.5 rounded-lg text-neutral-400 hover:text-amber-600 hover:bg-amber-50 transition"
            >
              <Ban className="size-4" />
            </button>
          ) : (
            <button
              type="button"
              onClick={() =>
                setStatusChangeTarget({ user, newStatus: 'ACTIVE' })
              }
              title="Kích hoạt lại"
              className="p-1.5 rounded-lg text-neutral-400 hover:text-emerald-600 hover:bg-emerald-50 transition"
            >
              <CheckCircle2 className="size-4" />
            </button>
          )}

          <button
            type="button"
            onClick={() => setDeleteTargetUser(user)}
            title="Xóa người dùng"
            className="p-1.5 rounded-lg text-neutral-400 hover:text-rose-600 hover:bg-rose-50 transition"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">QUẢN TRỊ TÀI KHOẢN</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Người dùng
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Xem danh sách tài khoản khách hàng và nhân viên, cập nhật trạng thái hoạt động.
          </p>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-4 flex flex-col sm:flex-row items-center justify-between gap-4 shadow-xs">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo tên hoặc email người dùng..."
          className="max-w-md"
        />

        <div className="flex items-center gap-3 w-full sm:w-auto">
          <div className="flex items-center gap-1.5 text-xs text-neutral-500 shrink-0">
            <Filter className="size-3.5" />
            <span>Trạng thái:</span>
          </div>
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value as UserStatus | '');
              setPage(1);
            }}
            className="rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs font-medium text-neutral-800 outline-none transition focus:border-neutral-900 shadow-xs cursor-pointer"
          >
            <option value="">Tất cả trạng thái</option>
            <option value="ACTIVE">Hoạt động (ACTIVE)</option>
            <option value="INACTIVE">Chưa kích hoạt (INACTIVE)</option>
            <option value="BLOCKED">Bị khóa (BLOCKED)</option>
          </select>
        </div>
      </div>

      {/* Data Table */}
      <DataTable
        columns={columns}
        data={Array.isArray(usersData?.data) ? usersData.data : []}
        isLoading={isLoading}
        page={page}
        totalPages={usersData?.totalPages || 1}
        totalItems={usersData?.totalItems}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy người dùng nào phù hợp với bộ lọc."
      />

      {/* User Details Modal */}
      <Dialog open={!!selectedUser} onOpenChange={(open) => !open && setSelectedUser(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold text-neutral-900">
              Chi tiết người dùng #{selectedUser?.id}
            </DialogTitle>
          </DialogHeader>

          {selectedUser && (
            <div className="space-y-5 pt-2">
              <div className="flex items-center gap-4 p-4 rounded-xl bg-neutral-50 border border-[#EAE6DF]">
                <Avatar
                  src={selectedUser.avatarUrl}
                  alt={selectedUser.fullName}
                  size="lg"
                  className="size-14"
                />
                <div>
                  <h3 className="font-semibold text-sm text-neutral-900">
                    {selectedUser.fullName}
                  </h3>
                  <p className="text-xs text-neutral-500">{selectedUser.email}</p>
                  <div className="mt-1 flex gap-2">
                    {getStatusBadge(selectedUser.status)}
                    <Badge variant="lime" className="text-[10px]">
                      {selectedUser.role?.name || 'Customer'}
                    </Badge>
                  </div>
                </div>
              </div>

              <div className="space-y-3 text-xs">
                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Laptop className="size-3.5" /> Trạng thái trực tuyến
                  </span>
                  <div className="flex items-center gap-2">
                    {onlineStatusMap[selectedUser.id] || selectedUserSessions.length > 0 ? (
                      <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-50 text-emerald-700 border border-emerald-200">
                        <span className="size-2 rounded-full bg-emerald-500 animate-pulse" />
                        Đang online ({selectedUserSessions.length} tab/thiết bị)
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-neutral-100 text-neutral-500 border border-neutral-200">
                        <span className="size-2 rounded-full bg-neutral-400" />
                        Ngoại tuyến
                      </span>
                    )}
                  </div>
                </div>

                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Phone className="size-3.5" /> Số điện thoại
                  </span>
                  <span className="font-medium text-neutral-800">
                    {selectedUser.phoneNumber || 'Chưa cung cấp'}
                  </span>
                </div>

                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Sparkles className="size-3.5" /> Điểm thưởng tích lũy
                  </span>
                  <span className="font-bold text-neutral-900">
                    {(selectedUser.rewardPoints || 0).toLocaleString('vi-VN')} điểm
                  </span>
                </div>

                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Clock className="size-3.5" /> Ngày tạo tài khoản
                  </span>
                  <span className="font-medium text-neutral-800">
                    {selectedUser.createdAt
                      ? new Date(selectedUser.createdAt).toLocaleDateString('vi-VN')
                      : '—'}
                  </span>
                </div>

                {selectedUserSessions.length > 0 && (
                  <div className="p-3 bg-neutral-50 rounded-xl border border-neutral-100 space-y-1.5">
                    <span className="font-bold text-[10px] text-neutral-400 uppercase tracking-wider block">
                      Các phiên Socket đang hoạt động ({selectedUserSessions.length})
                    </span>
                    {selectedUserSessions.map((s) => (
                      <div key={s.socketId} className="flex items-center justify-between text-[11px] font-mono text-neutral-600">
                        <span className="truncate max-w-[200px]">{s.socketId}</span>
                        <span className="text-[10px] text-neutral-400">
                          {new Date(s.connectedAt).toLocaleTimeString('vi-VN')}
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Change Status Dialog */}
      <ConfirmDialog
        open={!!statusChangeTarget}
        onOpenChange={(open) => !open && setStatusChangeTarget(null)}
        title={statusChangeTarget?.newStatus === 'BLOCKED' ? 'Khóa tài khoản người dùng' : 'Kích hoạt lại tài khoản'}
        description={`Bạn có chắc chắn muốn thay đổi trạng thái của tài khoản ${statusChangeTarget?.user.fullName} sang "${statusChangeTarget?.newStatus}"?`}
        variant={statusChangeTarget?.newStatus === 'BLOCKED' ? 'destructive' : 'default'}
        confirmText="Xác nhận thay đổi"
        isLoading={updateStatusMutation.isPending}
        onConfirm={() => {
          if (statusChangeTarget) {
            updateStatusMutation.mutate({
              userId: statusChangeTarget.user.id,
              status: statusChangeTarget.newStatus,
            });
          }
        }}
      />

      {/* Confirm Delete User Dialog */}
      <ConfirmDialog
        open={!!deleteTargetUser}
        onOpenChange={(open) => !open && setDeleteTargetUser(null)}
        title="Xóa vĩnh viễn người dùng"
        description={`Cảnh báo: Hành động này sẽ xóa người dùng "${deleteTargetUser?.fullName}" (${deleteTargetUser?.email}) khỏi cơ sở dữ liệu. Thao tác không thể hoàn tác.`}
        variant="destructive"
        confirmText="Xóa người dùng"
        isLoading={deleteUserMutation.isPending}
        onConfirm={() => {
          if (deleteTargetUser) {
            deleteUserMutation.mutate(deleteTargetUser.id);
          }
        }}
      />
    </div>
  );
}


```

# admin\vouchers\page.tsx

| Property | Value |
|----------|-------|
| Path | `admin\vouchers\page.tsx` |
| Size | 21,030 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 13:58:31.866965 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  Ticket,
  Plus,
  Edit3,
  Trash2,
  Power,
  CheckCircle2,
  XCircle,
  Percent,
  DollarSign,
} from 'lucide-react';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { SearchInput } from '@/components/ui/search-input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { updateVoucher, deleteVoucher } from '@/modules/loyalty-engage/api';
import { formatCurrency } from '@/lib/utils/format-currency';
import { formatDate } from '@/lib/utils/format-date';
import type { Voucher } from '@/types';

export default function AdminVouchersPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isCreateOpen, setIsCreateOpen] = React.useState(false);
  const [editingVoucher, setEditingVoucher] = React.useState<Voucher | null>(null);
  const [deleteTarget, setDeleteTarget] = React.useState<Voucher | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-vouchers', page, search],
    queryFn: async () => {
      const res = await apiClient.get<any>(API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS, {
        params: { page, limit: 10, search: search || undefined },
      });
      return res.data;
    },
  });

  const defaultFormValues = React.useMemo(() => {
    const now = new Date();
    const nextWeek = new Date(now.getTime() + 7 * 86400000);

    return {
      code: '',
      discountType: 'FIXED_AMOUNT',
      discountValue: 50000,
      minOrderValue: 200000,
      totalUsageLimit: 100,
      userUsageLimit: 1,
      startDate: now.toISOString().slice(0, 16),
      endDate: nextWeek.toISOString().slice(0, 16),
    };
  }, []);

  const { register, handleSubmit, reset, setValue } = useForm({
    defaultValues: defaultFormValues,
  });

  // Create mutation
  const createMutation = useMutation({
    mutationFn: (values: any) =>
      apiClient.post(API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS, {
        ...values,
        code: values.code.trim().toUpperCase(),
        discountValue: Number(values.discountValue),
        minOrderValue: Number(values.minOrderValue),
        totalUsageLimit: Number(values.totalUsageLimit),
        userUsageLimit: Number(values.userUsageLimit),
        startDate: new Date(values.startDate).toISOString(),
        endDate: new Date(values.endDate).toISOString(),
      }),
    onSuccess: () => {
      toast.success('Tạo Voucher mới thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
      setIsCreateOpen(false);
      reset(defaultFormValues);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Tạo Voucher thất bại');
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: ({ id, values }: { id: number; values: any }) =>
      updateVoucher(id, values),
    onSuccess: () => {
      toast.success('Cập nhật Voucher thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
      setEditingVoucher(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật Voucher thất bại');
    },
  });

  // Toggle active mutation
  const toggleActiveMutation = useMutation({
    mutationFn: ({ id, isActive }: { id: number; isActive: boolean }) =>
      updateVoucher(id, { isActive }),
    onSuccess: (_, variables) => {
      toast.success(variables.isActive ? 'Đã kích hoạt mã Voucher!' : 'Đã vô hiệu hóa mã Voucher!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Thao tác thất bại');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteVoucher(id),
    onSuccess: () => {
      toast.success('Đã xóa Voucher thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-vouchers'] });
      setDeleteTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa Voucher thất bại');
    },
  });

  const handleOpenEdit = (v: Voucher) => {
    setEditingVoucher(v);
    setValue('code', v.code);
    setValue('discountType', v.discountType);
    setValue('discountValue', Number(v.discountValue));
    setValue('minOrderValue', Number(v.minOrderValue));
    setValue('totalUsageLimit', v.totalUsageLimit);
    setValue('userUsageLimit', v.userUsageLimit);
    setValue(
      'startDate',
      v.startDate ? new Date(v.startDate).toISOString().slice(0, 16) : ''
    );
    setValue(
      'endDate',
      v.endDate ? new Date(v.endDate).toISOString().slice(0, 16) : ''
    );
  };

  const vouchers: Voucher[] = Array.isArray(data?.data?.data)
    ? data.data.data
    : Array.isArray(data?.data)
    ? data.data
    : [];

  const totalPages = data?.data?.totalPages ?? data?.totalPages ?? 1;
  const totalItems = data?.data?.totalItems ?? data?.totalItems ?? 0;

  const columns: Column<Voucher>[] = [
    {
      header: 'Mã Voucher',
      cell: (v) => (
        <div className="flex items-center gap-2">
          <Ticket className="size-4 text-amber-500" />
          <span className="font-mono font-bold text-xs text-neutral-900 bg-neutral-100 px-2.5 py-1 rounded-md">
            {v.code}
          </span>
        </div>
      ),
    },
    {
      header: 'Loại giảm giá',
      cell: (v) => (
        <span className="text-xs font-semibold text-neutral-800">
          {v.discountType === 'PERCENTAGE'
            ? `${v.discountValue}%`
            : formatCurrency(v.discountValue)}
        </span>
      ),
    },
    {
      header: 'Đơn tối thiểu',
      cell: (v) => <span className="text-xs text-neutral-500">{formatCurrency(v.minOrderValue)}</span>,
    },
    {
      header: 'Lượt sử dụng',
      cell: (v) => (
        <span className="text-xs font-mono">
          {v.usedCount} / {v.totalUsageLimit || '∞'}
        </span>
      ),
    },
    {
      header: 'Thời hạn',
      cell: (v) => (
        <div className="text-[11px] text-neutral-500 leading-tight">
          <p>{formatDate(v.startDate)}</p>
          <p className="text-neutral-400">đến {formatDate(v.endDate)}</p>
        </div>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (v) => (
        <Badge variant={v.isActive ? 'success' : 'outline'} className="text-[10px]">
          {v.isActive ? 'Đang bật' : 'Vô hiệu hóa'}
        </Badge>
      ),
    },
    {
      header: 'Thao tác',
      className: 'text-right',
      cell: (v) => (
        <div className="flex justify-end items-center gap-1">
          <Button
            size="sm"
            variant="outline"
            onClick={() => toggleActiveMutation.mutate({ id: v.id, isActive: !v.isActive })}
            disabled={toggleActiveMutation.isPending}
            className={`h-8 px-2 text-xs rounded-lg transition ${
              v.isActive
                ? 'text-amber-700 hover:bg-amber-50 border-amber-200'
                : 'text-emerald-700 hover:bg-emerald-50 border-emerald-200'
            }`}
            title={v.isActive ? 'Vô hiệu hóa Voucher' : 'Kích hoạt Voucher'}
          >
            <Power className="size-3.5 mr-1" />
            {v.isActive ? 'Tắt' : 'Bật'}
          </Button>

          <button
            type="button"
            onClick={() => handleOpenEdit(v)}
            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
            title="Sửa Voucher"
          >
            <Edit3 className="size-4" />
          </button>

          <button
            type="button"
            onClick={() => setDeleteTarget(v)}
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
            title="Xóa Voucher"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">MARKETING</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Voucher & Khuyến mãi
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Cấu hình mã giảm giá, kiểm soát thời hạn và kích hoạt/vô hiệu hóa linh hoạt.
          </p>
        </div>
        <Button
          onClick={() => {
            reset(defaultFormValues);
            setIsCreateOpen(true);
          }}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white flex items-center gap-2"
        >
          <Plus className="size-4" /> Tạo Voucher mới
        </Button>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-[#EAE6DF] shadow-xs">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo mã Voucher..."
          className="max-w-md"
        />
      </div>

      <DataTable
        columns={columns}
        data={vouchers}
        isLoading={isLoading}
        page={page}
        totalPages={totalPages}
        totalItems={totalItems}
        onPageChange={setPage}
        emptyMessage="Chưa có mã giảm giá nào."
      />

      {/* Modal Tạo Voucher */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Ticket className="size-5 text-amber-500" />
              Tạo mã Voucher mới
            </DialogTitle>
          </DialogHeader>

          <form
            onSubmit={handleSubmit((d) => createMutation.mutate(d))}
            className="space-y-4 mt-2 text-xs"
          >
            <div>
              <label className="font-bold text-neutral-600 uppercase block mb-1">Mã Code *</label>
              <input
                {...register('code', { required: true })}
                placeholder="VD: SALE50K, TET2026..."
                className="w-full rounded-xl border border-[#EAE6DF] px-4 py-2 font-mono uppercase text-sm outline-none focus:border-neutral-900"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">Loại giảm *</label>
                <select
                  {...register('discountType')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs bg-white outline-none"
                >
                  <option value="FIXED_AMOUNT">Số tiền cố định (VNĐ)</option>
                  <option value="PERCENTAGE">Phần trăm (%)</option>
                </select>
              </div>
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Giá trị giảm *
                </label>
                <input
                  type="number"
                  {...register('discountValue', { required: true, min: 1 })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Đơn tối thiểu (VNĐ)
                </label>
                <input
                  type="number"
                  {...register('minOrderValue')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Tổng lượt dùng tối đa
                </label>
                <input
                  type="number"
                  {...register('totalUsageLimit')}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none focus:border-neutral-900"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Ngày bắt đầu *
                </label>
                <input
                  type="datetime-local"
                  {...register('startDate', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                />
              </div>
              <div>
                <label className="font-bold text-neutral-600 uppercase block mb-1">
                  Ngày kết thúc *
                </label>
                <input
                  type="datetime-local"
                  {...register('endDate', { required: true })}
                  className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                />
              </div>
            </div>

            <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsCreateOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="rounded-xl bg-[#1D1F20] text-white"
              >
                {createMutation.isPending ? 'Đang tạo...' : 'Tạo Voucher'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal Sửa Voucher */}
      <Dialog
        open={!!editingVoucher}
        onOpenChange={(open) => !open && setEditingVoucher(null)}
      >
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900 flex items-center gap-2">
              <Edit3 className="size-5 text-neutral-700" />
              Sửa mã Voucher #{editingVoucher?.code}
            </DialogTitle>
          </DialogHeader>

          {editingVoucher && (
            <form
              onSubmit={handleSubmit((d) =>
                updateMutation.mutate({
                  id: editingVoucher.id,
                  values: {
                    discountType: d.discountType,
                    discountValue: Number(d.discountValue),
                    minOrderValue: Number(d.minOrderValue),
                    totalUsageLimit: Number(d.totalUsageLimit),
                    startDate: d.startDate ? new Date(d.startDate).toISOString() : undefined,
                    endDate: d.endDate ? new Date(d.endDate).toISOString() : undefined,
                  },
                })
              )}
              className="space-y-4 mt-2 text-xs"
            >
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">Loại giảm *</label>
                  <select
                    {...register('discountType')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs bg-white outline-none"
                  >
                    <option value="FIXED_AMOUNT">Số tiền cố định (VNĐ)</option>
                    <option value="PERCENTAGE">Phần trăm (%)</option>
                  </select>
                </div>
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Giá trị giảm *
                  </label>
                  <input
                    type="number"
                    {...register('discountValue', { required: true })}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Đơn tối thiểu (VNĐ)
                  </label>
                  <input
                    type="number"
                    {...register('minOrderValue')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Tổng lượt dùng
                  </label>
                  <input
                    type="number"
                    {...register('totalUsageLimit')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Ngày bắt đầu
                  </label>
                  <input
                    type="datetime-local"
                    {...register('startDate')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
                <div>
                  <label className="font-bold text-neutral-600 uppercase block mb-1">
                    Ngày kết thúc
                  </label>
                  <input
                    type="datetime-local"
                    {...register('endDate')}
                    className="w-full rounded-xl border border-[#EAE6DF] px-3 py-2 text-xs outline-none"
                  />
                </div>
              </div>

              <DialogFooter className="mt-6 flex justify-end gap-2 pt-2 border-t border-[#EAE6DF]">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingVoucher(null)}
                  className="rounded-xl border-[#EAE6DF]"
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  disabled={updateMutation.isPending}
                  className="rounded-xl bg-[#1D1F20] text-white"
                >
                  {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                </Button>
              </DialogFooter>
            </form>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title={`Xóa mã Voucher "${deleteTarget?.code}"?`}
        description="Bạn có chắc chắn muốn xóa mã giảm giá này? Thao tác này không thể hoàn tác."
        confirmText="Xóa Voucher"
        cancelText="Hủy"
        variant="destructive"
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
      />
    </div>
  );
}
```

# favicon.ico

| Property | Value |
|----------|-------|
| Path | `favicon.ico` |
| Size | 25,931 bytes |
| Extension | `.ico` |
| Modified | 2026-09-29 13:25:28.291414 |

> **File nhị phân** (đã bỏ qua nội dung)

# globals.css

| Property | Value |
|----------|-------|
| Path | `globals.css` |
| Size | 948 bytes |
| Extension | `.css` |
| Modified | 2026-10-01 17:02:22.908285 |

```css
@import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;500;600;700&family=Inter:wght@400;500;600;700&display=swap');
@import 'tailwindcss';
@import 'tw-animate-css';
:root {
  --cream: #fbf9f4;
  --ink: #1d1f20;
  --lime: #84ea53;
  --line: #eae6df;
}
* {
  box-sizing: border-box;
}
html {
  background: var(--cream);
}
body {
  margin: 0;
  background: var(--cream);
  color: var(--ink);
  font-family: 'Inter', Arial, sans-serif;
}
a {
  color: inherit;
  text-decoration: none;
}
button,
input {
  font: inherit;
}
.font-display {
  font-family: 'Cormorant Garamond', Georgia, serif;
}
.eyebrow {
  color: #74736e;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}
.section-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: clamp(2.2rem, 5vw, 3.5rem);
  line-height: 0.9;
  letter-spacing: -0.035em;
}

```

# layout.tsx

| Property | Value |
|----------|-------|
| Path | `layout.tsx` |
| Size | 1,044 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.485050 |

```tsx
import type { Metadata } from 'next';
import { QueryProvider } from '@/components/providers/query-provider';
import { AuthProvider } from '@/components/providers/auth-provider';
import { SocketProvider } from '@/components/providers/socket-provider';
import { LiveChatWidget } from './LiveChatWidget';
import { Toaster } from 'sonner';
import './globals.css';

export const metadata: Metadata = {
  title: 'Fashion Store — Considered Goods',
  description: 'A curated edit of fashion and everyday objects.',
};

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="vi">
      <body className="bg-[#FBF9F4] text-[#1D1F20] antialiased">
        <QueryProvider>
          <AuthProvider>
            <SocketProvider>
              {children}
              <LiveChatWidget />
              <Toaster position="top-right" richColors />
            </SocketProvider>
          </AuthProvider>
        </QueryProvider>
      </body>
    </html>
  );
}
```

# LiveChatWidget.tsx

| Property | Value |
|----------|-------|
| Path | `LiveChatWidget.tsx` |
| Size | 105 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-12 22:18:00.161384 |

```tsx
'use client';

export { LiveChatWidget } from '@/modules/loyalty-engage/components/live-chat-widget';

```

# page.tsx

| Property | Value |
|----------|-------|
| Path | `page.tsx` |
| Size | 10,437 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 19:23:36.353660 |

```tsx
'use client';

import Link from 'next/link';
import Image from 'next/image';
import { ChevronRight, ShoppingBag, ArrowRight, Layers } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { catalogApi } from '@/api/catalog.api';
import { ProductCard } from '@/modules/catalog/components/product-card';
import { FlashSaleSection } from '@/modules/flash-sale/components/flash-sale-section';
import { Skeleton } from '@/components/ui/skeleton';
import type { Product } from '@/modules/catalog/mock';
import { SiteHeader } from '@/components/layouts/site-header';
import { SiteFooter } from '@/components/layouts/site-footer'; // Nhớ import Footer để đủ khung nền

// Danh sách ảnh fallback minh họa chất lượng cao khi danh mục chưa cấu hình iconUrl
const CATEGORY_IMAGES = [
  'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80',
];

export default function Home() {
  // 1. Fetch danh mục từ server
  const { data: categoryResponse, isLoading: isLoadingCategories } = useQuery({
    queryKey: ['home-categories'],
    queryFn: () => catalogApi.getCategories(),
  });

  // 2. Fetch danh sách sản phẩm từ server
  const { data: productResponse, isLoading: isLoadingProducts } = useQuery({
    queryKey: ['home-products'],
    queryFn: () => catalogApi.getProducts({ limit: 8, status: 'PUBLISHED' }),
  });

  const categories = categoryResponse?.data || [];
  const apiProducts = productResponse?.data || [];

  // Map dữ liệu sản phẩm từ Server API sang model hiển thị của ProductCard
  const products: Product[] = apiProducts.map((p: any) => {
    const firstVariant = p.variants?.[0];
    const originalPrice = firstVariant?.originalPrice ? Number(firstVariant.originalPrice) : 350000;
    const sellingPrice = firstVariant?.sellingPrice ? Number(firstVariant.sellingPrice) : undefined;
    const images =
      p.images && p.images.length > 0
        ? p.images.map((img: any) => img.imageUrl)
        : [p.thumbnailUrl || 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80'];

    return {
      slug: String(p.id),
      name: p.defaultName, 
      brand: p.brand?.defaultName || 'Fashion Studio',
      category: p.category?.defaultName || 'Thời trang',
      price: sellingPrice || originalPrice,
      originalPrice: sellingPrice && sellingPrice < originalPrice ? originalPrice : undefined,
      flashSalePrice: undefined,
      rating: 5.0,
      reviews: 12,
      tag: p.tags?.[0]?.name || undefined,
      images,
      colors: ['#1D1F20', '#EAE6DF', '#a38a75'],
      sizes: ['S', 'M', 'L', 'XL'],
      description: p.defaultName,
    };
  });

  return (
    <main className="min-h-screen flex flex-col bg-[#FBF9F4]">
      <SiteHeader />

      {/* ========================================= */}
      {/* 1. HERO SECTION (Banner trên cùng cực cháy) */}
      {/* ========================================= */}
      <section className="relative overflow-hidden bg-[#1D1F20] text-white py-24 px-4 sm:px-8 lg:px-12 text-center">
        {/* Background Image với lớp phủ mờ */}
        <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1445205170230-053b83016050?q=80&w=2000&auto=format&fit=crop')] bg-cover bg-center opacity-20" />
        
        <div className="relative z-10 max-w-3xl mx-auto space-y-6">
          <h1 className="font-display text-5xl sm:text-7xl font-bold tracking-tight">
            Thời Trang Đương Đại
          </h1>
          <p className="text-neutral-300 text-lg sm:text-xl">
            Khám phá các bộ sưu tập tối giản, tinh tế, phù hợp cho mọi khoảnh khắc trong cuộc sống của bạn.
          </p>
          <div className="pt-4">
            <Link 
              href="/products" 
              className="inline-flex items-center gap-2 bg-[#84EA53] text-[#1D1F20] px-8 py-3.5 rounded-full font-bold hover:bg-[#72d244] transition-transform hover:scale-105 active:scale-95"
            >
              Khám phá ngay <ArrowRight className="size-4" />
            </Link>
          </div>
        </div>
      </section>

      {/* ========================================= */}
      {/* 2. FLASH SALE SECTION (Khuyến mãi Giờ Vàng) */}
      {/* ========================================= */}
      {/* Component này sẽ tự gọi API lấy danh sách Flash Sale đang diễn ra và tự ẩn nếu không có */}
      <FlashSaleSection />

      {/* ========================================= */}
      {/* 3. CATEGORIES SECTION (Danh mục nổi bật)  */}
      {/* ========================================= */}
      <section className="mx-auto w-full max-w-[1440px] px-4 py-12 sm:px-8 lg:px-12 lg:py-16">
        <div className="mb-7 flex items-end justify-between border-b border-[#EAE6DF] pb-4">
          <div>
            <p className="eyebrow text-[#74736e]">BROWSE THE EDIT</p>
            <h2 className="section-title mt-1 text-[#1D1F20]">Danh mục mua sắm</h2>
          </div>
          <Link
            href="/products"
            className="hidden items-center gap-1 text-sm font-semibold sm:flex hover:text-[#1D1F20] text-neutral-500 transition-colors"
          >
            Tất cả danh mục <ChevronRight size={16} />
          </Link>
        </div>

        {isLoadingCategories ? (
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-4 lg:grid-cols-6">
            {Array.from({ length: 6 }).map((_, i) => (
              <Skeleton key={i} className="min-h-[160px] rounded-2xl sm:min-h-[200px]" />
            ))}
          </div>
        ) : categories.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-[#EAE6DF] bg-white p-8 text-center text-sm text-neutral-500">
            Chưa có danh mục nào từ máy chủ.
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-4 lg:grid-cols-6">
            {categories.slice(0, 6).map((category, index) => {
              const name = category.defaultName;
              const image = category.iconUrl || CATEGORY_IMAGES[index % CATEGORY_IMAGES.length];

              return (
                <Link
                  key={category.id}
                  href={`/products?search=${encodeURIComponent(name)}`} // Chuyển sang tìm kiếm theo tên
                  className="group relative min-h-[160px] overflow-hidden rounded-2xl bg-white border border-[#EAE6DF] sm:min-h-[200px]"
                >
                  <Image
                    src={image}
                    alt={name}
                    fill
                    sizes="(max-width: 640px) 50vw, (max-width: 1024px) 25vw, 16vw"
                    className="object-cover transition-transform duration-700 group-hover:scale-110"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent opacity-80" />
                  
                  <div className="absolute bottom-4 left-4 right-4 text-center">
                    <p className="font-display text-lg sm:text-xl font-bold !text-white [&_*]:!text-white tracking-wide">
                      {name}
                    </p>
                    <span className="inline-block mt-1.5 opacity-0 -translate-y-2 group-hover:opacity-100 group-hover:translate-y-0 transition-all duration-300 text-[10px] uppercase font-bold text-[#84EA53] bg-white/20 backdrop-blur px-2 py-0.5 rounded-full">
                      Khám phá
                    </span>
                  </div>
                </Link>
              );
            })}
          </div>
        )}
      </section>

      {/* ========================================= */}
      {/* 4. PRODUCTS SECTION (Sản phẩm nổi bật)    */}
      {/* ========================================= */}
      <section className="mx-auto w-full max-w-[1440px] px-4 pb-24 sm:px-8 lg:px-12">
        <div className="mb-7 flex items-end justify-between border-b border-[#EAE6DF] pb-4">
          <div>
            <p className="eyebrow text-[#74736e]">NEW ARRIVALS</p>
            <h2 className="section-title mt-1 text-[#1D1F20]">Sản phẩm mới nhất</h2>
          </div>
          <Link
            href="/products"
            className="flex items-center gap-1 text-sm font-semibold hover:text-[#1D1F20] text-neutral-500 transition-colors"
          >
            Xem tất cả <ChevronRight size={16} />
          </Link>
        </div>

        {isLoadingProducts ? (
          <div className="grid grid-cols-2 gap-x-4 gap-y-10 sm:grid-cols-3 lg:grid-cols-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="space-y-3">
                <Skeleton className="aspect-[.88] w-full rounded-2xl" />
                <Skeleton className="h-4 w-3/4 rounded" />
                <Skeleton className="h-4 w-1/2 rounded" />
              </div>
            ))}
          </div>
        ) : products.length === 0 ? (
          <div className="py-16 text-center bg-white rounded-3xl border border-dashed border-[#EAE6DF]">
            <ShoppingBag className="mx-auto size-12 text-neutral-300 mb-4" />
            <p className="text-sm font-medium text-neutral-600">Hiện chưa có sản phẩm nào.</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-x-4 gap-y-10 sm:grid-cols-3 lg:grid-cols-4">
            {products.slice(0, 8).map((product) => (
              <ProductCard key={product.slug} product={product} />
            ))}
          </div>
        )}
      </section>

      {/* Tích hợp Footer toàn trang */}
      <SiteFooter />
    </main>
  );
}
```

