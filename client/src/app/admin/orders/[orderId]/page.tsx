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