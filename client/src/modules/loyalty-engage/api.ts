import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import type { ChatMessage, ChatSession, ProductReview } from '@/types/loyalty-engage';

export type DiscountType = 'PERCENTAGE' | 'FIXED_AMOUNT';

export interface VoucherEntity {
  id: number;
  code: string;
  discountType: DiscountType;
  discountValue: number | string;
  minOrderValue: number | string;
  maxDiscountAmt?: number | string | null;
  totalUsageLimit: number;
  usedCount: number;
  userUsageLimit: number;
  startDate: string;
  endDate: string;
  isActive: boolean;
}

export interface VoucherValidationData {
  code: string;
  discountType: DiscountType;
  discountAmount: number;
  finalTotal: number;
  voucher: VoucherEntity;
}

export interface VoucherValidationResponse {
  statusCode: number;
  message: string;
  data: VoucherValidationData;
}

export interface PointBalanceData {
  rewardPoints: number;
}

export interface PointBalanceResponse {
  statusCode: number;
  data: PointBalanceData;
}

export interface RedeemPointsPayload {
  orderId: number;
  pointsToRedeem: number;
}

export interface RedeemPointsResponse {
  statusCode: number;
  message: string;
  discountAmount: number;
  currentPoint: number;
  transaction?: unknown;
}

export interface ReviewAuthor {
  id: number;
  fullName: string;
  avatarUrl: string | null;
}

export interface ReviewMediaItem {
  id?: number;
  url: string;
  type: string;
}

export interface ProductReviewItem {
  id: number;
  productId: number;
  orderId?: number | null;
  rating: number;
  content: string;
  shopReply: string | null;
  isHidden?: boolean;
  createdAt: string;
  updatedAt?: string;
  user: ReviewAuthor;
  medias: ReviewMediaItem[];
}

export interface RatingStats {
  averageRating: number;
  totalReviews: number;
}

export interface PaginationMeta {
  page: number;
  limit: number;
  total: number;
  totalPages: number;
}

export interface ProductReviewsResponse {
  statusCode: number;
  data: ProductReviewItem[];
  ratingStats: RatingStats;
  pagination: PaginationMeta;
}

export interface CreateReviewPayload {
  productId: number;
  orderId?: number;
  rating: number;
  content: string;
  mediaUrls?: ReviewMediaItem[];
}

export interface CreateReviewResponse {
  statusCode: number;
  message: string;
  data: ProductReviewItem;
}

export interface SocketHeartbeatResponse {
  statusCode: number;
  message: string;
  data?: {
    socketId: string;
    userId: number;
    extended: boolean;
  };
}

export interface ChatMessageItem {
  id?: number;
  senderId?: number;
  receiverId?: number;
  content: string;
  attachmentUrl?: string | null;
  createdAt?: string;
  sender?: {
    id: number;
    fullName: string;
    avatarUrl?: string | null;
  };
  isSelf?: boolean;
}

export interface ChatHistoryResponse {
  statusCode: number;
  data: ChatMessage[];
  pagination: {
    page: number;
    limit: number;
    nextCursor: number | null;
    hasMore: boolean;
  };
}

export interface ChatSessionResponse {
  statusCode: number;
  data: ChatSession;
}

export interface ChatConversationsResponse {
  statusCode: number;
  data: ChatSession[];
}

export async function validateVoucher(
  code: string,
  orderTotal: number,
): Promise<VoucherValidationResponse> {
  const res = await apiClient.post<VoucherValidationResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}/validate`,
    {
      code: code.trim().toUpperCase(),
      orderTotal,
    },
  );
  return res.data;
}

export async function getPointBalance(): Promise<PointBalanceResponse> {
  const res = await apiClient.get<PointBalanceResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/balance`,
  );
  return res.data;
}

export async function redeemPoints(
  orderId: number,
  pointsToRedeem: number,
): Promise<RedeemPointsResponse> {
  const res = await apiClient.post<RedeemPointsResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/redeem`,
    {
      orderId,
      pointsToRedeem,
    },
  );
  return res.data;
}

export async function getReviewsByProduct(params: {
  productId: number;
  page?: number;
  limit?: number;
  rating?: number;
}): Promise<ProductReviewsResponse> {
  const query = new URLSearchParams();
  query.append('productId', params.productId.toString());
  if (params.page) query.append('page', params.page.toString());
  if (params.limit) query.append('limit', params.limit.toString());
  if (params.rating) query.append('rating', params.rating.toString());

  const res = await apiClient.get<ProductReviewsResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS}?${query.toString()}`,
  );
  return res.data;
}

export async function createReview(
  payload: CreateReviewPayload,
): Promise<CreateReviewResponse> {
  const res = await apiClient.post<CreateReviewResponse>(
    API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS,
    payload,
  );
  return res.data;
}

