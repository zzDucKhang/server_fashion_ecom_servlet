'use client';

import { useFormContext } from 'react-hook-form';
import { CheckoutFormValues } from '../schemas';

interface PaymentMethodProps {
  isSubmitting: boolean;
  totalAmount: number;
}

const PAYMENT_OPTIONS = [
  { id: 'COD', label: 'Thanh toán khi nhận hàng (COD)' },
  { id: 'VNPAY', label: 'Thanh toán qua VNPAY' },
  { id: 'MOMO', label: 'Thanh toán qua ví MoMo' },
  { id: 'BANK_TRANSFER', label: 'Chuyển khoản ngân hàng' },
];

export function PaymentMethodComponent({ isSubmitting, totalAmount }: PaymentMethodProps) {
  const { register, formState: { errors } } = useFormContext<CheckoutFormValues>();

  return (
    <div className="mt-6 space-y-4">
      <div className="space-y-3">
        {PAYMENT_OPTIONS.map((option) => (
          <label
            key={option.id}
            className="flex cursor-pointer items-center gap-3 rounded-xl border border-[#EAE6DF] p-4 transition-colors hover:bg-gray-50 has-[:checked]:border-[#84EA53] has-[:checked]:bg-[#84EA53]/10"
          >
            <input
              type="radio"
              value={option.id}
              {...register('paymentMethod')}
              className="h-4 w-4 text-[#84EA53] focus:ring-[#84EA53]"
            />
            <span className="text-sm font-medium">{option.label}</span>
          </label>
        ))}
        {errors.paymentMethod && (
          <p className="text-xs text-red-500">{errors.paymentMethod.message}</p>
        )}
      </div>

      <button
        type="submit"
        disabled={isSubmitting}
        className="mt-6 w-full rounded-xl bg-[#84EA53] py-3.5 text-sm font-bold transition-transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50"
      >
        {isSubmitting ? 'Processing...' : `Place order · $${totalAmount.toLocaleString()}`}
      </button>
    </div>
  );
}
