# Project Dump

**Root:** `C:\Programing\cnpm\fashion-ecom\client\src\components`

**Generated:** 2026-10-14 11:38:00.117266

**Files:** 22 | **Total size:** 58,721 bytes

---

# layouts\admin-sidebar.tsx

| Property | Value |
|----------|-------|
| Path | `layouts\admin-sidebar.tsx` |
| Size | 70 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.884291 |

```tsx
export { AdminSidebar } from '@/modules/admin/components/sidebar';


```

# layouts\mobile-nav.tsx

| Property | Value |
|----------|-------|
| Path | `layouts\mobile-nav.tsx` |
| Size | 0 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-10 15:51:13.125206 |

```tsx

```

# layouts\site-footer.tsx

| Property | Value |
|----------|-------|
| Path | `layouts\site-footer.tsx` |
| Size | 2,546 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.886554 |

```tsx
import Link from 'next/link';

export function SiteFooter() {
  return (
    <footer className="border-t border-[#EAE6DF] bg-white py-12 mt-auto">
      <div className="mx-auto max-w-[1440px] px-4 sm:px-8 lg:px-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <Link href="/" className="font-display text-2xl font-semibold tracking-[-.06em]">
              common<span className="text-[#84EA53]">.</span>
            </Link>
            <p className="mt-3 text-xs text-neutral-500 leading-relaxed max-w-xs">
              Bộ sưu tập chọn lọc thời trang đương đại và các sản phẩm phong cách sống tối giản.
            </p>
          </div>
          <div>
            <h4 className="eyebrow mb-3">Tài khoản</h4>
            <ul className="space-y-2 text-xs text-neutral-600">
              <li><Link href="/profile" className="hover:text-neutral-900 transition">Hồ sơ cá nhân</Link></li>
              <li><Link href="/orders" className="hover:text-neutral-900 transition">Lịch sử đơn hàng</Link></li>
              <li><Link href="/addresses" className="hover:text-neutral-900 transition">Sổ địa chỉ</Link></li>
              <li><Link href="/security" className="hover:text-neutral-900 transition">Bảo mật tài khoản</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="eyebrow mb-3">Hỗ trợ khách hàng</h4>
            <ul className="space-y-2 text-xs text-neutral-600">
              <li><Link href="#" className="hover:text-neutral-900 transition">Chính sách đổi trả</Link></li>
              <li><Link href="#" className="hover:text-neutral-900 transition">Hướng dẫn chọn size</Link></li>
              <li><Link href="#" className="hover:text-neutral-900 transition">Phương thức thanh toán</Link></li>
              <li><Link href="#" className="hover:text-neutral-900 transition">Câu hỏi thường gặp</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="eyebrow mb-3">Về chúng tôi</h4>
            <p className="text-xs text-neutral-500 leading-relaxed">
              Trải nghiệm mua sắm hiện đại, tinh tế và bền vững.
            </p>
            <p className="text-xs text-neutral-400 mt-4">
              © {new Date().getFullYear()} Common Fashion. All rights reserved.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}


```

# layouts\site-header.tsx

| Property | Value |
|----------|-------|
| Path | `layouts\site-header.tsx` |
| Size | 14,581 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 19:21:02.723432 |

```tsx
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
```

# providers\auth-provider.tsx

| Property | Value |
|----------|-------|
| Path | `providers\auth-provider.tsx` |
| Size | 3,703 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 19:05:04.468395 |

```tsx
'use client';

import React, { createContext, useContext, useSyncExternalStore, useEffect } from 'react'; // <-- Thêm useEffect
import { UserProfile } from '@/types';
import { cartApi } from '@/api/cart.api';
import { apiClient } from '@/api/client'; // <-- Thêm apiClient

interface AuthContextType {
  user: UserProfile | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (accessToken: string, refreshToken: string, user: UserProfile) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

function subscribe(callback: () => void) {
  window.addEventListener('storage', callback);
  window.addEventListener('local-storage-change', callback);
  return () => {
    window.removeEventListener('storage', callback);
    window.removeEventListener('local-storage-change', callback);
  };
}

function getStoredUserSnapshot(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('user');
}

function getStoredUserServerSnapshot(): string | null {
  return null;
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const userString = useSyncExternalStore(
    subscribe,
    getStoredUserSnapshot,
    getStoredUserServerSnapshot
  );

  const user: UserProfile | null = React.useMemo(() => {
    if (!userString) return null;
    try {
      return JSON.parse(userString);
    } catch {
      return null;
    }
  }, [userString]);


  useEffect(() => {
    if (user?.id) {
      apiClient.get(`/identity/users/${user.id}`)
        .then(res => {
          const freshUser = res.data?.data;
          if (freshUser && typeof freshUser.rewardPoints === 'number') {
            // Nếu điểm trong DB khác với dưới LocalStorage -> Cập nhật đè lên LocalStorage
            if (freshUser.rewardPoints !== user.rewardPoints || freshUser.status !== user.status) {
              const updatedUser = { ...user, ...freshUser };
              localStorage.setItem('user', JSON.stringify(updatedUser));
              window.dispatchEvent(new Event('local-storage-change')); // Ép Header render lại số điểm ngay lập tức
            }
          }
        })
        .catch(() => {}); // Nuốt lỗi im lặng để không phiền khách nếu mất mạng
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id]); // Chỉ quét 1 lần khi load app

  const login = async (accessToken: string, refreshToken: string, newUser: UserProfile) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(newUser));
    document.cookie = `accessToken=${accessToken}; path=/; max-age=3600`;

    window.dispatchEvent(new Event('local-storage-change'));

    try {
      await cartApi.mergeCart();
    } catch (err) {
      console.error('Merge cart failed:', err);
    }
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    document.cookie = 'accessToken=; path=/; max-age=0';

    window.dispatchEvent(new Event('local-storage-change'));
    window.location.href = '/login';
  };

  const value = React.useMemo(
    () => ({
      user,
      isAuthenticated: !!user,
      isAdmin: user?.role?.code === 'ADMIN',
      login,
      logout,
    }),
    [user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
```

