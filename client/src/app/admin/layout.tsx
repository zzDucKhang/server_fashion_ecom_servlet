'use client';

import * as React from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { AdminSidebar } from '@/modules/admin/components/sidebar';
import { useAuth } from '@/components/providers/auth-provider';
import { Avatar } from '@/components/ui/avatar';
import { Bell, Search, ShieldAlert, ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { user, isAuthenticated, isAdmin } = useAuth();
  const router = useRouter();
  const pathname = usePathname();
  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#FBF9F4]">
        <div className="animate-pulse text-sm text-neutral-500">Đang khởi tạo quyền quản trị...</div>
      </div>
    );
  }

  const canAccessStaffChat =
    isAuthenticated && user?.role?.code === 'STAFF' && pathname === '/admin/chat';

  if (canAccessStaffChat) {
    return (
      <div className="flex min-h-screen bg-[#FBF9F4]">
        <AdminSidebar />
        <main className="min-w-0 flex-1 p-4 sm:p-8">{children}</main>
      </div>
    );
  }

  if (!isAuthenticated || !isAdmin) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-[#FBF9F4] p-6 text-center">
        <div className="size-14 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center mb-4">
          <ShieldAlert className="size-7" />
        </div>
        <h2 className="text-xl font-semibold text-neutral-900 mb-2">
          Truy cập bị từ chối
        </h2>
        <p className="text-xs text-neutral-500 max-w-sm mb-6 leading-relaxed">
          Khu vực này chỉ dành cho tài khoản có quyền Quản trị viên (Admin). Bạn không có quyền truy cập vào nội dung này.
        </p>
        <div className="flex gap-3">
          <Button
            variant="outline"
            onClick={() => router.push('/')}
            className="rounded-xl border-[#EAE6DF]"
          >
            <ArrowLeft className="size-4 mr-2" /> Về trang chủ
          </Button>
          <Button
            onClick={() => router.push('/login')}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white"
          >
            Đăng nhập tài khoản khác
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex bg-[#FBF9F4]">
      {/* Sidebar */}
      <AdminSidebar />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Topbar */}
        <header className="h-16 bg-white/80 backdrop-blur border-b border-[#EAE6DF] px-6 sm:px-8 flex items-center justify-between sticky top-0 z-30">
          <div className="flex items-center gap-3">
            <span className="eyebrow text-[#74736E]">ADMINISTRATION CONSOLE</span>
          </div>

          <div className="flex items-center gap-4">
            <button
              type="button"
              className="p-2 text-neutral-400 hover:text-neutral-700 hover:bg-neutral-100 rounded-xl transition relative"
            >
              <Bell className="size-4" />
              <span className="absolute top-1.5 right-1.5 size-2 bg-emerald-500 rounded-full ring-2 ring-white" />
            </button>

            <div className="h-6 w-px bg-[#EAE6DF]" />

            <Link
              href="/profile"
              className="flex items-center gap-2.5 p-1 rounded-xl hover:bg-neutral-50 transition"
            >
              <Avatar
                src={user?.avatarUrl}
                alt={user?.fullName || 'Admin'}
                size="sm"
                className="size-7"
              />
              <span className="text-xs font-semibold text-neutral-800 hidden sm:inline">
                {user?.fullName}
              </span>
            </Link>
          </div>
        </header>

        {/* Dynamic page content */}
        <main className="flex-1 p-6 sm:p-8 max-w-[1440px] w-full mx-auto">
          {children}
        </main>
      </div>
    </div>
  );
}
