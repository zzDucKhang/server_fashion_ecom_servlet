'use client';

import * as React from 'react';
import { Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { Eye, EyeOff, Loader2 } from 'lucide-react';
import { resetPasswordSchema, type ResetPasswordFormData } from '@/modules/auth/schemas';
import { useAuth } from '@/modules/auth/hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

function ResetPasswordFormContent() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token') || '';
  const [showPassword, setShowPassword] = React.useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = React.useState(false);
  const { resetPasswordMutation } = useAuth();

  const form = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: {
      token,
      newPassword: '',
      confirmPassword: '',
    },
  });

  React.useEffect(() => {
    if (token) {
      form.setValue('token', token);
    }
  }, [token, form]);

  const onSubmit = (data: ResetPasswordFormData) => {
    resetPasswordMutation.mutate(data);
  };

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
      <input type="hidden" {...form.register('token')} />

      {!token && (
        <div className="p-3 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-xs">
          Không tìm thấy mã xác thực khôi phục mật khẩu. Vui lòng bắt đầu lại từ bước Quên mật khẩu.
        </div>
      )}

      <div className="relative">
        <FormField
          control={form.control}
          name="newPassword"
          label="Mật khẩu mới"
          type={showPassword ? 'text' : 'password'}
          placeholder="Tối thiểu 8 ký tự (hoa, thường, số, ký tự)"
          error={form.formState.errors.newPassword?.message}
        />
        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
          tabIndex={-1}
        >
          {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
        </button>
      </div>

      <div className="relative">
        <FormField
          control={form.control}
          name="confirmPassword"
          label="Xác nhận mật khẩu mới"
          type={showConfirmPassword ? 'text' : 'password'}
          placeholder="Nhập lại mật khẩu mới"
          error={form.formState.errors.confirmPassword?.message}
        />
        <button
          type="button"
          onClick={() => setShowConfirmPassword(!showConfirmPassword)}
          className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
          tabIndex={-1}
        >
          {showConfirmPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
        </button>
      </div>

      <Button
        type="submit"
        disabled={resetPasswordMutation.isPending || !token}
        className="w-full h-11 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99] mt-2"
      >
        {resetPasswordMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin mr-2" />
            Đang cập nhật mật khẩu...
          </>
        ) : (
          'Lưu mật khẩu mới'
        )}
      </Button>

      <div className="text-center pt-2">
        <Link
          href="/login"
          className="text-xs text-neutral-500 hover:text-neutral-900 transition underline underline-offset-4"
        >
          ← Quay về đăng nhập
        </Link>
      </div>
    </form>
  );
}

export default function ResetPasswordPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Đặt lại mật khẩu
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Nhập mật khẩu mới an toàn cho tài khoản của bạn.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <Suspense fallback={<div className="text-center py-6 text-sm text-neutral-500">Đang tải...</div>}>
            <ResetPasswordFormContent />
          </Suspense>
        </div>

        <div className="text-center mt-6">
          <Link
            href="/"
            className="text-xs text-neutral-500 hover:text-neutral-900 transition underline-offset-4 hover:underline"
          >
            ← Quay lại trang chủ
          </Link>
        </div>
      </div>
    </main>
  );
}