# providers\query-provider.tsx

| Property | Value |
|----------|-------|
| Path | `providers\query-provider.tsx` |
| Size | 623 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 14:53:18.462067 |

```tsx
// src/components/providers/query-provider.tsx
'use client';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState } from 'react';

export function QueryProvider({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 60 * 1000, // Cache 1 phút
            refetchOnWindowFocus: false,
            retry: 1,
          },
        },
      })
  );

  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}
```

# providers\socket-provider.tsx

| Property | Value |
|----------|-------|
| Path | `providers\socket-provider.tsx` |
| Size | 1,972 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-12 17:08:47.188977 |

```tsx
'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { io, Socket } from 'socket.io-client';

interface SocketContextType {
  socket: Socket | null;
  isConnected: boolean;
}

const SocketContext = createContext<SocketContextType>({
  socket: null,
  isConnected: false,
});

export function SocketProvider({ children }: { children: React.ReactNode }) {
  const [socket, setSocket] = useState<Socket | null>(null);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const socketInstance = io(process.env.NEXT_PUBLIC_SOCKET_URL || 'http://localhost:3000', {
      autoConnect: false,
    });

    const onConnect = () => setIsConnected(true);
    const onDisconnect = () => setIsConnected(false);
    const syncAuthentication = () => {
      const token = localStorage.getItem('accessToken');
      socketInstance.auth = { token };
      if (token) {
        if (!socketInstance.connected) socketInstance.connect();
      } else {
        socketInstance.disconnect();
      }
    };

    socketInstance.on('connect', onConnect);
    socketInstance.on('disconnect', onDisconnect);
    window.addEventListener('storage', syncAuthentication);
    window.addEventListener('local-storage-change', syncAuthentication);
    syncAuthentication();
    const timer = setTimeout(() => setSocket(socketInstance), 0);

    return () => {
      clearTimeout(timer);
      window.removeEventListener('storage', syncAuthentication);
      window.removeEventListener('local-storage-change', syncAuthentication);
      socketInstance.off('connect', onConnect);
      socketInstance.off('disconnect', onDisconnect);
      socketInstance.disconnect();
    };
  }, []);

  return (
    <SocketContext.Provider value={{ socket, isConnected }}>
      {children}
    </SocketContext.Provider>
  );
}

export const useSocket = () => useContext(SocketContext);
```

# ui\avatar.tsx

| Property | Value |
|----------|-------|
| Path | `ui\avatar.tsx` |
| Size | 1,365 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.888693 |

```tsx
import * as React from 'react';
import { cn } from '@/lib/utils';

interface AvatarProps extends React.HTMLAttributes<HTMLDivElement> {
  src?: string | null;
  alt?: string;
  fallback?: string;
  size?: 'sm' | 'default' | 'lg' | 'xl';
}

export function Avatar({
  src,
  alt = 'Avatar',
  fallback,
  size = 'default',
  className,
  ...props
}: AvatarProps) {
  const [imageError, setImageError] = React.useState(false);

  const sizeClasses = {
    sm: 'size-7 text-xs',
    default: 'size-9 text-xs',
    lg: 'size-12 text-sm',
    xl: 'size-16 text-base font-medium',
  };

  const getFallbackText = () => {
    if (fallback) return fallback.slice(0, 2).toUpperCase();
    if (alt) return alt.slice(0, 2).toUpperCase();
    return 'U';
  };

  return (
    <div
      className={cn(
        'relative inline-flex shrink-0 items-center justify-center overflow-hidden rounded-full bg-neutral-200 text-neutral-700 font-semibold select-none ring-1 ring-black/5',
        sizeClasses[size],
        className
      )}
      {...props}
    >
      {src && !imageError ? (
        <img
          src={src}
          alt={alt}
          onError={() => setImageError(true)}
          className="size-full object-cover"
        />
      ) : (
        <span>{getFallbackText()}</span>
      )}
    </div>
  );
}


```

# ui\badge.tsx

| Property | Value |
|----------|-------|
| Path | `ui\badge.tsx` |
| Size | 1,284 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.525512 |

```tsx
import * as React from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/utils';

const badgeVariants = cva(
  'inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium tracking-wide transition-colors',
  {
    variants: {
      variant: {
        default: 'bg-neutral-900 !text-white [&_*]:!text-white',
        secondary: 'bg-neutral-100 text-neutral-800 border border-neutral-200',
        outline: 'border border-[#EAE6DF] text-neutral-700 bg-transparent',
        success: 'bg-emerald-50 text-emerald-700 border border-emerald-200/60',
        warning: 'bg-amber-50 text-amber-700 border border-amber-200/60',
        destructive: 'bg-rose-50 text-rose-700 border border-rose-200/60',
        lime: 'bg-[#84EA53]/20 text-neutral-900 border border-[#84EA53]/40 font-semibold',
      },
    },
    defaultVariants: {
      variant: 'default',
    },
  }
);

export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof badgeVariants> {}

function Badge({ className, variant, ...props }: BadgeProps) {
  return (
    <div className={cn(badgeVariants({ variant }), className)} {...props} />
  );
}

export { Badge, badgeVariants };


```

