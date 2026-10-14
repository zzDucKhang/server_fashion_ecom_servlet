import { Metadata } from 'next';
import { Suspense } from 'react';
import Link from 'next/link';
import { RegisterForm } from '@/modules/auth/components/register-form';

export const metadata: Metadata = {
  title: 'Đăng ký tài khoản — Fashion Store',
  description: 'Tạo tài khoản mới để nhận ưu đãi thành viên và theo dõi đơn hàng tiện lợi.',
};

export default function RegisterPage() {
  return (
    <main className="min-h-screen w-full flex flex-col justify-center items-center p-4 sm:p-6 bg-[#FBF9F4]">
      <div className="w-full max-w-md my-8">
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <span className="eyebrow block mb-2 text-[#74736E]">FASHION STUDIO</span>
            <h1 className="font-display text-4xl text-[#1D1F20] tracking-tight">
              Đăng ký
            </h1>
          </Link>
          <p className="text-xs text-neutral-500 mt-2">
            Đăng ký tài khoản để bắt đầu trải nghiệm mua sắm độc quyền.
          </p>
        </div>

        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
          <Suspense fallback={<div className="h-48 animate-pulse rounded-xl bg-neutral-50" />}>
            <RegisterForm />
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

