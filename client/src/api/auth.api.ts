import { apiClient } from './client';
import { API_ENDPOINTS } from './endpoints';
import {
  ApiResponse,
  LoginPayload,
  RegisterPayload,
  VerifyCodePayload,
  ResetPasswordPayload,
  ChangePasswordPayload,
  CreateAddressPayload,
  ShippingAddress,
  UserProfile,
} from '@/types';

export const authApi = {
  login: async (payload: LoginPayload) => {
    const res = await apiClient.post<ApiResponse<{ accessToken: string; refreshToken: string; user: UserProfile }>>(
      API_ENDPOINTS.IDENTITY.LOGIN,
      payload
    );
    return res.data;
  },

  register: async (payload: RegisterPayload) => {
    const res = await apiClient.post<ApiResponse<{ message: string }>>(
      API_ENDPOINTS.IDENTITY.REGISTER,
      payload
    );
    return res.data;
  },

  verifyCode: async (payload: VerifyCodePayload) => {
    const res = await apiClient.post<ApiResponse<{ message: string }>>(
      API_ENDPOINTS.IDENTITY.VERIFY_CODE,
      payload
    );
    return res.data;
  },

  forgotPassword: async (email: string) => {
    const res = await apiClient.post<ApiResponse<{ message: string }>>(
      API_ENDPOINTS.IDENTITY.FORGOT_PASSWORD,
      { email }
    );
    return res.data;
  },

  resetPassword: async (payload: ResetPasswordPayload) => {
    const res = await apiClient.post<ApiResponse<{ message: string }>>(
      API_ENDPOINTS.IDENTITY.RESET_PASSWORD,
      payload
    );
    return res.data;
  },

  changePassword: async (payload: ChangePasswordPayload) => {
    const res = await apiClient.post<ApiResponse<{ message: string }>>(
      API_ENDPOINTS.IDENTITY.CHANGE_PASSWORD,
      payload
    );
    return res.data;
  },

  logout: async (refreshToken: string) => {
    const res = await apiClient.post<ApiResponse<{ message: string }>>(
      API_ENDPOINTS.IDENTITY.LOGOUT,
      { refreshToken }
    );
    return res.data;
  },

  // Sổ địa chỉ
  getAddresses: async () => {
    const res = await apiClient.get<ApiResponse<ShippingAddress[]>>(
      API_ENDPOINTS.IDENTITY.ADDRESSES
    );
    return res.data;
  },

  createAddress: async (payload: CreateAddressPayload) => {
    const res = await apiClient.post<ApiResponse<ShippingAddress>>(
      API_ENDPOINTS.IDENTITY.ADDRESSES,
      payload
    );
    return res.data;
  },

  updateAddress: async (addressId: number, payload: Partial<CreateAddressPayload>) => {
    const res = await apiClient.put<ApiResponse<ShippingAddress>>(
      `${API_ENDPOINTS.IDENTITY.ADDRESSES}/${addressId}`,
      payload
    );
    return res.data;
  },

  deleteAddress: async (addressId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.IDENTITY.ADDRESSES}/${addressId}`
    );
    return res.data;
  },
};