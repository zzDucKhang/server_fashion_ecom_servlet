import { apiClient } from './client';
import { API_ENDPOINTS } from './endpoints';
import {
  ApiResponse,
  PaginatedResult,
  Order,
  CreateOrderPayload,
  UpdateOrderStatusPayload,
  BasePaginationQuery,
} from '@/types';

interface OrderQuery extends BasePaginationQuery {
  status?: string;
  all?: boolean;
}

export const orderApi = {
  createOrder: async (payload: CreateOrderPayload) => {
    const res = await apiClient.post<ApiResponse<Order>>(
      API_ENDPOINTS.CART_ORDER.ORDERS,
      payload
    );
    return res.data;
  },

  getOrders: async (query?: OrderQuery) => {
    const res = await apiClient.get<ApiResponse<PaginatedResult<Order>>>(
      API_ENDPOINTS.CART_ORDER.ORDERS,
      { params: query }
    );
    return res.data;
  },

  getOrderById: async (orderId: number) => {
    const res = await apiClient.get<ApiResponse<Order>>(
      `${API_ENDPOINTS.CART_ORDER.ORDERS}/${orderId}`
    );
    return res.data;
  },

  cancelOrder: async (orderId: number) => {
    const res = await apiClient.post<ApiResponse<Order>>(
      `${API_ENDPOINTS.CART_ORDER.ORDERS}/${orderId}/cancel`
    );
    return res.data;
  },

  updateOrderStatus: async (orderId: number, payload: UpdateOrderStatusPayload) => {
    const res = await apiClient.patch<ApiResponse<Order>>(
      `${API_ENDPOINTS.CART_ORDER.ORDERS}/${orderId}/status`,
      payload
    );
    return res.data;
  },

  // THÊM API TẠO THANH TOÁN (QR CODE)
  createPayment: async (orderId: number) => {
    const res = await apiClient.post<ApiResponse<{ qrUrl: string; amount: number; transferContent: string; paymentId: string }>>(
      API_ENDPOINTS.FULFILLMENT.PAYMENTS,
      { orderId }
    );
    return res.data;
  },

  getPaymentDetails: async (orderId: number) => {
    const res = await apiClient.get<ApiResponse<{ qrUrl: string; amount?: number; transferContent?: string; orderCode?: string; totalAmount?: number; paymentMethod?: string }>>(
      `${API_ENDPOINTS.CART_ORDER.ORDERS}/${orderId}/payment-details`
    );
    return res.data;
  },
};