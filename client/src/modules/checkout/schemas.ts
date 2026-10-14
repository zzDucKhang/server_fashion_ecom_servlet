import { z } from 'zod';
import { PaymentMethod } from '@/types';

// Regex kiểm tra số điện thoại chuẩn Việt Nam
const phoneRegex = /^(84|0)[35789]\d{8}$/;

export const shippingAddressSchema = z.object({
  receiverName: z
    .string()
    .trim()
    .min(2, 'Tên người nhận phải có ít nhất 2 ký tự')
    .max(100, 'Tên quá dài, tối đa 100 ký tự'),
  receiverPhone: z
    .string()
    .trim()
    .regex(phoneRegex, 'Số điện thoại không hợp lệ (VD: 0912345678)'),
  province: z.string().trim().min(1, 'Vui lòng chọn Tỉnh/Thành phố'),
  district: z.string().trim().min(1, 'Vui lòng chọn Quận/Huyện'),
  ward: z.string().trim().min(1, 'Vui lòng chọn Phường/Xã'),
  detailAddress: z
    .string()
    .trim()
    .min(5, 'Địa chỉ chi tiết phải có ít nhất 5 ký tự')
    .max(500, 'Địa chỉ quá dài'),
});

export const checkoutSchema = z
  .object({
    // Nếu truyền danh sách ID vào thì chỉ thanh toán những món đó, nếu không truyền sẽ thanh toán toàn bộ giỏ
    cartItemIds: z.array(z.number()).optional(),

    // Khách chọn địa chỉ đã lưu hoặc nhập địa chỉ mới
    shippingAddressId: z.number().optional(),
    shippingAddress: shippingAddressSchema.optional(),

    paymentMethod: z.enum(['COD', 'VNPAY', 'MOMO', 'BANK_TRANSFER'], {
      required_error: 'Vui lòng chọn phương thức thanh toán',
      invalid_type_error: 'Phương thức thanh toán không hợp lệ',
    }),
    
    shippingMethod: z.string().optional(),
    customerNote: z.string().max(1000, 'Ghi chú không được quá 1000 ký tự').optional(),
    voucherCode: z.string().optional().nullable(),
    pointUsed: z.number().min(0).default(0).optional(),
  })
  .refine((data) => data.shippingAddressId || data.shippingAddress, {
    message: 'Vui lòng chọn địa chỉ đã lưu hoặc nhập địa chỉ giao hàng mới',
    path: ['shippingAddress'], // Báo lỗi tại field shippingAddress
  });

export type CheckoutFormValues = z.infer<typeof checkoutSchema>;
