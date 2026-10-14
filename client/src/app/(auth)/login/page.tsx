import { Metadata } from 'next';
import Link from 'next/link';
import { LoginForm } from '@/modules/auth/components/login-form';

export const metadata: Metadata = {
  title: 'Đăng nhập — Fashion Store',
  description: 'Đăng nhập vào tài khoản của bạn để trải nghiệm mua sắm và quản lý đơn hàng.',
};

export default function LoginPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Đăng nhập
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Chào mừng bạn quay trở lại. Hãy điền thông tin để tiếp tục.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <LoginForm />
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

