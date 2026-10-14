'use client';

import * as React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { Eye, EyeOff, Loader2 } from 'lucide-react';
import { loginSchema, type LoginFormData } from '../schemas';
import { useAuth } from '../hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

export function LoginForm() {
  const [showPassword, setShowPassword] = React.useState(false);
  const { loginMutation } = useAuth();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = (data: LoginFormData) => {
    loginMutation.mutate(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <FormField
        control={control}
        name="email"
        label="Địa chỉ Email"
        type="email"
        placeholder="example@domain.com"
        error={errors.email?.message}
      />

      <div className="relative">
        <FormField
          control={control}
          name="password"
          label="Mật khẩu"
          type={showPassword ? 'text' : 'password'}
          placeholder="••••••••"
          error={errors.password?.message}
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

      <div className="flex items-center justify-between pt-1">
        <label className="flex items-center gap-2 cursor-pointer text-xs text-neutral-600">
          <input
            type="checkbox"
            className="rounded border-[#EAE6DF] accent-neutral-900 size-3.5"
          />
          Ghi nhớ đăng nhập
        </label>
        <Link
          href="/forgot-password"
          className="text-xs text-neutral-500 hover:text-neutral-900 transition underline-offset-4 hover:underline"
        >
          Quên mật khẩu?
        </Link>
      </div>

      <Button
        type="submit"
        disabled={loginMutation.isPending}
        className="w-full h-11 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99] mt-2"
      >
        {loginMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin mr-2" />
            Đang đăng nhập...
          </>
        ) : (
          'Đăng nhập'
        )}
      </Button>

      <div className="text-center pt-2">
        <p className="text-xs text-neutral-500">
          Chưa có tài khoản?{' '}
          <Link
            href="/register"
            className="font-semibold text-neutral-900 hover:underline underline-offset-4"
          >
            Đăng ký tài khoản mới
          </Link>
        </p>
      </div>
    </form>
  );
}

