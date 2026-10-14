'use client';

import * as React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  LayoutDashboard,
  Users,
  Shield,
  ShoppingBag,
  Layers,
  Sparkles,
  Zap,
  Package,
  Boxes,
  Truck,
  RotateCcw,
  Ticket,
  Star,
  MessageSquare,
  Tag,
  Coins,
  ChevronRight,
  LogOut,
  ExternalLink,
} from 'lucide-react';
import { useAuth } from '@/components/providers/auth-provider';
import { Avatar } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

interface NavGroup {
  title: string;
  items: {
    title: string;
    href: string;
    icon: React.ComponentType<{ className?: string }>;
  }[];
}

const navGroups: NavGroup[] = [
  {
    title: 'TỔNG QUAN',
    items: [
      { title: 'Dashboard', href: '/admin/dashboard', icon: LayoutDashboard },
    ],
  },
  {
    title: 'NGƯỜI DÙNG & PHÂN QUYỀN',
    items: [
      { title: 'Quản lý người dùng', href: '/admin/users', icon: Users },
      { title: 'Vai trò & Quyền hạn', href: '/admin/roles', icon: Shield },
    ],
  },
  {
    title: 'SẢN PHẨM & CATALOG',
    items: [
      { title: 'Sản phẩm', href: '/admin/products', icon: ShoppingBag },
      { title: 'Danh mục', href: '/admin/categories', icon: Layers },
      { title: 'Thương hiệu', href: '/admin/brands', icon: Sparkles },
      { title: 'Thẻ sản phẩm', href: '/admin/tags', icon: Tag },
      { title: 'Flash Sales', href: '/admin/flash-sales', icon: Zap },
    ],
  },
  {
    title: 'ĐƠN HÀNG & VẬN HÀNH',
    items: [
      { title: 'Đơn hàng', href: '/admin/orders', icon: Package },
      { title: 'Quản lý tồn kho', href: '/admin/inventory', icon: Boxes },
      { title: 'Vận chuyển', href: '/admin/shipments', icon: Truck },
      { title: 'Yêu cầu đổi trả', href: '/admin/returns', icon: RotateCcw },
    ],
  },
  {
    title: 'MARKETING & CSKH',
    items: [
      { title: 'Vouchers & Khuyến mãi', href: '/admin/vouchers', icon: Ticket },
      { title: 'Điểm thưởng', href: '/admin/points', icon: Coins },
      { title: 'Đánh giá sản phẩm', href: '/admin/reviews', icon: Star },
      { title: 'Tin nhắn CSKH', href: '/admin/chat', icon: MessageSquare },
    ],
  },
];

export function AdminSidebar() {
  const pathname = usePathname();
  const { user, logout } = useAuth();
  const isStaff = user?.role?.code === 'STAFF';
  const visibleNavGroups = isStaff
    ? navGroups
        .map((group) => ({
          ...group,
          items: group.items.filter((item) => item.href === '/admin/chat'),
        }))
        .filter((group) => group.items.length > 0)
    : navGroups;

  return (
    <aside className="w-64 shrink-0 bg-white border-r border-[#EAE6DF] min-h-screen flex flex-col justify-between sticky top-0 h-screen select-none">
      <div className="flex flex-col flex-1 overflow-hidden">
        {/* Brand header */}
        <div className="h-16 px-6 flex items-center justify-between border-b border-[#EAE6DF]">
          <Link href={isStaff ? '/admin/chat' : '/admin/dashboard'} className="flex items-center gap-2">
            <span className="font-display text-2xl font-semibold tracking-[-.06em]">
              common<span className="text-[#84EA53]">.</span>
            </span>
            <Badge variant="lime" className="text-[9px] uppercase px-1.5 py-0">
              {isStaff ? 'CSKH' : 'Admin'}
            </Badge>
          </Link>
          <Link
            href="/"
            target="_blank"
            title="Xem cửa hàng"
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 transition"
          >
            <ExternalLink className="size-4" />
          </Link>
        </div>

        {/* Navigation list */}
        <div className="flex-1 overflow-y-auto px-4 py-4 space-y-6">
          {visibleNavGroups.map((group) => (
            <div key={group.title} className="space-y-1">
              <h4 className="text-[10px] font-bold uppercase tracking-wider text-neutral-400 px-3 mb-2">
                {group.title}
              </h4>
              {group.items.map((item) => {
                const Icon = item.icon;
                const isActive = pathname === item.href || (item.href !== '/admin/dashboard' && pathname.startsWith(item.href));
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={cn(
                      'flex items-center justify-between px-3 py-2 rounded-xl text-xs font-medium transition',
                      isActive
                        ? 'bg-[#1D1F20] !text-white [&_*]:!text-white shadow-xs'
                        : 'text-neutral-600 hover:text-neutral-900 hover:bg-neutral-50'
                    )}
                  >
                    <div className="flex items-center gap-2.5">
                      <Icon className={cn('size-4', isActive ? 'text-[#84EA53]' : 'text-neutral-500')} />
                      <span>{item.title}</span>
                    </div>
                    {isActive && <ChevronRight className="size-3 text-neutral-400" />}
                  </Link>
                );
              })}
            </div>
          ))}
        </div>
      </div>

      {/* User profile footer */}
      <div className="p-4 border-t border-[#EAE6DF] bg-neutral-50/50">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-2.5 min-w-0">
            <Avatar
              src={user?.avatarUrl}
              alt={user?.fullName || 'Admin'}
              size="sm"
              className="size-8 ring-1 ring-black/5"
            />
            <div className="min-w-0">
              <p className="text-xs font-semibold text-neutral-900 truncate">
                {user?.fullName || 'Quản trị viên'}
              </p>
              <p className="text-[10px] text-neutral-500 truncate">{user?.email}</p>
            </div>
          </div>
          <button
            type="button"
            onClick={logout}
            title="Đăng xuất"
            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
          >
            <LogOut className="size-4" />
          </button>
        </div>
      </div>
    </aside>
  );
}
