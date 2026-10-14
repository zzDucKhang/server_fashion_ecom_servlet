'use client';

import * as React from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import {
  Search,
  ShoppingBag,
  User,
  Settings,
  LogOut,
  MapPin,
  Package,
  Shield,
  LayoutDashboard,
  LogIn,
  UserPlus,
  Sparkles,
  ChevronRight,
} from 'lucide-react';
import { useCart } from '@/modules/cart/hooks/use-cart';
import { useAuth } from '@/components/providers/auth-provider';
import { Avatar } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';


// Snapshot kiểm tra SSR Client / Server tránh lỗi Hydration
const emptySubscribe = () => () => {};
function useIsMounted() {
  return React.useSyncExternalStore(
    emptySubscribe,
    () => true,
    () => false
  );
}

export function SiteHeader() {
  const isMounted = useIsMounted();
  const pathname = usePathname();
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const [isSettingsOpen, setIsSettingsOpen] = React.useState(false);
  const menuRef = React.useRef<HTMLDivElement>(null);
  const [searchQuery, setSearchQuery] = React.useState('');
  const router = useRouter();

  const { data: cartResponse } = useCart();
  const cartItems = cartResponse?.data?.cartItems || [];

  const totalQuantity = cartItems.reduce(
    (total, item) => total + (item.quantity || 0),
    0
  );

  // Tự động đóng menu khi click ra ngoài hoặc đổi trang
  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsSettingsOpen(false);
      }
    };

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setIsSettingsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  React.useEffect(() => {
    setIsSettingsOpen(false);
  }, [pathname]);

  const handleSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && searchQuery.trim()) {
      router.push(`/products?search=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  return (
    <header className="sticky top-0 z-40 border-b border-[#EAE6DF] bg-[#FBF9F4]/95 backdrop-blur select-none">
      <div className="mx-auto flex h-[72px] max-w-[1440px] items-center gap-5 px-4 sm:px-8 lg:px-12">
        {/* Logo */}
        <Link
          href="/"
          className="shrink-0 font-display text-3xl font-semibold tracking-[-.06em]"
        >
          common<span className="text-[#84EA53]">.</span>
        </Link>

        {/* Search Bar */}
        <div className="hidden h-11 max-w-[700px] flex-1 items-center rounded-full border border-[#EAE6DF] bg-white px-4 shadow-[0_5px_20px_rgba(29,31,32,.04)] sm:flex">
          <Search size={17} className="mr-3 text-[#74736e]" />
          <input
            value={searchQuery} // <-- LIÊN KẾT STATE
            onChange={(e) => setSearchQuery(e.target.value)} // <-- LẮNG NGHE THAY ĐỔI
            onKeyDown={handleSearch} // <-- LẮNG NGHE NÚT ENTER
            aria-label="Search products"
            placeholder="Tìm kiếm sản phẩm, danh mục..."
            className="w-full bg-transparent text-sm outline-none placeholder:text-[#9b9991]"
          />
        </div>

        {/* Action Buttons */}
        <div className="ml-auto flex items-center gap-2">
          {/* Mobile Search */}
          <Link
            href="/products" 
            className="rounded-full p-2.5 hover:bg-white sm:hidden transition"
          >
            <Search size={20} />
          </Link>

          {/* User Orders Shortcut */}
          <Link
            href="/orders"
            className="relative rounded-full p-2.5 text-neutral-700 hover:bg-white hover:text-neutral-900 transition"
            title="Đơn mua của tôi"
          >
            <User size={20} />
          </Link>

          {/* Cart Icon */}
          <Link
            href="/cart"
            className="relative rounded-full p-2.5 text-neutral-700 hover:bg-white hover:text-neutral-900 transition"
            title="Giỏ hàng"
          >
            <ShoppingBag size={20} />
            {isMounted && totalQuantity > 0 && (
              <span className="absolute right-0 top-0 flex h-4 min-w-4 px-1 items-center justify-center rounded-full bg-[#84EA53] text-[9px] font-bold text-[#1D1F20]">
                {totalQuantity > 99 ? '99+' : totalQuantity}
              </span>
            )}
          </Link>

          {/* ======================================================== */}
          {/* NÚT BÁNH RĂNG CÀI ĐẶT & USER DROPDOWN MENU CHUẨN SHOP LỚN */}
          {/* ======================================================== */}
          <div className="relative" ref={menuRef}>
            <button
              type="button"
              onClick={() => setIsSettingsOpen((prev) => !prev)}
              className={cn(
                'relative flex items-center justify-center rounded-full p-2.5 text-neutral-700 transition cursor-pointer',
                isSettingsOpen
                  ? 'bg-neutral-900 text-white shadow-xs'
                  : 'hover:bg-white hover:text-neutral-900'
              )}
              title="Cài đặt & Tài khoản"
              aria-label="Cài đặt & Tài khoản"
              aria-expanded={isSettingsOpen}
            >
              <Settings
                size={20}
                className={cn(
                  'transition-transform duration-300',
                  isSettingsOpen ? 'rotate-90 text-[#84EA53]' : 'hover:rotate-45'
                )}
              />
              {/* Chấm xanh báo hiệu tài khoản đã đăng nhập */}
              {isMounted && isAuthenticated && (
                <span className="absolute top-1.5 right-1.5 size-2 bg-emerald-500 rounded-full ring-2 ring-white" />
              )}
            </button>

            {/* Menu Popup */}
            {isSettingsOpen && (
              <div className="absolute right-0 mt-3 w-80 sm:w-84 rounded-2xl border border-[#EAE6DF] bg-white shadow-xl p-2 z-50 animate-in fade-in zoom-in-95 duration-150">
                {isMounted && isAuthenticated ? (
                  <>
                    {/* Header thông tin người dùng */}
                    <div className="flex items-center gap-3 p-3 rounded-xl bg-neutral-50/80 border border-[#EAE6DF]/60 mb-2">
                      <Avatar
                        src={user?.avatarUrl}
                        alt={user?.fullName || 'User'}
                        size="lg"
                        className="size-11 ring-2 ring-[#84EA53]/50"
                      />
                      <div className="min-w-0 flex-1">
                        <div className="flex items-center gap-1.5">
                          <p className="text-sm font-bold text-neutral-900 truncate">
                            {user?.fullName || 'Khách hàng'}
                          </p>
                          <Badge
                            variant={isAdmin ? 'lime' : 'secondary'}
                            className="text-[9px] py-0 px-1.5 h-4"
                          >
                            {isAdmin ? 'Admin' : 'Thành viên'}
                          </Badge>
                        </div>
                        <p className="text-xs text-neutral-500 truncate mt-0.5">
                          {user?.email}
                        </p>
                        <div className="flex items-center gap-1 mt-1 text-[11px] font-semibold text-neutral-700">
                          <Sparkles className="size-3 text-amber-500 fill-amber-500" />
                          <span>
                            {(user?.rewardPoints || 0).toLocaleString('vi-VN')} điểm thưởng
                          </span>
                        </div>
                      </div>
                    </div>

                    {/* Danh mục cài đặt */}
                    <div className="space-y-0.5 text-xs font-medium text-neutral-700">
                      <Link
                        href="/profile"
                        className="flex items-center justify-between px-3 py-2.5 rounded-xl hover:bg-neutral-100 hover:text-neutral-900 transition group"
                      >
                        <div className="flex items-center gap-2.5">
                          <User className="size-4 text-neutral-500 group-hover:text-neutral-900 transition" />
                          <span>Thông tin cá nhân</span>
                        </div>
                        <ChevronRight className="size-3.5 text-neutral-400 group-hover:translate-x-0.5 transition" />
                      </Link>

                      <Link
                        href="/orders"
                        className="flex items-center justify-between px-3 py-2.5 rounded-xl hover:bg-neutral-100 hover:text-neutral-900 transition group"
                      >
                        <div className="flex items-center gap-2.5">
                          <Package className="size-4 text-neutral-500 group-hover:text-neutral-900 transition" />
                          <span>Đơn mua của tôi</span>
                        </div>
                        <ChevronRight className="size-3.5 text-neutral-400 group-hover:translate-x-0.5 transition" />
                      </Link>

                      <Link
                        href="/addresses"
                        className="flex items-center justify-between px-3 py-2.5 rounded-xl hover:bg-neutral-100 hover:text-neutral-900 transition group"
                      >
                        <div className="flex items-center gap-2.5">
                          <MapPin className="size-4 text-neutral-500 group-hover:text-neutral-900 transition" />
                          <span>Sổ địa chỉ nhận hàng</span>
                        </div>
                        <ChevronRight className="size-3.5 text-neutral-400 group-hover:translate-x-0.5 transition" />
                      </Link>

                      <Link
                        href="/security"
                        className="flex items-center justify-between px-3 py-2.5 rounded-xl hover:bg-neutral-100 hover:text-neutral-900 transition group"
                      >
                        <div className="flex items-center gap-2.5">
                          <Shield className="size-4 text-neutral-500 group-hover:text-neutral-900 transition" />
                          <span>Bảo mật & Đổi mật khẩu</span>
                        </div>
                        <ChevronRight className="size-3.5 text-neutral-400 group-hover:translate-x-0.5 transition" />
                      </Link>

                      {/* Nút dành riêng cho Admin */}
                      {isAdmin && (
                        <Link
                          href="/admin/dashboard"
                          className="flex items-center justify-between px-3 py-2.5 rounded-xl bg-emerald-50 text-emerald-900 hover:bg-emerald-100 font-semibold transition mt-1.5"
                        >
                          <div className="flex items-center gap-2.5">
                            <LayoutDashboard className="size-4 text-emerald-700" />
                            <span>Trang quản trị (Admin)</span>
                          </div>
                          <ChevronRight className="size-3.5 text-emerald-600" />
                        </Link>
                      )}
                    </div>

                    {/* Phân cách & Nút Đăng xuất */}
                    <div className="pt-2 mt-2 border-t border-[#EAE6DF]">
                      <button
                        type="button"
                        onClick={logout}
                        className="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-xs font-semibold text-rose-600 hover:bg-rose-50 transition cursor-pointer"
                      >
                        <LogOut className="size-4" />
                        <span>Đăng xuất tài khoản</span>
                      </button>
                    </div>
                  </>
                ) : (
                  /* Trạng thái chưa đăng nhập */
                  <div className="p-3 text-center space-y-3">
                    <div className="size-12 rounded-full bg-neutral-100 text-neutral-700 flex items-center justify-center mx-auto">
                      <User size={22} />
                    </div>
                    <div>
                      <h4 className="text-sm font-bold text-neutral-900">
                        Chào mừng quý khách
                      </h4>
                      <p className="text-xs text-neutral-500 mt-1">
                        Đăng nhập để theo dõi đơn hàng và tận hưởng ưu đãi thành viên.
                      </p>
                    </div>

                    <div className="flex flex-col gap-2 pt-1">
                      <Link
                        href="/login"
                        className="flex items-center justify-center gap-2 w-full py-2.5 rounded-xl bg-[#1D1F20] hover:bg-black !text-white text-xs font-bold transition shadow-xs"
                      >
                        <LogIn size={14} />
                        <span>Đăng nhập</span>
                      </Link>
                      <Link
                        href="/register"
                        className="flex items-center justify-center gap-2 w-full py-2.5 rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 text-neutral-800 text-xs font-bold transition"
                      >
                        <UserPlus size={14} />
                        <span>Tạo tài khoản mới</span>
                      </Link>
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}