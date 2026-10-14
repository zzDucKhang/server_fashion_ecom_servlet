'use client';

import * as React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  User,
  Shield,
  MapPin,
  Package,
  Coins,
  LogOut,
  LayoutDashboard,
} from 'lucide-react';
import { SiteHeader } from '@/components/layouts/site-header';
import { SiteFooter } from '@/components/layouts/site-footer';
import { useAuth } from '@/components/providers/auth-provider';
import { Avatar } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

const navItems = [
  { href: '/profile', label: 'Thông tin cá nhân', icon: User },
  { href: '/points', label: 'Lịch sử tích điểm', icon: Coins },
  { href: '/addresses', label: 'Sổ địa chỉ', icon: MapPin },
  { href: '/security', label: 'Bảo mật & Mật khẩu', icon: Shield },
  { href: '/orders', label: 'Đơn mua', icon: Package },
];

export default function AccountLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();
  const { user, isAdmin, logout } = useAuth();

  return (
    <div className="min-h-screen flex flex-col bg-[#FBF9F4]">
      <SiteHeader />

      <main className="flex-1 mx-auto w-full max-w-[1440px] px-4 sm:px-8 lg:px-12 py-8 sm:py-12">
        <div className="mb-8">
          <span className="eyebrow block mb-2">QUẢN LÝ TÀI KHOẢN</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Tài khoản của tôi
          </h1>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          {/* Sidebar */}
          <aside className="lg:col-span-1">
            <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs sticky top-28 space-y-6">
              {/* User overview */}
              <div className="flex items-center gap-3.5 pb-6 border-b border-[#EAE6DF]">
                <Avatar
                  src={user?.avatarUrl}
                  alt={user?.fullName || 'User'}
                  size="lg"
                  className="size-12 ring-2 ring-[#84EA53]/30"
                />
                <div className="min-w-0 flex-1">
                  <h3 className="text-sm font-semibold text-neutral-900 truncate">
                    {user?.fullName || 'Người dùng'}
                  </h3>
                  <p className="text-xs text-neutral-500 truncate">
                    {user?.email}
                  </p>
                  <div className="mt-1">
                    <Badge
                      variant={isAdmin ? 'lime' : 'secondary'}
                      className="text-[10px] py-0 px-2"
                    >
                      {user?.role?.name || (isAdmin ? 'Admin' : 'Khách hàng')}
                    </Badge>
                  </div>
                </div>
              </div>

              {/* Navigation Menu */}
              <nav className="space-y-1">
                {navItems.map((item) => {
                  const Icon = item.icon;
                  const isActive = pathname === item.href;
                  return (
                    <Link
                      key={item.href}
                      href={item.href}
                      className={cn(
                        'flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-medium transition',
                        isActive
                          ? 'bg-[#1D1F20] !text-white [&_*]:!text-white shadow-xs'
                          : 'text-neutral-600 hover:text-neutral-900 hover:bg-neutral-100',
                      )}
                    >
                      <Icon className="size-4 shrink-0" />
                      <span>{item.label}</span>
                    </Link>
                  );
                })}

                {isAdmin && (
                  <Link
                    href="/admin/dashboard"
                    className="flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold text-emerald-800 bg-emerald-50 hover:bg-emerald-100 transition mt-2"
                  >
                    <LayoutDashboard className="size-4 shrink-0 text-emerald-700" />
                    <span>Trang quản trị (Admin)</span>
                  </Link>
                )}
              </nav>

              {/* Logout Button */}
              <div className="pt-4 border-t border-[#EAE6DF]">
                <button
                  type="button"
                  onClick={logout}
                  className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-medium text-rose-600 hover:bg-rose-50 transition"
                >
                  <LogOut className="size-4 shrink-0" />
                  <span>Đăng xuất</span>
                </button>
              </div>
            </div>
          </aside>

          {/* Content Area */}
          <section className="lg:col-span-3">{children}</section>
        </div>
      </main>

      <SiteFooter />
    </div>
  );
}
