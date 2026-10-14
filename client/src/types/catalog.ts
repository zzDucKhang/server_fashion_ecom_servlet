import { ProductStatus } from './enums';

export interface Category {
  id: number;
  defaultName: string;
  iconUrl?: string | null;
  isVisible: boolean;
  parentId?: number | null;
  parent?: Category | null;
  children?: Category[];
}

export interface Brand {
  id: number;
  defaultName: string;
  logoUrl?: string | null;
}

export interface Tag {
  id: number;
  name: string;
}

export interface ProductVariant {
  id: number;
  productId: number;
  sku: string;
  stockQuantity: number;
  originalPrice: number | string;
  sellingPrice: number | string;
  weightGram: number;
  imageUrl?: string | null;
  // Đã phẳng hóa chuẩn Class Diagram & Prisma
  variant?: string | null;          // Ví dụ: "Màu sắc - Kích cỡ"
  variantValue?: string | null;     // Ví dụ: "Đen - XL"
  flashSaleItems?: Array<{
    id: number;
    flashSalePrice: number | string;
    promoQuantity: number;
    soldQuantity: number;
    campaign: {
      id: number;
      name: string;
      isActive: boolean;
      startTime: string;
      endTime: string;
    };
  }>;
}

export interface ProductImage {
  id: number;
  productId: number;
  imageUrl: string;
  displayOrder: number;
}

export interface Product {
  id: number;
  defaultName: string;
  thumbnailUrl?: string | null;
  status: ProductStatus;
  categoryId: number;
  brandId?: number | null;
  category?: Category;
  brand?: Brand | null;
  images?: ProductImage[];
  variants?: ProductVariant[];
  tags?: Tag[];
  createdAt: string;
  updatedAt?: string;
}

// Payload đồng bộ biến thể
export interface SyncVariantsPayload {
  productId: number;
  variants: Array<{
    id?: number | null;
    sku: string;
    originalPrice: number;
    sellingPrice: number;
    stockQuantity: number;
    weightGram: number;
    imageUrl?: string | null;
    variant?: string;
    variantValue?: string;
  }>;
}