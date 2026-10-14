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