export async function sendSocketHeartbeat(
  socketId: string,
): Promise<SocketHeartbeatResponse> {
  const res = await apiClient.post<SocketHeartbeatResponse>(
    API_ENDPOINTS.CHAT.HEARTBEAT,
    { socketId },
  );
  return res.data;
}

export async function replyReview(
  reviewId: number,
  shopReply: string,
): Promise<{ statusCode: number; data: ProductReview }> {
  const res = await apiClient.post<{ statusCode: number; data: ProductReview }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS}/${reviewId}/reply`,
    { shopReply },
  );
  return res.data;
}

export async function hideReview(
  reviewId: number,
  isHidden: boolean,
): Promise<{ statusCode: number; data: ProductReview }> {
  const res = await apiClient.patch<{ statusCode: number; data: ProductReview }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS}/${reviewId}/hide`,
    { isHidden },
  );
  return res.data;
}

export async function updateVoucher(
  voucherId: number,
  payload: any,
): Promise<{ statusCode: number; data: any }> {
  const res = await apiClient.patch<{ statusCode: number; data: any }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}/${voucherId}`,
    payload,
  );
  return res.data;
}

export async function deleteVoucher(
  voucherId: number,
): Promise<{ statusCode: number; message: string }> {
  const res = await apiClient.delete<{ statusCode: number; message: string }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}/${voucherId}`,
  );
  return res.data;
}

export async function getPointHistory(params?: {
  page?: number;
  limit?: number;
}): Promise<{
  data: any[];
  pagination: { page: number; limit: number; total: number; totalPages: number };
}> {
  const res = await apiClient.get<any>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/history`,
    { params },
  );
  return res.data;
}

export async function earnPoints(payload: {
  orderId: number;
  description?: string;
}): Promise<{ message: string; currentPoint: number; transaction: any }> {
  const res = await apiClient.post<any>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/earn`,
    payload,
  );
  return res.data;
}

export const chatApi = {
  getActiveChat: async (): Promise<ChatSessionResponse> => {
    const res = await apiClient.get<ChatSessionResponse>(
      `${API_ENDPOINTS.CHAT.ACTIVE}/active`,
    );
    return res.data;
  },
  getHistory: async (chatId: number, cursor?: number, limit = 100) => {
    const res = await apiClient.get<ChatHistoryResponse>(
      API_ENDPOINTS.CHAT.HISTORY,
      {
        params: { chatId, limit, cursor },
      },
    );
    return res.data;
  },
  getConversations: async (): Promise<ChatConversationsResponse> => {
    const res = await apiClient.get<ChatConversationsResponse>(
      API_ENDPOINTS.CHAT.CONVERSATIONS,
    );
    return res.data;
  },
  sendMessage: async (
    chatId: number,
    content: string,
  ): Promise<{ statusCode: number; data: ChatMessage }> => {
    const res = await apiClient.post<{ statusCode: number; data: ChatMessage }>(
      API_ENDPOINTS.CHAT.SEND,
      { chatId, content },
    );
    return res.data;
  },
  markAsRead: async (
    chatId: number,
  ): Promise<{ statusCode: number; updatedCount: number }> => {
    const res = await apiClient.post<{
      statusCode: number;
      updatedCount: number;
    }>(API_ENDPOINTS.CHAT.READ, { chatId });
    return res.data;
  },
  closeConversation: async (
    chatId: number,
  ): Promise<{ statusCode: number }> => {
    const res = await apiClient.post<{ statusCode: number }>(
      `${API_ENDPOINTS.CHAT.CONVERSATIONS}/${chatId}/close`,
    );
    return res.data;
  },
  getUserOnlineStatus: async (
    userId: number,
  ): Promise<{ statusCode: number; data: { userId: number; isOnline: boolean } }> => {
    const res = await apiClient.get<{
      statusCode: number;
      data: { userId: number; isOnline: boolean };
    }>(`/loyalty-engage/chat/sessions/${userId}/online`);
    return res.data;
  },
  getUserSessions: async (
    userId: number,
  ): Promise<{
    statusCode: number;
    data: Array<{ socketId: string; userId: number; connectedAt: string }>;
  }> => {
    const res = await apiClient.get<{
      statusCode: number;
      data: Array<{ socketId: string; userId: number; connectedAt: string }>;
    }>(`/loyalty-engage/chat/sessions/${userId}`);
    return res.data;
  },
};

export async function getAvailableVouchers(): Promise<{ data: VoucherEntity[] }> {
  const res = await apiClient.get<{ data: VoucherEntity[] }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}?isActive=true&limit=50`
  );
  return res.data;
}

