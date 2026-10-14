import Image from 'next/image';
import { Minus, Plus, Trash2 } from 'lucide-react';
import { CartItem } from '@/types';
import { useUpdateCartItem, useRemoveCartItem } from '../hooks/use-cart';
import { formatCurrency } from '@/lib/utils/format-currency';

interface CartItemProps {
  item: CartItem;
  isSelected?: boolean;
  onToggleSelect?: (id: number) => void;
}

export function CartItemComponent({ item, isSelected = false, onToggleSelect }: CartItemProps) {
  const updateMutation = useUpdateCartItem();
  const removeMutation = useRemoveCartItem();

  const handleUpdate = (delta: number) => {
    const newQuantity = Math.max(1, item.quantity + delta);
    if (newQuantity !== item.quantity) {
      updateMutation.mutate({ itemId: item.id, payload: { quantity: newQuantity } });
    }
  };

  const handleRemove = () => {
    removeMutation.mutate(item.id);
  };

  const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
    if (!fsi.campaign || !fsi.campaign.isActive) return false;
    const now = new Date().getTime();
    const start = new Date(fsi.campaign.startTime).getTime();
    const end = new Date(fsi.campaign.endTime).getTime();
    const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
    return now >= start && now <= end && remainingQty >= item.quantity;
  });

  const currentPrice = activeFlashSale
    ? Number(activeFlashSale.flashSalePrice)
    : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
    
  const originalPrice = Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
  const totalItemPrice = currentPrice * item.quantity;
  const imageUrl = item.variant.imageUrl || item.variant.product.thumbnailUrl || '/placeholder.png';
  const variantDisplay = item.variant.variantValue || 'Phân loại mặc định';

  return (
    <div className="flex items-center gap-3.5 py-5">
      {/* Checkbox chọn sản phẩm */}
      <input
        type="checkbox"
        checked={isSelected}
        onChange={() => onToggleSelect?.(item.id)}
        className="size-4 shrink-0 rounded border-[#EAE6DF] accent-[#84EA53] cursor-pointer"
        aria-label={`Chọn sản phẩm ${item.variant.product.defaultName}`}
      />

      <div className="relative h-28 w-24 sm:h-32 sm:w-28 shrink-0 overflow-hidden rounded-xl bg-[#e9e5dc]">
        <Image
          src={imageUrl}
          alt={item.variant.product.defaultName || 'Product'}
          fill
          sizes="112px"
          className="object-cover"
        />
      </div>

      <div className="flex min-w-0 flex-1 flex-col justify-between self-stretch">
        <div className="flex justify-between gap-3">
          <div>
            <p className="text-sm font-semibold line-clamp-2 text-neutral-900 flex items-center gap-2">
              {item.variant.product.defaultName}
              {activeFlashSale && (
                <span className="bg-red-600 text-white text-[9px] px-1.5 py-0.5 rounded font-bold uppercase tracking-wider">
                  Flash Sale
                </span>
              )}
            </p>
            <p className="mt-1.5 text-xs text-[#74736e]">
              {variantDisplay} • {formatCurrency(currentPrice)} 
              {activeFlashSale && <span className="line-through ml-1 text-neutral-400">{formatCurrency(originalPrice)}</span>}
            </p>
          </div>
          <button
            onClick={handleRemove}
            disabled={removeMutation.isPending}
            aria-label="Remove item"
            className="h-fit text-[#9b9991] hover:text-red-500 disabled:opacity-50 transition-colors p-1"
          >
            <Trash2 size={15} />
          </button>
        </div>

        <div className="mt-3 flex items-center justify-between">
          <div className="flex items-center rounded-lg border border-[#EAE6DF] bg-white">
            <button
              onClick={() => handleUpdate(-1)}
              disabled={updateMutation.isPending || item.quantity <= 1}
              className="p-1.5 disabled:opacity-50 text-neutral-600 hover:text-neutral-900"
              aria-label="Decrease quantity"
            >
              <Minus size={12} />
            </button>
            <span className="w-6 text-center text-xs font-semibold">
              {updateMutation.isPending ? '...' : item.quantity}
            </span>
            <button
              onClick={() => handleUpdate(1)}
              disabled={updateMutation.isPending}
              className="p-1.5 disabled:opacity-50 text-neutral-600 hover:text-neutral-900"
              aria-label="Increase quantity"
            >
              <Plus size={12} />
            </button>
          </div>
          <span className="text-sm font-bold text-neutral-900">
            {formatCurrency(totalItemPrice)}
          </span>
        </div>
      </div>
    </div>
  );
}