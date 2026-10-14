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