import { DiscountType, PointTransType } from './enums';
import { UserProfile } from './auth';
import { Product } from './catalog';

export type { FlashSaleItem, FlashSaleCampaign } from './inventory-sale';

export interface Voucher {
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

export interface PointTransaction {
  id: number;
  userId: number;
  orderId?: number | null;
  pointChange: number;
  currentPoint: number;
  transactionType: PointTransType;
  description: string;
  createdAt: string;
}

export interface ProductReview {
  id: number;
  productId: number;
  userId: number;
  orderId?: number | null;
  rating: number;
  content: string;
  shopReply?: string | null;
  updateCount: number;
  isHidden: boolean;
  createdAt: string;
  user?: UserProfile;
  product?: Product;
  medias?: Array<{
    id: number;
    mediaUrl: string;
    mediaType: string;
  }>;
}

// Khớp chuẩn model Chat & Message trong Prisma
export interface ChatSession {
  id: number;
  userId: number;
  status: 'OPEN' | 'WAITING' | 'CLOSED';
  startedAt: string;
  endedAt: string | null;
  createdAt: string;
  updatedAt: string;
  user?: Pick<UserProfile, 'id' | 'fullName' | 'avatarUrl'>;
  messages?: ChatMessage[];
}

export interface ChatMessage {
  id: number;
  chatId: number;
  senderId: number;
  content: string;
  isRead: boolean;
  sendDate: string;
  sender?: Pick<UserProfile, 'id' | 'fullName' | 'avatarUrl'>;
}