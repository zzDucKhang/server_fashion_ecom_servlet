import { Metadata } from 'next';
import Link from 'next/link';
import { ForgotPasswordForm } from '@/modules/auth/components/forgot-password-form';

export const metadata: Metadata = {
  title: 'Quên mật khẩu — Fashion Store',
  description: 'Khôi phục mật khẩu tài khoản của bạn thông qua mã xác thực gửi về email.',
};

export default function ForgotPasswordPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Quên mật khẩu
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Nhập email của bạn để nhận mã xác thực khôi phục mật khẩu.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <ForgotPasswordForm />
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