# ui\button.tsx

| Property | Value |
|----------|-------|
| Path | `ui\button.tsx` |
| Size | 3,303 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 15:21:31.367495 |

```tsx
import { Button as ButtonPrimitive } from '@base-ui/react/button';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/utils';

const buttonVariants = cva(
  "group/button inline-flex shrink-0 items-center justify-center rounded-lg border border-transparent bg-clip-padding text-sm font-medium whitespace-nowrap transition-all outline-none select-none focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 active:not-aria-[haspopup]:translate-y-px disabled:pointer-events-none disabled:opacity-50 aria-invalid:border-destructive aria-invalid:ring-3 aria-invalid:ring-destructive/20 dark:aria-invalid:border-destructive/50 dark:aria-invalid:ring-destructive/40 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4",
  {
    variants: {
      variant: {
        default: 'bg-primary text-primary-foreground hover:bg-primary/80',
        outline:
          'border-border bg-background hover:bg-muted hover:text-foreground aria-expanded:bg-muted aria-expanded:text-foreground dark:border-input dark:bg-input/30 dark:hover:bg-input/50',
        secondary:
          'bg-secondary text-secondary-foreground hover:bg-[color-mix(in_oklch,var(--secondary),var(--foreground)_5%)] aria-expanded:bg-secondary aria-expanded:text-secondary-foreground',
        ghost:
          'hover:bg-muted hover:text-foreground aria-expanded:bg-muted aria-expanded:text-foreground dark:hover:bg-muted/50',
        destructive:
          'bg-destructive/10 text-destructive hover:bg-destructive/20 focus-visible:border-destructive/40 focus-visible:ring-destructive/20 dark:bg-destructive/20 dark:hover:bg-destructive/30 dark:focus-visible:ring-destructive/40',
        link: 'text-primary underline-offset-4 hover:underline',
      },
      size: {
        default:
          'h-8 gap-1.5 px-2.5 has-data-[icon=inline-end]:pr-2 has-data-[icon=inline-start]:pl-2',
        xs: "h-6 gap-1 rounded-[min(var(--radius-md),10px)] px-2 text-xs in-data-[slot=button-group]:rounded-lg has-data-[icon=inline-end]:pr-1.5 has-data-[icon=inline-start]:pl-1.5 [&_svg:not([class*='size-'])]:size-3",
        sm: "h-7 gap-1 rounded-[min(var(--radius-md),12px)] px-2.5 text-[0.8rem] in-data-[slot=button-group]:rounded-lg has-data-[icon=inline-end]:pr-1.5 has-data-[icon=inline-start]:pl-1.5 [&_svg:not([class*='size-'])]:size-3.5",
        lg: 'h-9 gap-1.5 px-2.5 has-data-[icon=inline-end]:pr-2 has-data-[icon=inline-start]:pl-2',
        icon: 'size-8',
        'icon-xs':
          "size-6 rounded-[min(var(--radius-md),10px)] in-data-[slot=button-group]:rounded-lg [&_svg:not([class*='size-'])]:size-3",
        'icon-sm':
          'size-7 rounded-[min(var(--radius-md),12px)] in-data-[slot=button-group]:rounded-lg',
        'icon-lg': 'size-9',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'default',
    },
  },
);

function Button({
  className,
  variant = 'default',
  size = 'default',
  ...props
}: ButtonPrimitive.Props & VariantProps<typeof buttonVariants>) {
  return (
    <ButtonPrimitive
      data-slot="button"
      className={cn(buttonVariants({ variant, size, className }))}
      {...props}
    />
  );
}

export { Button, buttonVariants };

```

# ui\card.tsx

| Property | Value |
|----------|-------|
| Path | `ui\card.tsx` |
| Size | 2,746 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 15:21:31.367495 |

```tsx
import * as React from 'react';
import { cn } from '@/lib/utils';

function Card({
  className,
  size = 'default',
  ...props
}: React.ComponentProps<'div'> & { size?: 'default' | 'sm' }) {
  return (
    <div
      data-slot="card"
      data-size={size}
      className={cn(
        'group/card flex flex-col gap-(--card-spacing) overflow-hidden rounded-xl bg-card py-(--card-spacing) text-sm text-card-foreground ring-1 ring-foreground/10 [--card-spacing:--spacing(4)] has-data-[slot=card-footer]:pb-0 has-[>img:first-child]:pt-0 data-[size=sm]:[--card-spacing:--spacing(3)] data-[size=sm]:has-data-[slot=card-footer]:pb-0 *:[img:first-child]:rounded-t-xl *:[img:last-child]:rounded-b-xl',
        className,
      )}
      {...props}
    />
  );
}

function CardHeader({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      data-slot="card-header"
      className={cn(
        'group/card-header @container/card-header grid auto-rows-min items-start gap-1 rounded-t-xl px-(--card-spacing) has-data-[slot=card-action]:grid-cols-[1fr_auto] has-data-[slot=card-description]:grid-rows-[auto_auto] [.border-b]:pb-(--card-spacing)',
        className,
      )}
      {...props}
    />
  );
}

function CardTitle({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      data-slot="card-title"
      className={cn(
        'font-heading text-base leading-snug font-medium group-data-[size=sm]/card:text-sm',
        className,
      )}
      {...props}
    />
  );
}

function CardDescription({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      data-slot="card-description"
      className={cn('text-sm text-muted-foreground', className)}
      {...props}
    />
  );
}

function CardAction({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      data-slot="card-action"
      className={cn(
        'col-start-2 row-span-2 row-start-1 self-start justify-self-end',
        className,
      )}
      {...props}
    />
  );
}

function CardContent({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      data-slot="card-content"
      className={cn('px-(--card-spacing)', className)}
      {...props}
    />
  );
}

function CardFooter({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      data-slot="card-footer"
      className={cn(
        'flex items-center rounded-b-xl border-t bg-muted/50 p-(--card-spacing)',
        className,
      )}
      {...props}
    />
  );
}

export {
  Card,
  CardHeader,
  CardFooter,
  CardTitle,
  CardAction,
  CardDescription,
  CardContent,
};

```

