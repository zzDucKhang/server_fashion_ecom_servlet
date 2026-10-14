'use client';

import * as React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Loader2, MailCheck } from 'lucide-react';
import {
  forgotPasswordSchema,
  verifyCodeSchema,
  type ForgotPasswordFormData,
  type VerifyCodeFormData,
} from '../schemas';
import { useAuth } from '../hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

export function ForgotPasswordForm() {
  const router = useRouter();
  const [step, setStep] = React.useState<'email' | 'verify'>('email');
  const [userEmail, setUserEmail] = React.useState('');
  const { forgotPasswordMutation, verifyCodeMutation } = useAuth();

  const emailForm = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
    defaultValues: { email: '' },
  });

  const verifyForm = useForm<VerifyCodeFormData>({
    resolver: zodResolver(verifyCodeSchema),
    defaultValues: {
      email: '',
      type: 'FORGOT_PASSWORD',
      code: '',
    },
  });

  const onEmailSubmit = (data: ForgotPasswordFormData) => {
    forgotPasswordMutation.mutate(data.email, {
      onSuccess: () => {
        setUserEmail(data.email);
        verifyForm.setValue('email', data.email);
        setStep('verify');
      },
    });
  };

  const onVerifySubmit = (data: VerifyCodeFormData) => {
    verifyCodeMutation.mutate(data, {
      onSuccess: (res: any) => {
        const resetToken = res?.data?.resetToken || res?.resetToken;
        if (resetToken) {
          router.push(`/reset-password?token=${encodeURIComponent(resetToken)}`);
        } else {
          router.push('/login');
        }
      },
    });
  };

  if (step === 'verify') {
    return (
      <div className="space-y-5">
        <div className="text-center space-y-2">
          <div className="mx-auto size-12 rounded-full bg-neutral-100 text-neutral-800 flex items-center justify-center">
            <MailCheck className="size-6" />
          </div>
          <h2 className="text-lg font-semibold text-neutral-900">Nhập mã xác thực</h2>
          <p className="text-xs text-neutral-500">
            Mã OTP 6 số đã được gửi tới email <strong className="text-neutral-800">{userEmail}</strong>.
          </p>
        </div>

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
                Đang xác thực...
              </>
            ) : (
              'Tiếp tục đặt lại mật khẩu'
            )}
          </Button>

          <div className="text-center pt-2">
            <button
              type="button"
              onClick={() => setStep('email')}
              className="text-xs text-neutral-500 hover:text-neutral-900 underline underline-offset-4"
            >
              ← Gửi lại mã hoặc đổi email
            </button>
          </div>
        </form>
      </div>
    );
  }

  return (
    <form onSubmit={emailForm.handleSubmit(onEmailSubmit)} className="space-y-4">
      <FormField
        control={emailForm.control}
        name="email"
        label="Địa chỉ Email"
        type="email"
        placeholder="example@domain.com"
        error={emailForm.formState.errors.email?.message}
      />

      <Button
        type="submit"
        disabled={forgotPasswordMutation.isPending}
        className="w-full h-11 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99] mt-2"
      >
        {forgotPasswordMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin mr-2" />
            Đang gửi mã xác thực...
          </>
        ) : (
          'Gửi mã xác thực'
        )}
      </Button>

      <div className="text-center pt-2">
        <Link
          href="/login"
          className="text-xs text-neutral-500 hover:text-neutral-900 transition underline underline-offset-4"
        >
          ← Nhớ lại mật khẩu? Quay về đăng nhập
        </Link>
      </div>
    </form>
  );
}

