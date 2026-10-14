import { create } from 'zustand';
import { FlashSaleCampaign, FlashSaleItem } from '@/types/inventory-sale';
import { flashSaleApi } from '@/api/flash-sale.api';

interface FlashSaleState {
  activeCampaigns: FlashSaleCampaign[];
  selectedCampaignId: number | null;
  loading: boolean;
  error: string | null;

  // Actions
  fetchActiveCampaigns: () => Promise<void>;
  setSelectedCampaignId: (id: number | null) => void;
  updateItemSoldQuantity: (variantId: number, soldQuantity: number) => void;
  getActiveCampaign: () => FlashSaleCampaign | undefined;
}

export const useFlashSaleStore = create<FlashSaleState>((set, get) => ({
  activeCampaigns: [],
  selectedCampaignId: null,
  loading: false,
  error: null,

  fetchActiveCampaigns: async () => {
    set({ loading: true, error: null });
    try {
      const res = await flashSaleApi.getActiveCampaigns();
      const campaigns = res.data || [];
      set({
        activeCampaigns: campaigns,
        selectedCampaignId: campaigns.length > 0 ? campaigns[0].id : null,
        loading: false,
      });
    } catch (err: any) {
      set({
        error: err?.response?.data?.message || 'Không thể tải danh sách Flash Sale',
        loading: false,
      });
    }
  },

  setSelectedCampaignId: (id: number | null) => {
    set({ selectedCampaignId: id });
  },

  updateItemSoldQuantity: (variantId: number, soldQuantity: number) => {
    set((state) => ({
      activeCampaigns: state.activeCampaigns.map((campaign) => ({
        ...campaign,
        items: campaign.items?.map((item) =>
          item.variantId === variantId ? { ...item, soldQuantity } : item
        ),
      })),
    }));
  },

getActiveCampaign: () => {
    const { activeCampaigns, selectedCampaignId } = get();
    if (activeCampaigns.length === 0) return undefined;
    if (!selectedCampaignId) return activeCampaigns[0];
    return activeCampaigns.find((c) => c.id === selectedCampaignId) || activeCampaigns[0];
  },
}));