# ui\confirm-dialog.tsx

| Property | Value |
|----------|-------|
| Path | `ui\confirm-dialog.tsx` |
| Size | 2,948 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.528513 |

```tsx
'use client';

import * as React from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from './dialog';
import { Button } from './button';
import { AlertTriangle, Info } from 'lucide-react';

interface ConfirmDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: string;
  description: string;
  onConfirm: () => void | Promise<void>;
  confirmText?: string;
  cancelText?: string;
  variant?: 'default' | 'destructive';
  isLoading?: boolean;
}

export function ConfirmDialog({
  open,
  onOpenChange,
  title,
  description,
  onConfirm,
  confirmText = 'Xác nhận',
  cancelText = 'Hủy',
  variant = 'default',
  isLoading = false,
}: ConfirmDialogProps) {
  const handleConfirm = async () => {
    await onConfirm();
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6 shadow-xl">
        <DialogHeader className="gap-3">
          <div className="flex items-center gap-3">
            <div
              className={`p-2.5 rounded-xl ${
                variant === 'destructive'
                  ? 'bg-rose-50 text-rose-600'
                  : 'bg-neutral-100 text-neutral-800'
              }`}
            >
              {variant === 'destructive' ? (
                <AlertTriangle className="size-5" />
              ) : (
                <Info className="size-5" />
              )}
            </div>
            <DialogTitle className="text-base font-semibold text-neutral-900">
              {title}
            </DialogTitle>
          </div>
          <DialogDescription className="text-sm text-neutral-600 leading-relaxed pt-1">
            {description}
          </DialogDescription>
        </DialogHeader>

        <DialogFooter className="mt-6 flex flex-row justify-end gap-2.5 pt-2">
          <Button
            type="button"
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={isLoading}
            className="rounded-xl border-[#EAE6DF] hover:bg-neutral-50 text-neutral-700"
          >
            {cancelText}
          </Button>
          <Button
            type="button"
            variant={variant === 'destructive' ? 'destructive' : 'default'}
            onClick={handleConfirm}
            disabled={isLoading}
            className={`rounded-xl ${
              variant === 'destructive'
                ? 'bg-rose-600 hover:bg-rose-700 !text-white [&_*]:!text-white'
                : 'bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white'
            }`}
          >
            {isLoading ? 'Đang xử lý...' : confirmText}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}


```

# ui\dialog.tsx

| Property | Value |
|----------|-------|
| Path | `ui\dialog.tsx` |
| Size | 4,246 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 15:21:31.367495 |

