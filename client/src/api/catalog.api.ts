import { apiClient } from './client';
import { API_ENDPOINTS } from './endpoints';
import {
  ApiResponse,
  PaginatedResult,
  Product,
  Category,
  Brand,
  Tag,
  ProductVariant,
  SyncVariantsPayload,
  BasePaginationQuery,
} from '@/types';

interface ProductQuery extends BasePaginationQuery {
  categoryId?: number;
  brandId?: number;
  status?: string;
}

export const catalogApi = {
  // Sản phẩm
  getProducts: async (query?: ProductQuery) => {
    const res = await apiClient.get<PaginatedResult<Product>>(API_ENDPOINTS.CATALOG.PRODUCTS, {
      params: query,
    });
    return res.data;
  },

  getProductById: async (productId: number) => {
    const res = await apiClient.get<ApiResponse<Product>>(
      `${API_ENDPOINTS.CATALOG.PRODUCTS}/${productId}`
    );
    return res.data;
  },

  createProduct: async (payload: any) => {
    const res = await apiClient.post<ApiResponse<Product>>(
      API_ENDPOINTS.CATALOG.PRODUCTS,
      payload
    );
    return res.data;
  },

  updateProduct: async (productId: number, payload: any) => {
    const res = await apiClient.put<ApiResponse<Product>>(
      `${API_ENDPOINTS.CATALOG.PRODUCTS}/${productId}`,
      payload
    );
    return res.data;
  },

  deleteProduct: async (productId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.CATALOG.PRODUCTS}/${productId}`
    );
    return res.data;
  },

  // Danh mục & Thương hiệu & Thẻ (Tags)
  getCategories: async () => {
    const res = await apiClient.get<{ data: Category[]; totalItems: number }>(
      API_ENDPOINTS.CATALOG.CATEGORIES
    );
    return res.data;
  },

  createCategory: async (payload: {
    defaultName: string;
    logo?: string | null;
    parentCategoryId?: number | null;
  }) => {
    const res = await apiClient.post<ApiResponse<Category>>(
      API_ENDPOINTS.CATALOG.CATEGORIES,
      payload
    );
    return res.data;
  },

  updateCategory: async (
    categoryId: number,
    payload: {
      defaultName?: string;
      logo?: string | null;
      parentCategoryId?: number | null;
    }
  ) => {
    const res = await apiClient.put<ApiResponse<Category>>(
      `${API_ENDPOINTS.CATALOG.CATEGORIES}/${categoryId}`,
      payload
    );
    return res.data;
  },

  deleteCategory: async (categoryId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.CATALOG.CATEGORIES}/${categoryId}`
    );
    return res.data;
  },

  getBrands: async (query?: BasePaginationQuery) => {
    const res = await apiClient.get<PaginatedResult<Brand>>(API_ENDPOINTS.CATALOG.BRANDS, {
      params: query,
    });
    return res.data;
  },

  createBrand: async (payload: { defaultName: string; logo?: string }) => {
    const res = await apiClient.post<ApiResponse<Brand>>(
      API_ENDPOINTS.CATALOG.BRANDS,
      payload
    );
    return res.data;
  },

  updateBrand: async (
    brandId: number,
    payload: { defaultName?: string; logo?: string }
  ) => {
    const res = await apiClient.put<ApiResponse<Brand>>(
      `${API_ENDPOINTS.CATALOG.BRANDS}/${brandId}`,
      payload
    );
    return res.data;
  },

  deleteBrand: async (brandId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.CATALOG.BRANDS}/${brandId}`
    );
    return res.data;
  },

  getTags: async (query?: { page?: number; limit?: number; search?: string }) => {
    const res = await apiClient.get<PaginatedResult<Tag>>(API_ENDPOINTS.CATALOG.TAGS, {
      params: query,
    });
    return res.data;
  },

  createTag: async (payload: { name: string }) => {
    const res = await apiClient.post<ApiResponse<Tag>>(
      API_ENDPOINTS.CATALOG.TAGS,
      payload
    );
    return res.data;
  },

  updateTag: async (tagId: number, payload: { name: string }) => {
    const res = await apiClient.put<ApiResponse<Tag>>(
      `${API_ENDPOINTS.CATALOG.TAGS}/${tagId}`,
      payload
    );
    return res.data;
  },

  deleteTag: async (tagId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.CATALOG.TAGS}/${tagId}`
    );
    return res.data;
  },

  // Biến thể & Tồn kho
  getVariantsByProductId: async (productId: number) => {
    const res = await apiClient.get<ApiResponse<ProductVariant[]>>(
      `${API_ENDPOINTS.CATALOG.VARIANTS}/products/${productId}`
    );
    return res.data;
  },

  syncVariants: async (payload: SyncVariantsPayload) => {
    const res = await apiClient.post<ApiResponse<ProductVariant[]>>(
      `${API_ENDPOINTS.CATALOG.VARIANTS}/sync`,
      payload
    );
    return res.data;
  },

  updateVariantStock: async (variantId: number, quantityChange: number) => {
    const res = await apiClient.patch<ApiResponse<ProductVariant>>(
      `${API_ENDPOINTS.CATALOG.VARIANTS}/${variantId}/stock`,
      { quantityChange }
    );
    return res.data;
  },
};