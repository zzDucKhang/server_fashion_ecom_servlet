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