```tsx
'use client';

import * as React from 'react';
import { Dialog as DialogPrimitive } from '@base-ui/react/dialog';
import { cn } from '@/lib/utils';

import { Button } from '@/components/ui/button';
import { XIcon } from 'lucide-react';

function Dialog({ ...props }: DialogPrimitive.Root.Props) {
  return <DialogPrimitive.Root data-slot="dialog" {...props} />;
}

function DialogTrigger({ ...props }: DialogPrimitive.Trigger.Props) {
  return <DialogPrimitive.Trigger data-slot="dialog-trigger" {...props} />;
}

function DialogPortal({ ...props }: DialogPrimitive.Portal.Props) {
  return <DialogPrimitive.Portal data-slot="dialog-portal" {...props} />;
}

function DialogClose({ ...props }: DialogPrimitive.Close.Props) {
  return <DialogPrimitive.Close data-slot="dialog-close" {...props} />;
}

function DialogOverlay({
  className,
  ...props
}: DialogPrimitive.Backdrop.Props) {
  return (
    <DialogPrimitive.Backdrop
      data-slot="dialog-overlay"
      className={cn(
        'fixed inset-0 isolate z-50 bg-black/10 duration-100 supports-backdrop-filter:backdrop-blur-xs data-open:animate-in data-open:fade-in-0 data-closed:animate-out data-closed:fade-out-0',
        className,
      )}
      {...props}
    />
  );
}

function DialogContent({
  className,
  children,
  showCloseButton = true,
  ...props
}: DialogPrimitive.Popup.Props & {
  showCloseButton?: boolean;
}) {
  return (
    <DialogPortal>
      <DialogOverlay />
      <DialogPrimitive.Popup
        data-slot="dialog-content"
        className={cn(
          'fixed top-1/2 left-1/2 z-50 grid w-full max-w-[calc(100%-2rem)] -translate-x-1/2 -translate-y-1/2 gap-4 rounded-xl bg-popover p-4 text-sm text-popover-foreground ring-1 ring-foreground/10 duration-100 outline-none sm:max-w-sm data-open:animate-in data-open:fade-in-0 data-open:zoom-in-95 data-closed:animate-out data-closed:fade-out-0 data-closed:zoom-out-95',
          className,
        )}
        {...props}
      >
        {children}
        {showCloseButton && (
          <DialogPrimitive.Close
            data-slot="dialog-close"
            render={
              <Button
                variant="ghost"
                className="absolute top-2 right-2"
                size="icon-sm"
              />
            }
          >
            <XIcon />
            <span className="sr-only">Close</span>
          </DialogPrimitive.Close>
        )}
      </DialogPrimitive.Popup>
    </DialogPortal>
  );
}

function DialogHeader({ className, ...props }: React.ComponentProps<'div'>) {
  return (
    <div
      data-slot="dialog-header"
      className={cn('flex flex-col gap-2', className)}
      {...props}
    />
  );
}

function DialogFooter({
  className,
  showCloseButton = false,
  children,
  ...props
}: React.ComponentProps<'div'> & {
  showCloseButton?: boolean;
}) {
  return (
    <div
      data-slot="dialog-footer"
      className={cn(
        '-mx-4 -mb-4 flex flex-col-reverse gap-2 rounded-b-xl border-t bg-muted/50 p-4 sm:flex-row sm:justify-end',
        className,
      )}
      {...props}
    >
      {children}
      {showCloseButton && (
        <DialogPrimitive.Close render={<Button variant="outline" />}>
          Close
        </DialogPrimitive.Close>
      )}
    </div>
  );
}

function DialogTitle({ className, ...props }: DialogPrimitive.Title.Props) {
  return (
    <DialogPrimitive.Title
      data-slot="dialog-title"
      className={cn(
        'font-heading text-base leading-none font-medium',
        className,
      )}
      {...props}
    />
  );
}

function DialogDescription({
  className,
  ...props
}: DialogPrimitive.Description.Props) {
  return (
    <DialogPrimitive.Description
      data-slot="dialog-description"
      className={cn(
        'text-sm text-muted-foreground *:[a]:underline *:[a]:underline-offset-3 *:[a]:hover:text-foreground',
        className,
      )}
      {...props}
    />
  );
}

export {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
};

```

# ui\dropdown-menu.tsx

| Property | Value |
|----------|-------|
| Path | `ui\dropdown-menu.tsx` |
| Size | 9,079 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 15:21:31.186415 |

