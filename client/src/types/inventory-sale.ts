import { BasePaginationQuery } from './common';

export interface FlashSaleItemVariant {
  id: number; 
  productId: number; 
  sku: string;
  stockQuantity: number;
  originalPrice: number | string;
  sellingPrice: number | string;
  imageUrl?: string | null; 
  product?: {
    defaultName: string;
    thumbnailUrl?: string | null; 
  };
}

export interface FlashSaleItem {
  id: number;
  campaignId: number;
  variantId: number;
  flashSalePrice: number | string;
  promoQuantity: number;
  soldQuantity: number;
  variant?: FlashSaleItemVariant;
}

export interface FlashSaleCampaign {
  id: number;
  name: string;
  bannerUrl?: string | null;
  startTime: string;
  endTime: string;
  isActive: boolean;
  items?: FlashSaleItem[];
  createdAt?: string;
  updatedAt?: string;
  deletedAt?: string | null;
}

export interface FlashSaleItemPayload {
  variantId: number;
  flashSalePrice: number;
  promoQuantity: number;
}

export interface CreateFlashSaleInput {
  name: string;
  bannerUrl?: string;
  startTime: string | Date;
  endTime: string | Date;
  isActive?: boolean;
  items: FlashSaleItemPayload[];
}

export interface UpdateFlashSaleInput {
  name?: string;
  bannerUrl?: string;
  startTime?: string | Date;
  endTime?: string | Date;
  isActive?: boolean;
  items?: FlashSaleItemPayload[];
}

export interface GetFlashSaleQuery extends BasePaginationQuery {
  isActive?: boolean;
}
