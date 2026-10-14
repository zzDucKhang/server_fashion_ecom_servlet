// src/modules/cart/store/useCartStore.ts
import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

export interface CartItem {
  variantId: number;
  productId?: number;
  name: string;
  sku?: string;
  price: number;
  originalPrice?: number;
  quantity: number;
  imageUrl?: string;
  attributes?: Record<string, string>;
}

export interface CartStore {
  items: CartItem[];
  addItem: (item: Omit<CartItem, "quantity"> & { quantity?: number }) => void;
  updateQuantity: (variantId: number, quantity: number) => void;
  removeItem: (variantId: number) => void;
  clearCart: () => void;
  getSubtotal: () => number;
}

export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],

      addItem: (newItem) => {
        const quantityToAdd = Math.max(1, newItem.quantity ?? 1);
        set((state) => {
          const existingIndex = state.items.findIndex(
            (item) => item.variantId === newItem.variantId
          );

          if (existingIndex > -1) {
            const updatedItems = [...state.items];
            const currentItem = updatedItems[existingIndex];
            updatedItems[existingIndex] = {
              ...currentItem,
              ...newItem,
              quantity: currentItem.quantity + quantityToAdd,
            };
            return { items: updatedItems };
          }

          return {
            items: [
              ...state.items,
              {
                ...newItem,
                quantity: quantityToAdd,
              },
            ],
          };
        });
      },

      updateQuantity: (variantId, quantity) => {
        if (quantity <= 0) {
          get().removeItem(variantId);
          return;
        }

        set((state) => ({
          items: state.items.map((item) =>
            item.variantId === variantId ? { ...item, quantity } : item
          ),
        }));
      },

      removeItem: (variantId) => {
        set((state) => ({
          items: state.items.filter((item) => item.variantId !== variantId),
        }));
      },

      clearCart: () => {
        set({ items: [] });
      },

      getSubtotal: () => {
        return get().items.reduce(
          (sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 1),
          0
        );
      },
    }),
    {
      name: "fashion_cart_storage",
      storage: createJSONStorage(() => localStorage),
    }
  )
);
