import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cartApi } from '@/api/cart.api';
import { AddToCartPayload, UpdateCartItemPayload } from '@/types';
import { toast } from 'sonner';

export const CART_QUERY_KEYS = {
  cart: ['cart'] as const,
};

// 1. Lấy thông tin giỏ hàng
export const useCart = () => {
  return useQuery({
    queryKey: CART_QUERY_KEYS.cart,
    queryFn: cartApi.getCart,
  });
};

// 2. Thêm sản phẩm vào giỏ
export const useAddToCart = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: AddToCartPayload) => cartApi.addToCart(payload),
    onSuccess: () => {
      toast.success('Đã thêm vào giỏ hàng');
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Không thể thêm vào giỏ hàng');
    },
  });
};

// 3. Cập nhật số lượng sản phẩm trong giỏ
export const useUpdateCartItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      itemId,
      payload,
    }: {
      itemId: number;
      payload: UpdateCartItemPayload;
    }) => cartApi.updateCartItem(itemId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Lỗi khi cập nhật số lượng');
    },
  });
};

// 4. Xóa một sản phẩm khỏi giỏ
export const useRemoveCartItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (itemId: number) => cartApi.removeCartItem(itemId),
    onSuccess: () => {
      toast.success('Đã xóa sản phẩm khỏi giỏ');
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Lỗi khi xóa sản phẩm');
    },
  });
};

// 5. Xóa toàn bộ giỏ hàng
export const useClearCart = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => cartApi.clearCart(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
  });
};

// 6. Gộp giỏ hàng (Guest -> User khi đăng nhập)
export const useMergeCart = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => cartApi.mergeCart(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
  });
};

