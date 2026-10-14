"use client";

import React, { useState } from "react";
import useSWR, { useSWRConfig } from "swr";
import {
  Star,
  CheckCircle2,
  AlertCircle,
  Loader2,
  ChevronLeft,
  ChevronRight,
  MessageSquare,
  CornerDownRight,
  ShieldCheck,
  X
} from "lucide-react";
import {
  getReviewsByProduct,
  createReview,
  type ProductReviewItem,
} from "../api";

interface StarRatingProps {
  rating: number;
  maxStars?: number;
  size?: number;
  interactive?: boolean;
  onRatingChange?: (rating: number) => void;
}

export function StarRating({
  rating,
  maxStars = 5,
  size = 18,
  interactive = false,
  onRatingChange,
}: StarRatingProps) {
  const [hoverRating, setHoverRating] = useState<number | null>(null);

  const displayRating = hoverRating !== null ? hoverRating : rating;

  return (
    <div className="flex items-center gap-1">
      {Array.from({ length: maxStars }, (_, i) => {
        const starNumber = i + 1;
        const isFilled = starNumber <= displayRating;

        return (
          <button
            key={starNumber}
            type="button"
            disabled={!interactive}
            onClick={() => onRatingChange?.(starNumber)}
            onMouseEnter={() => interactive && setHoverRating(starNumber)}
            onMouseLeave={() => interactive && setHoverRating(null)}
            className={`transition-transform ${
              interactive
                ? "cursor-pointer hover:scale-110 focus:outline-none"
                : "cursor-default"
            }`}
            aria-label={`${starNumber} sao`}
          >
            <Star
              size={size}
              stroke="#1D1F20"
              strokeWidth={1.5}
              fill={isFilled ? "#1D1F20" : "transparent"}
              className="transition-colors"
            />
          </button>
        );
      })}
    </div>
  );
}

export interface ProductReviewListProps {
  productId: number;
}

