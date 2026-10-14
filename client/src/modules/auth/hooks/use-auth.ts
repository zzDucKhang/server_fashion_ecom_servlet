'use client';

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';
import { authApi } from '@/api/auth.api';
import { useAuth as useAuthContext } from '@/components/providers/auth-provider';
import type {
  LoginPayload,
  RegisterPayload,
  VerifyCodePayload,
  ResetPasswordPayload,
  ChangePasswordPayload,
} from '@/types';

export function useAuth() {
  const context = useAuthContext();
  const router = useRouter();
  const queryClient = useQueryClient();

  const loginMutation = useMutation({
    mutationFn: (payload: LoginPayload) => authApi.login(payload),
    onSuccess: async (res) => {
      if (res.data) {
        await context.login(res.data.accessToken, res.data.refreshToken, res.data.user);
        toast.success('Đăng nhập thành công!');
        if (res.data.user.role?.code === 'ADMIN') {
          router.push('/admin/dashboard');
        } else {
          router.push('/');
        }
      }
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đăng nhập không thành công, vui lòng kiểm tra lại thông tin!';
      toast.error(msg);
    },
  });

  const registerMutation = useMutation({
    mutationFn: (payload: RegisterPayload) => authApi.register(payload),
    onSuccess: () => {
      toast.success('Đăng ký thành công! Vui lòng kiểm tra email để lấy mã xác thực.');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đăng ký thất bại, email có thể đã tồn tại!';
      toast.error(msg);
    },
  });

  const verifyCodeMutation = useMutation({
    mutationFn: (payload: VerifyCodePayload) => authApi.verifyCode(payload),
    onSuccess: () => {
      toast.success('Xác thực tài khoản thành công!');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Mã xác thực không chính xác hoặc đã hết hạn!';
      toast.error(msg);
    },
  });

  const forgotPasswordMutation = useMutation({
    mutationFn: (email: string) => authApi.forgotPassword(email),
    onSuccess: () => {
      toast.success('Mã OTP xác thực đã được gửi về email của bạn.');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Gửi yêu cầu thất bại, vui lòng kiểm tra email!';
      toast.error(msg);
    },
  });

  const resetPasswordMutation = useMutation({
    mutationFn: (payload: ResetPasswordPayload) => authApi.resetPassword(payload),
    onSuccess: () => {
      toast.success('Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.');
      router.push('/login');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đặt lại mật khẩu thất bại!';
      toast.error(msg);
    },
  });

  const changePasswordMutation = useMutation({
    mutationFn: (payload: ChangePasswordPayload) => authApi.changePassword(payload),
    onSuccess: () => {
      toast.success('Đổi mật khẩu thành công!');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đổi mật khẩu không thành công!';
      toast.error(msg);
    },
  });

  return {
    ...context,
    loginMutation,
    registerMutation,
    verifyCodeMutation,
    forgotPasswordMutation,
    resetPasswordMutation,
    changePasswordMutation,
  };
}

