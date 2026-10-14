import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().trim().min(1, 'Email không được để trống').email('Email không đúng định dạng'),
  password: z.string().min(1, 'Mật khẩu không được để trống'),
});

export const registerSchema = z
  .object({
    fullName: z.string().trim().min(1, 'Họ và tên không được để trống').max(100),
    email: z.string().trim().min(1, 'Email không được để trống').email('Email không đúng định dạng'),
    phoneNumber: z.string().trim().optional(),
    password: z
      .string()
      .min(8, 'Mật khẩu tối thiểu 8 ký tự')
      .max(72, 'Mật khẩu tối đa 72 ký tự')
      .regex(/[A-Z]/, 'Cần ít nhất 1 chữ in hoa')
      .regex(/[a-z]/, 'Cần ít nhất 1 chữ in thường')
      .regex(/[0-9]/, 'Cần ít nhất 1 chữ số')
      .regex(/[^A-Za-z0-9]/, 'Cần ít nhất 1 ký tự đặc biệt'),
    confirmPassword: z.string().min(1, 'Xác nhận mật khẩu là bắt buộc'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
  });

export const verifyCodeSchema = z.object({
  email: z.string().trim().email('Email không hợp lệ'),
  type: z.enum(['REGISTER', 'FORGOT_PASSWORD']),
  code: z
    .string()
    .trim()
    .length(6, 'Mã xác thực gồm 6 chữ số')
    .regex(/^\d+$/, 'Mã xác thực chỉ chứa số'),
});

export const forgotPasswordSchema = z.object({
  email: z.string().trim().min(1, 'Email không được để trống').email('Email không đúng định dạng'),
});

export const resetPasswordSchema = z
  .object({
    token: z.string().min(1, 'Token xác thực là bắt buộc'),
    newPassword: z
      .string()
      .min(8, 'Mật khẩu mới tối thiểu 8 ký tự')
      .max(72, 'Mật khẩu tối đa 72 ký tự')
      .regex(/[A-Z]/, 'Cần ít nhất 1 chữ hoa')
      .regex(/[a-z]/, 'Cần ít nhất 1 chữ thường')
      .regex(/[0-9]/, 'Cần ít nhất 1 chữ số')
      .regex(/[^A-Za-z0-9]/, 'Cần ít nhất 1 ký tự đặc biệt'),
    confirmPassword: z.string().min(1, 'Xác nhận mật khẩu là bắt buộc'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
  });

export const changePasswordSchema = z
  .object({
    oldPassword: z.string().min(1, 'Mật khẩu hiện tại là bắt buộc'),
    newPassword: z
      .string()
      .min(8, 'Mật khẩu mới tối thiểu 8 ký tự')
      .max(72, 'Mật khẩu tối đa 72 ký tự')
      .regex(/[A-Z]/, 'Cần ít nhất 1 chữ hoa')
      .regex(/[a-z]/, 'Cần ít nhất 1 chữ thường')
      .regex(/[0-9]/, 'Cần ít nhất 1 chữ số')
      .regex(/[^A-Za-z0-9]/, 'Cần ít nhất 1 ký tự đặc biệt'),
    confirmPassword: z.string().min(1, 'Xác nhận mật khẩu là bắt buộc'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
  });

export const addressSchema = z.object({
  receiverName: z.string().trim().min(1, 'Họ tên người nhận không được để trống'),
  phoneNumber: z.string().trim().min(1, 'Số điện thoại không được để trống'),
  province: z.string().trim().min(1, 'Tỉnh/Thành phố không được để trống'),
  district: z.string().trim().min(1, 'Quận/Huyện không được để trống'),
  ward: z.string().trim().min(1, 'Phường/Xã không được để trống'),
  detailAddress: z.string().trim().min(1, 'Địa chỉ chi tiết không được để trống'),
  isDefault: z.boolean().optional(),
});

export const profileSchema = z.object({
  fullName: z.string().trim().min(1, 'Họ và tên không được để trống'),
  phoneNumber: z.string().trim().optional(),
  avatarUrl: z.string().url('URL ảnh không hợp lệ').optional().or(z.literal('')),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
export type VerifyCodeFormData = z.infer<typeof verifyCodeSchema>;
export type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;
export type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;
export type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;
export type AddressFormData = z.infer<typeof addressSchema>;
export type ProfileFormData = z.infer<typeof profileSchema>;