export function ProductReviewList({ productId }: ProductReviewListProps) {
  const [page, setPage] = useState<number>(1);
  const [ratingFilter, setRatingFilter] = useState<number | undefined>(undefined);
  const [previewImage, setPreviewImage] = useState<string | null>(null);

  const swrKey = `/reviews?productId=${productId}&page=${page}&rating=${ratingFilter ?? ""}`;
  const { data: response, error, isLoading, mutate } = useSWR(
    swrKey,
    () => getReviewsByProduct({ productId, page, limit: 5, rating: ratingFilter }),
    {
      revalidateOnFocus: false,
      keepPreviousData: true,
    }
  );

  const reviews = response?.data ?? [];
  const ratingStats = response?.ratingStats ?? { averageRating: 0, totalReviews: 0 };
  const pagination = response?.pagination ?? { page: 1, limit: 5, total: 0, totalPages: 1 };

  return (
    <section className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-6 sm:p-8 space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6 pb-6 border-b border-[#EAE6DF]">
        <div>
          <h3 className="text-xl sm:text-2xl font-bold text-[#1D1F20] tracking-tight">
            Đánh giá sản phẩm
          </h3>
          <p className="text-sm text-[#74736e] mt-1">
            Nhận xét chân thực từ những khách hàng đã mua và trải nghiệm
          </p>
        </div>

        <div className="flex items-center gap-4 bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl px-5 py-3 self-start sm:self-auto">
          <div className="text-3xl font-extrabold text-[#1D1F20]">
            {ratingStats.averageRating > 0 ? ratingStats.averageRating.toFixed(1) : "5.0"}
          </div>
          <div>
            <StarRating rating={Math.round(ratingStats.averageRating) || 5} size={16} />
            <span className="text-xs text-[#74736e] mt-0.5 block">
              {ratingStats.totalReviews} lượt đánh giá
            </span>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2 overflow-x-auto pb-2 text-sm">
        <button
          type="button"
          onClick={() => {
            setRatingFilter(undefined);
            setPage(1);
          }}
          className={`px-4 py-1.5 rounded-full font-medium transition cursor-pointer border ${
            ratingFilter === undefined
              ? "bg-[#1D1F20] !text-white [&_*]:!text-white border-[#1D1F20]"
              : "bg-[#FBF9F4] text-[#1D1F20] border-[#EAE6DF] hover:border-[#1D1F20]"
          }`}
        >
          Tất cả
        </button>
        {[5, 4, 3, 2, 1].map((star) => (
          <button
            key={star}
            type="button"
            onClick={() => {
              setRatingFilter(star);
              setPage(1);
            }}
            className={`px-3 py-1.5 rounded-full font-medium flex items-center gap-1 transition cursor-pointer border ${
              ratingFilter === star
                ? "bg-[#1D1F20] !text-white [&_*]:!text-white border-[#1D1F20]"
                : "bg-[#FBF9F4] text-[#1D1F20] border-[#EAE6DF] hover:border-[#1D1F20]"
            }`}
          >
            <span>{star}</span>
            <Star size={12} fill={ratingFilter === star ? "#FFFFFF" : "#1D1F20"} stroke="none" />
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((skeletonId) => (
            <div
              key={skeletonId}
              className="p-5 rounded-xl border border-[#EAE6DF] bg-[#FBF9F4] animate-pulse space-y-3"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-[#EAE6DF]" />
                <div className="space-y-1.5 flex-1">
                  <div className="w-32 h-3.5 bg-[#EAE6DF] rounded" />
                  <div className="w-20 h-2.5 bg-[#EAE6DF] rounded" />
                </div>
              </div>
              <div className="w-full h-12 bg-[#EAE6DF]/60 rounded-lg" />
            </div>
          ))}
        </div>
      ) : error ? (
        <div className="p-8 text-center bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl space-y-3">
          <AlertCircle className="w-8 h-8 text-[#dc2626] mx-auto" />
          <p className="text-sm text-[#1D1F20] font-medium">
            Không thể tải danh sách đánh giá.
          </p>
          <button
            type="button"
            onClick={() => mutate()}
            className="px-4 py-2 text-xs font-semibold bg-[#1D1F20] !text-white [&_*]:!text-white rounded-xl hover:bg-black transition"
          >
            Thử lại
          </button>
        </div>
      ) : reviews.length === 0 ? (
        <div className="p-12 text-center bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl space-y-3">
          <MessageSquare className="w-10 h-10 text-[#74736e] mx-auto opacity-40" />
          <h4 className="text-base font-semibold text-[#1D1F20]">Chưa có đánh giá nào</h4>
          <p className="text-sm text-[#74736e] max-w-sm mx-auto">
            {ratingFilter !== undefined
              ? `Chưa có đánh giá ${ratingFilter} sao nào cho sản phẩm này.`
              : "Hãy là người đầu tiên trải nghiệm và chia sẻ cảm nhận về sản phẩm này nhé!"}
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {reviews.map((review: ProductReviewItem) => (
            <article
              key={review.id}
              className="p-5 sm:p-6 rounded-xl border border-[#EAE6DF] bg-[#FFFFFF] hover:border-[#84EA53] transition-colors space-y-4"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex items-center gap-3">
                  {review.user?.avatarUrl ? (
                    <img
                      src={review.user.avatarUrl}
                      alt={review.user.fullName || "User"}
                      className="w-10 h-10 rounded-full object-cover border border-[#EAE6DF]"
                    />
                  ) : (
                    <div className="w-10 h-10 rounded-full bg-[#FBF9F4] border border-[#EAE6DF] flex items-center justify-center font-bold text-sm text-[#1D1F20]">
                      {review.user?.fullName?.charAt(0).toUpperCase() || "U"}
                    </div>
                  )}

                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-[#1D1F20] text-sm">
                        {review.user?.fullName || "Khách hàng"}
                      </span>
                      <span className="inline-flex items-center gap-1 text-[11px] text-[#16a34a] font-medium bg-[#84EA53]/15 px-2 py-0.5 rounded-full">
                        <ShieldCheck size={12} />
                        Đã mua hàng
                      </span>
                    </div>
                    <time className="text-xs text-[#74736e] mt-0.5 block">
                      {new Date(review.createdAt).toLocaleDateString("vi-VN", {
                        year: "numeric",
                        month: "long",
                        day: "numeric",
                      })}
                    </time>
                  </div>
                </div>

                <StarRating rating={review.rating} size={15} />
              </div>

              <p className="text-sm text-[#1D1F20] leading-relaxed whitespace-pre-line">
                {review.content}
              </p>

              {review.medias && review.medias.length > 0 && (
                <div className="flex flex-wrap gap-2 pt-1">
                  {review.medias.map((media: any, idx: number) => {
                    const src = media.mediaUrl || media.url;
                    return (
                      <img
                        key={media.id || idx}
                        src={src}
                        alt="Ảnh đánh giá"
                        className="w-20 h-20 object-cover rounded-xl border border-[#EAE6DF] hover:opacity-90 hover:scale-105 transition-all cursor-pointer shadow-xs"
                        onClick={() => src && setPreviewImage(src)} // <-- Bấm để mở modal
                      />
                    );
                  })}
                </div>
              )}

              {review.shopReply && (
                <div className="mt-3 p-3.5 rounded-xl bg-[#FBF9F4] border-l-2 border-[#84EA53] text-xs space-y-1">
                  <div className="flex items-center gap-1.5 font-semibold text-[#1D1F20]">
                    <CornerDownRight size={14} className="text-[#84EA53]" />
                    <span>Phản hồi từ Người bán</span>
                  </div>
                  <p className="text-[#74736e] leading-relaxed pl-5">
                    {review.shopReply}
                  </p>
                </div>
              )}
            </article>
          ))}

          {pagination.totalPages > 1 && (
            <div className="flex items-center justify-between pt-4 border-t border-[#EAE6DF]">
              <button
                type="button"
                disabled={page <= 1}
                onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold border border-[#EAE6DF] rounded-xl bg-[#FFFFFF] text-[#1D1F20] hover:bg-[#FBF9F4] disabled:opacity-40 disabled:cursor-not-allowed transition"
              >
                <ChevronLeft size={14} />
                Trang trước
              </button>

              <span className="text-xs text-[#74736e] font-medium">
                Trang {pagination.page} / {pagination.totalPages}
              </span>

              <button
                type="button"
                disabled={page >= pagination.totalPages}
                onClick={() => setPage((prev) => Math.min(prev + 1, pagination.totalPages))}
                className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold border border-[#EAE6DF] rounded-xl bg-[#FFFFFF] text-[#1D1F20] hover:bg-[#FBF9F4] disabled:opacity-40 disabled:cursor-not-allowed transition"
              >
                Trang sau
                <ChevronRight size={14} />
              </button>
            </div>
          )}
        </div>
      )}
    {/* Khung Modal xem ảnh phóng to với hiệu ứng làm mờ nền */}
      {previewImage && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-4 animate-in fade-in duration-200"
          onClick={() => setPreviewImage(null)}
        >
          {/* Nút đóng góc trên bên phải */}
          <button
            type="button"
            onClick={() => setPreviewImage(null)}
            className="absolute top-5 right-5 p-2 rounded-full bg-white/10 hover:bg-white/20 text-white transition cursor-pointer"
            aria-label="Đóng xem ảnh"
          >
            <X size={24} />
          </button>

          {/* Vùng hiển thị ảnh (ngăn nổi bọt click để không bị tự đóng khi bấm vào ảnh) */}
          <div
            className="relative max-w-4xl max-h-[85vh] flex items-center justify-center"
            onClick={(e) => e.stopPropagation()}
          >
            <img
              src={previewImage}
              alt="Ảnh đánh giá phóng to"
              className="max-w-full max-h-[85vh] w-auto h-auto object-contain rounded-2xl shadow-2xl border border-white/15 animate-in zoom-in-95 duration-200"
            />
          </div>
        </div>
      )}
    </section>

    
  );
}

export interface ReviewFormProps {
  orderId: number;
  productId: number;
  onReviewSubmitted?: (review: ProductReviewItem) => void;
}

const RATING_LABELS: Record<number, string> = {
  1: "Rất không hài lòng",
  2: "Không hài lòng",
  3: "Bình thường",
  4: "Hài lòng",
  5: "Tuyệt vời",
};

export function ReviewForm({
  orderId,
  productId,
  onReviewSubmitted,
}: ReviewFormProps) {
  const { mutate } = useSWRConfig();
  const [rating, setRating] = useState<number>(5);
  const [content, setContent] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setSuccessMessage(null);

    const trimmedContent = content.trim();
    if (!trimmedContent) {
      setErrorMessage("Vui lòng viết nhận xét cảm nhận của bạn về sản phẩm.");
      return;
    }

    if (trimmedContent.length < 5) {
      setErrorMessage("Nội dung đánh giá tối thiểu 5 ký tự.");
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await createReview({
        productId,
        orderId,
        rating,
        content: trimmedContent,
      });

      mutate(
        (key: any) =>
          typeof key === "string" &&
          key.startsWith(`/reviews?productId=${productId}`)
      );

      setSuccessMessage("Cảm ơn bạn! Đánh giá của bạn đã được ghi nhận thành công.");
      setContent("");
      setRating(5);
      if (onReviewSubmitted && response.data) {
        onReviewSubmitted(response.data);
      }
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : "Không thể gửi đánh giá. Vui lòng thử lại sau.";
      setErrorMessage(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-6 sm:p-8 space-y-6 shadow-sm"
    >
      <div>
        <h4 className="text-xl font-bold text-[#1D1F20] tracking-tight">
          Viết đánh giá sản phẩm
        </h4>
        <p className="text-sm text-[#74736e] mt-1">
          Đơn hàng #{orderId} • Chia sẻ trải nghiệm để giúp đỡ cộng đồng mua sắm
        </p>
      </div>

      <div className="space-y-2 bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl p-4">
        <label className="text-xs font-bold text-[#74736e] uppercase tracking-wider block">
          Chất lượng sản phẩm
        </label>
        <div className="flex items-center gap-3">
          <StarRating
            rating={rating}
            size={24}
            interactive={!isSubmitting}
            onRatingChange={(newRating) => setRating(newRating)}
          />
          <span className="text-sm font-semibold text-[#1D1F20]">
            {RATING_LABELS[rating] || ""}
          </span>
        </div>
      </div>

      <div className="space-y-2">
        <label
          htmlFor="review-content"
          className="text-xs font-bold text-[#74736e] uppercase tracking-wider block"
        >
          Nhận xét chi tiết
        </label>
        <textarea
          id="review-content"
          rows={4}
          value={content}
          disabled={isSubmitting}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Hãy chia sẻ cảm nhận về chất liệu, form dáng, độ vừa vặn và dịch vụ giao hàng..."
          className="w-full rounded-xl border border-[#EAE6DF] bg-[#FFFFFF] p-4 text-sm text-[#1D1F20] placeholder-[#74736e]/60 focus:border-[#84EA53] outline-none transition resize-none"
          maxLength={2000}
        />
        <div className="flex justify-between text-xs text-[#74736e]">
          <span>Tối thiểu 5 ký tự</span>
          <span>{content.length}/2000 ký tự</span>
        </div>
      </div>

      {errorMessage && (
        <div className="flex items-center gap-2 p-3.5 rounded-xl bg-[#dc2626]/10 border border-[#dc2626]/20 text-[#dc2626] text-sm">
          <AlertCircle size={16} className="shrink-0" />
          <span>{errorMessage}</span>
        </div>
      )}

      {successMessage && (
        <div className="flex items-center gap-2 p-3.5 rounded-xl bg-[#84EA53]/15 border border-[#84EA53]/30 text-[#16a34a] text-sm font-medium">
          <CheckCircle2 size={16} className="shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      <div className="flex justify-end pt-2">
        <button
          type="submit"
          disabled={isSubmitting || !content.trim()}
          className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-xl bg-[#84EA53] text-[#1D1F20] font-bold text-sm hover:opacity-90 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed transition shadow-sm cursor-pointer"
        >
          {isSubmitting ? (
            <>
              <Loader2 size={16} className="animate-spin" />
              <span>Đang gửi đánh giá...</span>
            </>
          ) : (
            <span>Gửi đánh giá</span>
          )}
        </button>
      </div>
    </form>
  );
}
