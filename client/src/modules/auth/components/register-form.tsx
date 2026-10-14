'use client';

import * as React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { Eye, EyeOff, Loader2, CheckCircle2, AlertCircle, RotateCw } from 'lucide-react';
import { registerSchema, verifyCodeSchema, type RegisterFormData, type VerifyCodeFormData } from '../schemas';
import { useAuth } from '../hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

export function RegisterForm() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [showPassword, setShowPassword] = React.useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = React.useState(false);
  const [apiError, setApiError] = React.useState<string | null>(null);
  const [countdown, setCountdown] = React.useState(0);

  // Lấy step và email trực tiếp từ URL query params
  const stepParam = searchParams.get('step');
  const emailParam = searchParams.get('email') || '';

  const isVerifyStep = stepParam === 'verify' && Boolean(emailParam);

  const { registerMutation, verifyCodeMutation, resendCodeMutation } = useAuth() as any;

  const form = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      fullName: '',
      email: emailParam,
      phoneNumber: '',
      password: '',
      confirmPassword: '',
    },
  });

  const verifyForm = useForm<VerifyCodeFormData>({
    resolver: zodResolver(verifyCodeSchema),
    defaultValues: {
      email: emailParam,
      type: 'REGISTER',
      code: '',
    },
  });

  // Đồng bộ giá trị email vào verifyForm khi URL thay đổi
  React.useEffect(() => {
    if (emailParam) {
      verifyForm.setValue('email', emailParam);
    }
  }, [emailParam, verifyForm]);

  // Bộ đếm ngược cho nút Gửi lại mã OTP
  React.useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown((c) => c - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  // Submit form đăng ký ban đầu
  const onRegisterSubmit = (data: RegisterFormData) => {
    setApiError(null);
    registerMutation.mutate(
      {
        fullName: data.fullName,
        email: data.email,
        phoneNumber: data.phoneNumber || undefined,
        password: data.password,
      },
      {
        onSuccess: () => {
          setCountdown(60);
          // Đẩy URL sang step=verify để hỗ trợ Back/Forward và F5
          router.push(`/register?step=verify&email=${encodeURIComponent(data.email)}`);
        },
        onError: (err: any) => {
          const message =
            err?.response?.data?.message ||
            err?.message ||
            'Không thể đăng ký lúc này. Vui lòng kiểm tra lại cấu hình gửi mail của server.';
          setApiError(message);
        },
      }
    );
  };

  // Submit xác thực OTP
  const onVerifySubmit = (data: VerifyCodeFormData) => {
    setApiError(null);
    verifyCodeMutation.mutate(
      {
        ...data,
        email: emailParam,
      },
      {
        onSuccess: () => {
          router.push('/login?registered=true');
        },
        onError: (err: any) => {
          setApiError(
            err?.response?.data?.message ||
            err?.message ||
            'Mã OTP không hợp lệ hoặc đã hết hạn.'
          );
        },
      }
    );
  };

  // Xử lý gửi lại OTP
  const handleResendOtp = () => {
    if (countdown > 0 || !emailParam) return;
    setApiError(null);

    if (resendCodeMutation) {
      resendCodeMutation.mutate(
        { email: emailParam, type: 'REGISTER' },
        {
          onSuccess: () => setCountdown(60),
          onError: (err: any) => {
            setApiError(err?.response?.data?.message || 'Không thể gửi lại OTP lúc này');
          },
        }
      );
    } else {
      // Fallback: Gọi lại chính API register với cùng email để server tạo lại OTP
      registerMutation.mutate(
        {
          fullName: form.getValues('fullName') || 'Khách hàng',
          email: emailParam,
          password: form.getValues('password') || 'DefaultPass123@',
        },
        {
          onSuccess: () => setCountdown(60),
          onError: (err: any) => {
            setApiError(err?.response?.data?.message || 'Không thể gửi lại OTP');
          },
        }
      );
    }
  };

  // MÀN HÌNH NHẬP OTP
  if (isVerifyStep) {
    return (
      <div className="space-y-5">
        <div className="text-center space-y-2">
          <div className="mx-auto size-12 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <CheckCircle2 className="size-6" />
          </div>
          <h2 className="text-lg font-semibold text-neutral-900">Xác thực tài khoản</h2>
          <p className="text-xs text-neutral-500">
            Mã OTP 6 số đã được gửi tới email <strong className="text-neutral-800">{emailParam}</strong>.
          </p>
        </div>

        {apiError && (
          <div className="flex items-center gap-2 p-3 text-xs text-red-600 bg-red-50 border border-red-100 rounded-xl">
            <AlertCircle className="size-4 shrink-0" />
            <span>{apiError}</span>
          </div>
        )}

        <form onSubmit={verifyForm.handleSubmit(onVerifySubmit)} className="space-y-4">
          <FormField
            control={verifyForm.control}
            name="code"
            label="Mã xác thực (OTP)"
            placeholder="123456"
            error={verifyForm.formState.errors.code?.message}
          />

          <Button
            type="submit"
            disabled={verifyCodeMutation.isPending}
            className="w-full h-11 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium"
          >
            {verifyCodeMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-2" />
                Đang kích hoạt...
              </>
            ) : (
              'Hoàn tất kích hoạt'
            )}
          </Button>

          <div className="flex items-center justify-between pt-1 text-xs">
            <button
              type="button"
              onClick={() => router.push('/register')}
              className="text-neutral-500 hover:text-neutral-900 underline underline-offset-4"
            >
              ← Quay lại form đăng ký
            </button>

            <button
              type="button"
              disabled={countdown > 0 || (resendCodeMutation?.isPending ?? registerMutation.isPending)}
              onClick={handleResendOtp}
              className="inline-flex items-center gap-1 font-medium text-neutral-700 hover:text-neutral-950 disabled:text-neutral-400 disabled:cursor-not-allowed"
            >
              <RotateCw className={`size-3.5 ${(resendCodeMutation?.isPending ?? registerMutation.isPending) ? 'animate-spin' : ''}`} />
              {countdown > 0 ? `Gửi lại mã (${countdown}s)` : 'Gửi lại mã OTP'}
            </button>
          </div>
        </form>
      </div>
    );
  }

  // MÀN HÌNH ĐIỀN FORM ĐĂNG KÝ
  return (
    <form onSubmit={form.handleSubmit(onRegisterSubmit)} className="space-y-4">
      {apiError && (
        <div className="flex items-center gap-2 p-3 text-xs text-red-600 bg-red-50 border border-red-100 rounded-xl">
          <AlertCircle className="size-4 shrink-0" />
          <span>{apiError}</span>
        </div>
      )}

      <FormField
        control={form.control}
        name="fullName"
        label="Họ và tên"
        placeholder="Nguyễn Văn A"
        error={form.formState.errors.fullName?.message}
      />

      <FormField
        control={form.control}
        name="email"
        label="Địa chỉ Email"
        type="email"
        placeholder="example@domain.com"
        error={form.formState.errors.email?.message}
      />

      <FormField
        control={form.control}
        name="phoneNumber"
        label="Số điện thoại (tùy chọn)"
        placeholder="0912345678"
        error={form.formState.errors.phoneNumber?.message}
      />

      <div className="relative">
        <FormField
          control={form.control}
          name="password"
          label="Mật khẩu"
          type={showPassword ? 'text' : 'password'}
          placeholder="Tối thiểu 8 ký tự (hoa, thường, số, ký tự)"
          error={form.formState.errors.password?.message}
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
          label="Xác nhận mật khẩu"
          type={showConfirmPassword ? 'text' : 'password'}
          placeholder="Nhập lại mật khẩu"
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
        disabled={registerMutation.isPending}
        className="w-full h-11 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99] mt-2"
      >
        {registerMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin mr-2" />
            Đang tạo tài khoản...
          </>
        ) : (
          'Tạo tài khoản'
        )}
      </Button>

      <div className="text-center pt-2 space-y-1">
        <p className="text-xs text-neutral-500">
          Đã có tài khoản?{' '}
          <Link href="/login" className="font-semibold text-neutral-900 hover:underline underline-offset-4">
            Đăng nhập ngay
          </Link>
        </p>

        {/* Lối tắt cho người dùng đã lỡ tạo tài khoản nhưng chưa kịp nhập OTP */}
        <p className="text-xs text-neutral-500">
          Chưa nhập mã xác thực?{' '}
          <button
            type="button"
            onClick={() => {
              const currentEmail = form.getValues('email');
              if (currentEmail) {
                router.push(`/register?step=verify&email=${encodeURIComponent(currentEmail)}`);
              } else {
                setApiError('Vui lòng nhập email ở trên trước khi bấm xác thực.');
              }
            }}
            className="font-medium text-blue-600 hover:underline underline-offset-4"
          >
            Nhập mã OTP tại đây
          </button>
        </p>
      </div>
    </form>
  );
}