import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderApi } from '@/api/order.api';
import { CreateOrderPayload } from '@/types';
import { toast } from 'sonner';
import { CART_QUERY_KEYS } from '@/modules/cart/hooks/use-cart';

export const useCreateOrder = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateOrderPayload) => orderApi.createOrder(payload),
    onSuccess: () => {
      // Chỉ xóa cache giỏ hàng, việc điều hướng & xử lý QR sẽ do CheckoutView quyết định
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Có lỗi xảy ra khi đặt hàng');
    },
  });
};