```tsx
'use client';

import * as React from 'react';
import { Menu as MenuPrimitive } from '@base-ui/react/menu';
import { cn } from '@/lib/utils';
import { ChevronRightIcon, CheckIcon } from 'lucide-react';

function DropdownMenu({ ...props }: MenuPrimitive.Root.Props) {
  return <MenuPrimitive.Root data-slot="dropdown-menu" {...props} />;
}

function DropdownMenuPortal({ ...props }: MenuPrimitive.Portal.Props) {
  return <MenuPrimitive.Portal data-slot="dropdown-menu-portal" {...props} />;
}

function DropdownMenuTrigger({ ...props }: MenuPrimitive.Trigger.Props) {
  return <MenuPrimitive.Trigger data-slot="dropdown-menu-trigger" {...props} />;
}

function DropdownMenuContent({
  align = 'start',
  alignOffset = 0,
  side = 'bottom',
  sideOffset = 4,
  className,
  ...props
}: MenuPrimitive.Popup.Props &
  Pick<
    MenuPrimitive.Positioner.Props,
    'align' | 'alignOffset' | 'side' | 'sideOffset'
  >) {
  return (
    <MenuPrimitive.Portal>
      <MenuPrimitive.Positioner
        className="isolate z-50 outline-none"
        align={align}
        alignOffset={alignOffset}
        side={side}
        sideOffset={sideOffset}
      >
        <MenuPrimitive.Popup
          data-slot="dropdown-menu-content"
          className={cn(
            'z-50 max-h-(--available-height) w-(--anchor-width) min-w-32 origin-(--transform-origin) overflow-x-hidden overflow-y-auto rounded-lg bg-popover p-1 text-popover-foreground shadow-md ring-1 ring-foreground/10 duration-100 outline-none data-[side=bottom]:slide-in-from-top-2 data-[side=inline-end]:slide-in-from-left-2 data-[side=inline-start]:slide-in-from-right-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2 data-open:animate-in data-open:fade-in-0 data-open:zoom-in-95 data-closed:animate-out data-closed:overflow-hidden data-closed:fade-out-0 data-closed:zoom-out-95',
            className,
          )}
          {...props}
        />
      </MenuPrimitive.Positioner>
    </MenuPrimitive.Portal>
  );
}

function DropdownMenuGroup({ ...props }: MenuPrimitive.Group.Props) {
  return <MenuPrimitive.Group data-slot="dropdown-menu-group" {...props} />;
}

function DropdownMenuLabel({
  className,
  inset,
  ...props
}: MenuPrimitive.GroupLabel.Props & {
  inset?: boolean;
}) {
  return (
    <MenuPrimitive.GroupLabel
      data-slot="dropdown-menu-label"
      data-inset={inset}
      className={cn(
        'px-1.5 py-1 text-xs font-medium text-muted-foreground data-inset:pl-7',
        className,
      )}
      {...props}
    />
  );
}

function DropdownMenuItem({
  className,
  inset,
  variant = 'default',
  ...props
}: MenuPrimitive.Item.Props & {
  inset?: boolean;
  variant?: 'default' | 'destructive';
}) {
  return (
    <MenuPrimitive.Item
      data-slot="dropdown-menu-item"
      data-inset={inset}
      data-variant={variant}
      className={cn(
        "group/dropdown-menu-item relative flex cursor-default items-center gap-1.5 rounded-md px-1.5 py-1 text-sm outline-hidden select-none focus:bg-accent focus:text-accent-foreground not-data-[variant=destructive]:focus:**:text-accent-foreground data-inset:pl-7 data-[variant=destructive]:text-destructive data-[variant=destructive]:focus:bg-destructive/10 data-[variant=destructive]:focus:text-destructive dark:data-[variant=destructive]:focus:bg-destructive/20 data-disabled:pointer-events-none data-disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4 data-[variant=destructive]:*:[svg]:text-destructive",
        className,
      )}
      {...props}
    />
  );
}

function DropdownMenuSub({ ...props }: MenuPrimitive.SubmenuRoot.Props) {
  return <MenuPrimitive.SubmenuRoot data-slot="dropdown-menu-sub" {...props} />;
}

function DropdownMenuSubTrigger({
  className,
  inset,
  children,
  ...props
}: MenuPrimitive.SubmenuTrigger.Props & {
  inset?: boolean;
}) {
  return (
    <MenuPrimitive.SubmenuTrigger
      data-slot="dropdown-menu-sub-trigger"
      data-inset={inset}
      className={cn(
        "flex cursor-default items-center gap-1.5 rounded-md px-1.5 py-1 text-sm outline-hidden select-none focus:bg-accent focus:text-accent-foreground not-data-[variant=destructive]:focus:**:text-accent-foreground data-inset:pl-7 data-popup-open:bg-accent data-popup-open:text-accent-foreground data-open:bg-accent data-open:text-accent-foreground [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4",
        className,
      )}
      {...props}
    >
      {children}
      <ChevronRightIcon className="ml-auto" />
    </MenuPrimitive.SubmenuTrigger>
  );
}

function DropdownMenuSubContent({
  align = 'start',
  alignOffset = -3,
  side = 'right',
  sideOffset = 0,
  className,
  ...props
}: React.ComponentProps<typeof DropdownMenuContent>) {
  return (
    <DropdownMenuContent
      data-slot="dropdown-menu-sub-content"
      className={cn(
        'w-auto min-w-[96px] rounded-lg bg-popover p-1 text-popover-foreground shadow-lg ring-1 ring-foreground/10 duration-100 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2 data-open:animate-in data-open:fade-in-0 data-open:zoom-in-95 data-closed:animate-out data-closed:fade-out-0 data-closed:zoom-out-95',
        className,
      )}
      align={align}
      alignOffset={alignOffset}
      side={side}
      sideOffset={sideOffset}
      {...props}
    />
  );
}

function DropdownMenuCheckboxItem({
  className,
  children,
  checked,
  inset,
  ...props
}: MenuPrimitive.CheckboxItem.Props & {
  inset?: boolean;
}) {
  return (
    <MenuPrimitive.CheckboxItem
      data-slot="dropdown-menu-checkbox-item"
      data-inset={inset}
      className={cn(
        "relative flex cursor-default items-center gap-1.5 rounded-md py-1 pr-8 pl-1.5 text-sm outline-hidden select-none focus:bg-accent focus:text-accent-foreground focus:**:text-accent-foreground data-inset:pl-7 data-disabled:pointer-events-none data-disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4",
        className,
      )}
      checked={checked}
      {...props}
    >
      <span
        className="pointer-events-none absolute right-2 flex items-center justify-center"
        data-slot="dropdown-menu-checkbox-item-indicator"
      >
        <MenuPrimitive.CheckboxItemIndicator>
          <CheckIcon />
        </MenuPrimitive.CheckboxItemIndicator>
      </span>
      {children}
    </MenuPrimitive.CheckboxItem>
  );
}

function DropdownMenuRadioGroup({ ...props }: MenuPrimitive.RadioGroup.Props) {
  return (
    <MenuPrimitive.RadioGroup
      data-slot="dropdown-menu-radio-group"
      {...props}
    />
  );
}

function DropdownMenuRadioItem({
  className,
  children,
  inset,
  ...props
}: MenuPrimitive.RadioItem.Props & {
  inset?: boolean;
}) {
  return (
    <MenuPrimitive.RadioItem
      data-slot="dropdown-menu-radio-item"
      data-inset={inset}
      className={cn(
        "relative flex cursor-default items-center gap-1.5 rounded-md py-1 pr-8 pl-1.5 text-sm outline-hidden select-none focus:bg-accent focus:text-accent-foreground focus:**:text-accent-foreground data-inset:pl-7 data-disabled:pointer-events-none data-disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4",
        className,
      )}
      {...props}
    >
      <span
        className="pointer-events-none absolute right-2 flex items-center justify-center"
        data-slot="dropdown-menu-radio-item-indicator"
      >
        <MenuPrimitive.RadioItemIndicator>
          <CheckIcon />
        </MenuPrimitive.RadioItemIndicator>
      </span>
      {children}
    </MenuPrimitive.RadioItem>
  );
}

function DropdownMenuSeparator({
  className,
  ...props
}: MenuPrimitive.Separator.Props) {
  return (
    <MenuPrimitive.Separator
      data-slot="dropdown-menu-separator"
      className={cn('-mx-1 my-1 h-px bg-border', className)}
      {...props}
    />
  );
}

function DropdownMenuShortcut({
  className,
  ...props
}: React.ComponentProps<'span'>) {
  return (
    <span
      data-slot="dropdown-menu-shortcut"
      className={cn(
        'ml-auto text-xs tracking-widest text-muted-foreground group-focus/dropdown-menu-item:text-accent-foreground',
        className,
      )}
      {...props}
    />
  );
}

export {
  DropdownMenu,
  DropdownMenuPortal,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuLabel,
  DropdownMenuItem,
  DropdownMenuCheckboxItem,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuSub,
  DropdownMenuSubTrigger,
  DropdownMenuSubContent,
};

```

