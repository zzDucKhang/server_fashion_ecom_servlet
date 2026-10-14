import { apiClient } from './client';
import { API_ENDPOINTS } from './endpoints';
import { ApiResponse, Cart, AddToCartPayload, UpdateCartItemPayload } from '@/types';

export const cartApi = {
  getCart: async () => {
    const res = await apiClient.get<ApiResponse<Cart>>(API_ENDPOINTS.CART_ORDER.CART);
    return res.data;
  },

  addToCart: async (payload: AddToCartPayload) => {
    const res = await apiClient.post<ApiResponse<Cart>>(
      API_ENDPOINTS.CART_ORDER.CART_ITEMS,
      payload
    );
    return res.data;
  },

  updateCartItem: async (itemId: number, payload: UpdateCartItemPayload) => {
    const res = await apiClient.patch<ApiResponse<Cart>>(
      `${API_ENDPOINTS.CART_ORDER.CART_ITEMS}/${itemId}`,
      payload
    );
    return res.data;
  },

  removeCartItem: async (itemId: number) => {
    const res = await apiClient.delete<ApiResponse<Cart>>(
      `${API_ENDPOINTS.CART_ORDER.CART_ITEMS}/${itemId}`
    );
    return res.data;
  },

  clearCart: async () => {
    const res = await apiClient.delete<ApiResponse<Cart>>(API_ENDPOINTS.CART_ORDER.CART);
    return res.data;
  },

  mergeCart: async () => {
    const res = await apiClient.post<ApiResponse<Cart>>(API_ENDPOINTS.CART_ORDER.CART_MERGE);
    return res.data;
  },
};