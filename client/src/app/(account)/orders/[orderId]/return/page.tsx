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