# ui\form-field.tsx

| Property | Value |
|----------|-------|
| Path | `ui\form-field.tsx` |
| Size | 1,191 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 14:54:46.911571 |

```tsx
// src/components/ui/form-field.tsx
import React from 'react';
import { Control, Controller, FieldValues, Path } from 'react-hook-form';

interface FormFieldProps<T extends FieldValues> {
  control: Control<T>;
  name: Path<T>;
  label: string;
  type?: string;
  placeholder?: string;
  error?: string;
}

export function FormField<T extends FieldValues>({
  control,
  name,
  label,
  type = 'text',
  placeholder,
  error,
}: FormFieldProps<T>) {
  return (
    <div className="space-y-1">
      <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
        {label}
      </label>
      <Controller
        name={name}
        control={control}
        render={({ field }) => (
          <input
            {...field}
            type={type}
            placeholder={placeholder}
            className={`w-full rounded-xl border px-4 py-2.5 text-sm outline-none transition focus:border-neutral-900 ${
              error ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF] bg-white'
            }`}
          />
        )}
      />
      {error && <p className="text-xs text-red-500">{error}</p>}
    </div>
  );
}
```

# ui\input.tsx

| Property | Value |
|----------|-------|
| Path | `ui\input.tsx` |
| Size | 1,064 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 15:21:31.177108 |

```tsx
import * as React from 'react';
import { Input as InputPrimitive } from '@base-ui/react/input';
import { cn } from '@/lib/utils';

function Input({ className, type, ...props }: React.ComponentProps<'input'>) {
  return (
    <InputPrimitive
      type={type}
      data-slot="input"
      className={cn(
        'h-8 w-full min-w-0 rounded-lg border border-input bg-transparent px-2.5 py-1 text-base transition-colors outline-none file:inline-flex file:h-6 file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 disabled:pointer-events-none disabled:cursor-not-allowed disabled:bg-input/50 disabled:opacity-50 aria-invalid:border-destructive aria-invalid:ring-3 aria-invalid:ring-destructive/20 md:text-sm dark:bg-input/30 dark:disabled:bg-input/80 dark:aria-invalid:border-destructive/50 dark:aria-invalid:ring-destructive/40',
        className,
      )}
      {...props}
    />
  );
}

export { Input };

```

# ui\pagination.tsx

| Property | Value |
|----------|-------|
| Path | `ui\pagination.tsx` |
| Size | 3,272 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.531892 |

```tsx
import * as React from 'react';
import { ChevronLeft, ChevronRight, MoreHorizontal } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from './button';

interface PaginationProps {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  className?: string;
  totalItems?: number;
}

export function Pagination({
  page,
  totalPages,
  onPageChange,
  className,
  totalItems,
}: PaginationProps) {
  if (totalPages <= 1 && !totalItems) return null;

  // Generate page numbers to show
  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else {
      pages.push(1);
      if (page > 3) pages.push('ellipsis-start');

      const start = Math.max(2, page - 1);
      const end = Math.min(totalPages - 1, page + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (page < totalPages - 2) pages.push('ellipsis-end');
      pages.push(totalPages);
    }
    return pages;
  };

  return (
    <div className={cn('flex flex-col sm:flex-row items-center justify-between gap-4 py-3', className)}>
      {totalItems !== undefined && (
        <p className="text-xs text-neutral-500">
          Hiển thị trang <span className="font-semibold text-neutral-800">{page}</span> / <span className="font-semibold text-neutral-800">{totalPages || 1}</span> (Tổng cộng <span className="font-semibold text-neutral-800">{totalItems}</span> bản ghi)
        </p>
      )}

      <div className="flex items-center gap-1.5 ml-auto">
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(page - 1)}
          disabled={page <= 1}
          className="rounded-lg h-8 px-2 text-xs"
        >
          <ChevronLeft className="size-4" />
          <span className="hidden sm:inline">Trước</span>
        </Button>

        {getPageNumbers().map((p, idx) => {
          if (typeof p === 'string') {
            return (
              <span key={`ellipsis-${idx}`} className="px-2 text-neutral-400">
                <MoreHorizontal className="size-4" />
              </span>
            );
          }
          return (
            <Button
              key={p}
              variant={page === p ? 'default' : 'outline'}
              size="sm"
              onClick={() => onPageChange(p)}
              className={cn(
                'size-8 p-0 text-xs rounded-lg font-medium',
                page === p
                  ? 'bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800'
                  : 'text-neutral-700 hover:bg-neutral-100 border-[#EAE6DF]'
              )}
            >
              {p}
            </Button>
          );
        })}

        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages}
          className="rounded-lg h-8 px-2 text-xs"
        >
          <span className="hidden sm:inline">Sau</span>
          <ChevronRight className="size-4" />
        </Button>
      </div>
    </div>
  );
}


```

