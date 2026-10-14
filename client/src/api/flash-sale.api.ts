import { apiClient } from './client';
import { API_ENDPOINTS } from './endpoints';
import {
  ApiResponse,
  PaginatedResult,
  FlashSaleCampaign,
  CreateFlashSaleInput,
  UpdateFlashSaleInput,
  GetFlashSaleQuery,
} from '@/types';

export const flashSaleApi = {
  // Lấy danh sách các chiến dịch Flash Sale đang hoạt động (cho khách hàng)
  getActiveCampaigns: async () => {
    const res = await apiClient.get<ApiResponse<FlashSaleCampaign[]>>(
      `${API_ENDPOINTS.INVENTORY_SALE.FLASH_SALES}/active`
    );
    return res.data;
  },

  // Lấy danh sách chiến dịch Flash Sale có phân trang / lọc
  getCampaigns: async (query?: GetFlashSaleQuery) => {
    const res = await apiClient.get<PaginatedResult<FlashSaleCampaign>>(
      API_ENDPOINTS.INVENTORY_SALE.FLASH_SALES,
      { params: query }
    );
    return res.data;
  },

  // Chi tiết chiến dịch Flash Sale theo ID
  getCampaignById: async (campaignId: number) => {
    const res = await apiClient.get<ApiResponse<FlashSaleCampaign>>(
      `${API_ENDPOINTS.INVENTORY_SALE.FLASH_SALES}/${campaignId}`
    );
    return res.data;
  },

  // [ADMIN] Tạo chiến dịch Flash Sale mới
  createCampaign: async (payload: CreateFlashSaleInput) => {
    const res = await apiClient.post<ApiResponse<FlashSaleCampaign>>(
      API_ENDPOINTS.INVENTORY_SALE.FLASH_SALES,
      payload
    );
    return res.data;
  },

  // [ADMIN] Cập nhật chiến dịch Flash Sale
  updateCampaign: async (campaignId: number, payload: UpdateFlashSaleInput) => {
    const res = await apiClient.put<ApiResponse<FlashSaleCampaign>>(
      `${API_ENDPOINTS.INVENTORY_SALE.FLASH_SALES}/${campaignId}`,
      payload
    );
    return res.data;
  },

  // [ADMIN] Xóa chiến dịch Flash Sale
  deleteCampaign: async (campaignId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.INVENTORY_SALE.FLASH_SALES}/${campaignId}`
    );
    return res.data;
  },
};