# ui\search-input.tsx

| Property | Value |
|----------|-------|
| Path | `ui\search-input.tsx` |
| Size | 1,834 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.893605 |

```tsx
'use client';

import * as React from 'react';
import { Search, X } from 'lucide-react';
import { cn } from '@/lib/utils';

interface SearchInputProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange'> {
  value?: string;
  onChange?: (value: string) => void;
  debounceMs?: number;
}

export function SearchInput({
  value: initialValue = '',
  onChange,
  debounceMs = 300,
  className,
  placeholder = 'Tìm kiếm...',
  ...props
}: SearchInputProps) {
  const [searchTerm, setSearchTerm] = React.useState(initialValue);

  React.useEffect(() => {
    setSearchTerm(initialValue);
  }, [initialValue]);

  React.useEffect(() => {
    const timer = setTimeout(() => {
      onChange?.(searchTerm);
    }, debounceMs);

    return () => clearTimeout(timer);
  }, [searchTerm, debounceMs, onChange]);

  return (
    <div className={cn('relative flex items-center w-full max-w-sm', className)}>
      <Search className="absolute left-3 size-4 text-neutral-400 pointer-events-none" />
      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder={placeholder}
        className="w-full rounded-xl border border-[#EAE6DF] bg-white pl-9 pr-9 py-2 text-sm text-neutral-800 placeholder:text-neutral-400 outline-none transition focus:border-neutral-900 focus:ring-1 focus:ring-neutral-900/10 shadow-xs"
        {...props}
      />
      {searchTerm && (
        <button
          type="button"
          onClick={() => {
            setSearchTerm('');
            onChange?.('');
          }}
          className="absolute right-3 text-neutral-400 hover:text-neutral-700 focus:outline-none"
        >
          <X className="size-3.5" />
        </button>
      )}
    </div>
  );
}


```

# ui\select.tsx

| Property | Value |
|----------|-------|
| Path | `ui\select.tsx` |
| Size | 0 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 09:57:03.098958 |

```tsx

```

# ui\skeleton.tsx

| Property | Value |
|----------|-------|
| Path | `ui\skeleton.tsx` |
| Size | 306 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.894568 |

```tsx
import * as React from 'react';
import { cn } from '@/lib/utils';

export function Skeleton({
  className,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn('animate-pulse rounded-md bg-neutral-200/70', className)}
      {...props}
    />
  );
}


```

# ui\table.tsx

| Property | Value |
|----------|-------|
| Path | `ui\table.tsx` |
| Size | 2,588 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.895664 |

```tsx
import * as React from 'react';
import { cn } from '@/lib/utils';

function Table({ className, ...props }: React.ComponentProps<'table'>) {
  return (
    <div data-slot="table-container" className="relative w-full overflow-x-auto">
      <table
        data-slot="table"
        className={cn('w-full caption-bottom text-left text-sm', className)}
        {...props}
      />
    </div>
  );
}

function TableHeader({ className, ...props }: React.ComponentProps<'thead'>) {
  return (
    <thead
      data-slot="table-header"
      className={cn('border-b border-[#EAE6DF] bg-neutral-50/50 text-xs uppercase tracking-wider text-neutral-500 font-medium', className)}
      {...props}
    />
  );
}

function TableBody({ className, ...props }: React.ComponentProps<'tbody'>) {
  return (
    <tbody
      data-slot="table-body"
      className={cn('[&_tr:last-child]:border-0 divide-y divide-[#EAE6DF]', className)}
      {...props}
    />
  );
}

function TableFooter({ className, ...props }: React.ComponentProps<'tfoot'>) {
  return (
    <tfoot
      data-slot="table-footer"
      className={cn('border-t border-[#EAE6DF] bg-muted/50 font-medium [&>tr]:last:border-b-0', className)}
      {...props}
    />
  );
}

function TableRow({ className, ...props }: React.ComponentProps<'tr'>) {
  return (
    <tr
      data-slot="table-row"
      className={cn(
        'border-b border-[#EAE6DF] transition-colors hover:bg-neutral-50/80 data-[state=selected]:bg-muted',
        className
      )}
      {...props}
    />
  );
}

function TableHead({ className, ...props }: React.ComponentProps<'th'>) {
  return (
    <th
      data-slot="table-head"
      className={cn(
        'h-11 px-4 text-left align-middle font-semibold text-neutral-700 whitespace-nowrap [&:has([role=checkbox])]:pr-0',
        className
      )}
      {...props}
    />
  );
}

function TableCell({ className, ...props }: React.ComponentProps<'td'>) {
  return (
    <td
      data-slot="table-cell"
      className={cn('p-4 align-middle whitespace-nowrap text-neutral-800 [&:has([role=checkbox])]:pr-0', className)}
      {...props}
    />
  );
}

function TableCaption({
  className,
  ...props
}: React.ComponentProps<'caption'>) {
  return (
    <caption
      data-slot="table-caption"
      className={cn('mt-4 text-sm text-neutral-500', className)}
      {...props}
    />
  );
}

export {
  Table,
  TableHeader,
  TableBody,
  TableFooter,
  TableHead,
  TableRow,
  TableCell,
  TableCaption,
};


```

# ui\textarea.tsx

| Property | Value |
|----------|-------|
| Path | `ui\textarea.tsx` |
| Size | 0 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-02 09:57:03.100870 |

```tsx

```

