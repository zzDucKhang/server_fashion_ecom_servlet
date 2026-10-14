# Project Dump

**Root:** `C:\Programing\cnpm\fashion-ecom\client\src\modules`

**Generated:** 2026-10-14 11:38:24.827082

**Files:** 47 | **Total size:** 245,393 bytes

---

# admin\api.ts

| Property | Value |
|----------|-------|
| Path | `admin\api.ts` |
| Size | 4,948 bytes |
| Extension | `.ts` |
| Modified | 2026-10-06 21:26:07.896142 |

```typescript
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import type {
  ApiResponse,
  PaginatedResult,
  UserProfile,
  Product,
  Role,
  Permission,
} from '@/types';
import type {
  UserQuery,
  RoleQuery,
  PermissionQuery,
  CreateRolePayload,
  UpdateRolePayload,
  UpdateUserPayload,
  UpdateUserStatusPayload,
  DashboardStats,
} from './types';

export const adminApi = {
  // === Dashboard Stats ===
  getDashboardStats: async (): Promise<ApiResponse<DashboardStats>> => {
    try {
      const res = await apiClient.get<ApiResponse<DashboardStats>>(
        `${API_ENDPOINTS.CART_ORDER.ORDERS}/dashboard-stats`
      );
      if (res.data?.data) {
        return res.data;
      }
      return {
        statusCode: 200,
        data: (res.data as unknown) as DashboardStats,
      };
    } catch {
      const [usersRes, productsRes] = await Promise.all([
        apiClient.get<PaginatedResult<UserProfile>>(API_ENDPOINTS.IDENTITY.USERS, { params: { limit: 1 } }).catch(() => null),
        apiClient.get<PaginatedResult<Product>>(API_ENDPOINTS.CATALOG.PRODUCTS, { params: { limit: 1 } }).catch(() => null),
      ]);
      const totalUsers = usersRes?.data?.totalItems || 0;
      const totalProducts = productsRes?.data?.totalItems || 0;

      return {
        statusCode: 200,
        data: {
          totalUsers,
          totalOrders: 0,
          totalRevenue: 0,
          totalProducts,
          newUsersThisMonth: 0,
          ordersPending: 0,
        },
      };
    }
  },

  // === User Management ===
  getUsers: async (query?: UserQuery): Promise<PaginatedResult<UserProfile>> => {
    const res = await apiClient.get<any>(
      API_ENDPOINTS.IDENTITY.USERS,
      { params: query }
    );
    const body = res.data;
    if (body?.data?.data && Array.isArray(body.data.data)) {
      return body.data;
    }
    if (body?.data && Array.isArray(body.data)) {
      return body;
    }
    return body?.data || body || { data: [], totalItems: 0, totalPages: 1, page: 1, limit: 10 };
  },

  getUserById: async (userId: number) => {
    const res = await apiClient.get<ApiResponse<UserProfile>>(
      `${API_ENDPOINTS.IDENTITY.USERS}/${userId}`
    );
    return res.data;
  },

  updateUser: async (userId: number, payload: UpdateUserPayload) => {
    const res = await apiClient.put<ApiResponse<UserProfile>>(
      `${API_ENDPOINTS.IDENTITY.USERS}/${userId}`,
      payload
    );
    return res.data;
  },

  updateUserStatus: async (userId: number, payload: UpdateUserStatusPayload) => {
    const res = await apiClient.patch<ApiResponse<UserProfile>>(
      `${API_ENDPOINTS.IDENTITY.USERS}/${userId}/status`,
      payload
    );
    return res.data;
  },

  deleteUser: async (userId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.IDENTITY.USERS}/${userId}`
    );
    return res.data;
  },

  // === Role Management ===
  getRoles: async (query?: RoleQuery): Promise<PaginatedResult<Role>> => {
    const res = await apiClient.get<any>(
      API_ENDPOINTS.IDENTITY.ROLES,
      { params: query }
    );
    const body = res.data;
    if (body?.data?.data && Array.isArray(body.data.data)) {
      return body.data;
    }
    if (body?.data && Array.isArray(body.data)) {
      return body;
    }
    return body?.data || body || { data: [], totalItems: 0, totalPages: 1, page: 1, limit: 10 };
  },

  getRoleById: async (roleId: number) => {
    const res = await apiClient.get<ApiResponse<Role>>(
      `${API_ENDPOINTS.IDENTITY.ROLES}/${roleId}`
    );
    return res.data;
  },

  createRole: async (payload: CreateRolePayload) => {
    const res = await apiClient.post<ApiResponse<Role>>(
      API_ENDPOINTS.IDENTITY.ROLES,
      payload
    );
    return res.data;
  },

  updateRole: async (roleId: number, payload: UpdateRolePayload) => {
    const res = await apiClient.put<ApiResponse<Role>>(
      `${API_ENDPOINTS.IDENTITY.ROLES}/${roleId}`,
      payload
    );
    return res.data;
  },

  deleteRole: async (roleId: number) => {
    const res = await apiClient.delete<ApiResponse<{ message: string }>>(
      `${API_ENDPOINTS.IDENTITY.ROLES}/${roleId}`
    );
    return res.data;
  },

  // === Permission Management ===
  getPermissions: async (query?: PermissionQuery): Promise<PaginatedResult<Permission>> => {
    const res = await apiClient.get<any>(
      API_ENDPOINTS.IDENTITY.PERMISSIONS,
      { params: query }
    );
    const body = res.data;
    if (body?.data?.data && Array.isArray(body.data.data)) {
      return body.data;
    }
    if (body?.data && Array.isArray(body.data)) {
      return body;
    }
    return body?.data || body || { data: [], totalItems: 0, totalPages: 1, page: 1, limit: 10 };
  },
};


```

# admin\components\dashboard-card.tsx

| Property | Value |
|----------|-------|
| Path | `admin\components\dashboard-card.tsx` |
| Size | 2,227 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 20:42:08.905611 |

```tsx
import * as React from 'react';
import { LucideIcon, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import { cn } from '@/lib/utils';

interface DashboardCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  change?: string;
  trend?: 'up' | 'down' | 'neutral';
  description?: string;
  className?: string;
}

export function DashboardCard({
  title,
  value,
  icon: Icon,
  change,
  trend = 'up',
  description,
  className,
}: DashboardCardProps) {
  return (
    <div
      className={cn(
        'bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs flex flex-col justify-between transition-all hover:border-neutral-300',
        className
      )}
    >
      <div className="flex items-center justify-between gap-4">
        <span className="text-xs font-semibold uppercase tracking-wider text-neutral-500">
          {title}
        </span>
        <div className="p-2.5 rounded-xl bg-neutral-100 text-neutral-800 shrink-0">
          <Icon className="size-5 text-neutral-700" />
        </div>
      </div>

      <div className="mt-4 space-y-2">
        <p className="text-2xl sm:text-3xl font-bold text-neutral-900 tracking-tight font-display">
          {value}
        </p>

        {(change || description) && (
          <div className="flex items-center gap-2 text-xs">
            {change && (
              <span
                className={cn(
                  'inline-flex items-center gap-0.5 font-semibold px-1.5 py-0.5 rounded-md text-[11px]',
                  trend === 'up'
                    ? 'bg-emerald-50 text-emerald-700'
                    : trend === 'down'
                    ? 'bg-rose-50 text-rose-700'
                    : 'bg-neutral-100 text-neutral-600'
                )}
              >
                {trend === 'up' && <ArrowUpRight className="size-3" />}
                {trend === 'down' && <ArrowDownRight className="size-3" />}
                {change}
              </span>
            )}
            {description && (
              <span className="text-neutral-500">{description}</span>
            )}
          </div>
        )}
      </div>
    </div>
  );
}


```

# admin\components\data-table.tsx

| Property | Value |
|----------|-------|
| Path | `admin\components\data-table.tsx` |
| Size | 3,110 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-06 21:26:07.896996 |

```tsx
import * as React from 'react';
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';
import { Skeleton } from '@/components/ui/skeleton';
import { Pagination } from '@/components/ui/pagination';

export interface Column<T> {
  header: string;
  accessorKey?: keyof T;
  className?: string;
  cell?: (item: T, index: number) => React.ReactNode;
}

interface GenericDataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  isLoading?: boolean;
  page?: number;
  totalPages?: number;
  totalItems?: number;
  onPageChange?: (page: number) => void;
  emptyMessage?: string;
}

export function DataTable<T extends { id: string | number }>({
  columns,
  data,
  isLoading,
  page,
  totalPages,
  totalItems,
  onPageChange,
  emptyMessage = 'Không tìm thấy dữ liệu phù hợp.',
}: GenericDataTableProps<T>) {
  return (
    <div className="space-y-4">
      <div className="rounded-2xl border border-[#EAE6DF] bg-white overflow-hidden shadow-xs">
        <Table>
          <TableHeader>
            <TableRow>
              {columns.map((col, idx) => (
                <TableHead key={idx} className={col.className}>
                  {col.header}
                </TableHead>
              ))}
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              Array.from({ length: 5 }).map((_, rIdx) => (
                <TableRow key={`skeleton-row-${rIdx}`}>
                  {columns.map((_, cIdx) => (
                    <TableCell key={`skeleton-cell-${cIdx}`}>
                      <Skeleton className="h-5 w-full rounded" />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : !Array.isArray(data) || data.length === 0 ? (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-32 text-center text-xs text-neutral-500"
                >
                  {emptyMessage}
                </TableCell>
              </TableRow>
            ) : (
              data.map((item, index) => (
                <TableRow key={item.id} className="transition-colors hover:bg-neutral-50/70">
                  {columns.map((col, cIdx) => (
                    <TableCell key={cIdx} className={col.className}>
                      {col.cell
                        ? col.cell(item, index)
                        : col.accessorKey
                        ? String(item[col.accessorKey] ?? '')
                        : null}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {totalPages !== undefined && page !== undefined && onPageChange && (
        <Pagination
          page={page}
          totalPages={totalPages}
          totalItems={totalItems}
          onPageChange={onPageChange}
        />
      )}
    </div>
  );
}


```

# admin\components\sidebar.tsx

| Property | Value |
|----------|-------|
| Path | `admin\components\sidebar.tsx` |
| Size | 6,607 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.966907 |

```tsx
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

```

# admin\types.ts

| Property | Value |
|----------|-------|
| Path | `admin\types.ts` |
| Size | 960 bytes |
| Extension | `.ts` |
| Modified | 2026-10-05 20:42:08.910566 |

```typescript
import type { UserProfile, Role, Permission, UserStatus, BasePaginationQuery } from '@/types';

export interface DashboardStats {
  totalUsers: number;
  totalOrders: number;
  totalRevenue: number;
  totalProducts: number;
  newUsersThisMonth: number;
  ordersPending: number;
}

export interface UserQuery extends BasePaginationQuery {
  status?: UserStatus;
  roleId?: number;
}

export interface RoleQuery extends BasePaginationQuery {}

export interface PermissionQuery extends BasePaginationQuery {
  module?: string;
}

export interface CreateRolePayload {
  name: string;
  code: string;
  permissionIds?: number[];
}

export interface UpdateRolePayload {
  name?: string;
  isActive?: boolean;
  permissionIds?: number[];
}

export interface UpdateUserPayload {
  fullName?: string;
  phoneNumber?: string;
  avatarUrl?: string | null;
}

export interface UpdateUserStatusPayload {
  status: UserStatus;
}


```

# auth\api.ts

| Property | Value |
|----------|-------|
| Path | `auth\api.ts` |
| Size | 93 bytes |
| Extension | `.ts` |
| Modified | 2026-10-05 20:42:08.912565 |

```typescript
import { authApi } from '@/api/auth.api';

export { authApi };
export default authApi;


```

# auth\components\forgot-password-form.tsx

| Property | Value |
|----------|-------|
| Path | `auth\components\forgot-password-form.tsx` |
| Size | 4,959 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.538893 |

```tsx
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


```

# auth\components\login-form.tsx

| Property | Value |
|----------|-------|
| Path | `auth\components\login-form.tsx` |
| Size | 3,347 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 18:15:17.840044 |

```tsx
'use client';

import * as React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { Eye, EyeOff, Loader2 } from 'lucide-react';
import { loginSchema, type LoginFormData } from '../schemas';
import { useAuth } from '../hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

export function LoginForm() {
  const [showPassword, setShowPassword] = React.useState(false);
  const { loginMutation } = useAuth();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = (data: LoginFormData) => {
    loginMutation.mutate(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <FormField
        control={control}
        name="email"
        label="Địa chỉ Email"
        type="email"
        placeholder="example@domain.com"
        error={errors.email?.message}
      />

      <div className="relative">
        <FormField
          control={control}
          name="password"
          label="Mật khẩu"
          type={showPassword ? 'text' : 'password'}
          placeholder="••••••••"
          error={errors.password?.message}
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

      <div className="flex items-center justify-between pt-1">
        <label className="flex items-center gap-2 cursor-pointer text-xs text-neutral-600">
          <input
            type="checkbox"
            className="rounded border-[#EAE6DF] accent-neutral-900 size-3.5"
          />
          Ghi nhớ đăng nhập
        </label>
        <Link
          href="/forgot-password"
          className="text-xs text-neutral-500 hover:text-neutral-900 transition underline-offset-4 hover:underline"
        >
          Quên mật khẩu?
        </Link>
      </div>

      <Button
        type="submit"
        disabled={loginMutation.isPending}
        className="w-full h-11 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99] mt-2"
      >
        {loginMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin mr-2" />
            Đang đăng nhập...
          </>
        ) : (
          'Đăng nhập'
        )}
      </Button>

      <div className="text-center pt-2">
        <p className="text-xs text-neutral-500">
          Chưa có tài khoản?{' '}
          <Link
            href="/register"
            className="font-semibold text-neutral-900 hover:underline underline-offset-4"
          >
            Đăng ký tài khoản mới
          </Link>
        </p>
      </div>
    </form>
  );
}


```

# auth\components\register-form.tsx

| Property | Value |
|----------|-------|
| Path | `auth\components\register-form.tsx` |
| Size | 11,753 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 17:56:17.978449 |

```tsx
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
```

# auth\hooks\use-auth.ts

| Property | Value |
|----------|-------|
| Path | `auth\hooks\use-auth.ts` |
| Size | 3,581 bytes |
| Extension | `.ts` |
| Modified | 2026-10-11 18:09:28.221604 |

```typescript
'use client';

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';
import { authApi } from '@/api/auth.api';
import { useAuth as useAuthContext } from '@/components/providers/auth-provider';
import type {
  LoginPayload,
  RegisterPayload,
  VerifyCodePayload,
  ResetPasswordPayload,
  ChangePasswordPayload,
} from '@/types';

export function useAuth() {
  const context = useAuthContext();
  const router = useRouter();
  const queryClient = useQueryClient();

  const loginMutation = useMutation({
    mutationFn: (payload: LoginPayload) => authApi.login(payload),
    onSuccess: async (res) => {
      if (res.data) {
        await context.login(res.data.accessToken, res.data.refreshToken, res.data.user);
        toast.success('Đăng nhập thành công!');
        if (res.data.user.role?.code === 'ADMIN') {
          router.push('/admin/dashboard');
        } else {
          router.push('/');
        }
      }
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đăng nhập không thành công, vui lòng kiểm tra lại thông tin!';
      toast.error(msg);
    },
  });

  const registerMutation = useMutation({
    mutationFn: (payload: RegisterPayload) => authApi.register(payload),
    onSuccess: () => {
      toast.success('Đăng ký thành công! Vui lòng kiểm tra email để lấy mã xác thực.');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đăng ký thất bại, email có thể đã tồn tại!';
      toast.error(msg);
    },
  });

  const verifyCodeMutation = useMutation({
    mutationFn: (payload: VerifyCodePayload) => authApi.verifyCode(payload),
    onSuccess: () => {
      toast.success('Xác thực tài khoản thành công!');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Mã xác thực không chính xác hoặc đã hết hạn!';
      toast.error(msg);
    },
  });

  const forgotPasswordMutation = useMutation({
    mutationFn: (email: string) => authApi.forgotPassword(email),
    onSuccess: () => {
      toast.success('Mã OTP xác thực đã được gửi về email của bạn.');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Gửi yêu cầu thất bại, vui lòng kiểm tra email!';
      toast.error(msg);
    },
  });

  const resetPasswordMutation = useMutation({
    mutationFn: (payload: ResetPasswordPayload) => authApi.resetPassword(payload),
    onSuccess: () => {
      toast.success('Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.');
      router.push('/login');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đặt lại mật khẩu thất bại!';
      toast.error(msg);
    },
  });

  const changePasswordMutation = useMutation({
    mutationFn: (payload: ChangePasswordPayload) => authApi.changePassword(payload),
    onSuccess: () => {
      toast.success('Đổi mật khẩu thành công!');
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || 'Đổi mật khẩu không thành công!';
      toast.error(msg);
    },
  });

  return {
    ...context,
    loginMutation,
    registerMutation,
    verifyCodeMutation,
    forgotPasswordMutation,
    resetPasswordMutation,
    changePasswordMutation,
  };
}


```

# auth\schemas.ts

| Property | Value |
|----------|-------|
| Path | `auth\schemas.ts` |
| Size | 4,747 bytes |
| Extension | `.ts` |
| Modified | 2026-10-05 20:42:08.928568 |

```typescript
import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().trim().min(1, 'Email không được để trống').email('Email không đúng định dạng'),
  password: z.string().min(1, 'Mật khẩu không được để trống'),
});

export const registerSchema = z
  .object({
    fullName: z.string().trim().min(1, 'Họ và tên không được để trống').max(100),
    email: z.string().trim().min(1, 'Email không được để trống').email('Email không đúng định dạng'),
    phoneNumber: z.string().trim().optional(),
    password: z
      .string()
      .min(8, 'Mật khẩu tối thiểu 8 ký tự')
      .max(72, 'Mật khẩu tối đa 72 ký tự')
      .regex(/[A-Z]/, 'Cần ít nhất 1 chữ in hoa')
      .regex(/[a-z]/, 'Cần ít nhất 1 chữ in thường')
      .regex(/[0-9]/, 'Cần ít nhất 1 chữ số')
      .regex(/[^A-Za-z0-9]/, 'Cần ít nhất 1 ký tự đặc biệt'),
    confirmPassword: z.string().min(1, 'Xác nhận mật khẩu là bắt buộc'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
  });

export const verifyCodeSchema = z.object({
  email: z.string().trim().email('Email không hợp lệ'),
  type: z.enum(['REGISTER', 'FORGOT_PASSWORD']),
  code: z
    .string()
    .trim()
    .length(6, 'Mã xác thực gồm 6 chữ số')
    .regex(/^\d+$/, 'Mã xác thực chỉ chứa số'),
});

export const forgotPasswordSchema = z.object({
  email: z.string().trim().min(1, 'Email không được để trống').email('Email không đúng định dạng'),
});

export const resetPasswordSchema = z
  .object({
    token: z.string().min(1, 'Token xác thực là bắt buộc'),
    newPassword: z
      .string()
      .min(8, 'Mật khẩu mới tối thiểu 8 ký tự')
      .max(72, 'Mật khẩu tối đa 72 ký tự')
      .regex(/[A-Z]/, 'Cần ít nhất 1 chữ hoa')
      .regex(/[a-z]/, 'Cần ít nhất 1 chữ thường')
      .regex(/[0-9]/, 'Cần ít nhất 1 chữ số')
      .regex(/[^A-Za-z0-9]/, 'Cần ít nhất 1 ký tự đặc biệt'),
    confirmPassword: z.string().min(1, 'Xác nhận mật khẩu là bắt buộc'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
  });

export const changePasswordSchema = z
  .object({
    oldPassword: z.string().min(1, 'Mật khẩu hiện tại là bắt buộc'),
    newPassword: z
      .string()
      .min(8, 'Mật khẩu mới tối thiểu 8 ký tự')
      .max(72, 'Mật khẩu tối đa 72 ký tự')
      .regex(/[A-Z]/, 'Cần ít nhất 1 chữ hoa')
      .regex(/[a-z]/, 'Cần ít nhất 1 chữ thường')
      .regex(/[0-9]/, 'Cần ít nhất 1 chữ số')
      .regex(/[^A-Za-z0-9]/, 'Cần ít nhất 1 ký tự đặc biệt'),
    confirmPassword: z.string().min(1, 'Xác nhận mật khẩu là bắt buộc'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
  });

export const addressSchema = z.object({
  receiverName: z.string().trim().min(1, 'Họ tên người nhận không được để trống'),
  phoneNumber: z.string().trim().min(1, 'Số điện thoại không được để trống'),
  province: z.string().trim().min(1, 'Tỉnh/Thành phố không được để trống'),
  district: z.string().trim().min(1, 'Quận/Huyện không được để trống'),
  ward: z.string().trim().min(1, 'Phường/Xã không được để trống'),
  detailAddress: z.string().trim().min(1, 'Địa chỉ chi tiết không được để trống'),
  isDefault: z.boolean().optional(),
});

export const profileSchema = z.object({
  fullName: z.string().trim().min(1, 'Họ và tên không được để trống'),
  phoneNumber: z.string().trim().optional(),
  avatarUrl: z.string().url('URL ảnh không hợp lệ').optional().or(z.literal('')),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
export type VerifyCodeFormData = z.infer<typeof verifyCodeSchema>;
export type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;
export type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;
export type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;
export type AddressFormData = z.infer<typeof addressSchema>;
export type ProfileFormData = z.infer<typeof profileSchema>;


```

# auth\types.ts

| Property | Value |
|----------|-------|
| Path | `auth\types.ts` |
| Size | 116 bytes |
| Extension | `.ts` |
| Modified | 2026-10-05 20:42:08.938597 |

```typescript
export * from '@/types/auth';

export interface AuthState {
  isLoading: boolean;
  error: string | null;
}


```

# cart\components\cart-item.tsx

| Property | Value |
|----------|-------|
| Path | `cart\components\cart-item.tsx` |
| Size | 4,986 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 20:01:09.217520 |

```tsx
import Image from 'next/image';
import { Minus, Plus, Trash2 } from 'lucide-react';
import { CartItem } from '@/types';
import { useUpdateCartItem, useRemoveCartItem } from '../hooks/use-cart';
import { formatCurrency } from '@/lib/utils/format-currency';

interface CartItemProps {
  item: CartItem;
  isSelected?: boolean;
  onToggleSelect?: (id: number) => void;
}

export function CartItemComponent({ item, isSelected = false, onToggleSelect }: CartItemProps) {
  const updateMutation = useUpdateCartItem();
  const removeMutation = useRemoveCartItem();

  const handleUpdate = (delta: number) => {
    const newQuantity = Math.max(1, item.quantity + delta);
    if (newQuantity !== item.quantity) {
      updateMutation.mutate({ itemId: item.id, payload: { quantity: newQuantity } });
    }
  };

  const handleRemove = () => {
    removeMutation.mutate(item.id);
  };

  const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
    if (!fsi.campaign || !fsi.campaign.isActive) return false;
    const now = new Date().getTime();
    const start = new Date(fsi.campaign.startTime).getTime();
    const end = new Date(fsi.campaign.endTime).getTime();
    const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
    return now >= start && now <= end && remainingQty >= item.quantity;
  });

  const currentPrice = activeFlashSale
    ? Number(activeFlashSale.flashSalePrice)
    : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
    
  const originalPrice = Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
  const totalItemPrice = currentPrice * item.quantity;
  const imageUrl = item.variant.imageUrl || item.variant.product.thumbnailUrl || '/placeholder.png';
  const variantDisplay = item.variant.variantValue || 'Phân loại mặc định';

  return (
    <div className="flex items-center gap-3.5 py-5">
      {/* Checkbox chọn sản phẩm */}
      <input
        type="checkbox"
        checked={isSelected}
        onChange={() => onToggleSelect?.(item.id)}
        className="size-4 shrink-0 rounded border-[#EAE6DF] accent-[#84EA53] cursor-pointer"
        aria-label={`Chọn sản phẩm ${item.variant.product.defaultName}`}
      />

      <div className="relative h-28 w-24 sm:h-32 sm:w-28 shrink-0 overflow-hidden rounded-xl bg-[#e9e5dc]">
        <Image
          src={imageUrl}
          alt={item.variant.product.defaultName || 'Product'}
          fill
          sizes="112px"
          className="object-cover"
        />
      </div>

      <div className="flex min-w-0 flex-1 flex-col justify-between self-stretch">
        <div className="flex justify-between gap-3">
          <div>
            <p className="text-sm font-semibold line-clamp-2 text-neutral-900 flex items-center gap-2">
              {item.variant.product.defaultName}
              {activeFlashSale && (
                <span className="bg-red-600 text-white text-[9px] px-1.5 py-0.5 rounded font-bold uppercase tracking-wider">
                  Flash Sale
                </span>
              )}
            </p>
            <p className="mt-1.5 text-xs text-[#74736e]">
              {variantDisplay} • {formatCurrency(currentPrice)} 
              {activeFlashSale && <span className="line-through ml-1 text-neutral-400">{formatCurrency(originalPrice)}</span>}
            </p>
          </div>
          <button
            onClick={handleRemove}
            disabled={removeMutation.isPending}
            aria-label="Remove item"
            className="h-fit text-[#9b9991] hover:text-red-500 disabled:opacity-50 transition-colors p-1"
          >
            <Trash2 size={15} />
          </button>
        </div>

        <div className="mt-3 flex items-center justify-between">
          <div className="flex items-center rounded-lg border border-[#EAE6DF] bg-white">
            <button
              onClick={() => handleUpdate(-1)}
              disabled={updateMutation.isPending || item.quantity <= 1}
              className="p-1.5 disabled:opacity-50 text-neutral-600 hover:text-neutral-900"
              aria-label="Decrease quantity"
            >
              <Minus size={12} />
            </button>
            <span className="w-6 text-center text-xs font-semibold">
              {updateMutation.isPending ? '...' : item.quantity}
            </span>
            <button
              onClick={() => handleUpdate(1)}
              disabled={updateMutation.isPending}
              className="p-1.5 disabled:opacity-50 text-neutral-600 hover:text-neutral-900"
              aria-label="Increase quantity"
            >
              <Plus size={12} />
            </button>
          </div>
          <span className="text-sm font-bold text-neutral-900">
            {formatCurrency(totalItemPrice)}
          </span>
        </div>
      </div>
    </div>
  );
}
```

# cart\components\cart-summary.tsx

| Property | Value |
|----------|-------|
| Path | `cart\components\cart-summary.tsx` |
| Size | 3,348 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 20:01:43.826004 |

```tsx
import Link from 'next/link';
import { CartItem } from '@/types';
import { formatCurrency } from '@/lib/utils/format-currency';

interface CartSummaryProps {
  items: CartItem[];
  selectedItemIds: number[];
}

export function CartSummary({ items, selectedItemIds }: CartSummaryProps) {
  // Chỉ tính tiền các sản phẩm được chọn
  const selectedItems = items.filter((item) => selectedItemIds.includes(item.id));
  const subtotal = selectedItems.reduce((sum, item) => {
    const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
      if (!fsi.campaign || !fsi.campaign.isActive) return false;
      const now = new Date().getTime();
      const start = new Date(fsi.campaign.startTime).getTime();
      const end = new Date(fsi.campaign.endTime).getTime();
      const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
      return now >= start && now <= end && remainingQty >= item.quantity;
    });

    const price = activeFlashSale
      ? Number(activeFlashSale.flashSalePrice)
      : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
      
    return sum + price * item.quantity;
  }, 0);

  const hasSelected = selectedItemIds.length > 0;
  const checkoutHref = hasSelected ? `/checkout?items=${selectedItemIds.join(',')}` : '#';

  return (
    <aside className="h-fit rounded-2xl bg-white p-6 border border-[#EAE6DF] shadow-xs lg:sticky lg:top-36">
      <h2 className="font-display text-3xl text-neutral-900">Tóm tắt đơn hàng</h2>
      
      <div className="mt-7 space-y-3 border-b border-[#EAE6DF] pb-5 text-sm">
        <div className="flex justify-between text-neutral-600">
          <span>Đã chọn</span>
          <span className="font-semibold text-neutral-900">{selectedItems.length} sản phẩm</span>
        </div>
        <div className="flex justify-between text-neutral-600">
          <span>Tạm tính</span>
          <span className="font-semibold text-neutral-900">{formatCurrency(subtotal)}</span>
        </div>
        <div className="flex justify-between text-neutral-600">
          <span>Phí vận chuyển</span>
          <span className="text-xs text-neutral-400 italic">Tính tại bước thanh toán</span>
        </div>
      </div>

      <div className="flex justify-between py-5 text-base font-bold text-neutral-900">
        <span>Tổng cộng</span>
        <span className="text-lg text-emerald-600">{formatCurrency(subtotal)}</span>
      </div>

      {hasSelected ? (
        <Link
          href={checkoutHref}
          className="block w-full rounded-xl bg-[#84EA53] py-3.5 text-center text-sm font-bold text-neutral-900 transition-transform hover:scale-[1.01] active:scale-[0.99] shadow-xs"
        >
          Mua hàng ({selectedItems.length})
        </Link>
      ) : (
        <button
          type="button"
          disabled
          className="block w-full rounded-xl bg-neutral-100 py-3.5 text-center text-sm font-bold text-neutral-400 cursor-not-allowed border border-[#EAE6DF]"
        >
          Vui lòng chọn sản phẩm
        </button>
      )}

      <p className="mt-4 text-center text-[11px] text-[#74736e]">
        Miễn phí vận chuyển cho đơn hàng từ 500.000 ₫
      </p>
    </aside>
  );
}
```

# cart\components\cart-view.tsx

| Property | Value |
|----------|-------|
| Path | `cart\components\cart-view.tsx` |
| Size | 4,676 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.546903 |

```tsx
'use client';

import { useState, useMemo } from 'react';
import Link from 'next/link';
import { useCart } from '../hooks/use-cart';
import { CartItemComponent } from './cart-item';
import { CartSummary } from './cart-summary';

export function CartView() {
  const { data: cartResponse, isLoading, isError } = useCart();
  const items = cartResponse?.data?.cartItems || [];

  // Quản lý danh sách ID các món BỊ BỎ CHỌN (mặc định rỗng = chọn tất cả)
  const [unselectedIds, setUnselectedIds] = useState<number[]>([]);

  // Tính toán trực tiếp selectedIds ngay khi render (Pure computation - không cần useEffect)
  const unselectedSet = useMemo(() => new Set(unselectedIds), [unselectedIds]);

  const selectedIds = useMemo(() => {
    return items
      .filter((item) => !unselectedSet.has(item.id))
      .map((item) => item.id);
  }, [items, unselectedSet]);

  const isAllSelected = items.length > 0 && selectedIds.length === items.length;

  const handleToggleSelectAll = () => {
    if (isAllSelected) {
      // Đang chọn tất cả -> Bỏ chọn tất cả (thêm toàn bộ ID vào danh sách unselected)
      setUnselectedIds(items.map((i) => i.id));
    } else {
      // Chọn tất cả -> Làm rỗng danh sách unselected
      setUnselectedIds([]);
    }
  };

  const handleToggleItem = (id: number) => {
    setUnselectedIds((prev) =>
      prev.includes(id) ? prev.filter((itemId) => itemId !== id) : [...prev, id]
    );
  };

  if (isLoading) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
        <div className="animate-pulse space-y-8">
          <div className="h-8 w-48 rounded-xl bg-neutral-200" />
          <div className="h-64 rounded-2xl bg-neutral-200" />
        </div>
      </main>
    );
  }

  if (isError) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16 text-center">
        <p className="text-red-500 text-sm">Không thể tải dữ liệu giỏ hàng. Vui lòng thử lại sau.</p>
      </main>
    );
  }

  if (items.length === 0) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-20 sm:px-8 text-center">
        <h1 className="font-display text-4xl mb-4 text-neutral-900">Giỏ hàng của bạn đang trống</h1>
        <p className="text-[#74736e] mb-8 text-sm">Hãy chọn các sản phẩm thời trang yêu thích của bạn.</p>
        <Link
          href="/products"
          className="inline-block rounded-xl bg-[#1D1F20] px-8 py-3.5 text-sm font-bold !text-white [&_*]:!text-white transition-transform hover:scale-[1.02] active:scale-[0.98]"
        >
          Khám phá sản phẩm
        </Link>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
      <div className="mb-8">
        <p className="eyebrow">DANH SÁCH ĐÃ CHỌN</p>
        <h1 className="section-title mt-2">Giỏ hàng</h1>
        <p className="mt-2 text-xs text-[#74736e]">
          {items.length} món trong giỏ hàng · Sẵn sàng đặt bất cứ lúc nào.
        </p>
      </div>

      <div className="grid gap-10 lg:grid-cols-[1fr_360px]">
        <div className="rounded-2xl bg-white px-6 py-4 border border-[#EAE6DF] shadow-xs h-fit">
          {/* Thanh công cụ Chọn tất cả */}
          <div className="flex items-center justify-between pb-4 border-b border-[#EAE6DF]">
            <label className="flex items-center gap-3 text-xs font-semibold text-neutral-800 cursor-pointer select-none">
              <input
                type="checkbox"
                checked={isAllSelected}
                onChange={handleToggleSelectAll}
                className="size-4 rounded border-[#EAE6DF] accent-[#84EA53] cursor-pointer"
              />
              <span>Chọn tất cả ({items.length} sản phẩm)</span>
            </label>
            <span className="text-xs text-neutral-500">
              Đã chọn: <strong className="text-neutral-900">{selectedIds.length}</strong>
            </span>
          </div>

          <div className="divide-y divide-[#EAE6DF]">
            {items.map((item) => (
              <CartItemComponent
                key={item.id}
                item={item}
                isSelected={selectedIds.includes(item.id)}
                onToggleSelect={handleToggleItem}
              />
            ))}
          </div>
        </div>

        <CartSummary items={items} selectedItemIds={selectedIds} />
      </div>
    </main>
  );
}
```

# cart\hooks\use-cart.ts

| Property | Value |
|----------|-------|
| Path | `cart\hooks\use-cart.ts` |
| Size | 2,737 bytes |
| Extension | `.ts` |
| Modified | 2026-10-05 10:24:43.388458 |

```typescript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cartApi } from '@/api/cart.api';
import { AddToCartPayload, UpdateCartItemPayload } from '@/types';
import { toast } from 'sonner';

export const CART_QUERY_KEYS = {
  cart: ['cart'] as const,
};

// 1. Lấy thông tin giỏ hàng
export const useCart = () => {
  return useQuery({
    queryKey: CART_QUERY_KEYS.cart,
    queryFn: cartApi.getCart,
  });
};

// 2. Thêm sản phẩm vào giỏ
export const useAddToCart = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: AddToCartPayload) => cartApi.addToCart(payload),
    onSuccess: () => {
      toast.success('Đã thêm vào giỏ hàng');
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Không thể thêm vào giỏ hàng');
    },
  });
};

// 3. Cập nhật số lượng sản phẩm trong giỏ
export const useUpdateCartItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      itemId,
      payload,
    }: {
      itemId: number;
      payload: UpdateCartItemPayload;
    }) => cartApi.updateCartItem(itemId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Lỗi khi cập nhật số lượng');
    },
  });
};

// 4. Xóa một sản phẩm khỏi giỏ
export const useRemoveCartItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (itemId: number) => cartApi.removeCartItem(itemId),
    onSuccess: () => {
      toast.success('Đã xóa sản phẩm khỏi giỏ');
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Lỗi khi xóa sản phẩm');
    },
  });
};

// 5. Xóa toàn bộ giỏ hàng
export const useClearCart = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => cartApi.clearCart(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
  });
};

// 6. Gộp giỏ hàng (Guest -> User khi đăng nhập)
export const useMergeCart = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => cartApi.mergeCart(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
  });
};


```

# cart\index.ts

| Property | Value |
|----------|-------|
| Path | `cart\index.ts` |
| Size | 69 bytes |
| Extension | `.ts` |
| Modified | 2026-10-06 21:26:07.911107 |

```typescript
// src/modules/cart/index.ts
export * from "./store/useCartStore";

```

# cart\store\useCartStore.ts

| Property | Value |
|----------|-------|
| Path | `cart\store\useCartStore.ts` |
| Size | 2,556 bytes |
| Extension | `.ts` |
| Modified | 2026-10-06 21:26:07.913358 |

```typescript
// src/modules/cart/store/useCartStore.ts
import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

export interface CartItem {
  variantId: number;
  productId?: number;
  name: string;
  sku?: string;
  price: number;
  originalPrice?: number;
  quantity: number;
  imageUrl?: string;
  attributes?: Record<string, string>;
}

export interface CartStore {
  items: CartItem[];
  addItem: (item: Omit<CartItem, "quantity"> & { quantity?: number }) => void;
  updateQuantity: (variantId: number, quantity: number) => void;
  removeItem: (variantId: number) => void;
  clearCart: () => void;
  getSubtotal: () => number;
}

export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],

      addItem: (newItem) => {
        const quantityToAdd = Math.max(1, newItem.quantity ?? 1);
        set((state) => {
          const existingIndex = state.items.findIndex(
            (item) => item.variantId === newItem.variantId
          );

          if (existingIndex > -1) {
            const updatedItems = [...state.items];
            const currentItem = updatedItems[existingIndex];
            updatedItems[existingIndex] = {
              ...currentItem,
              ...newItem,
              quantity: currentItem.quantity + quantityToAdd,
            };
            return { items: updatedItems };
          }

          return {
            items: [
              ...state.items,
              {
                ...newItem,
                quantity: quantityToAdd,
              },
            ],
          };
        });
      },

      updateQuantity: (variantId, quantity) => {
        if (quantity <= 0) {
          get().removeItem(variantId);
          return;
        }

        set((state) => ({
          items: state.items.map((item) =>
            item.variantId === variantId ? { ...item, quantity } : item
          ),
        }));
      },

      removeItem: (variantId) => {
        set((state) => ({
          items: state.items.filter((item) => item.variantId !== variantId),
        }));
      },

      clearCart: () => {
        set({ items: [] });
      },

      getSubtotal: () => {
        return get().items.reduce(
          (sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 1),
          0
        );
      },
    }),
    {
      name: "fashion_cart_storage",
      storage: createJSONStorage(() => localStorage),
    }
  )
);

```

# catalog\components\filter-sidebar.tsx

| Property | Value |
|----------|-------|
| Path | `catalog\components\filter-sidebar.tsx` |
| Size | 5,920 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-01 17:02:22.926284 |

```tsx
'use client';
import { ChevronDown, SlidersHorizontal, X } from 'lucide-react';
import { useState } from 'react';
export function FilterSidebar({
  open,
  onClose,
}: {
  open: boolean;
  onClose: () => void;
}) {
  const [expanded, setExpanded] = useState('Category');
  return (
    <aside
      className={`${open ? 'translate-x-0 opacity-100' : 'pointer-events-none -translate-x-5 opacity-0'} fixed inset-y-0 left-0 z-50 w-[min(360px,88vw)] overflow-y-auto border-r border-[#EAE6DF] bg-white p-6 shadow-[10px_0_40px_rgba(29,31,32,.08)] transition-all duration-300 lg:absolute lg:top-0 lg:h-full lg:shadow-none`}
    >
      <div className="mb-8 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <SlidersHorizontal size={18} />
          <h2 className="font-display text-3xl">Filters</h2>
        </div>
        <button
          onClick={onClose}
          aria-label="Close filters"
          className="rounded-full p-2 hover:bg-[#FBF9F4]"
        >
          <X size={18} />
        </button>
      </div>
      <div className="divide-y divide-[#EAE6DF]">
        {['Category', 'Brand', 'Size', 'Color'].map((name) => (
          <div key={name} className="py-5 first:pt-0">
            <button
              onClick={() => setExpanded(expanded === name ? '' : name)}
              className="flex w-full items-center justify-between text-sm font-bold"
            >
              {name}
              <ChevronDown
                size={16}
                className={
                  expanded === name ? 'rotate-180 transition' : 'transition'
                }
              />
            </button>
            {expanded === name && (
              <div className="mt-4 space-y-3 text-sm text-[#74736e]">
                {name === 'Category' && (
                  <>
                    <label className="flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          defaultChecked
                          className="mr-3 accent-[#84EA53]"
                        />
                        All products
                      </span>
                      <span>160</span>
                    </label>
                    <label className="ml-4 flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          className="mr-3 accent-[#84EA53]"
                        />
                        Apparel
                      </span>
                      <span>48</span>
                    </label>
                    <label className="ml-4 flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          className="mr-3 accent-[#84EA53]"
                        />
                        Objects
                      </span>
                      <span>32</span>
                    </label>
                  </>
                )}
                {name === 'Brand' &&
                  [
                    'Morrow Studio',
                    'Form & Field',
                    'House of Common',
                    'Sunday Objects',
                  ].map((brand, i) => (
                    <label key={brand} className="flex justify-between">
                      <span>
                        <input
                          type="checkbox"
                          className="mr-3 accent-[#84EA53]"
                        />
                        {brand}
                      </span>
                      <span>{[14, 9, 12, 7][i]}</span>
                    </label>
                  ))}
                {name === 'Size' && (
                  <div className="flex flex-wrap gap-2">
                    {['XS', 'S', 'M', 'L', '28', '30', '32', 'One size'].map(
                      (size) => (
                        <button
                          key={size}
                          className="rounded-lg border border-[#EAE6DF] px-3 py-2 text-xs hover:border-[#84EA53]"
                        >
                          {size}
                        </button>
                      ),
                    )}
                  </div>
                )}
                {name === 'Color' && (
                  <div className="flex flex-wrap gap-4">
                    {[
                      '#1D1F20',
                      '#ded5c8',
                      '#b8794b',
                      '#8d9a87',
                      '#d8c5ac',
                      '#e1e1dc',
                    ].map((color) => (
                      <button
                        key={color}
                        aria-label={`Color ${color}`}
                        className="h-7 w-7 rounded-full border-2 border-white outline outline-1 outline-[#EAE6DF]"
                        style={{ backgroundColor: color }}
                      />
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
        <div className="py-5">
          <p className="mb-5 text-sm font-bold">Price range</p>
          <input
            type="range"
            min="0"
            max="500"
            defaultValue="280"
            className="w-full accent-[#84EA53]"
          />
          <div className="mt-3 flex justify-between text-xs text-[#74736e]">
            <span>$0</span>
            <span>$500+</span>
          </div>
        </div>
      </div>
      <button
        onClick={onClose}
        className="mt-8 w-full rounded-xl bg-[#84EA53] py-3 text-sm font-bold"
      >
        Show 160 results
      </button>
    </aside>
  );
}

```

# catalog\components\product-card.tsx

| Property | Value |
|----------|-------|
| Path | `catalog\components\product-card.tsx` |
| Size | 3,651 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-10 15:34:49.366570 |

```tsx
'use client';
import Image from 'next/image';
import Link from 'next/link';
import { ChevronLeft, ChevronRight, Heart, Star } from 'lucide-react';
import { useState } from 'react';
import type { Product } from '@/modules/catalog/mock';
import { formatCurrency } from '@/lib/utils/format-currency';

export function ProductCard({ product }: { product: Product }) {
  const [index, setIndex] = useState(0);
  const formatPrice = (val?: number) =>
    val !== undefined && val !== null ? (val > 1000 ? formatCurrency(val) : `$${val}`) : '';
  return (
    <article className="group min-w-0">
      <div className="relative overflow-hidden rounded-xl bg-[#e9e5dc]">
        <Link href={`/products/${product.slug}`}>
          <div className="relative aspect-[.88] w-full">
            <Image
              src={product.images[index]}
              alt={product.name}
              fill
              sizes="(max-width: 640px) 50vw, (max-width: 1024px) 33vw, 25vw"
              className="object-cover transition-transform duration-500 group-hover:scale-[1.03]"
            />
          </div>
        </Link>
        {product.tag && (
          <span className="absolute left-3 top-3 rounded-full bg-[#84EA53] px-2.5 py-1 text-[10px] font-bold uppercase tracking-wide text-[#1D1F20]">
            {product.tag}
          </span>
        )}
        <button
          aria-label="Save product"
          className="absolute right-3 top-3 rounded-full bg-white/85 p-2 backdrop-blur transition hover:bg-white"
        >
          <Heart size={15} />
        </button>
        {product.images.length > 1 && (
          <>
            <button
              aria-label="Previous image"
              onClick={() =>
                setIndex(index === 0 ? product.images.length - 1 : index - 1)
              }
              className="absolute left-2 top-1/2 rounded-full bg-white/85 p-1.5 opacity-0 transition group-hover:opacity-100"
            >
              <ChevronLeft size={15} />
            </button>
            <button
              aria-label="Next image"
              onClick={() => setIndex((index + 1) % product.images.length)}
              className="absolute right-2 top-1/2 rounded-full bg-white/85 p-1.5 opacity-0 transition group-hover:opacity-100"
            >
              <ChevronRight size={15} />
            </button>
          </>
        )}
      </div>
      <div className="px-0.5 pt-3">
        <div className="flex items-center justify-between gap-2">
          <h3 className="truncate text-sm font-semibold">{product.name}</h3>
          <span className="flex shrink-0 items-center gap-1 text-xs">
            <Star size={11} fill="#1D1F20" /> {product.rating}
          </span>
        </div>
        <p className="mt-1 text-xs text-[#74736e]">
          {product.brand} · {product.reviews} reviews
        </p>
        <div className="mt-2 flex items-center gap-2">
          <span className="text-sm font-bold">
            {formatPrice(product.flashSalePrice ?? product.price)}
          </span>
          {product.originalPrice && (
            <span className="text-xs text-[#9b9991] line-through">
              {formatPrice(product.originalPrice)}
            </span>
          )}
        </div>
        <div className="mt-2 flex gap-1.5">
          {product.colors.map((color) => (
            <span
              key={color}
              className="h-3 w-3 rounded-full border border-black/10"
              style={{ backgroundColor: color }}
            />
          ))}
        </div>
      </div>
    </article>
  );
}
```

# catalog\components\product-detail.tsx

| Property | Value |
|----------|-------|
| Path | `catalog\components\product-detail.tsx` |
| Size | 6,847 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.551834 |

```tsx
'use client';
import Image from 'next/image';
import Link from 'next/link';
import {
  Check,
  ChevronLeft,
  ChevronRight,
  Heart,
  Minus,
  Plus,
  Star,
} from 'lucide-react';
import { useState } from 'react';
import type { Product } from '@/modules/catalog/mock';
export function ProductDetail({ product }: { product: Product }) {
  const [image, setImage] = useState(0);
  const [size, setSize] = useState(product.sizes[0]);
  const [color, setColor] = useState(product.colors[0]);
  const [quantity, setQuantity] = useState(1);
  return (
    <main className="mx-auto max-w-[1280px] px-4 py-6 sm:px-8 lg:py-12">
      <Link
        href="/search"
        className="mb-6 inline-flex items-center gap-1 text-xs text-[#74736e]"
      >
        <ChevronLeft size={14} /> Back to edit
      </Link>
      <div className="grid gap-10 lg:grid-cols-[1.05fr_.95fr] lg:gap-16">
        <div>
          <div className="relative aspect-[.95] overflow-hidden rounded-2xl bg-white">
            <Image
              src={product.images[image]}
              alt={product.name}
              fill
              priority
              sizes="(max-width: 1024px) 100vw, 55vw"
              className="object-cover"
            />
            <button
              onClick={() =>
                setImage(image === 0 ? product.images.length - 1 : image - 1)
              }
              aria-label="Previous product image"
              className="absolute left-4 top-1/2 rounded-full bg-white/90 p-2"
            >
              <ChevronLeft size={18} />
            </button>
            <button
              onClick={() => setImage((image + 1) % product.images.length)}
              aria-label="Next product image"
              className="absolute right-4 top-1/2 rounded-full bg-white/90 p-2"
            >
              <ChevronRight size={18} />
            </button>
          </div>
          <div className="mt-3 grid grid-cols-2 gap-3">
            {product.images.map((src, index) => (
              <button
                key={src}
                onClick={() => setImage(index)}
                className={`relative aspect-square overflow-hidden rounded-xl ${image === index ? 'ring-2 ring-[#84EA53]' : 'opacity-60'}`}
              >
                <Image
                  src={src}
                  alt=""
                  fill
                  sizes="120px"
                  className="object-cover"
                />
              </button>
            ))}
          </div>
        </div>
        <div className="self-center rounded-2xl bg-white p-6 sm:p-10">
          <div className="mb-7 flex items-start justify-between">
            <div>
              <p className="eyebrow">
                {product.brand} · {product.category}
              </p>
              <h1 className="section-title mt-3">{product.name}</h1>
            </div>
            <button
              aria-label="Save product"
              className="rounded-full border border-[#EAE6DF] p-3"
            >
              <Heart size={18} />
            </button>
          </div>
          <div className="flex items-center gap-3 border-b border-[#EAE6DF] pb-7">
            <span className="text-xl font-bold">
              ${product.flashSalePrice ?? product.price}
            </span>
            {product.originalPrice && (
              <span className="text-sm text-[#9b9991] line-through">
                ${product.originalPrice}
              </span>
            )}
            <span className="ml-auto flex items-center gap-1 text-sm">
              <Star size={14} fill="#1D1F20" /> {product.rating}{' '}
              <span className="text-[#74736e]">({product.reviews})</span>
            </span>
          </div>
          <p className="py-7 text-sm leading-6 text-[#74736e]">
            {product.description}
          </p>
          <div className="space-y-6 border-t border-[#EAE6DF] pt-6">
            <div>
              <div className="mb-3 flex justify-between text-sm font-semibold">
                <span>Color</span>
                <span className="font-normal text-[#74736e]">Selected</span>
              </div>
              <div className="flex gap-3">
                {product.colors.map((item) => (
                  <button
                    key={item}
                    onClick={() => setColor(item)}
                    aria-label={`Select color ${item}`}
                    className={`h-8 w-8 rounded-full border-2 border-white outline outline-1 ${color === item ? 'outline-[#1D1F20]' : 'outline-[#EAE6DF]'}`}
                    style={{ backgroundColor: item }}
                  />
                ))}
              </div>
            </div>
            <div>
              <div className="mb-3 flex justify-between text-sm font-semibold">
                <span>Size</span>
                <button className="text-xs font-normal underline">
                  Size guide
                </button>
              </div>
              <div className="flex flex-wrap gap-2">
                {product.sizes.map((item) => (
                  <button
                    key={item}
                    onClick={() => setSize(item)}
                    className={`rounded-lg border px-4 py-2.5 text-xs ${size === item ? 'border-[#1D1F20] bg-[#1D1F20] !text-white [&_*]:!text-white' : 'border-[#EAE6DF]'}`}
                  >
                    {item}
                  </button>
                ))}
              </div>
            </div>
            <div className="flex gap-3">
              <div className="flex items-center rounded-xl border border-[#EAE6DF]">
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  className="p-3"
                  aria-label="Decrease quantity"
                >
                  <Minus size={14} />
                </button>
                <span className="w-5 text-center text-sm">{quantity}</span>
                <button
                  onClick={() => setQuantity(quantity + 1)}
                  className="p-3"
                  aria-label="Increase quantity"
                >
                  <Plus size={14} />
                </button>
              </div>
              <button className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-[#84EA53] py-3.5 text-sm font-bold transition hover:brightness-95">
                Add to cart · $
                {((product.flashSalePrice ?? product.price) * quantity).toFixed(
                  2,
                )}{' '}
                <Check size={16} />
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}

```

# catalog\components\search-shell.tsx

| Property | Value |
|----------|-------|
| Path | `catalog\components\search-shell.tsx` |
| Size | 2,595 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.553831 |

```tsx
'use client';
import { SlidersHorizontal } from 'lucide-react';
import { useState } from 'react';
import { ProductCard } from '@/modules/catalog/components/product-card';
import { FilterSidebar } from '@/modules/catalog/components/filter-sidebar';
import type { Product } from '@/modules/catalog/mock';
export function SearchShell({
  products,
  category,
}: {
  products: Product[];
  category?: string;
}) {
  const [open, setOpen] = useState(false);
  return (
    <div className="relative mx-auto max-w-[1440px] px-4 pb-20 sm:px-8 lg:px-12">
      <div className="mb-7 flex items-center justify-between border-b border-[#EAE6DF] py-5">
        <button
          onClick={() => setOpen(true)}
          className="flex items-center gap-2 rounded-full border border-[#1D1F20] px-4 py-2 text-sm font-semibold transition hover:bg-[#1D1F20] hover:!text-white [&_*]:!text-white"
        >
          <SlidersHorizontal size={16} /> Filters
        </button>
        <p className="hidden text-sm text-[#74736e] sm:block">
          {products.length * 20 + 4} pieces
        </p>
        <select className="rounded-full border border-[#EAE6DF] bg-transparent px-3 py-2 text-xs outline-none">
          <option>Sort: Recommended</option>
          <option>Price: low to high</option>
          <option>Newest first</option>
        </select>
      </div>
      <div className="flex gap-8">
        <FilterSidebar open={open} onClose={() => setOpen(false)} />
        {open && (
          <button
            aria-label="Close filter overlay"
            onClick={() => setOpen(false)}
            className="fixed inset-0 z-40 bg-[#1D1F20]/20 lg:hidden"
          />
        )}
        {open && <div className="hidden w-[300px] shrink-0 lg:block" />}
        <div className="min-w-0 flex-1">
          <div className="mb-5 flex items-end justify-between">
            <div>
              <p className="eyebrow">{category ?? 'All pieces'}</p>
              <h1 className="section-title mt-2">
                {category ? `${category}, considered` : 'Find your everyday'}
              </h1>
            </div>
            <span className="text-xs text-[#74736e] sm:hidden">
              {products.length * 20 + 4} items
            </span>
          </div>
          <div className="grid grid-cols-2 gap-x-3 gap-y-10 sm:grid-cols-3 lg:grid-cols-4">
            {products.map((product) => (
              <ProductCard key={product.slug} product={product} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

```

# catalog\components\site-header.tsx

| Property | Value |
|----------|-------|
| Path | `catalog\components\site-header.tsx` |
| Size | 2,609 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-01 17:02:22.930284 |

```tsx
'use client';
import Link from 'next/link';
import { Search, ShoppingBag } from 'lucide-react';
import { categories } from '@/modules/catalog/data';
export function SiteHeader() {
  return (
    <header className="sticky top-0 z-40 border-b border-[#EAE6DF] bg-[#FBF9F4]/95 backdrop-blur">
      <div className="mx-auto flex h-[72px] max-w-[1440px] items-center gap-5 px-4 sm:px-8 lg:px-12">
        <Link
          href="/"
          className="shrink-0 font-display text-3xl font-semibold tracking-[-.06em]"
        >
          common<span className="text-[#84EA53]">.</span>
        </Link>
        <div className="hidden h-11 max-w-[430px] flex-1 items-center rounded-full border border-[#EAE6DF] bg-white px-4 shadow-[0_5px_20px_rgba(29,31,32,.04)] sm:flex">
          <Search size={17} className="mr-3 text-[#74736e]" />
          <input
            aria-label="Search products"
            placeholder="Search for an object, a feeling..."
            className="w-full bg-transparent text-sm outline-none placeholder:text-[#9b9991]"
          />
          <kbd className="rounded bg-[#FBF9F4] px-2 py-1 text-[10px] text-[#74736e]">
            ⌘ K
          </kbd>
        </div>
        <div className="ml-auto flex items-center gap-2">
          <Link
            href="/search"
            className="rounded-full p-2.5 hover:bg-white sm:hidden"
          >
            <Search size={20} />
          </Link>
          <Link
            href="/cart"
            className="relative rounded-full p-2.5 hover:bg-white"
          >
            <ShoppingBag size={20} />
            <span className="absolute right-0 top-0 flex h-4 w-4 items-center justify-center rounded-full bg-[#84EA53] text-[9px] font-bold">
              2
            </span>
          </Link>
        </div>
      </div>
      <nav className="scrollbar-none mx-auto flex max-w-[1440px] gap-7 overflow-x-auto px-4 pb-3 sm:px-8 lg:px-12">
        {categories.map((category) => (
          <Link
            key={category.name}
            href={`/search?category=${category.name}`}
            className="group flex min-w-fit flex-col items-center gap-1 text-[#74736e] transition-colors hover:text-[#1D1F20]"
          >
            <span className="flex h-8 w-8 items-center justify-center rounded-full border border-[#EAE6DF] bg-white text-base group-hover:border-[#84EA53]">
              {category.icon}
            </span>
            <span className="text-[10px] font-medium">{category.name}</span>
          </Link>
        ))}
      </nav>
    </header>
  );
}

```

# catalog\data.ts

| Property | Value |
|----------|-------|
| Path | `catalog\data.ts` |
| Size | 5,580 bytes |
| Extension | `.ts` |
| Modified | 2026-10-01 17:02:22.931284 |

```typescript
export type Product = {
  slug: string;
  name: string;
  brand: string;
  category: string;
  price: number;
  originalPrice?: number;
  flashSalePrice?: number;
  rating: number;
  reviews: number;
  tag?: string;
  images: string[];
  colors: string[];
  sizes: string[];
  description: string;
};
const image = (id: string, w = 900, h = 1100) =>
  `https://images.unsplash.com/${id}?auto=format&fit=crop&w=${w}&h=${h}&q=85`;
export const categories = [
  {
    name: 'Apparel',
    count: 48,
    icon: '✦',
    image: image('photo-1496747611176-843222e1e57c'),
  },
  {
    name: 'Objects',
    count: 32,
    icon: '◒',
    image: image('photo-1494438639946-1ebd1d20bf85'),
  },
  {
    name: 'Accessories',
    count: 26,
    icon: '◇',
    image: image('photo-1525507119028-ed4c629a60a3'),
  },
  {
    name: 'Wellness',
    count: 18,
    icon: '⌁',
    image: image('photo-1608571423902-eed4a5ad8108'),
  },
  {
    name: 'Stationery',
    count: 14,
    icon: '▱',
    image: image('photo-1455390582262-044cdead277a'),
  },
  {
    name: 'Home',
    count: 22,
    icon: '⌂',
    image: image('photo-1618221195710-dd6b41faaea6'),
  },
];
export const products: Product[] = [
  {
    slug: 'linen-overshirt',
    name: 'Linen Overshirt',
    brand: 'Morrow Studio',
    category: 'Apparel',
    price: 168,
    originalPrice: 210,
    flashSalePrice: 168,
    rating: 4.9,
    reviews: 86,
    tag: 'Bestseller',
    images: [
      image('photo-1594633312681-425c7b97ccd1'),
      image('photo-1529139574466-a303027c1d8b'),
    ],
    colors: ['#ded5c8', '#1D1F20', '#bec9bf'],
    sizes: ['XS', 'S', 'M', 'L'],
    description:
      'An easy, everyday layer cut from breathable European linen. Relaxed through the body with a clean, considered finish.',
  },
  {
    slug: 'everyday-tote',
    name: 'Everyday Tote',
    brand: 'Form & Field',
    category: 'Accessories',
    price: 92,
    rating: 4.8,
    reviews: 42,
    tag: 'New',
    images: [
      image('photo-1548036328-c9fa89d128fa'),
      image('photo-1553062407-98eeb64c6a62'),
    ],
    colors: ['#b8794b', '#242424', '#ded5c8'],
    sizes: ['One size'],
    description:
      'A generous, structured canvas tote with vegetable-tanned leather handles and room for all your daily rituals.',
  },
  {
    slug: 'ribbed-knit-set',
    name: 'Ribbed Knit Set',
    brand: 'House of Common',
    category: 'Apparel',
    price: 144,
    originalPrice: 180,
    flashSalePrice: 144,
    rating: 4.7,
    reviews: 31,
    tag: '20% off',
    images: [
      image('photo-1483985988355-763728e1935b'),
      image('photo-1515886657613-9f3515b0c78f'),
    ],
    colors: ['#c8b8ad', '#5b6353'],
    sizes: ['XS', 'S', 'M', 'L'],
    description:
      'Soft, tactile knitwear made to move through the week. A matching set that does more with less.',
  },
  {
    slug: 'stoneware-mug',
    name: 'Stoneware Mug',
    brand: 'Kiln Notes',
    category: 'Objects',
    price: 34,
    rating: 4.9,
    reviews: 112,
    images: [
      image('photo-1514228742587-6b1558fcca3d'),
      image('photo-1577937927133-66ef06acdf18'),
    ],
    colors: ['#d8c5ac', '#b9c8c2', '#202121'],
    sizes: ['One size'],
    description:
      'Hand-finished stoneware with a generous handle and a quiet, matte glaze. Each one is subtly unique.',
  },
  {
    slug: 'pleated-trouser',
    name: 'Pleated Trouser',
    brand: 'Morrow Studio',
    category: 'Apparel',
    price: 128,
    rating: 4.6,
    reviews: 24,
    images: [
      image('photo-1506629905607-d9b1f1f3d7a8'),
      image('photo-1473966968600-fa801b869a1a'),
    ],
    colors: ['#282a2a', '#ded7c9'],
    sizes: ['28', '30', '32', '34'],
    description:
      'A high-waisted trouser with a softly tailored line, finished in a fluid, mid-weight cotton blend.',
  },
  {
    slug: 'daily-journal',
    name: 'Daily Journal',
    brand: 'Paper Sunday',
    category: 'Stationery',
    price: 28,
    rating: 4.8,
    reviews: 67,
    tag: 'Staff pick',
    images: [
      image('photo-1517842645767-c639042777db'),
      image('photo-1499951360447-b19be8fe80f5'),
    ],
    colors: ['#d8c9b8', '#8d9a87', '#2c3430'],
    sizes: ['A5'],
    description:
      'A calm place for plans, notes, and half-formed ideas. 160 pages of smooth, fountain-pen friendly paper.',
  },
  {
    slug: 'everyday-sandal',
    name: 'Everyday Sandal',
    brand: 'Lumen Goods',
    category: 'Accessories',
    price: 76,
    rating: 4.7,
    reviews: 18,
    images: [
      image('photo-1543163521-1bf539c55dd2'),
      image('photo-1560343090-f0409e92791a'),
    ],
    colors: ['#c8895b', '#1f2321'],
    sizes: ['36', '37', '38', '39', '40'],
    description:
      'A minimal leather sandal with a cushioned footbed, designed for long walks and slow afternoons.',
  },
  {
    slug: 'linen-candle',
    name: 'Linen + Cedar Candle',
    brand: 'Sunday Objects',
    category: 'Wellness',
    price: 42,
    rating: 4.9,
    reviews: 53,
    images: [
      image('photo-1603006905003-be475563bc59'),
      image('photo-1602523961358-f9f03dd557db'),
    ],
    colors: ['#e4dac8'],
    sizes: ['220g'],
    description:
      'A warm, grounding blend of washed linen, cedar, and a little smoke. Poured by hand in small batches.',
  },
];
export const getProduct = (slug: string) =>
  products.find((product) => product.slug === slug) ?? products[0];

```

# catalog\mock.ts

| Property | Value |
|----------|-------|
| Path | `catalog\mock.ts` |
| Size | 5,580 bytes |
| Extension | `.ts` |
| Modified | 2026-10-02 09:57:03.134075 |

```typescript
export type Product = {
  slug: string;
  name: string;
  brand: string;
  category: string;
  price: number;
  originalPrice?: number;
  flashSalePrice?: number;
  rating: number;
  reviews: number;
  tag?: string;
  images: string[];
  colors: string[];
  sizes: string[];
  description: string;
};
const image = (id: string, w = 900, h = 1100) =>
  `https://images.unsplash.com/${id}?auto=format&fit=crop&w=${w}&h=${h}&q=85`;
export const categories = [
  {
    name: 'Apparel',
    count: 48,
    icon: '✦',
    image: image('photo-1496747611176-843222e1e57c'),
  },
  {
    name: 'Objects',
    count: 32,
    icon: '◒',
    image: image('photo-1494438639946-1ebd1d20bf85'),
  },
  {
    name: 'Accessories',
    count: 26,
    icon: '◇',
    image: image('photo-1525507119028-ed4c629a60a3'),
  },
  {
    name: 'Wellness',
    count: 18,
    icon: '⌁',
    image: image('photo-1608571423902-eed4a5ad8108'),
  },
  {
    name: 'Stationery',
    count: 14,
    icon: '▱',
    image: image('photo-1455390582262-044cdead277a'),
  },
  {
    name: 'Home',
    count: 22,
    icon: '⌂',
    image: image('photo-1618221195710-dd6b41faaea6'),
  },
];
export const products: Product[] = [
  {
    slug: 'linen-overshirt',
    name: 'Linen Overshirt',
    brand: 'Morrow Studio',
    category: 'Apparel',
    price: 168,
    originalPrice: 210,
    flashSalePrice: 168,
    rating: 4.9,
    reviews: 86,
    tag: 'Bestseller',
    images: [
      image('photo-1594633312681-425c7b97ccd1'),
      image('photo-1529139574466-a303027c1d8b'),
    ],
    colors: ['#ded5c8', '#1D1F20', '#bec9bf'],
    sizes: ['XS', 'S', 'M', 'L'],
    description:
      'An easy, everyday layer cut from breathable European linen. Relaxed through the body with a clean, considered finish.',
  },
  {
    slug: 'everyday-tote',
    name: 'Everyday Tote',
    brand: 'Form & Field',
    category: 'Accessories',
    price: 92,
    rating: 4.8,
    reviews: 42,
    tag: 'New',
    images: [
      image('photo-1548036328-c9fa89d128fa'),
      image('photo-1553062407-98eeb64c6a62'),
    ],
    colors: ['#b8794b', '#242424', '#ded5c8'],
    sizes: ['One size'],
    description:
      'A generous, structured canvas tote with vegetable-tanned leather handles and room for all your daily rituals.',
  },
  {
    slug: 'ribbed-knit-set',
    name: 'Ribbed Knit Set',
    brand: 'House of Common',
    category: 'Apparel',
    price: 144,
    originalPrice: 180,
    flashSalePrice: 144,
    rating: 4.7,
    reviews: 31,
    tag: '20% off',
    images: [
      image('photo-1483985988355-763728e1935b'),
      image('photo-1515886657613-9f3515b0c78f'),
    ],
    colors: ['#c8b8ad', '#5b6353'],
    sizes: ['XS', 'S', 'M', 'L'],
    description:
      'Soft, tactile knitwear made to move through the week. A matching set that does more with less.',
  },
  {
    slug: 'stoneware-mug',
    name: 'Stoneware Mug',
    brand: 'Kiln Notes',
    category: 'Objects',
    price: 34,
    rating: 4.9,
    reviews: 112,
    images: [
      image('photo-1514228742587-6b1558fcca3d'),
      image('photo-1577937927133-66ef06acdf18'),
    ],
    colors: ['#d8c5ac', '#b9c8c2', '#202121'],
    sizes: ['One size'],
    description:
      'Hand-finished stoneware with a generous handle and a quiet, matte glaze. Each one is subtly unique.',
  },
  {
    slug: 'pleated-trouser',
    name: 'Pleated Trouser',
    brand: 'Morrow Studio',
    category: 'Apparel',
    price: 128,
    rating: 4.6,
    reviews: 24,
    images: [
      image('photo-1506629905607-d9b1f1f3d7a8'),
      image('photo-1473966968600-fa801b869a1a'),
    ],
    colors: ['#282a2a', '#ded7c9'],
    sizes: ['28', '30', '32', '34'],
    description:
      'A high-waisted trouser with a softly tailored line, finished in a fluid, mid-weight cotton blend.',
  },
  {
    slug: 'daily-journal',
    name: 'Daily Journal',
    brand: 'Paper Sunday',
    category: 'Stationery',
    price: 28,
    rating: 4.8,
    reviews: 67,
    tag: 'Staff pick',
    images: [
      image('photo-1517842645767-c639042777db'),
      image('photo-1499951360447-b19be8fe80f5'),
    ],
    colors: ['#d8c9b8', '#8d9a87', '#2c3430'],
    sizes: ['A5'],
    description:
      'A calm place for plans, notes, and half-formed ideas. 160 pages of smooth, fountain-pen friendly paper.',
  },
  {
    slug: 'everyday-sandal',
    name: 'Everyday Sandal',
    brand: 'Lumen Goods',
    category: 'Accessories',
    price: 76,
    rating: 4.7,
    reviews: 18,
    images: [
      image('photo-1543163521-1bf539c55dd2'),
      image('photo-1560343090-f0409e92791a'),
    ],
    colors: ['#c8895b', '#1f2321'],
    sizes: ['36', '37', '38', '39', '40'],
    description:
      'A minimal leather sandal with a cushioned footbed, designed for long walks and slow afternoons.',
  },
  {
    slug: 'linen-candle',
    name: 'Linen + Cedar Candle',
    brand: 'Sunday Objects',
    category: 'Wellness',
    price: 42,
    rating: 4.9,
    reviews: 53,
    images: [
      image('photo-1603006905003-be475563bc59'),
      image('photo-1602523961358-f9f03dd557db'),
    ],
    colors: ['#e4dac8'],
    sizes: ['220g'],
    description:
      'A warm, grounding blend of washed linen, cedar, and a little smoke. Poured by hand in small batches.',
  },
];
export const getProduct = (slug: string) =>
  products.find((product) => product.slug === slug) ?? products[0];

```

# checkout\components\checkout-view.tsx

| Property | Value |
|----------|-------|
| Path | `checkout\components\checkout-view.tsx` |
| Size | 15,077 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 20:02:31.762203 |

```tsx
'use client';

import Link from 'next/link';
import { ArrowLeft, Check, Lock } from 'lucide-react';
import { useState, useMemo, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { catalogApi } from '@/api/catalog.api';
import { useRouter, useSearchParams } from 'next/navigation';
import { useForm, FormProvider } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { checkoutSchema, CheckoutFormValues } from '../schemas';
import { useCart } from '@/modules/cart/hooks/use-cart';
import { useCreateOrder } from '@/modules/order/hooks/use-orders';
import { orderApi } from '@/api/order.api';
import { ShippingForm } from './shipping-form';
import { PaymentMethodComponent } from './payment-method';
import { formatCurrency } from '@/lib/utils/format-currency';
import { toast } from 'sonner';
import {
  VoucherInput,
  PointRedeemSlider,
  type VoucherValidationData,
  getPointBalance,
} from '@/modules/loyalty-engage';

export function CheckoutView() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [step, setStep] = useState(1);
  const { data: cartResponse } = useCart();
  const createOrderMutation = useCreateOrder();

  const [qrData, setQrData] = useState<{ qrUrl: string; amount: number; transferContent: string } | null>(null);
  const [createdOrderCode, setCreatedOrderCode] = useState<string | null>(null);
  const [isCreatingPayment, setIsCreatingPayment] = useState(false);

  const [appliedVoucher, setAppliedVoucher] = useState<VoucherValidationData | null>(null);
  const [usedPoints, setUsedPoints] = useState<number>(0);
  const [userPointsBalance, setUserPointsBalance] = useState<number>(0);

  useEffect(() => {
    getPointBalance()
      .then((res) => {
        if (res?.data?.rewardPoints) {
          setUserPointsBalance(res.data.rewardPoints);
        }
      })
      .catch(() => {});
  }, []);

    const isDirectBuy = searchParams.get('directBuy') === 'true';
  const directProductId = Number(searchParams.get('productId'));
  const directVariantId = Number(searchParams.get('variantId'));
  const directQuantity = Number(searchParams.get('quantity')) || 1;

  const { data: directProductRes } = useQuery({
    queryKey: ['direct-buy-product', directProductId],
    queryFn: () => catalogApi.getProductById(directProductId),
    enabled: isDirectBuy && !!directProductId,
  });

  const itemsParam = searchParams.get('items');
  const selectedItemIds = useMemo(() => {
    if (!itemsParam) return null;
    return itemsParam.split(',').map(Number).filter((id) => !isNaN(id) && id > 0);
  }, [itemsParam]);

  const allCartItems = cartResponse?.data?.cartItems || [];
  
  const checkoutItems = useMemo(() => {
    if (isDirectBuy && directProductRes?.data) {
      const variant = directProductRes.data.variants?.find((v: any) => v.id === directVariantId);
      if (variant) {
        return [{
          id: -1,
          cartId: -1,
          variantId: variant.id,
          quantity: directQuantity,
          variant: {
            ...variant,
            product: directProductRes.data
          }
        }];
      }
    }
    if (!selectedItemIds || selectedItemIds.length === 0) return allCartItems;
    return allCartItems.filter((item) => selectedItemIds.includes(item.id));
  }, [allCartItems, selectedItemIds, isDirectBuy, directProductRes, directVariantId, directQuantity]);

  const methods = useForm<CheckoutFormValues>({
    resolver: zodResolver(checkoutSchema),
    defaultValues: { shippingMethod: 'STANDARD', paymentMethod: 'COD' },
    mode: 'onTouched',
  });

  const subTotal = checkoutItems.reduce((sum, item) => {
    const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
      if (!fsi.campaign || !fsi.campaign.isActive) return false;
      const now = new Date().getTime();
      const start = new Date(fsi.campaign.startTime).getTime();
      const end = new Date(fsi.campaign.endTime).getTime();
      const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
      return now >= start && now <= end && remainingQty >= item.quantity;
    });

    const price = activeFlashSale
      ? Number(activeFlashSale.flashSalePrice)
      : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);
      
    return sum + price * item.quantity;
  }, 0);

  const voucherDiscount = appliedVoucher ? appliedVoucher.discountAmount : 0;
  const remainingAfterVoucher = Math.max(0, subTotal - voucherDiscount);
  const pointDiscount = Math.min(usedPoints, remainingAfterVoucher);
  const finalTotal = Math.max(0, subTotal - voucherDiscount - pointDiscount);

  const onSubmit = (data: CheckoutFormValues) => {
    const orderPayload: any = {
      shippingAddress: data.shippingAddress,
      paymentMethod: data.paymentMethod,
      shippingMethod: data.shippingMethod,
      customerNote: data.customerNote,
      voucherCode: data.voucherCode,
      pointUsed: data.pointUsed,
    };

    if (isDirectBuy) {
      orderPayload.directBuy = {
        variantId: directVariantId,
        quantity: directQuantity,
      };
    } else {
      orderPayload.cartItemIds = selectedItemIds && selectedItemIds.length > 0 ? selectedItemIds : undefined;
    }

    createOrderMutation.mutate(orderPayload, {
      onSuccess: async (res) => {
        const orderData = res.data;
        if (data.paymentMethod !== 'COD') {
          setIsCreatingPayment(true);
          try {
            const paymentRes = await orderApi.createPayment(orderData.id);
            setQrData(paymentRes.data);
            setCreatedOrderCode(orderData.orderCode);
            setStep(3);
            toast.success('Đặt hàng thành công! Vui lòng quét mã thanh toán.');
          } catch (err) {
            toast.error('Không thể tải mã QR lúc này, bạn có thể thanh toán sau trong chi tiết đơn hàng.');
            router.push(`/order-success/${orderData.orderCode}`);
          } finally {
            setIsCreatingPayment(false);
          }
        } else {
          toast.success('Đặt hàng thành công!');
          router.push(`/order-success/${orderData.orderCode}`);
        }
      }
    });
  };

  if (step === 3 && qrData) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
        <div className="max-w-xl mx-auto bg-white rounded-3xl border border-[#84EA53] p-8 sm:p-10 text-center shadow-lg animate-in zoom-in-95 duration-500">
          <h2 className="font-display text-3xl text-neutral-900 mb-2">Thanh toán đơn hàng</h2>
          <p className="text-sm text-neutral-600 mb-8">
            Mã đơn hàng: <strong className="text-neutral-900 font-mono">#{createdOrderCode}</strong><br />
            Vui lòng mở ứng dụng ngân hàng hoặc ví điện tử và quét mã QR dưới đây.
          </p>
          
          <div className="flex justify-center p-4 bg-neutral-50 rounded-2xl border border-[#EAE6DF] max-w-xs mx-auto mb-6 shadow-xs">
            <img src={qrData.qrUrl} alt="Mã QR Thanh toán" className="w-full h-auto object-contain mix-blend-multiply" />
          </div>

          <div className="bg-amber-50 border border-amber-200 p-5 rounded-2xl max-w-sm mx-auto text-left space-y-3 mb-8">
            <div className="flex justify-between items-center border-b border-amber-200/50 pb-3">
              <span className="text-xs text-amber-800">Số tiền cần chuyển:</span>
              <span className="text-lg font-black text-amber-900">{formatCurrency(qrData.amount)}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-xs text-amber-800">Nội dung CK:</span>
              <span className="font-mono font-bold text-sm text-amber-900 bg-amber-200/50 px-2 py-1 rounded select-all">{qrData.transferContent}</span>
            </div>
          </div>

          <button
            type="button"
            onClick={() => router.push(`/order-success/${createdOrderCode}`)}
            className="w-full max-w-sm mx-auto rounded-xl bg-[#1D1F20] hover:bg-black py-4 text-sm font-bold !text-white [&_*]:!text-white transition-all shadow-sm active:scale-[0.99] block"
          >
            Tôi đã thanh toán thành công
          </button>
        </div>
      </main>
    );
  }

  if (checkoutItems.length === 0) {
    return (
      <main className="mx-auto max-w-[1180px] px-4 py-20 sm:px-8 text-center">
        <h1 className="font-display text-3xl mb-4 text-neutral-900">Không có sản phẩm nào được chọn</h1>
        <Link href="/cart" className="text-emerald-600 underline font-semibold hover:text-emerald-700">
          Quay lại giỏ hàng
        </Link>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-[1180px] px-4 py-10 sm:px-8 lg:py-16">
      <div className="mb-10 flex items-end justify-between">
        <div>
          <p className="eyebrow">XÁC NHẬN MUA HÀNG</p>
          <h1 className="section-title mt-2">Thanh toán</h1>
        </div>
        <Link href="/cart" className="flex items-center gap-1 text-xs text-[#74736e] hover:text-neutral-900 transition">
          <ArrowLeft size={14} /> Quay lại giỏ hàng
        </Link>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)} className="grid gap-10 lg:grid-cols-[1fr_360px]">
          <div className="space-y-4">
            <section
              className={`rounded-2xl bg-white p-6 border transition-all ${
                step === 1 ? 'border-[#84EA53] shadow-xs' : 'border-[#EAE6DF]'
              }`}
            >
              <div className="flex items-center justify-between cursor-pointer" onClick={() => setStep(1)}>
                <h2 className="font-display text-3xl text-neutral-900">01 · Địa chỉ nhận hàng</h2>
                {step > 1 && <Check size={18} color="#61bc39" />}
              </div>
              {step === 1 && <ShippingForm onNext={() => setStep(2)} />}
            </section>

            <section
              className={`rounded-2xl bg-white p-6 border transition-all ${
                step === 2
                  ? 'border-[#84EA53] shadow-xs'
                  : 'border-[#EAE6DF] opacity-60 pointer-events-none'
              }`}
            >
              <h2 className="font-display text-3xl text-neutral-900">02 · Phương thức thanh toán</h2>
              {step === 2 && (
                <div className="mt-6 space-y-6">
                  <VoucherInput
                    orderTotal={subTotal}
                    onValidate={(v) => {
                      methods.setValue('voucherCode', v.code);
                      setAppliedVoucher(v);
                    }}
                    onRemove={() => {
                      methods.setValue('voucherCode', undefined);
                      setAppliedVoucher(null);
                    }}
                  />
                  {userPointsBalance > 0 && (
                    <PointRedeemSlider
                      maxPoints={userPointsBalance}
                      orderTotal={remainingAfterVoucher}
                      onRedeem={(pts) => {
                        methods.setValue('pointUsed', pts);
                        setUsedPoints(pts);
                      }}
                    />
                  )}
                  <PaymentMethodComponent
                    isSubmitting={createOrderMutation.isPending || isCreatingPayment}
                    totalAmount={finalTotal}
                  />
                </div>
              )}
            </section>
          </div>

          <aside className="h-fit rounded-2xl bg-[#1D1F20] p-6 !text-white [&_*]:!text-white lg:sticky lg:top-36 shadow-lg">
            <div className="flex items-center gap-2 text-sm font-semibold">
              <Lock size={15} color="#84EA53" /> Thanh toán an toàn
            </div>
            <div className="mt-6 divide-y divide-white/10">
              {checkoutItems.map((item) => {
                // Tính lại giá cho từng Item hiển thị
                const activeFlashSale = item.variant.flashSaleItems?.find((fsi: any) => {
                  if (!fsi.campaign || !fsi.campaign.isActive) return false;
                  const now = new Date().getTime();
                  const start = new Date(fsi.campaign.startTime).getTime();
                  const end = new Date(fsi.campaign.endTime).getTime();
                  const remainingQty = fsi.promoQuantity - (fsi.soldQuantity || 0);
                  return now >= start && now <= end && remainingQty >= item.quantity;
                });
                const currentPrice = activeFlashSale ? Number(activeFlashSale.flashSalePrice) : Number(item.variant.sellingPrice || item.variant.originalPrice || 0);

                return (
                  <div key={item.id} className="py-3 flex justify-between text-xs">
                    <span className="!text-white [&_*]:!text-white/70 line-clamp-1 w-2/3">
                      {item.quantity}x {item.variant.product.defaultName}
                    </span>
                    <span className="font-medium">
                      {/* Hiển thị giá nhân với số lượng */}
                      {formatCurrency(currentPrice * item.quantity)}
                    </span>
                  </div>
                );
              })}
            </div>

            <div className="mt-6 space-y-2 border-t border-white/15 pt-5 text-xs !text-white [&_*]:!text-white/70">
              <div className="flex justify-between">
                <span>Tạm tính</span>
                <span className="!text-white [&_*]:!text-white">{formatCurrency(subTotal)}</span>
              </div>
              {voucherDiscount > 0 && (
                <div className="flex justify-between text-[#84EA53]">
                  <span>Voucher giảm giá ({appliedVoucher?.code})</span>
                  <span>-{formatCurrency(voucherDiscount)}</span>
                </div>
              )}
              {pointDiscount > 0 && (
                <div className="flex justify-between text-[#84EA53]">
                  <span>Điểm tích lũy</span>
                  <span>-{formatCurrency(pointDiscount)}</span>
                </div>
              )}
            </div>

            <div className="mt-4 flex justify-between border-t border-white/15 pt-4 text-sm">
              <span className="!text-white [&_*]:!text-white/70 font-semibold">Tổng thanh toán</span>
              <strong className="text-lg text-[#84EA53]">{formatCurrency(finalTotal)}</strong>
            </div>
          </aside>
        </form>
      </FormProvider>
    </main>
  );
}
```

# checkout\components\payment-method.tsx

| Property | Value |
|----------|-------|
| Path | `checkout\components\payment-method.tsx` |
| Size | 1,886 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-05 10:24:43.392342 |

```tsx
'use client';

import { useFormContext } from 'react-hook-form';
import { CheckoutFormValues } from '../schemas';

interface PaymentMethodProps {
  isSubmitting: boolean;
  totalAmount: number;
}

const PAYMENT_OPTIONS = [
  { id: 'COD', label: 'Thanh toán khi nhận hàng (COD)' },
  { id: 'VNPAY', label: 'Thanh toán qua VNPAY' },
  { id: 'MOMO', label: 'Thanh toán qua ví MoMo' },
  { id: 'BANK_TRANSFER', label: 'Chuyển khoản ngân hàng' },
];

export function PaymentMethodComponent({ isSubmitting, totalAmount }: PaymentMethodProps) {
  const { register, formState: { errors } } = useFormContext<CheckoutFormValues>();

  return (
    <div className="mt-6 space-y-4">
      <div className="space-y-3">
        {PAYMENT_OPTIONS.map((option) => (
          <label
            key={option.id}
            className="flex cursor-pointer items-center gap-3 rounded-xl border border-[#EAE6DF] p-4 transition-colors hover:bg-gray-50 has-[:checked]:border-[#84EA53] has-[:checked]:bg-[#84EA53]/10"
          >
            <input
              type="radio"
              value={option.id}
              {...register('paymentMethod')}
              className="h-4 w-4 text-[#84EA53] focus:ring-[#84EA53]"
            />
            <span className="text-sm font-medium">{option.label}</span>
          </label>
        ))}
        {errors.paymentMethod && (
          <p className="text-xs text-red-500">{errors.paymentMethod.message}</p>
        )}
      </div>

      <button
        type="submit"
        disabled={isSubmitting}
        className="mt-6 w-full rounded-xl bg-[#84EA53] py-3.5 text-sm font-bold transition-transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50"
      >
        {isSubmitting ? 'Processing...' : `Place order · $${totalAmount.toLocaleString()}`}
      </button>
    </div>
  );
}

```

# checkout\components\shipping-form.tsx

| Property | Value |
|----------|-------|
| Path | `checkout\components\shipping-form.tsx` |
| Size | 23,722 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.559279 |

```tsx
'use client';

import * as React from 'react';
import { useFormContext } from 'react-hook-form';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  MapPin, 
  Plus, 
  Trash2, 
  ChevronDown, 
  Check, 
  Building2, 
  Phone, 
  User, 
  Loader2 
} from 'lucide-react';
import { toast } from 'sonner';
import { authApi } from '@/api/auth.api';
import { CheckoutFormValues } from '../schemas';
import type { ShippingAddress } from '@/types';

interface ShippingFormProps {
  onNext: () => void;
}

export function ShippingForm({ onNext }: ShippingFormProps) {
  const queryClient = useQueryClient();
  const { register, trigger, setValue, getValues, formState: { errors } } = useFormContext<CheckoutFormValues>();

  const [selectedSavedAddressId, setSelectedSavedAddressId] = React.useState<number | 'NEW'>('NEW');
  const [isDropdownOpen, setIsDropdownOpen] = React.useState(false);
  const hasAutoSelectedRef = React.useRef(false);

  // 1. Tải danh sách sổ địa chỉ của User
  const { data: addressesData, isLoading } = useQuery({
    queryKey: ['checkout-addresses'],
    queryFn: async (): Promise<ShippingAddress[]> => {
      try {
        const res: any = await authApi.getAddresses();
        if (Array.isArray(res)) return res;
        if (Array.isArray(res?.data)) return res.data;
        if (Array.isArray(res?.data?.data)) return res.data.data;
        return [];
      } catch {
        return [];
      }
    },
  });

  const addresses: ShippingAddress[] = React.useMemo(() => addressesData || [], [addressesData]);
  const selectedAddress = React.useMemo(
    () => addresses.find((a) => a.id === selectedSavedAddressId),
    [addresses, selectedSavedAddressId]
  );

  // 2. Điền thông tin vào form khi chọn một địa chỉ
  const handleSelectAddress = React.useCallback(
    (val: number | 'NEW') => {
      setSelectedSavedAddressId(val);

      if (val === 'NEW') {
        setValue('shippingAddressId', undefined);
        setValue('shippingAddress.receiverName', '');
        setValue('shippingAddress.receiverPhone', '');
        setValue('shippingAddress.province', '');
        setValue('shippingAddress.district', '');
        setValue('shippingAddress.ward', '');
        setValue('shippingAddress.detailAddress', '');
      } else {
        setValue('shippingAddressId', val);
        const addr = addresses.find((a) => a.id === val);
        if (addr) {
          setValue('shippingAddress.receiverName', addr.receiverName);
          setValue('shippingAddress.receiverPhone', addr.phoneNumber);
          setValue('shippingAddress.province', addr.province);
          setValue('shippingAddress.district', addr.district);
          setValue('shippingAddress.ward', addr.ward);
          setValue('shippingAddress.detailAddress', addr.detailAddress);
        }
      }
    },
    [addresses, setValue]
  );

  // 3. Tự động gán địa chỉ mặc định (chỉ chạy 1 lần sau khi tải xong dữ liệu, tránh cascading render)
  React.useEffect(() => {
    if (!hasAutoSelectedRef.current && addresses.length > 0) {
      hasAutoSelectedRef.current = true;
      const defaultAddr = addresses.find((a) => a.isDefault) || addresses[0];
      if (defaultAddr) {
        Promise.resolve().then(() => {
          handleSelectAddress(defaultAddr.id);
        });
      }
    }
  }, [addresses, handleSelectAddress]);

  // 4. Bắt thao tác chỉnh sửa tay trong form để chuyển thành địa chỉ mới
  const registerWithDetectEdit = (name: any) => {
    const reg = register(name);
    return {
      ...reg,
      onChange: (e: any) => {
        reg.onChange(e);
        if (selectedSavedAddressId !== 'NEW') {
          setSelectedSavedAddressId('NEW');
          setValue('shippingAddressId', undefined);
        }
      },
    };
  };

  // 5. Mutation xóa địa chỉ trực tiếp
  const deleteAddressMutation = useMutation({
    mutationFn: (id: number) => authApi.deleteAddress(id),
    onSuccess: (_, deletedId) => {
      toast.success('Đã xóa địa chỉ thành công');
      queryClient.invalidateQueries({ queryKey: ['checkout-addresses'] });

      // Nếu xóa trúng địa chỉ đang chọn, chuyển sang chế độ nhập mới
      if (selectedSavedAddressId === deletedId) {
        handleSelectAddress('NEW');
      }
    },
    onError: () => {
      toast.error('Không thể xóa địa chỉ lúc này, vui lòng thử lại sau.');
    },
  });

  // 6. Mutation lưu địa chỉ mới vào Database
  const saveAddressMutation = useMutation({
    mutationFn: (data: any) => authApi.createAddress(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['checkout-addresses'] });
    },
  });

  // 7. Xác thực form và tiến hành tiếp tục
  const handleContinue = async () => {
    const isStepValid = await trigger([
      'shippingAddress.receiverName',
      'shippingAddress.receiverPhone',
      'shippingAddress.province',
      'shippingAddress.district',
      'shippingAddress.ward',
      'shippingAddress.detailAddress',
    ]);

    if (isStepValid) {
      const formVals = getValues();

      // Nếu là địa chỉ mới điền tay, tự động lưu vào Sổ địa chỉ người dùng
      if (selectedSavedAddressId === 'NEW') {
        try {
          await saveAddressMutation.mutateAsync({
            receiverName: formVals.shippingAddress?.receiverName || '',
            phoneNumber: formVals.shippingAddress?.receiverPhone || '',
            province: formVals.shippingAddress?.province || '',
            district: formVals.shippingAddress?.district || '',
            ward: formVals.shippingAddress?.ward || '',
            detailAddress: formVals.shippingAddress?.detailAddress || '',
            isDefault: addresses.length === 0,
          });
        } catch (e) {
          console.error('Không thể lưu địa chỉ vào DB:', e);
        }
      }

      onNext();
    }
  };

  if (isLoading) {
    return (
      <div className="py-12 text-center text-sm text-neutral-500">
        <Loader2 className="mx-auto size-5 animate-spin text-neutral-400 mb-2" />
        Đang tải thông tin giao hàng...
      </div>
    );
  }

  return (
    <div className="mt-6 space-y-6 animate-in fade-in duration-300">
      
      {/* =========================================================================
          KHỐI 1: KHUNG CHỌN ĐỊA CHỈ ĐÃ LƯU (STYLE SHOPEE / LAZADA CAO CẤP)
      ========================================================================= */}
      {addresses.length > 0 && (
        <div className="space-y-2">
          <label className="text-xs font-bold uppercase tracking-wider text-neutral-600 flex items-center justify-between">
            <span className="flex items-center gap-1.5">
              <MapPin className="size-3.5 text-neutral-700" />
              Sổ địa chỉ đã lưu ({addresses.length})
            </span>
            <span className="text-[11px] text-neutral-400 lowercase font-normal">
              (bấm để xem & đổi địa chỉ)
            </span>
          </label>

          <div className="relative rounded-2xl border border-[#EAE6DF] bg-white shadow-xs overflow-hidden transition-all">
            {/* Header / Thanh hiển thị địa chỉ đang chọn */}
            <div
              onClick={() => setIsDropdownOpen(!isDropdownOpen)}
              className="p-4 flex items-center justify-between cursor-pointer bg-white hover:bg-neutral-50/70 transition"
            >
              {selectedSavedAddressId !== 'NEW' && selectedAddress ? (
                <div className="flex items-start gap-3 min-w-0 flex-1">
                  <div className="mt-1 size-8 rounded-xl bg-[#84EA53]/20 flex items-center justify-center text-[#1D1F20] shrink-0 border border-[#84EA53]/40">
                    <MapPin className="size-4" />
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="text-sm font-bold text-neutral-900">
                        {selectedAddress.receiverName}
                      </span>
                      <span className="text-neutral-300">|</span>
                      <span className="text-xs font-mono font-medium text-neutral-600">
                        {selectedAddress.phoneNumber}
                      </span>
                      {selectedAddress.isDefault && (
                        <span className="rounded-full bg-[#84EA53]/20 border border-[#84EA53]/60 px-2 py-0.5 text-[10px] font-bold text-[#1D1F20]">
                          Mặc định
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-neutral-700 mt-1 font-medium truncate">
                      {selectedAddress.detailAddress}
                    </p>
                    <p className="text-[11px] text-neutral-400 mt-0.5 truncate">
                      Phường/Xã: {selectedAddress.ward} • Quận/Huyện: {selectedAddress.district} • Tỉnh/TP: {selectedAddress.province}
                    </p>
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-3">
                  <div className="size-8 rounded-xl bg-neutral-100 flex items-center justify-center text-neutral-500 shrink-0">
                    <Plus className="size-4" />
                  </div>
                  <div>
                    <span className="text-sm font-semibold text-neutral-900 block">
                      Đang nhập địa chỉ mới
                    </span>
                    <span className="text-xs text-neutral-400 block">
                      Điền các thông tin người nhận ở bảng bên dưới
                    </span>
                  </div>
                </div>
              )}

              <div className="flex items-center gap-1.5 ml-3 shrink-0">
                <span className="text-xs font-semibold text-neutral-600 hidden sm:inline">
                  {isDropdownOpen ? 'Thu gọn' : 'Thay đổi'}
                </span>
                <ChevronDown
                  className={`size-4 text-neutral-400 transition-transform duration-200 ${
                    isDropdownOpen ? 'rotate-180' : ''
                  }`}
                />
              </div>
            </div>

            {/* Menu danh sách sổ địa chỉ khi bấm mở ra */}
            {isDropdownOpen && (
              <div className="border-t border-[#EAE6DF] bg-neutral-50/50 divide-y divide-[#EAE6DF] max-h-80 overflow-y-auto">
                {addresses.map((addr) => {
                  const isCurrent = selectedSavedAddressId === addr.id;
                  return (
                    <div
                      key={addr.id}
                      onClick={() => {
                        handleSelectAddress(addr.id);
                        setIsDropdownOpen(false);
                      }}
                      className={`p-4 flex items-start justify-between gap-3 cursor-pointer transition hover:bg-white ${
                        isCurrent ? 'bg-white/90 border-l-4 border-l-[#1D1F20]' : 'border-l-4 border-l-transparent'
                      }`}
                    >
                      {/* Check radio indicator */}
                      <div className="pt-1 shrink-0">
                        <div
                          className={`size-4 rounded-full border flex items-center justify-center ${
                            isCurrent
                              ? 'border-[#84EA53] bg-[#84EA53]'
                              : 'border-[#EAE6DF] bg-white'
                          }`}
                        >
                          {isCurrent && <Check className="size-2.5 text-neutral-900 stroke-[3]" />}
                        </div>
                      </div>

                      {/* Thông tin chi tiết từng địa chỉ */}
                      <div className="min-w-0 flex-1 space-y-1">
                        <div className="flex items-center gap-2 flex-wrap">
                          <span className="text-xs font-bold text-neutral-900">
                            {addr.receiverName}
                          </span>
                          <span className="text-xs text-neutral-500 font-mono">
                            ({addr.phoneNumber})
                          </span>
                          {addr.isDefault && (
                            <span className="rounded bg-neutral-100 border border-[#EAE6DF] px-1.5 py-0.2 text-[9px] font-semibold text-neutral-600">
                              Mặc định
                            </span>
                          )}
                        </div>

                        <div className="text-xs text-neutral-800">
                          <span className="font-semibold text-neutral-500">Đường: </span>
                          {addr.detailAddress}
                        </div>

                        <div className="text-[11px] text-neutral-500">
                          <span className="font-semibold text-neutral-400">Khu vực: </span>
                          {addr.ward}, {addr.district}, {addr.province}
                        </div>
                      </div>

                      {/* Icon Xóa địa chỉ */}
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          if (confirm(`Bạn có chắc muốn xóa địa chỉ của "${addr.receiverName}"?`)) {
                            deleteAddressMutation.mutate(addr.id);
                          }
                        }}
                        disabled={deleteAddressMutation.isPending}
                        className="p-2 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition shrink-0"
                        title="Xóa địa chỉ này"
                      >
                        {deleteAddressMutation.isPending && deleteAddressMutation.variables === addr.id ? (
                          <Loader2 className="size-4 animate-spin" />
                        ) : (
                          <Trash2 className="size-4" />
                        )}
                      </button>
                    </div>
                  );
                })}

                {/* Dòng thêm địa chỉ mới ở đáy menu */}
                <div
                  onClick={() => {
                    handleSelectAddress('NEW');
                    setIsDropdownOpen(false);
                  }}
                  className="p-3.5 px-4 flex items-center gap-2.5 text-xs font-bold text-[#1D1F20] hover:bg-white transition cursor-pointer bg-neutral-100/50"
                >
                  <Plus className="size-4 text-[#84EA53]" />
                  <span>+ Nhập địa chỉ giao hàng mới khác</span>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* =========================================================================
          KHỐI 2: FORM CHI TIẾT ĐỊA CHỈ GIAO HÀNG (AUTO-FILL HOẶC TỰ ĐIỀN)
      ========================================================================= */}
      <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 sm:p-6 shadow-xs space-y-5">
        <div className="pb-3 border-b border-[#EAE6DF] flex items-center justify-between">
          <h4 className="text-sm font-bold text-neutral-900 flex items-center gap-2">
            <Building2 className="size-4 text-neutral-700" />
            Điền thông tin giao hàng
          </h4>

          {selectedSavedAddressId !== 'NEW' ? (
            <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full">
              Đang chọn từ sổ địa chỉ
            </span>
          ) : (
            <span className="text-[11px] font-semibold text-neutral-500 bg-neutral-100 px-2 py-0.5 rounded-full">
              Địa chỉ mới (sẽ được lưu tự động)
            </span>
          )}
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          {/* Họ tên người nhận */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600 flex items-center gap-1.5">
              <User className="size-3 text-neutral-400" />
              Họ và tên người nhận *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.receiverName')}
              placeholder="Ví dụ: Nguyễn Văn A"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.receiverName ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.receiverName && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.receiverName.message}</p>
            )}
          </div>

          {/* Số điện thoại */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600 flex items-center gap-1.5">
              <Phone className="size-3 text-neutral-400" />
              Số điện thoại người nhận *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.receiverPhone')}
              placeholder="Ví dụ: 0912345678"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.receiverPhone ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.receiverPhone && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.receiverPhone.message}</p>
            )}
          </div>

          {/* Tỉnh / Thành phố */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Tỉnh / Thành phố *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.province')}
              placeholder="Ví dụ: TP. Hồ Chí Minh"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.province ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.province && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.province.message}</p>
            )}
          </div>

          {/* Quận / Huyện */}
          <div className="space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Quận / Huyện *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.district')}
              placeholder="Ví dụ: Quận 1"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.district ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.district && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.district.message}</p>
            )}
          </div>

          {/* Phường / Xã */}
          <div className="sm:col-span-2 space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Phường / Xã *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.ward')}
              placeholder="Ví dụ: Phường Bến Nghé"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.ward ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.ward && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.ward.message}</p>
            )}
          </div>

          {/* Địa chỉ chi tiết */}
          <div className="sm:col-span-2 space-y-1">
            <label className="text-xs font-semibold text-neutral-600">
              Địa chỉ chi tiết (Số nhà, tên tòa nhà, tên đường...) *
            </label>
            <input
              {...registerWithDetectEdit('shippingAddress.detailAddress')}
              placeholder="Ví dụ: 123 Đường Lê Lợi"
              className={`w-full rounded-xl border px-3.5 py-2.5 text-sm outline-none transition focus:border-[#84EA53] bg-neutral-50/50 focus:bg-white ${
                errors.shippingAddress?.detailAddress ? 'border-red-500 bg-red-50/20' : 'border-[#EAE6DF]'
              }`}
            />
            {errors.shippingAddress?.detailAddress && (
              <p className="text-[11px] text-red-500 mt-1">{errors.shippingAddress.detailAddress.message}</p>
            )}
          </div>
        </div>
      </div>

      {/* =========================================================================
          KHỐI 3: GHI CHÚ ĐƠN HÀNG (TÙY CHỌN)
      ========================================================================= */}
      <div className="space-y-1.5">
        <label className="text-xs font-bold uppercase tracking-wider text-neutral-600">
          Ghi chú đơn hàng (Tùy chọn)
        </label>
        <textarea
          {...register('customerNote')}
          placeholder="Ví dụ: Giao hàng trong giờ hành chính, gọi trước khi đến..."
          rows={3}
          className="w-full rounded-2xl border border-[#EAE6DF] bg-white px-4 py-3 text-sm outline-none focus:border-[#84EA53] resize-none transition shadow-xs"
        />
      </div>

      {/* Nút hành động chính */}
      <button
        type="button"
        onClick={handleContinue}
        disabled={saveAddressMutation.isPending}
        className="w-full flex items-center justify-center gap-2 rounded-2xl bg-[#1D1F20] hover:bg-black py-4 text-sm font-bold !text-white [&_*]:!text-white transition-all shadow-sm active:scale-[0.99] cursor-pointer"
      >
        {saveAddressMutation.isPending ? (
          <>
            <Loader2 className="size-4 animate-spin !text-white [&_*]:!text-white" />
            <span>Đang lưu địa chỉ...</span>
          </>
        ) : (
          <span>Lưu địa chỉ & Tiếp tục thanh toán</span>
        )}
      </button>
    </div>
  );
}
```

# checkout\schemas.ts

| Property | Value |
|----------|-------|
| Path | `checkout\schemas.ts` |
| Size | 2,159 bytes |
| Extension | `.ts` |
| Modified | 2026-10-06 21:26:07.921376 |

```typescript
import { z } from 'zod';
import { PaymentMethod } from '@/types';

// Regex kiểm tra số điện thoại chuẩn Việt Nam
const phoneRegex = /^(84|0)[35789]\d{8}$/;

export const shippingAddressSchema = z.object({
  receiverName: z
    .string()
    .trim()
    .min(2, 'Tên người nhận phải có ít nhất 2 ký tự')
    .max(100, 'Tên quá dài, tối đa 100 ký tự'),
  receiverPhone: z
    .string()
    .trim()
    .regex(phoneRegex, 'Số điện thoại không hợp lệ (VD: 0912345678)'),
  province: z.string().trim().min(1, 'Vui lòng chọn Tỉnh/Thành phố'),
  district: z.string().trim().min(1, 'Vui lòng chọn Quận/Huyện'),
  ward: z.string().trim().min(1, 'Vui lòng chọn Phường/Xã'),
  detailAddress: z
    .string()
    .trim()
    .min(5, 'Địa chỉ chi tiết phải có ít nhất 5 ký tự')
    .max(500, 'Địa chỉ quá dài'),
});

export const checkoutSchema = z
  .object({
    // Nếu truyền danh sách ID vào thì chỉ thanh toán những món đó, nếu không truyền sẽ thanh toán toàn bộ giỏ
    cartItemIds: z.array(z.number()).optional(),

    // Khách chọn địa chỉ đã lưu hoặc nhập địa chỉ mới
    shippingAddressId: z.number().optional(),
    shippingAddress: shippingAddressSchema.optional(),

    paymentMethod: z.enum(['COD', 'VNPAY', 'MOMO', 'BANK_TRANSFER'], {
      required_error: 'Vui lòng chọn phương thức thanh toán',
      invalid_type_error: 'Phương thức thanh toán không hợp lệ',
    }),
    
    shippingMethod: z.string().optional(),
    customerNote: z.string().max(1000, 'Ghi chú không được quá 1000 ký tự').optional(),
    voucherCode: z.string().optional().nullable(),
    pointUsed: z.number().min(0).default(0).optional(),
  })
  .refine((data) => data.shippingAddressId || data.shippingAddress, {
    message: 'Vui lòng chọn địa chỉ đã lưu hoặc nhập địa chỉ giao hàng mới',
    path: ['shippingAddress'], // Báo lỗi tại field shippingAddress
  });

export type CheckoutFormValues = z.infer<typeof checkoutSchema>;

```

# flash-sale\api.ts

| Property | Value |
|----------|-------|
| Path | `flash-sale\api.ts` |
| Size | 39 bytes |
| Extension | `.ts` |
| Modified | 2026-10-05 20:42:08.940640 |

```typescript
export * from '@/api/flash-sale.api';

```

# flash-sale\components\countdown.tsx

| Property | Value |
|----------|-------|
| Path | `flash-sale\components\countdown.tsx` |
| Size | 5,428 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 19:27:56.010738 |

```tsx
'use client';

import { useEffect, useState } from 'react';
import { Clock, Flame } from 'lucide-react';
import { cn } from '@/lib/utils';

interface CountdownProps {
  targetTime: string | Date;
  startTime?: string | Date;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
  showLabels?: boolean;
  onEnd?: () => void;
}

interface TimeRemaining {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
  isEnded: boolean;
  isUpcoming: boolean;
}

function calculateTimeRemaining(target: string | Date, start?: string | Date): TimeRemaining {
  const now = new Date().getTime();
  const startTime = start ? new Date(start).getTime() : 0;
  const targetTime = new Date(target).getTime();

  if (start && now < startTime) {
    const diff = startTime - now;
    return {
      days: Math.floor(diff / (1000 * 60 * 60 * 24)),
      hours: Math.floor((diff / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((diff / 1000 / 60) % 60),
      seconds: Math.floor((diff / 1000) % 60),
      isEnded: false,
      isUpcoming: true,
    };
  }

  const diff = targetTime - now;

  if (diff <= 0) {
    return {
      days: 0,
      hours: 0,
      minutes: 0,
      seconds: 0,
      isEnded: true,
      isUpcoming: false,
    };
  }

  return {
    days: Math.floor(diff / (1000 * 60 * 60 * 24)),
    hours: Math.floor((diff / (1000 * 60 * 60)) % 24),
    minutes: Math.floor((diff / 1000 / 60) % 60),
    seconds: Math.floor((diff / 1000) % 60),
    isEnded: false,
    isUpcoming: false,
  };
}

export function FlashSaleCountdown({
  targetTime,
  startTime,
  className,
  size = 'md',
  showLabels = true,
  onEnd,
}: CountdownProps) {
  const [time, setTime] = useState<TimeRemaining>(() =>
    calculateTimeRemaining(targetTime, startTime)
  );

  useEffect(() => {
    const timer = setInterval(() => {
      const nextTime = calculateTimeRemaining(targetTime, startTime);
      setTime(nextTime);

      if (nextTime.isEnded) {
        clearInterval(timer);
        onEnd?.();
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [targetTime, startTime, onEnd]);

  if (time.isEnded) {
    return (
      <div className={cn('inline-flex items-center gap-1.5 font-semibold text-neutral-500', className)}>
        <Clock className="size-4" />
        <span>Chiến dịch đã kết thúc</span>
      </div>
    );
  }

  const pad = (n: number) => n.toString().padStart(2, '0');

  // Đã thêm min-w để các ô số không bị bóp méo, căn giữa số bên trong
  const sizeClasses = {
    sm: {
      box: 'min-w-[28px] px-1.5 py-1 text-xs font-mono font-bold rounded flex items-center justify-center',
      colon: 'text-xs font-bold mx-1 text-neutral-400',
      label: 'text-[9px] uppercase tracking-wider text-neutral-500 mt-1',
    },
    md: {
      box: 'min-w-[36px] px-2 py-1.5 text-sm font-mono font-extrabold rounded-md flex items-center justify-center',
      colon: 'text-sm font-bold mx-1 text-neutral-400',
      label: 'text-[10px] uppercase tracking-wider text-neutral-500 mt-1',
    },
    lg: {
      box: 'min-w-[44px] px-2.5 py-2 text-lg font-mono font-black rounded-lg flex items-center justify-center',
      colon: 'text-base font-bold mx-1.5 text-neutral-400',
      label: 'text-xs uppercase tracking-wider text-neutral-500 mt-1',
    },
  }[size];

  return (
    <div className={cn('flex items-center gap-2 sm:gap-3', className)}>
      <div className="flex items-center text-red-600 font-bold animate-pulse">
        <Flame className={cn(size === 'lg' ? 'size-5' : size === 'sm' ? 'size-3.5' : 'size-4')} />
        <span className="text-xs uppercase ml-1 tracking-wider whitespace-nowrap">
          {time.isUpcoming ? 'Bắt đầu sau' : 'Kết thúc sau'}
        </span>
      </div>

      <div className="flex items-center">
        {time.days > 0 && (
          <>
            <div className="flex flex-col items-center">
              <span className={cn('bg-[#1D1F20] text-white shadow-sm', sizeClasses.box)}>
                {pad(time.days)}
              </span>
              {showLabels && <span className={sizeClasses.label}>ngày</span>}
            </div>
            <span className={sizeClasses.colon}>:</span>
          </>
        )}

        <div className="flex flex-col items-center">
          <span className={cn('bg-[#1D1F20] text-white shadow-sm', sizeClasses.box)}>
            {pad(time.hours)}
          </span>
          {showLabels && <span className={sizeClasses.label}>giờ</span>}
        </div>

        <span className={sizeClasses.colon}>:</span>

        <div className="flex flex-col items-center">
          <span className={cn('bg-[#1D1F20] text-white shadow-sm', sizeClasses.box)}>
            {pad(time.minutes)}
          </span>
          {showLabels && <span className={sizeClasses.label}>phút</span>}
        </div>

        <span className={sizeClasses.colon}>:</span>

        <div className="flex flex-col items-center">
          <span className={cn('bg-red-600 text-white shadow-sm shadow-red-500/20', sizeClasses.box)}>
            {pad(time.seconds)}
          </span>
          {showLabels && <span className={cn(sizeClasses.label, '!text-red-600 font-semibold')}>giây</span>}
        </div>
      </div>
    </div>
  );
}
```

# flash-sale\components\flash-sale-card.tsx

| Property | Value |
|----------|-------|
| Path | `flash-sale\components\flash-sale-card.tsx` |
| Size | 7,530 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 22:28:21.797448 |

```tsx
'use client';

import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Flame, ShoppingCart, Check, Loader2 } from 'lucide-react';
import { useState } from 'react';
import { FlashSaleItem } from '@/types/inventory-sale';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { cartApi } from '@/api/cart.api';

interface FlashSaleCardProps {
  item: FlashSaleItem;
  imageUrl?: string;
  className?: string;
}

export function FlashSaleCard({ item, imageUrl, className }: FlashSaleCardProps) {
  const router = useRouter();
  
  // Vẫn giữ nguyên các state gốc của bạn
  const [adding, setAdding] = useState(false);
  const [added, setAdded] = useState(false);

  const productName = item.variant?.product?.defaultName || `Biến thể #${item.variantId}`;
  const sku = item.variant?.sku || `SKU-${item.variantId}`;
  
  // Tự động lấy ảnh: Ưu tiên ảnh truyền vào -> Ảnh biến thể -> Ảnh sản phẩm gốc -> Fallback
  const resolvedImage = imageUrl || item.variant?.imageUrl || item.variant?.product?.thumbnailUrl || 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80';
  
  // Khởi tạo link URL chi tiết sản phẩm
  const productId = item.variant?.productId || '';
  const productHref = `/products/${productId}?variantId=${item.variantId}`;

  const flashPrice = Number(item.flashSalePrice);
  const originalPrice = Number(item.variant?.sellingPrice || item.variant?.originalPrice || flashPrice * 1.3);
  
  const discountPercent = originalPrice > flashPrice 
    ? Math.round(((originalPrice - flashPrice) / originalPrice) * 100) 
    : 0;

  const soldCount = item.soldQuantity || 0;
  const totalCount = item.promoQuantity || 1;
  const progressPercent = Math.min(Math.round((soldCount / totalCount) * 100), 100);
  const isSoldOut = soldCount >= totalCount;

  const formatPrice = (val: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
  };

  // Vẫn giữ nguyên hàm gốc đề phòng sau này bạn muốn dùng lại chức năng Thêm vào giỏ
  const handleAddToCart = async () => {
    if (isSoldOut || adding) return;
    setAdding(true);
    try {
      await cartApi.addToCart({
        variantId: item.variantId,
        quantity: 1,
      });
      setAdded(true);
      setTimeout(() => setAdded(false), 2000);
    } catch (err) {
      console.error('Không thể thêm sản phẩm vào giỏ hàng:', err);
    } finally {
      setAdding(false);
    }
  };

  // Hàm chuyển hướng trang chi tiết
  const handleBuyNow = () => {
    if (isSoldOut || !productId) return;
    router.push(productHref);
  };

  return (
    <article
      className={cn(
        'group relative flex flex-col justify-between overflow-hidden rounded-2xl border border-zinc-200/80 bg-white p-3 shadow-xs transition-all duration-300 hover:-translate-y-1 hover:shadow-xl dark:border-zinc-800 dark:bg-zinc-950',
        className
      )}
    >
      <div>
        {/* Image & Discount Badge Container (Đã bọc thẻ Link) */}
        <Link href={productHref} className="block relative aspect-square w-full overflow-hidden rounded-xl bg-zinc-100 dark:bg-zinc-900">
          {resolvedImage && (
            <Image
              src={resolvedImage}
              alt={productName}
              fill
              sizes="(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 25vw"
              className="object-cover transition-transform duration-500 group-hover:scale-105"
            />
          )}

          {/* Flash Sale Tag */}
          <div className="absolute left-2.5 top-2.5 flex items-center gap-1 rounded-full bg-red-600 px-2.5 py-1 text-[11px] font-black uppercase tracking-wider !text-white [&_*]:!text-white shadow-md shadow-red-600/30">
            <Flame className="size-3.5 fill-amber-300 text-amber-300 animate-pulse" />
            <span>Flash Sale</span>
          </div>

          {/* Discount Percentage Badge */}
          {discountPercent > 0 && (
            <div className="absolute right-2.5 top-2.5 rounded-full bg-amber-400 px-2 py-0.5 text-xs font-black text-zinc-950 shadow-sm">
              -{discountPercent}%
            </div>
          )}

          {/* Sold Out Overlay */}
          {isSoldOut && (
            <div className="absolute inset-0 flex items-center justify-center bg-zinc-950/70 backdrop-blur-[2px]">
              <span className="rounded-full bg-red-600/90 px-4 py-1.5 text-xs font-bold uppercase tracking-widest !text-white [&_*]:!text-white shadow-lg">
                Đã hết hàng
              </span>
            </div>
          )}
        </Link>

        {/* Content */}
        <div className="mt-3 flex flex-col gap-1.5">
          <div className="text-[11px] font-semibold text-zinc-400 dark:text-zinc-500">
            SKU: {sku}
          </div>
          
          {/* Tiêu đề sản phẩm (Đã bọc thẻ Link) */}
          <Link href={productHref}>
            <h3 className="line-clamp-2 text-sm font-semibold text-zinc-900 transition-colors group-hover:text-red-600 dark:text-zinc-100 dark:group-hover:text-red-400">
              {productName}
            </h3>
          </Link>

          {/* Pricing */}
          <div className="mt-1 flex items-baseline gap-2">
            <span className="text-lg font-extrabold text-red-600 dark:text-red-500">
              {formatPrice(flashPrice)}
            </span>
            {originalPrice > flashPrice && (
              <span className="text-xs font-medium text-zinc-400 line-through dark:text-zinc-500">
                {formatPrice(originalPrice)}
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Progress & Cart Action */}
      <div className="mt-4 flex flex-col gap-2.5">
        {/* Quantity Sold Progress Bar */}
        <div className="relative overflow-hidden rounded-full bg-zinc-100 p-1 dark:bg-zinc-800">
          <div
            className="h-3.5 rounded-full bg-gradient-to-r from-amber-500 to-red-600 transition-all duration-500"
            style={{ width: `${Math.max(progressPercent, 5)}%` }}
          />
          <span className="absolute inset-0 flex items-center justify-center text-[10px] font-black uppercase tracking-wider text-zinc-800 dark:text-zinc-200">
            {isSoldOut ? 'Đã bán hết' : `Đã bán ${soldCount}/${totalCount}`}
          </span>
        </div>

        {/* Nút bấm xử lý theo yêu cầu mới (Chuyển trang) */}
        <Button
          onClick={handleBuyNow}
          disabled={isSoldOut || adding}
          className={cn(
            'w-full font-bold transition-all shadow-sm',
            !isSoldOut && !added && 'bg-red-600 !text-white [&_*]:!text-white hover:bg-red-700 dark:bg-red-600 dark:hover:bg-red-700'
          )}
        >
          {adding ? (
            <>
              <Loader2 className="mr-1.5 size-4 animate-spin" />
              Đang tải...
            </>
          ) : isSoldOut ? (
            'Tạm hết hàng'
          ) : (
            <>
              <ShoppingCart className="mr-1.5 size-4" />
              Mua ngay
            </>
          )}
        </Button>
      </div>
    </article>
  );
}
```

# flash-sale\components\flash-sale-section.tsx

| Property | Value |
|----------|-------|
| Path | `flash-sale\components\flash-sale-section.tsx` |
| Size | 3,631 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 19:35:12.270094 |

```tsx
'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { Flame, ArrowRight, Sparkles } from 'lucide-react';
import { FlashSaleCampaign } from '@/types/inventory-sale';
import { flashSaleApi } from '@/api/flash-sale.api';
import { FlashSaleCountdown } from './countdown';
import { FlashSaleCard } from './flash-sale-card';

export function FlashSaleSection() {
  const [campaigns, setCampaigns] = useState<FlashSaleCampaign[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchActive() {
      try {
        const res = await flashSaleApi.getActiveCampaigns();
        if (res.data) {
          setCampaigns(res.data);
        }
      } catch (err) {
        console.error('Failed to fetch active flash sale campaigns:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchActive();
  }, []);

  if (loading) {
    return (
      <div className="mx-auto w-full max-w-[1440px] px-4 py-8 sm:px-8 lg:px-12">
        <div className="h-48 animate-pulse rounded-3xl bg-neutral-100" />
      </div>
    );
  }

  if (!campaigns.length) return null;

  const currentCampaign = campaigns[0];
  const items = currentCampaign.items || [];

  return (
    <section className="mx-auto w-full max-w-[1440px] px-4 py-8 sm:px-8 lg:px-12">
      <div className="overflow-hidden rounded-3xl border border-red-100 bg-gradient-to-b from-red-50 to-white p-6 shadow-sm sm:p-8">
        
        {/* Header Bar */}
        <div className="flex flex-col gap-6 sm:flex-row sm:items-center sm:justify-between border-b border-red-100 pb-6">
          <div className="flex items-center gap-4">
            <div className="flex size-12 shrink-0 items-center justify-center rounded-2xl bg-red-600 text-white shadow-lg shadow-red-600/30">
              <Flame className="size-7 fill-amber-300 text-amber-300 animate-bounce" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-black uppercase tracking-widest text-red-600">
                  Ưu đãi giới hạn
                </span>
                <span className="flex items-center gap-1 rounded-full bg-red-100 px-2 py-0.5 text-[10px] font-bold text-red-700">
                  <Sparkles className="size-3" /> HOT SALE
                </span>
              </div>
              <h2 className="text-2xl font-black tracking-tight text-neutral-900 sm:text-3xl mt-1">
                {currentCampaign.name}
              </h2>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-5 sm:justify-end">
            <FlashSaleCountdown
              targetTime={currentCampaign.endTime}
              startTime={currentCampaign.startTime}
              size="md"
            />
            <Link
              href="/flash-sale"
              className="inline-flex shrink-0 items-center justify-center gap-2 rounded-full bg-[#1D1F20] !text-white [&_*]:!text-white px-5 py-2.5 text-xs font-bold transition-colors hover:bg-red-600 shadow-sm"
            >
              Xem tất cả <ArrowRight className="size-4" />
            </Link>
          </div>
        </div>

        {/* Item Grid */}
        <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          {items.slice(0, 4).map((item) => (
            <FlashSaleCard key={item.id || item.variantId} item={item} />
          ))}
        </div>
      </div>
    </section>
  );
}
```

# flash-sale\context.tsx

| Property | Value |
|----------|-------|
| Path | `flash-sale\context.tsx` |
| Size | 1,699 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-06 00:08:13.658305 |

```tsx
'use client';

import React, { createContext, useContext, useState, ReactNode } from 'react';
import { FlashSaleCampaign, FlashSaleItem } from '@/types/inventory-sale';

interface FlashSaleContextType {
  campaign: FlashSaleCampaign | null;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  selectedItem: FlashSaleItem | null;
  setSelectedItem: (item: FlashSaleItem | null) => void;
  filteredItems: FlashSaleItem[];
}

const FlashSaleContext = createContext<FlashSaleContextType | undefined>(undefined);

export function FlashSaleProvider({
  campaign,
  children,
}: {
  campaign: FlashSaleCampaign | null;
  children: ReactNode;
}) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedItem, setSelectedItem] = useState<FlashSaleItem | null>(null);

  const rawItems = campaign?.items || [];
  const filteredItems = rawItems.filter((item) => {
    if (!searchQuery.trim()) return true;
    const name = item.variant?.product?.defaultName?.toLowerCase() || '';
    const sku = item.variant?.sku?.toLowerCase() || '';
    const q = searchQuery.toLowerCase();
    return name.includes(q) || sku.includes(q);
  });

  return (
    <FlashSaleContext.Provider
      value={{
        campaign,
        searchQuery,
        setSearchQuery,
        selectedItem,
        setSelectedItem,
        filteredItems,
      }}
    >
      {children}
    </FlashSaleContext.Provider>
  );
}

export function useFlashSaleContext() {
  const context = useContext(FlashSaleContext);
  if (!context) {
    throw new Error('useFlashSaleContext phải được dùng bên trong FlashSaleProvider');
  }
  return context;
}

```

# flash-sale\store.ts

| Property | Value |
|----------|-------|
| Path | `flash-sale\store.ts` |
| Size | 2,032 bytes |
| Extension | `.ts` |
| Modified | 2026-10-11 16:52:36.569986 |

```typescript
import { create } from 'zustand';
import { FlashSaleCampaign, FlashSaleItem } from '@/types/inventory-sale';
import { flashSaleApi } from '@/api/flash-sale.api';

interface FlashSaleState {
  activeCampaigns: FlashSaleCampaign[];
  selectedCampaignId: number | null;
  loading: boolean;
  error: string | null;

  // Actions
  fetchActiveCampaigns: () => Promise<void>;
  setSelectedCampaignId: (id: number | null) => void;
  updateItemSoldQuantity: (variantId: number, soldQuantity: number) => void;
  getActiveCampaign: () => FlashSaleCampaign | undefined;
}

export const useFlashSaleStore = create<FlashSaleState>((set, get) => ({
  activeCampaigns: [],
  selectedCampaignId: null,
  loading: false,
  error: null,

  fetchActiveCampaigns: async () => {
    set({ loading: true, error: null });
    try {
      const res = await flashSaleApi.getActiveCampaigns();
      const campaigns = res.data || [];
      set({
        activeCampaigns: campaigns,
        selectedCampaignId: campaigns.length > 0 ? campaigns[0].id : null,
        loading: false,
      });
    } catch (err: any) {
      set({
        error: err?.response?.data?.message || 'Không thể tải danh sách Flash Sale',
        loading: false,
      });
    }
  },

  setSelectedCampaignId: (id: number | null) => {
    set({ selectedCampaignId: id });
  },

  updateItemSoldQuantity: (variantId: number, soldQuantity: number) => {
    set((state) => ({
      activeCampaigns: state.activeCampaigns.map((campaign) => ({
        ...campaign,
        items: campaign.items?.map((item) =>
          item.variantId === variantId ? { ...item, soldQuantity } : item
        ),
      })),
    }));
  },

getActiveCampaign: () => {
    const { activeCampaigns, selectedCampaignId } = get();
    if (activeCampaigns.length === 0) return undefined;
    if (!selectedCampaignId) return activeCampaigns[0];
    return activeCampaigns.find((c) => c.id === selectedCampaignId) || activeCampaigns[0];
  },
}));

```

# flash-sale\types.ts

| Property | Value |
|----------|-------|
| Path | `flash-sale\types.ts` |
| Size | 200 bytes |
| Extension | `.ts` |
| Modified | 2026-10-05 20:42:08.949732 |

```typescript
export * from '@/types/inventory-sale';

export interface CountdownTime {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
  isEnded: boolean;
  isUpcoming: boolean;
}

```

# loyalty-engage\api.ts

| Property | Value |
|----------|-------|
| Path | `loyalty-engage\api.ts` |
| Size | 9,823 bytes |
| Extension | `.ts` |
| Modified | 2026-10-13 18:29:27.393873 |

```typescript
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import type { ChatMessage, ChatSession, ProductReview } from '@/types/loyalty-engage';

export type DiscountType = 'PERCENTAGE' | 'FIXED_AMOUNT';

export interface VoucherEntity {
  id: number;
  code: string;
  discountType: DiscountType;
  discountValue: number | string;
  minOrderValue: number | string;
  maxDiscountAmt?: number | string | null;
  totalUsageLimit: number;
  usedCount: number;
  userUsageLimit: number;
  startDate: string;
  endDate: string;
  isActive: boolean;
}

export interface VoucherValidationData {
  code: string;
  discountType: DiscountType;
  discountAmount: number;
  finalTotal: number;
  voucher: VoucherEntity;
}

export interface VoucherValidationResponse {
  statusCode: number;
  message: string;
  data: VoucherValidationData;
}

export interface PointBalanceData {
  rewardPoints: number;
}

export interface PointBalanceResponse {
  statusCode: number;
  data: PointBalanceData;
}

export interface RedeemPointsPayload {
  orderId: number;
  pointsToRedeem: number;
}

export interface RedeemPointsResponse {
  statusCode: number;
  message: string;
  discountAmount: number;
  currentPoint: number;
  transaction?: unknown;
}

export interface ReviewAuthor {
  id: number;
  fullName: string;
  avatarUrl: string | null;
}

export interface ReviewMediaItem {
  id?: number;
  url: string;
  type: string;
}

export interface ProductReviewItem {
  id: number;
  productId: number;
  orderId?: number | null;
  rating: number;
  content: string;
  shopReply: string | null;
  isHidden?: boolean;
  createdAt: string;
  updatedAt?: string;
  user: ReviewAuthor;
  medias: ReviewMediaItem[];
}

export interface RatingStats {
  averageRating: number;
  totalReviews: number;
}

export interface PaginationMeta {
  page: number;
  limit: number;
  total: number;
  totalPages: number;
}

export interface ProductReviewsResponse {
  statusCode: number;
  data: ProductReviewItem[];
  ratingStats: RatingStats;
  pagination: PaginationMeta;
}

export interface CreateReviewPayload {
  productId: number;
  orderId?: number;
  rating: number;
  content: string;
  mediaUrls?: ReviewMediaItem[];
}

export interface CreateReviewResponse {
  statusCode: number;
  message: string;
  data: ProductReviewItem;
}

export interface SocketHeartbeatResponse {
  statusCode: number;
  message: string;
  data?: {
    socketId: string;
    userId: number;
    extended: boolean;
  };
}

export interface ChatMessageItem {
  id?: number;
  senderId?: number;
  receiverId?: number;
  content: string;
  attachmentUrl?: string | null;
  createdAt?: string;
  sender?: {
    id: number;
    fullName: string;
    avatarUrl?: string | null;
  };
  isSelf?: boolean;
}

export interface ChatHistoryResponse {
  statusCode: number;
  data: ChatMessage[];
  pagination: {
    page: number;
    limit: number;
    nextCursor: number | null;
    hasMore: boolean;
  };
}

export interface ChatSessionResponse {
  statusCode: number;
  data: ChatSession;
}

export interface ChatConversationsResponse {
  statusCode: number;
  data: ChatSession[];
}

export async function validateVoucher(
  code: string,
  orderTotal: number,
): Promise<VoucherValidationResponse> {
  const res = await apiClient.post<VoucherValidationResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}/validate`,
    {
      code: code.trim().toUpperCase(),
      orderTotal,
    },
  );
  return res.data;
}

export async function getPointBalance(): Promise<PointBalanceResponse> {
  const res = await apiClient.get<PointBalanceResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/balance`,
  );
  return res.data;
}

export async function redeemPoints(
  orderId: number,
  pointsToRedeem: number,
): Promise<RedeemPointsResponse> {
  const res = await apiClient.post<RedeemPointsResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/redeem`,
    {
      orderId,
      pointsToRedeem,
    },
  );
  return res.data;
}

export async function getReviewsByProduct(params: {
  productId: number;
  page?: number;
  limit?: number;
  rating?: number;
}): Promise<ProductReviewsResponse> {
  const query = new URLSearchParams();
  query.append('productId', params.productId.toString());
  if (params.page) query.append('page', params.page.toString());
  if (params.limit) query.append('limit', params.limit.toString());
  if (params.rating) query.append('rating', params.rating.toString());

  const res = await apiClient.get<ProductReviewsResponse>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS}?${query.toString()}`,
  );
  return res.data;
}

export async function createReview(
  payload: CreateReviewPayload,
): Promise<CreateReviewResponse> {
  const res = await apiClient.post<CreateReviewResponse>(
    API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS,
    payload,
  );
  return res.data;
}

export async function sendSocketHeartbeat(
  socketId: string,
): Promise<SocketHeartbeatResponse> {
  const res = await apiClient.post<SocketHeartbeatResponse>(
    API_ENDPOINTS.CHAT.HEARTBEAT,
    { socketId },
  );
  return res.data;
}

export async function replyReview(
  reviewId: number,
  shopReply: string,
): Promise<{ statusCode: number; data: ProductReview }> {
  const res = await apiClient.post<{ statusCode: number; data: ProductReview }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS}/${reviewId}/reply`,
    { shopReply },
  );
  return res.data;
}

export async function hideReview(
  reviewId: number,
  isHidden: boolean,
): Promise<{ statusCode: number; data: ProductReview }> {
  const res = await apiClient.patch<{ statusCode: number; data: ProductReview }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.REVIEWS}/${reviewId}/hide`,
    { isHidden },
  );
  return res.data;
}

export async function updateVoucher(
  voucherId: number,
  payload: any,
): Promise<{ statusCode: number; data: any }> {
  const res = await apiClient.patch<{ statusCode: number; data: any }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}/${voucherId}`,
    payload,
  );
  return res.data;
}

export async function deleteVoucher(
  voucherId: number,
): Promise<{ statusCode: number; message: string }> {
  const res = await apiClient.delete<{ statusCode: number; message: string }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}/${voucherId}`,
  );
  return res.data;
}

export async function getPointHistory(params?: {
  page?: number;
  limit?: number;
}): Promise<{
  data: any[];
  pagination: { page: number; limit: number; total: number; totalPages: number };
}> {
  const res = await apiClient.get<any>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/history`,
    { params },
  );
  return res.data;
}

export async function earnPoints(payload: {
  orderId: number;
  description?: string;
}): Promise<{ message: string; currentPoint: number; transaction: any }> {
  const res = await apiClient.post<any>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.POINTS}/earn`,
    payload,
  );
  return res.data;
}

export const chatApi = {
  getActiveChat: async (): Promise<ChatSessionResponse> => {
    const res = await apiClient.get<ChatSessionResponse>(
      `${API_ENDPOINTS.CHAT.ACTIVE}/active`,
    );
    return res.data;
  },
  getHistory: async (chatId: number, cursor?: number, limit = 100) => {
    const res = await apiClient.get<ChatHistoryResponse>(
      API_ENDPOINTS.CHAT.HISTORY,
      {
        params: { chatId, limit, cursor },
      },
    );
    return res.data;
  },
  getConversations: async (): Promise<ChatConversationsResponse> => {
    const res = await apiClient.get<ChatConversationsResponse>(
      API_ENDPOINTS.CHAT.CONVERSATIONS,
    );
    return res.data;
  },
  sendMessage: async (
    chatId: number,
    content: string,
  ): Promise<{ statusCode: number; data: ChatMessage }> => {
    const res = await apiClient.post<{ statusCode: number; data: ChatMessage }>(
      API_ENDPOINTS.CHAT.SEND,
      { chatId, content },
    );
    return res.data;
  },
  markAsRead: async (
    chatId: number,
  ): Promise<{ statusCode: number; updatedCount: number }> => {
    const res = await apiClient.post<{
      statusCode: number;
      updatedCount: number;
    }>(API_ENDPOINTS.CHAT.READ, { chatId });
    return res.data;
  },
  closeConversation: async (
    chatId: number,
  ): Promise<{ statusCode: number }> => {
    const res = await apiClient.post<{ statusCode: number }>(
      `${API_ENDPOINTS.CHAT.CONVERSATIONS}/${chatId}/close`,
    );
    return res.data;
  },
  getUserOnlineStatus: async (
    userId: number,
  ): Promise<{ statusCode: number; data: { userId: number; isOnline: boolean } }> => {
    const res = await apiClient.get<{
      statusCode: number;
      data: { userId: number; isOnline: boolean };
    }>(`/loyalty-engage/chat/sessions/${userId}/online`);
    return res.data;
  },
  getUserSessions: async (
    userId: number,
  ): Promise<{
    statusCode: number;
    data: Array<{ socketId: string; userId: number; connectedAt: string }>;
  }> => {
    const res = await apiClient.get<{
      statusCode: number;
      data: Array<{ socketId: string; userId: number; connectedAt: string }>;
    }>(`/loyalty-engage/chat/sessions/${userId}`);
    return res.data;
  },
};

export async function getAvailableVouchers(): Promise<{ data: VoucherEntity[] }> {
  const res = await apiClient.get<{ data: VoucherEntity[] }>(
    `${API_ENDPOINTS.LOYALTY_ENGAGE.VOUCHERS}?isActive=true&limit=50`
  );
  return res.data;
}


```

# loyalty-engage\components\live-chat-widget.tsx

| Property | Value |
|----------|-------|
| Path | `loyalty-engage\components\live-chat-widget.tsx` |
| Size | 8,652 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 00:59:03.233041 |

```tsx
'use client';

import { FormEvent, useCallback, useEffect, useRef, useState } from 'react';
import { MessageCircle, Send, X } from 'lucide-react';
import { useAuth } from '@/components/providers/auth-provider';
import { chatApi } from '@/modules/loyalty-engage/api';
import { useChatSocket } from '@/modules/loyalty-engage/hooks/useChatSocket';
import { useSocket } from '@/components/providers/socket-provider';
import type { ChatMessage } from '@/types/loyalty-engage';

function appendMessage(messages: ChatMessage[], message: ChatMessage) {
  if (messages.some((existing) => existing.id === message.id)) return messages;
  return [...messages, message].sort(
    (left, right) => new Date(left.sendDate).getTime() - new Date(right.sendDate).getTime()
  );
}

export function LiveChatWidget() {
  const { user, isAuthenticated } = useAuth();
  const { isConnected } = useSocket();
  const [isOpen, setIsOpen] = useState(false);
  const [chatId, setChatId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const loadPromiseRef = useRef<Promise<{ chatId: number; messages: ChatMessage[] }> | null>(null);

  useEffect(() => {
    if (!isOpen || !isAuthenticated || chatId !== null) return;

    let cancelled = false;
    if (!loadPromiseRef.current) {
      setLoading(true);
      setError(null);
      loadPromiseRef.current = (async () => {
        const activeResponse = await chatApi.getActiveChat();
        const activeChat = activeResponse.data;
        const historyResponse = await chatApi.getHistory(activeChat.id);
        return { chatId: activeChat.id, messages: historyResponse.data };
      })();
    }

    const loadPromise = loadPromiseRef.current;
    void loadPromise
      .then(({ chatId: loadedChatId, messages: loadedMessages }) => {
        if (cancelled) return;
        setChatId(loadedChatId);
        setMessages(loadedMessages);
        void chatApi.markAsRead(loadedChatId).catch((cause: unknown) => {
          console.error('Không thể đánh dấu tin nhắn đã đọc:', cause);
        });
      })
      .catch((cause: unknown) => {
        console.error('Không thể tải cuộc trò chuyện:', cause);
        if (!cancelled) setError('Không thể tải cuộc trò chuyện. Vui lòng thử lại.');
      })
      .finally(() => {
        if (loadPromiseRef.current === loadPromise) loadPromiseRef.current = null;
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [isOpen, isAuthenticated, chatId]);

  const handleNewMessage = useCallback((message: ChatMessage) => {
    setMessages((current) => appendMessage(current, message));
    if (isOpen && message.senderId !== user?.id) {
      void chatApi.markAsRead(message.chatId).catch((cause: unknown) => {
        console.error('Không thể đánh dấu tin nhắn đã đọc:', cause);
      });
    }
  }, [isOpen, user?.id]);

  useChatSocket(chatId, handleNewMessage);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isOpen]);

  const handleSend = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const content = input.trim();
    if (!content || chatId === null || sending) return;

    setSending(true);
    setError(null);
    try {
      const response = await chatApi.sendMessage(chatId, content);
      setMessages((current) => appendMessage(current, response.data));
      setInput('');
    } catch (cause) {
      console.error('Không thể gửi tin nhắn:', cause);
      setError('Gửi tin nhắn thất bại. Vui lòng thử lại.');
    } finally {
      setSending(false);
    }
  };

  if (!isAuthenticated || user?.role?.code === 'ADMIN' || user?.role?.code === 'STAFF') return null;

  return (
    <div className="fixed bottom-4 right-4 z-50">
      {!isOpen ? (
        <button
          type="button"
          onClick={() => setIsOpen(true)}
          aria-label="Mở chat với cửa hàng"
          className="flex size-14 items-center justify-center rounded-full bg-[#1D1F20] text-white shadow-xl transition hover:bg-neutral-700 focus:outline-none focus:ring-2 focus:ring-[#84EA53]"
        >
          <MessageCircle size={24} />
        </button>
      ) : (
        <section
          aria-label="Chat hỗ trợ khách hàng"
          className="flex h-[min(520px,calc(100dvh-2rem))] w-[min(360px,calc(100vw-2rem))] flex-col overflow-hidden rounded-2xl border border-[#EAE6DF] bg-white shadow-2xl"
        >
          <header className="flex items-center justify-between bg-[#1D1F20] px-4 py-3 text-white">
            <div>
              <h2 className="text-sm font-semibold">Trò chuyện với cửa hàng</h2>
              <p className="mt-0.5 text-[11px] text-white/70">
                {isConnected ? 'Đang kết nối' : 'Đang kết nối lại...'}
              </p>
            </div>
            <button
              type="button"
              onClick={() => setIsOpen(false)}
              aria-label="Đóng chat"
              className="rounded-lg p-1 text-white/70 transition hover:bg-white/10 hover:text-white"
            >
              <X size={20} />
            </button>
          </header>

          <div className="flex-1 space-y-3 overflow-y-auto bg-[#FBF9F4] p-4">
            {loading ? (
              <p className="pt-12 text-center text-xs text-neutral-500">Đang tải tin nhắn...</p>
            ) : messages.length === 0 ? (
              <p className="pt-12 text-center text-xs text-neutral-500">
                Xin chào {user?.fullName}. Hãy gửi câu hỏi cho shop nhé!
              </p>
            ) : (
              messages.map((message) => {
                const isMe = message.senderId === user?.id;
                return (
                  <div
                    key={message.id}
                    className={`flex ${isMe ? 'justify-end' : 'justify-start'}`}
                  >
                    <div
                      className={`max-w-[82%] rounded-2xl px-3 py-2 text-sm leading-relaxed ${
                        isMe
                          ? 'rounded-br-sm bg-[#1D1F20] text-white'
                          : 'rounded-bl-sm border border-[#EAE6DF] bg-white text-neutral-800'
                      }`}
                    >
                      <p className="whitespace-pre-wrap break-words">{message.content}</p>
                      <time className={`mt-1 block text-[10px] ${isMe ? 'text-white/60' : 'text-neutral-400'}`}>
                        {new Date(message.sendDate).toLocaleTimeString('vi-VN', {
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </time>
                    </div>
                  </div>
                );
              })
            )}
            <div ref={messagesEndRef} />
          </div>

          {error && <p role="alert" className="px-3 pt-2 text-xs text-red-600">{error}</p>}

          <form onSubmit={handleSend} className="flex items-center gap-2 border-t border-[#EAE6DF] p-3">
            <input
              type="text"
              name="message"
              aria-label="Nội dung tin nhắn"
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder="Nhập nội dung tin nhắn..."
              maxLength={5000}
              disabled={loading || sending || chatId === null}
              className="h-10 min-w-0 flex-1 rounded-xl border border-[#EAE6DF] bg-[#FBF9F4] px-3 text-sm outline-none transition focus:border-neutral-900 disabled:opacity-60"
            />
            <button
              type="submit"
              aria-label="Gửi tin nhắn"
              disabled={!input.trim() || loading || sending || chatId === null}
              className="flex size-10 shrink-0 items-center justify-center rounded-xl bg-[#1D1F20] text-white transition hover:bg-neutral-700 disabled:cursor-not-allowed disabled:opacity-40"
            >
              <Send size={16} />
            </button>
          </form>
        </section>
      )}
    </div>
  );
}

```

# loyalty-engage\components\loyalty-checkout-widgets.tsx

| Property | Value |
|----------|-------|
| Path | `loyalty-engage\components\loyalty-checkout-widgets.tsx` |
| Size | 21,555 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 18:32:57.723606 |

```tsx
"use client";

import React, { useState, useEffect } from "react";
import {
  Tag,
  CheckCircle2,
  AlertCircle,
  Loader2,
  X,
  Ticket,
  ChevronRight,
  Clock,
  Sparkles,
  Coins,
} from "lucide-react";
import {
  validateVoucher,
  getAvailableVouchers,
  type VoucherValidationData,
  type VoucherEntity,
} from "../api";
import { formatCurrency } from "@/lib/utils/format-currency";

// =========================================================================
// COMPONENT 1: VOUCHER INPUT (GIAO DIỆN CHỌN VOUCHER KIỂU SHOPEE)
// =========================================================================

export interface VoucherInputProps {
  orderTotal: number;
  onValidate: (result: VoucherValidationData) => void;
  onRemove?: () => void;
}

export function VoucherInput({
  orderTotal,
  onValidate,
  onRemove,
}: VoucherInputProps) {
  const [code, setCode] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [appliedVoucher, setAppliedVoucher] = useState<VoucherValidationData | null>(null);

  // State Modal danh sách Voucher
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [voucherList, setVoucherList] = useState<VoucherEntity[]>([]);
  const [isLoadingList, setIsLoadingList] = useState<boolean>(false);

  // Tự động hủy nếu giỏ hàng thay đổi không còn đủ điều kiện minOrderValue
  useEffect(() => {
    if (appliedVoucher) {
      const minRequired = Number(appliedVoucher.voucher.minOrderValue);
      if (orderTotal < minRequired) {
        const timer = setTimeout(() => {
          setAppliedVoucher(null);
          setCode("");
          setErrorMessage(
            `Đơn hàng không còn đủ giá trị tối thiểu (${formatCurrency(minRequired)}) để dùng mã này.`
          );
          onRemove?.();
        }, 0);
        return () => clearTimeout(timer);
      }
    }
  }, [orderTotal, appliedVoucher, onRemove]);

  // Tải danh sách voucher khi mở modal
  const handleOpenModal = async () => {
    setIsModalOpen(true);
    setErrorMessage(null);
    setIsLoadingList(true);
    try {
      const res = await getAvailableVouchers();
      const list = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
      setVoucherList(list);
    } catch (err) {
      console.error("Không thể tải danh sách voucher:", err);
    } finally {
      setIsLoadingList(false);
    }
  };

  // Áp dụng voucher
  const handleApplyCode = async (targetCode: string) => {
    const cleanCode = targetCode.trim().toUpperCase();
    if (!cleanCode) {
      setErrorMessage("Vui lòng nhập mã voucher");
      return;
    }

    setIsLoading(true);
    setErrorMessage(null);

    try {
      const response = await validateVoucher(cleanCode, orderTotal);
      if (response?.data) {
        setAppliedVoucher(response.data);
        setCode(response.data.code);
        setErrorMessage(null);
        onValidate(response.data);
        setIsModalOpen(false);
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Mã voucher không hợp lệ hoặc đã hết hạn";
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRemoveVoucher = () => {
    setAppliedVoucher(null);
    setCode("");
    setErrorMessage(null);
    onRemove?.();
  };

  return (
    <div className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-5 sm:p-6 space-y-4 shadow-xs">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Tag size={18} className="text-[#1D1F20]" />
          <h4 className="text-base font-bold text-[#1D1F20]">Fashion Voucher</h4>
        </div>

        <button
          type="button"
          onClick={handleOpenModal}
          className="text-xs font-semibold text-emerald-700 hover:text-emerald-800 flex items-center gap-1 cursor-pointer transition"
        >
          <span>Xem tất cả mã</span>
          <ChevronRight size={14} />
        </button>
      </div>

      {appliedVoucher ? (
        <div className="flex items-center justify-between p-4 rounded-xl bg-[#84EA53]/15 border border-[#84EA53]/40 transition">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <CheckCircle2 size={16} className="text-[#16a34a] shrink-0" />
              <span className="font-bold text-sm text-[#1D1F20] tracking-wide font-mono">
                {appliedVoucher.code}
              </span>
              <span className="text-[10px] font-bold bg-[#1D1F20] text-white px-2 py-0.5 rounded-full">
                Đã chọn
              </span>
            </div>
            <p className="text-xs text-[#16a34a] font-semibold pl-6">
              Giảm -{formatCurrency(appliedVoucher.discountAmount)} vào tiền hàng
            </p>
          </div>

          <button
            type="button"
            onClick={handleRemoveVoucher}
            className="p-1.5 rounded-lg text-[#74736e] hover:text-[#dc2626] hover:bg-white/80 transition cursor-pointer"
            aria-label="Xóa mã giảm giá"
          >
            <X size={18} />
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          <div className="flex items-center gap-2">
            <input
              type="text"
              value={code}
              disabled={isLoading}
              onChange={(e) => {
                setCode(e.target.value.toUpperCase());
                if (errorMessage) setErrorMessage(null);
              }}
              onKeyDown={(e) => e.key === "Enter" && handleApplyCode(code)}
              placeholder="Nhập mã voucher hoặc chọn bên dưới"
              className="flex-1 rounded-xl border border-[#EAE6DF] bg-[#FBF9F4] px-4 py-2.5 text-sm text-[#1D1F20] uppercase placeholder:normal-case placeholder-[#74736e]/60 focus:border-[#84EA53] outline-none transition disabled:opacity-50"
            />
            <button
              type="button"
              onClick={() => handleApplyCode(code)}
              disabled={isLoading || !code.trim()}
              className="inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl bg-[#1D1F20] !text-white font-semibold text-sm hover:bg-black active:scale-[0.98] disabled:opacity-40 disabled:cursor-not-allowed transition cursor-pointer"
            >
              {isLoading ? <Loader2 size={15} className="animate-spin" /> : "Áp dụng"}
            </button>
          </div>

          {errorMessage && (
            <div className="flex items-center gap-2 text-xs text-[#dc2626] font-medium pt-1">
              <AlertCircle size={14} className="shrink-0" />
              <span>{errorMessage}</span>
            </div>
          )}

          <div
            onClick={handleOpenModal}
            className="flex items-center justify-between p-3 rounded-xl border border-dashed border-[#EAE6DF] bg-neutral-50/60 hover:bg-neutral-50 hover:border-neutral-400 transition cursor-pointer"
          >
            <div className="flex items-center gap-2 text-xs text-neutral-700 font-medium">
              <Ticket size={16} className="text-emerald-600" />
              <span>Chọn Voucher có sẵn của cửa hàng</span>
            </div>
            <span className="text-[11px] font-bold text-neutral-900 bg-white border border-[#EAE6DF] px-2 py-0.5 rounded-md">
              Chọn ngay
            </span>
          </div>
        </div>
      )}

      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-xs p-4 animate-in fade-in duration-200">
          <div className="relative w-full max-w-lg max-h-[85vh] flex flex-col rounded-3xl bg-white shadow-2xl border border-[#EAE6DF] text-neutral-900 animate-in zoom-in-95 duration-200">
            <div className="flex items-center justify-between border-b border-[#EAE6DF] px-6 py-4">
              <div>
                <h3 className="text-base font-bold text-neutral-900 flex items-center gap-2">
                  <Ticket className="size-5 text-emerald-600" />
                  Chọn Fashion Voucher
                </h3>
                <p className="text-xs text-neutral-500 mt-0.5">
                  Áp dụng mã giảm giá trực tiếp vào tiền hàng
                </p>
              </div>
              <button
                type="button"
                onClick={() => setIsModalOpen(false)}
                className="rounded-full p-1.5 text-neutral-400 hover:bg-neutral-100 hover:text-neutral-700 transition cursor-pointer"
              >
                <X className="size-5" />
              </button>
            </div>

            <div className="p-4 border-b border-[#EAE6DF] bg-neutral-50/50">
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="Nhập mã giảm giá..."
                  value={code}
                  onChange={(e) => setCode(e.target.value.toUpperCase())}
                  className="flex-1 rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2 text-xs uppercase font-mono outline-none focus:border-neutral-900"
                />
                <button
                  type="button"
                  onClick={() => handleApplyCode(code)}
                  disabled={isLoading || !code.trim()}
                  className="px-4 py-2 bg-[#1D1F20] text-white text-xs font-semibold rounded-xl hover:bg-black transition disabled:opacity-40"
                >
                  {isLoading ? <Loader2 size={13} className="animate-spin" /> : "Áp dụng"}
                </button>
              </div>
              {errorMessage && (
                <p className="text-[11px] text-red-600 font-medium mt-2 flex items-center gap-1">
                  <AlertCircle size={12} /> {errorMessage}
                </p>
              )}
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-3">
              <span className="text-[11px] font-bold uppercase tracking-wider text-neutral-400 block px-1">
                Mã giảm giá đơn hàng
              </span>

              {isLoadingList ? (
                <div className="py-12 text-center text-xs text-neutral-400">
                  <Loader2 className="size-5 animate-spin mx-auto mb-2" />
                  Đang tải danh sách voucher khả dụng...
                </div>
              ) : voucherList.length === 0 ? (
                <div className="py-12 text-center text-xs text-neutral-400">
                  Hiện chưa có mã giảm giá nào có sẵn.
                </div>
              ) : (
                voucherList.map((v) => {
                  const minOrder = Number(v.minOrderValue || 0);
                  const isEligible = orderTotal >= minOrder;
                  const discountDisplay =
                    v.discountType === "PERCENTAGE"
                      ? `Giảm ${v.discountValue}% ${v.maxDiscountAmt ? `(Tối đa ${formatCurrency(v.maxDiscountAmt)})` : ""}`
                      : `Giảm ${formatCurrency(v.discountValue)}`;

                  const isCurrentApplied = appliedVoucher?.code === v.code;

                  return (
                    <div
                      key={v.id}
                      className={`relative flex items-stretch rounded-2xl border transition-all overflow-hidden ${
                        isEligible
                          ? "bg-white border-[#EAE6DF] hover:border-emerald-500 shadow-xs"
                          : "bg-neutral-50 border-neutral-200 opacity-60"
                      }`}
                    >
                      <div className={`w-24 shrink-0 flex flex-col items-center justify-center p-3 text-center border-r border-dashed ${
                        isEligible ? "bg-emerald-50 text-emerald-800 border-emerald-200" : "bg-neutral-100 text-neutral-500 border-neutral-200"
                      }`}>
                        <Sparkles size={20} className={isEligible ? "text-emerald-600" : "text-neutral-400"} />
                        <span className="text-[10px] font-black uppercase tracking-wider mt-1">
                          VOUCHER
                        </span>
                      </div>

                      <div className="flex-1 p-3.5 space-y-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <span className="font-mono font-bold text-xs text-neutral-900 bg-neutral-100 px-1.5 py-0.5 rounded">
                            {v.code}
                          </span>
                          <span className="text-xs font-extrabold text-emerald-700 truncate">
                            {discountDisplay}
                          </span>
                        </div>

                        <p className="text-[11px] text-neutral-600">
                          Đơn tối thiểu: <strong>{formatCurrency(minOrder)}</strong>
                        </p>

                        {!isEligible && (
                          <p className="text-[10px] text-red-600 font-medium">
                            Mua thêm {formatCurrency(minOrder - orderTotal)} để sử dụng
                          </p>
                        )}

                        <div className="flex items-center gap-1 text-[10px] text-neutral-400 pt-1">
                          <Clock size={11} />
                          <span>HSD: {new Date(v.endDate).toLocaleDateString("vi-VN")}</span>
                        </div>
                      </div>

                      <div className="flex items-center pr-3.5 pl-1">
                        <button
                          type="button"
                          disabled={!isEligible || isLoading || isCurrentApplied}
                          onClick={() => handleApplyCode(v.code)}
                          className={`px-3 py-1.5 rounded-xl text-xs font-bold transition cursor-pointer ${
                            isCurrentApplied
                              ? "bg-emerald-100 text-emerald-700 border border-emerald-200 cursor-default"
                              : isEligible
                              ? "bg-[#1D1F20] text-white hover:bg-black"
                              : "bg-neutral-200 text-neutral-400 cursor-not-allowed"
                          }`}
                        >
                          {isCurrentApplied ? "Đang chọn" : "Áp dụng"}
                        </button>
                      </div>
                    </div>
                  );
                })
              )}
            </div>

            <div className="p-4 border-t border-[#EAE6DF] flex justify-end">
              <button
                type="button"
                onClick={() => setIsModalOpen(false)}
                className="px-5 py-2 text-xs font-bold rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition cursor-pointer"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}


// =========================================================================
// COMPONENT 2: POINT REDEEM SLIDER (SỬ DỤNG ĐIỂM THƯỞNG)
// =========================================================================

export interface PointRedeemSliderProps {
  maxPoints: number;
  orderTotal: number;
  onRedeem: (points: number) => void;
}

export function PointRedeemSlider({
  maxPoints,
  orderTotal,
  onRedeem,
}: PointRedeemSliderProps) {
  const [isEnabled, setIsEnabled] = useState<boolean>(false);
  const [points, setPoints] = useState<number>(0);

  const usableMaxPoints = Math.max(0, Math.min(maxPoints, Math.floor(orderTotal)));

  useEffect(() => {
    if (isEnabled && points > usableMaxPoints) {
      const timer = setTimeout(() => {
        setPoints(usableMaxPoints);
        onRedeem(usableMaxPoints);
      }, 0);
      return () => clearTimeout(timer);
    }
    if (usableMaxPoints <= 0 && isEnabled) {
      const timer = setTimeout(() => {
        setIsEnabled(false);
        setPoints(0);
        onRedeem(0);
      }, 0);
      return () => clearTimeout(timer);
    }
  }, [usableMaxPoints, isEnabled, points, onRedeem]);

  const handleToggle = () => {
    const nextState = !isEnabled;
    setIsEnabled(nextState);

    if (nextState) {
      const initialPoints = usableMaxPoints;
      setPoints(initialPoints);
      onRedeem(initialPoints);
    } else {
      setPoints(0);
      onRedeem(0);
    }
  };

  const handleSliderChange = (newVal: number) => {
    const clamped = Math.max(0, Math.min(newVal, usableMaxPoints));
    setPoints(clamped);
    onRedeem(clamped);
  };

  const handlePreset = (fraction: number) => {
    const calculated = Math.round(usableMaxPoints * fraction);
    handleSliderChange(calculated);
  };

  if (maxPoints <= 0) {
    return null;
  }

  return (
    <div className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-5 sm:p-6 space-y-5 shadow-sm">
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-[#84EA53]/20 border border-[#84EA53]/40 flex items-center justify-center text-[#1D1F20]">
            <Coins size={18} />
          </div>
          <div>
            <h4 className="text-base font-bold text-[#1D1F20]">Dùng điểm tích lũy</h4>
            <p className="text-xs text-[#74736e]">
              Bạn có{" "}
              <strong className="text-[#1D1F20]">
                {maxPoints.toLocaleString("vi-VN")} điểm
              </strong>{" "}
              (1 điểm = 1₫)
            </p>
          </div>
        </div>

        <button
          type="button"
          role="switch"
          aria-checked={isEnabled}
          onClick={handleToggle}
          className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
            isEnabled ? "bg-[#84EA53]" : "bg-[#EAE6DF]"
          }`}
        >
          <span
            className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow-md ring-0 transition duration-200 ease-in-out ${
              isEnabled ? "translate-x-5" : "translate-x-0"
            }`}
          />
        </button>
      </div>

      {isEnabled && (
        <div className="space-y-4 pt-2 border-t border-[#EAE6DF] animate-fadeIn">
          <div className="flex items-center justify-between p-3.5 rounded-xl bg-[#FBF9F4] border border-[#EAE6DF]">
            <span className="text-xs font-semibold text-[#74736e]">
              Số tiền giảm giá:
            </span>
            <div className="flex items-center gap-1.5 text-sm font-bold text-[#16a34a]">
              <Sparkles size={14} className="text-[#84EA53]" />
              <span>-{points.toLocaleString("vi-VN")}₫</span>
            </div>
          </div>

          <div className="space-y-2">
            <div className="flex justify-between text-xs text-[#74736e] font-medium">
              <span>0 điểm</span>
              <span className="text-[#1D1F20] font-bold">
                {points.toLocaleString("vi-VN")} / {usableMaxPoints.toLocaleString("vi-VN")} điểm
              </span>
            </div>

            <input
              type="range"
              min={0}
              max={usableMaxPoints}
              step={100}
              value={points}
              onChange={(e) => handleSliderChange(Number(e.target.value))}
              className="w-full h-2 bg-[#EAE6DF] rounded-lg appearance-none cursor-pointer accent-[#84EA53] focus:outline-none"
            />
          </div>

          <div className="flex items-center gap-2 pt-1">
            {[
              { label: "25%", frac: 0.25 },
              { label: "50%", frac: 0.5 },
              { label: "75%", frac: 0.75 },
              { label: "Tối đa", frac: 1 },
            ].map(({ label, frac }) => (
              <button
                key={label}
                type="button"
                onClick={() => handlePreset(frac)}
                className="flex-1 py-1.5 text-xs font-semibold rounded-lg bg-[#FBF9F4] border border-[#EAE6DF] text-[#1D1F20] hover:border-[#84EA53] hover:bg-[#84EA53]/10 transition cursor-pointer"
              >
                {label}
              </button>
            ))}

            <button
              type="button"
              onClick={() => handleSliderChange(0)}
              className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-transparent text-[#74736e] hover:text-[#dc2626] transition cursor-pointer"
            >
              Đặt lại
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
```

# loyalty-engage\components\review-widget.tsx

| Property | Value |
|----------|-------|
| Path | `loyalty-engage\components\review-widget.tsx` |
| Size | 19,530 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 18:23:09.047047 |

```tsx
"use client";

import React, { useState } from "react";
import useSWR, { useSWRConfig } from "swr";
import {
  Star,
  CheckCircle2,
  AlertCircle,
  Loader2,
  ChevronLeft,
  ChevronRight,
  MessageSquare,
  CornerDownRight,
  ShieldCheck,
  X
} from "lucide-react";
import {
  getReviewsByProduct,
  createReview,
  type ProductReviewItem,
} from "../api";

interface StarRatingProps {
  rating: number;
  maxStars?: number;
  size?: number;
  interactive?: boolean;
  onRatingChange?: (rating: number) => void;
}

export function StarRating({
  rating,
  maxStars = 5,
  size = 18,
  interactive = false,
  onRatingChange,
}: StarRatingProps) {
  const [hoverRating, setHoverRating] = useState<number | null>(null);

  const displayRating = hoverRating !== null ? hoverRating : rating;

  return (
    <div className="flex items-center gap-1">
      {Array.from({ length: maxStars }, (_, i) => {
        const starNumber = i + 1;
        const isFilled = starNumber <= displayRating;

        return (
          <button
            key={starNumber}
            type="button"
            disabled={!interactive}
            onClick={() => onRatingChange?.(starNumber)}
            onMouseEnter={() => interactive && setHoverRating(starNumber)}
            onMouseLeave={() => interactive && setHoverRating(null)}
            className={`transition-transform ${
              interactive
                ? "cursor-pointer hover:scale-110 focus:outline-none"
                : "cursor-default"
            }`}
            aria-label={`${starNumber} sao`}
          >
            <Star
              size={size}
              stroke="#1D1F20"
              strokeWidth={1.5}
              fill={isFilled ? "#1D1F20" : "transparent"}
              className="transition-colors"
            />
          </button>
        );
      })}
    </div>
  );
}

export interface ProductReviewListProps {
  productId: number;
}

export function ProductReviewList({ productId }: ProductReviewListProps) {
  const [page, setPage] = useState<number>(1);
  const [ratingFilter, setRatingFilter] = useState<number | undefined>(undefined);
  const [previewImage, setPreviewImage] = useState<string | null>(null);

  const swrKey = `/reviews?productId=${productId}&page=${page}&rating=${ratingFilter ?? ""}`;
  const { data: response, error, isLoading, mutate } = useSWR(
    swrKey,
    () => getReviewsByProduct({ productId, page, limit: 5, rating: ratingFilter }),
    {
      revalidateOnFocus: false,
      keepPreviousData: true,
    }
  );

  const reviews = response?.data ?? [];
  const ratingStats = response?.ratingStats ?? { averageRating: 0, totalReviews: 0 };
  const pagination = response?.pagination ?? { page: 1, limit: 5, total: 0, totalPages: 1 };

  return (
    <section className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-6 sm:p-8 space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6 pb-6 border-b border-[#EAE6DF]">
        <div>
          <h3 className="text-xl sm:text-2xl font-bold text-[#1D1F20] tracking-tight">
            Đánh giá sản phẩm
          </h3>
          <p className="text-sm text-[#74736e] mt-1">
            Nhận xét chân thực từ những khách hàng đã mua và trải nghiệm
          </p>
        </div>

        <div className="flex items-center gap-4 bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl px-5 py-3 self-start sm:self-auto">
          <div className="text-3xl font-extrabold text-[#1D1F20]">
            {ratingStats.averageRating > 0 ? ratingStats.averageRating.toFixed(1) : "5.0"}
          </div>
          <div>
            <StarRating rating={Math.round(ratingStats.averageRating) || 5} size={16} />
            <span className="text-xs text-[#74736e] mt-0.5 block">
              {ratingStats.totalReviews} lượt đánh giá
            </span>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2 overflow-x-auto pb-2 text-sm">
        <button
          type="button"
          onClick={() => {
            setRatingFilter(undefined);
            setPage(1);
          }}
          className={`px-4 py-1.5 rounded-full font-medium transition cursor-pointer border ${
            ratingFilter === undefined
              ? "bg-[#1D1F20] !text-white [&_*]:!text-white border-[#1D1F20]"
              : "bg-[#FBF9F4] text-[#1D1F20] border-[#EAE6DF] hover:border-[#1D1F20]"
          }`}
        >
          Tất cả
        </button>
        {[5, 4, 3, 2, 1].map((star) => (
          <button
            key={star}
            type="button"
            onClick={() => {
              setRatingFilter(star);
              setPage(1);
            }}
            className={`px-3 py-1.5 rounded-full font-medium flex items-center gap-1 transition cursor-pointer border ${
              ratingFilter === star
                ? "bg-[#1D1F20] !text-white [&_*]:!text-white border-[#1D1F20]"
                : "bg-[#FBF9F4] text-[#1D1F20] border-[#EAE6DF] hover:border-[#1D1F20]"
            }`}
          >
            <span>{star}</span>
            <Star size={12} fill={ratingFilter === star ? "#FFFFFF" : "#1D1F20"} stroke="none" />
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((skeletonId) => (
            <div
              key={skeletonId}
              className="p-5 rounded-xl border border-[#EAE6DF] bg-[#FBF9F4] animate-pulse space-y-3"
            >
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-[#EAE6DF]" />
                <div className="space-y-1.5 flex-1">
                  <div className="w-32 h-3.5 bg-[#EAE6DF] rounded" />
                  <div className="w-20 h-2.5 bg-[#EAE6DF] rounded" />
                </div>
              </div>
              <div className="w-full h-12 bg-[#EAE6DF]/60 rounded-lg" />
            </div>
          ))}
        </div>
      ) : error ? (
        <div className="p-8 text-center bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl space-y-3">
          <AlertCircle className="w-8 h-8 text-[#dc2626] mx-auto" />
          <p className="text-sm text-[#1D1F20] font-medium">
            Không thể tải danh sách đánh giá.
          </p>
          <button
            type="button"
            onClick={() => mutate()}
            className="px-4 py-2 text-xs font-semibold bg-[#1D1F20] !text-white [&_*]:!text-white rounded-xl hover:bg-black transition"
          >
            Thử lại
          </button>
        </div>
      ) : reviews.length === 0 ? (
        <div className="p-12 text-center bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl space-y-3">
          <MessageSquare className="w-10 h-10 text-[#74736e] mx-auto opacity-40" />
          <h4 className="text-base font-semibold text-[#1D1F20]">Chưa có đánh giá nào</h4>
          <p className="text-sm text-[#74736e] max-w-sm mx-auto">
            {ratingFilter !== undefined
              ? `Chưa có đánh giá ${ratingFilter} sao nào cho sản phẩm này.`
              : "Hãy là người đầu tiên trải nghiệm và chia sẻ cảm nhận về sản phẩm này nhé!"}
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {reviews.map((review: ProductReviewItem) => (
            <article
              key={review.id}
              className="p-5 sm:p-6 rounded-xl border border-[#EAE6DF] bg-[#FFFFFF] hover:border-[#84EA53] transition-colors space-y-4"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex items-center gap-3">
                  {review.user?.avatarUrl ? (
                    <img
                      src={review.user.avatarUrl}
                      alt={review.user.fullName || "User"}
                      className="w-10 h-10 rounded-full object-cover border border-[#EAE6DF]"
                    />
                  ) : (
                    <div className="w-10 h-10 rounded-full bg-[#FBF9F4] border border-[#EAE6DF] flex items-center justify-center font-bold text-sm text-[#1D1F20]">
                      {review.user?.fullName?.charAt(0).toUpperCase() || "U"}
                    </div>
                  )}

                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-[#1D1F20] text-sm">
                        {review.user?.fullName || "Khách hàng"}
                      </span>
                      <span className="inline-flex items-center gap-1 text-[11px] text-[#16a34a] font-medium bg-[#84EA53]/15 px-2 py-0.5 rounded-full">
                        <ShieldCheck size={12} />
                        Đã mua hàng
                      </span>
                    </div>
                    <time className="text-xs text-[#74736e] mt-0.5 block">
                      {new Date(review.createdAt).toLocaleDateString("vi-VN", {
                        year: "numeric",
                        month: "long",
                        day: "numeric",
                      })}
                    </time>
                  </div>
                </div>

                <StarRating rating={review.rating} size={15} />
              </div>

              <p className="text-sm text-[#1D1F20] leading-relaxed whitespace-pre-line">
                {review.content}
              </p>

              {review.medias && review.medias.length > 0 && (
                <div className="flex flex-wrap gap-2 pt-1">
                  {review.medias.map((media: any, idx: number) => {
                    const src = media.mediaUrl || media.url;
                    return (
                      <img
                        key={media.id || idx}
                        src={src}
                        alt="Ảnh đánh giá"
                        className="w-20 h-20 object-cover rounded-xl border border-[#EAE6DF] hover:opacity-90 hover:scale-105 transition-all cursor-pointer shadow-xs"
                        onClick={() => src && setPreviewImage(src)} // <-- Bấm để mở modal
                      />
                    );
                  })}
                </div>
              )}

              {review.shopReply && (
                <div className="mt-3 p-3.5 rounded-xl bg-[#FBF9F4] border-l-2 border-[#84EA53] text-xs space-y-1">
                  <div className="flex items-center gap-1.5 font-semibold text-[#1D1F20]">
                    <CornerDownRight size={14} className="text-[#84EA53]" />
                    <span>Phản hồi từ Người bán</span>
                  </div>
                  <p className="text-[#74736e] leading-relaxed pl-5">
                    {review.shopReply}
                  </p>
                </div>
              )}
            </article>
          ))}

          {pagination.totalPages > 1 && (
            <div className="flex items-center justify-between pt-4 border-t border-[#EAE6DF]">
              <button
                type="button"
                disabled={page <= 1}
                onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold border border-[#EAE6DF] rounded-xl bg-[#FFFFFF] text-[#1D1F20] hover:bg-[#FBF9F4] disabled:opacity-40 disabled:cursor-not-allowed transition"
              >
                <ChevronLeft size={14} />
                Trang trước
              </button>

              <span className="text-xs text-[#74736e] font-medium">
                Trang {pagination.page} / {pagination.totalPages}
              </span>

              <button
                type="button"
                disabled={page >= pagination.totalPages}
                onClick={() => setPage((prev) => Math.min(prev + 1, pagination.totalPages))}
                className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold border border-[#EAE6DF] rounded-xl bg-[#FFFFFF] text-[#1D1F20] hover:bg-[#FBF9F4] disabled:opacity-40 disabled:cursor-not-allowed transition"
              >
                Trang sau
                <ChevronRight size={14} />
              </button>
            </div>
          )}
        </div>
      )}
    {/* Khung Modal xem ảnh phóng to với hiệu ứng làm mờ nền */}
      {previewImage && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-4 animate-in fade-in duration-200"
          onClick={() => setPreviewImage(null)}
        >
          {/* Nút đóng góc trên bên phải */}
          <button
            type="button"
            onClick={() => setPreviewImage(null)}
            className="absolute top-5 right-5 p-2 rounded-full bg-white/10 hover:bg-white/20 text-white transition cursor-pointer"
            aria-label="Đóng xem ảnh"
          >
            <X size={24} />
          </button>

          {/* Vùng hiển thị ảnh (ngăn nổi bọt click để không bị tự đóng khi bấm vào ảnh) */}
          <div
            className="relative max-w-4xl max-h-[85vh] flex items-center justify-center"
            onClick={(e) => e.stopPropagation()}
          >
            <img
              src={previewImage}
              alt="Ảnh đánh giá phóng to"
              className="max-w-full max-h-[85vh] w-auto h-auto object-contain rounded-2xl shadow-2xl border border-white/15 animate-in zoom-in-95 duration-200"
            />
          </div>
        </div>
      )}
    </section>

    
  );
}

export interface ReviewFormProps {
  orderId: number;
  productId: number;
  onReviewSubmitted?: (review: ProductReviewItem) => void;
}

const RATING_LABELS: Record<number, string> = {
  1: "Rất không hài lòng",
  2: "Không hài lòng",
  3: "Bình thường",
  4: "Hài lòng",
  5: "Tuyệt vời",
};

export function ReviewForm({
  orderId,
  productId,
  onReviewSubmitted,
}: ReviewFormProps) {
  const { mutate } = useSWRConfig();
  const [rating, setRating] = useState<number>(5);
  const [content, setContent] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setSuccessMessage(null);

    const trimmedContent = content.trim();
    if (!trimmedContent) {
      setErrorMessage("Vui lòng viết nhận xét cảm nhận của bạn về sản phẩm.");
      return;
    }

    if (trimmedContent.length < 5) {
      setErrorMessage("Nội dung đánh giá tối thiểu 5 ký tự.");
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await createReview({
        productId,
        orderId,
        rating,
        content: trimmedContent,
      });

      mutate(
        (key: any) =>
          typeof key === "string" &&
          key.startsWith(`/reviews?productId=${productId}`)
      );

      setSuccessMessage("Cảm ơn bạn! Đánh giá của bạn đã được ghi nhận thành công.");
      setContent("");
      setRating(5);
      if (onReviewSubmitted && response.data) {
        onReviewSubmitted(response.data);
      }
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : "Không thể gửi đánh giá. Vui lòng thử lại sau.";
      setErrorMessage(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-6 sm:p-8 space-y-6 shadow-sm"
    >
      <div>
        <h4 className="text-xl font-bold text-[#1D1F20] tracking-tight">
          Viết đánh giá sản phẩm
        </h4>
        <p className="text-sm text-[#74736e] mt-1">
          Đơn hàng #{orderId} • Chia sẻ trải nghiệm để giúp đỡ cộng đồng mua sắm
        </p>
      </div>

      <div className="space-y-2 bg-[#FBF9F4] border border-[#EAE6DF] rounded-xl p-4">
        <label className="text-xs font-bold text-[#74736e] uppercase tracking-wider block">
          Chất lượng sản phẩm
        </label>
        <div className="flex items-center gap-3">
          <StarRating
            rating={rating}
            size={24}
            interactive={!isSubmitting}
            onRatingChange={(newRating) => setRating(newRating)}
          />
          <span className="text-sm font-semibold text-[#1D1F20]">
            {RATING_LABELS[rating] || ""}
          </span>
        </div>
      </div>

      <div className="space-y-2">
        <label
          htmlFor="review-content"
          className="text-xs font-bold text-[#74736e] uppercase tracking-wider block"
        >
          Nhận xét chi tiết
        </label>
        <textarea
          id="review-content"
          rows={4}
          value={content}
          disabled={isSubmitting}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Hãy chia sẻ cảm nhận về chất liệu, form dáng, độ vừa vặn và dịch vụ giao hàng..."
          className="w-full rounded-xl border border-[#EAE6DF] bg-[#FFFFFF] p-4 text-sm text-[#1D1F20] placeholder-[#74736e]/60 focus:border-[#84EA53] outline-none transition resize-none"
          maxLength={2000}
        />
        <div className="flex justify-between text-xs text-[#74736e]">
          <span>Tối thiểu 5 ký tự</span>
          <span>{content.length}/2000 ký tự</span>
        </div>
      </div>

      {errorMessage && (
        <div className="flex items-center gap-2 p-3.5 rounded-xl bg-[#dc2626]/10 border border-[#dc2626]/20 text-[#dc2626] text-sm">
          <AlertCircle size={16} className="shrink-0" />
          <span>{errorMessage}</span>
        </div>
      )}

      {successMessage && (
        <div className="flex items-center gap-2 p-3.5 rounded-xl bg-[#84EA53]/15 border border-[#84EA53]/30 text-[#16a34a] text-sm font-medium">
          <CheckCircle2 size={16} className="shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      <div className="flex justify-end pt-2">
        <button
          type="submit"
          disabled={isSubmitting || !content.trim()}
          className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-xl bg-[#84EA53] text-[#1D1F20] font-bold text-sm hover:opacity-90 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed transition shadow-sm cursor-pointer"
        >
          {isSubmitting ? (
            <>
              <Loader2 size={16} className="animate-spin" />
              <span>Đang gửi đánh giá...</span>
            </>
          ) : (
            <span>Gửi đánh giá</span>
          )}
        </button>
      </div>
    </form>
  );
}

```

# loyalty-engage\hooks\useChatSocket.ts

| Property | Value |
|----------|-------|
| Path | `loyalty-engage\hooks\useChatSocket.ts` |
| Size | 742 bytes |
| Extension | `.ts` |
| Modified | 2026-10-11 17:09:39.990290 |

```typescript
'use client';

import { useEffect } from 'react';
import { useSocket } from '@/components/providers/socket-provider';
import type { ChatMessage } from '@/types/loyalty-engage';

export function useChatSocket(
  chatId: number | null,
  onNewMessage: (message: ChatMessage) => void
) {
  const { socket, isConnected } = useSocket();

  useEffect(() => {
    if (!socket || chatId === null) return;

    const handleNewMessage = (message: ChatMessage) => {
      if (message.chatId === chatId) onNewMessage(message);
    };

    socket.on('new_message', handleNewMessage);
    return () => {
      socket.off('new_message', handleNewMessage);
    };
  }, [socket, chatId, onNewMessage]);

  return { isConnected };
}

```

# loyalty-engage\index.ts

| Property | Value |
|----------|-------|
| Path | `loyalty-engage\index.ts` |
| Size | 213 bytes |
| Extension | `.ts` |
| Modified | 2026-10-10 00:01:54.858094 |

```typescript
export * from "./api";
export * from "./hooks/useChatSocket";
export * from "./components/review-widget";
export * from "./components/loyalty-checkout-widgets";
export * from "./components/live-chat-widget";

```

# order\hooks\use-orders.ts

| Property | Value |
|----------|-------|
| Path | `order\hooks\use-orders.ts` |
| Size | 813 bytes |
| Extension | `.ts` |
| Modified | 2026-10-09 10:38:13.170979 |

```typescript
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderApi } from '@/api/order.api';
import { CreateOrderPayload } from '@/types';
import { toast } from 'sonner';
import { CART_QUERY_KEYS } from '@/modules/cart/hooks/use-cart';

export const useCreateOrder = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateOrderPayload) => orderApi.createOrder(payload),
    onSuccess: () => {
      // Chỉ xóa cache giỏ hàng, việc điều hướng & xử lý QR sẽ do CheckoutView quyết định
      queryClient.invalidateQueries({ queryKey: CART_QUERY_KEYS.cart });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Có lỗi xảy ra khi đặt hàng');
    },
  });
};
```

# user\api.ts

| Property | Value |
|----------|-------|
| Path | `user\api.ts` |
| Size | 172 bytes |
| Extension | `.ts` |
| Modified | 2026-10-10 00:01:54.859090 |

```typescript
import { apiClient } from '@/api/client';
import type { User } from './types';

export function getUser(id: string) {
  return apiClient.get<User>(`/users/${id}`);
}

```

# user\components\address-list.tsx

| Property | Value |
|----------|-------|
| Path | `user\components\address-list.tsx` |
| Size | 15,416 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-11 16:52:36.585461 |

```tsx
'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'sonner';
import { Plus, MapPin, Trash2, Edit3, Check, Loader2, AlertCircle, WifiOff } from 'lucide-react';
import { authApi } from '@/api/auth.api';
import { addressSchema, type AddressFormData } from '@/modules/auth/schemas';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { FormField } from '@/components/ui/form-field';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import type { ShippingAddress } from '@/types';

export function AddressList() {
  const queryClient = useQueryClient();
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [editingAddress, setEditingAddress] = React.useState<ShippingAddress | null>(null);
  const [deleteTargetId, setDeleteTargetId] = React.useState<number | null>(null);
  
  // State quản lý thông báo lỗi hiển thị trực tiếp trong Form
  const [formError, setFormError] = React.useState<string | null>(null);

  const { data: addressesData, isLoading } = useQuery({
    queryKey: ['addresses'],
    queryFn: async (): Promise<ShippingAddress[]> => {
      try {
        const res: any = await authApi.getAddresses();
        if (Array.isArray(res)) return res;
        if (Array.isArray(res?.data)) return res.data;
        if (Array.isArray(res?.data?.data)) return res.data.data;
        if (Array.isArray(res?.addresses)) return res.addresses;
        return [];
      } catch (error) {
        console.error('Lỗi khi tải danh sách địa chỉ:', error);
        return [];
      }
    },
  });

  const {
    control,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<AddressFormData>({
    resolver: zodResolver(addressSchema),
    defaultValues: {
      receiverName: '',
      phoneNumber: '',
      province: '',
      district: '',
      ward: '',
      detailAddress: '',
      isDefault: false,
    },
  });

  const isDefaultWatched = watch('isDefault');

  const openCreateDialog = () => {
    setEditingAddress(null);
    setFormError(null);
    reset({
      receiverName: '',
      phoneNumber: '',
      province: '',
      district: '',
      ward: '',
      detailAddress: '',
      isDefault: false,
    });
    setIsDialogOpen(true);
  };

  const openEditDialog = (addr: ShippingAddress) => {
    setEditingAddress(addr);
    setFormError(null);
    reset({
      receiverName: addr.receiverName,
      phoneNumber: addr.phoneNumber,
      province: addr.province,
      district: addr.district,
      ward: addr.ward,
      detailAddress: addr.detailAddress,
      isDefault: addr.isDefault,
    });
    setIsDialogOpen(true);
  };

  // Helper trích xuất thông báo lỗi chính xác
  const getErrorMessage = (err: any) => {
    if (!navigator.onLine || err?.message === 'Network Error' || err?.code === 'ERR_NETWORK') {
      return 'Mất kết nối mạng! Vui lòng kiểm tra lại đường truyền internet của bạn.';
    }
    return (
      err?.response?.data?.message ||
      err?.message ||
      'Thao tác thất bại do máy chủ không phản hồi, vui lòng thử lại sau.'
    );
  };

  const saveMutation = useMutation({
    mutationFn: async (data: AddressFormData) => {
      // Bắt ngay lỗi nếu trình duyệt đang ở trạng thái Offline
      if (typeof window !== 'undefined' && !navigator.onLine) {
        throw new Error('Mất kết nối mạng! Vui lòng kiểm tra lại đường truyền internet.');
      }

      if (editingAddress) {
        return authApi.updateAddress(editingAddress.id, data);
      } else {
        return authApi.createAddress(data);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      toast.success(editingAddress ? 'Cập nhật địa chỉ thành công!' : 'Thêm địa chỉ mới thành công!');
      setIsDialogOpen(false);
      setFormError(null);
    },
    onError: (err: any) => {
      const msg = getErrorMessage(err);
      setFormError(msg);
      toast.error(msg);
    },
    retry: false, // Tắt retry để khi offline lập tức trả về lỗi, không quay vòng vòng
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      if (typeof window !== 'undefined' && !navigator.onLine) {
        throw new Error('Mất kết nối mạng! Không thể thực hiện xóa.');
      }
      return authApi.deleteAddress(id);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['addresses'] });
      toast.success('Xóa địa chỉ thành công!');
      setDeleteTargetId(null);
    },
    onError: (err: any) => {
      const msg = getErrorMessage(err);
      toast.error(msg);
    },
    retry: false,
  });

  const onSubmit = (data: AddressFormData) => {
    setFormError(null);
    saveMutation.mutate(data);
  };

  const addresses: ShippingAddress[] = Array.isArray(addressesData) ? addressesData : [];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-6 border-b border-[#EAE6DF]">
        <div>
          <h2 className="text-xl font-semibold text-neutral-900">Sổ địa chỉ nhận hàng</h2>
          <p className="text-xs text-neutral-500 mt-1">
            Quản lý danh sách địa chỉ nhận hàng để tiện lợi khi thanh toán.
          </p>
        </div>
        <Button
          onClick={openCreateDialog}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2 shrink-0"
        >
          <Plus className="size-4" />
          <span>Thêm địa chỉ mới</span>
        </Button>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Skeleton className="h-44 rounded-2xl" />
          <Skeleton className="h-44 rounded-2xl" />
        </div>
      ) : addresses.length === 0 ? (
        <div className="text-center py-12 px-4 rounded-2xl border border-dashed border-[#EAE6DF] bg-neutral-50/40">
          <MapPin className="size-8 mx-auto text-neutral-400 mb-2" />
          <h3 className="text-sm font-semibold text-neutral-800">Chưa có địa chỉ nào</h3>
          <p className="text-xs text-neutral-500 mt-1 max-w-sm mx-auto">
            Bạn chưa lưu địa chỉ nhận hàng nào. Hãy thêm địa chỉ đầu tiên để bắt đầu đặt hàng.
          </p>
          <Button
            onClick={openCreateDialog}
            variant="outline"
            size="sm"
            className="mt-4 rounded-xl border-[#EAE6DF]"
          >
            Thêm ngay
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {addresses.map((addr) => (
            <div
              key={addr.id}
              className={`p-5 rounded-2xl border transition relative flex flex-col justify-between ${
                addr.isDefault
                  ? 'border-neutral-900 bg-neutral-50/30'
                  : 'border-[#EAE6DF] bg-white hover:border-neutral-300'
              }`}
            >
              <div className="space-y-2">
                <div className="flex items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <h4 className="text-sm font-semibold text-neutral-900">
                      {addr.receiverName}
                    </h4>
                    {addr.isDefault && (
                      <Badge variant="lime" className="text-[10px] py-0">
                        Mặc định
                      </Badge>
                    )}
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      type="button"
                      onClick={() => openEditDialog(addr)}
                      className="p-1.5 text-neutral-400 hover:text-neutral-700 transition rounded-lg hover:bg-neutral-100"
                    >
                      <Edit3 className="size-3.5" />
                    </button>
                    <button
                      type="button"
                      onClick={() => setDeleteTargetId(addr.id)}
                      className="p-1.5 text-neutral-400 hover:text-rose-600 transition rounded-lg hover:bg-rose-50"
                    >
                      <Trash2 className="size-3.5" />
                    </button>
                  </div>
                </div>

                <p className="text-xs text-neutral-600">
                  <span className="text-neutral-400">Điện thoại: </span>
                  {addr.phoneNumber}
                </p>

                <p className="text-xs text-neutral-600 leading-relaxed">
                  <span className="text-neutral-400">Địa chỉ: </span>
                  {addr.detailAddress}, {addr.ward}, {addr.district}, {addr.province}
                </p>
              </div>

              {!addr.isDefault && (
                <div className="pt-3 mt-3 border-t border-[#EAE6DF]/70">
                  <button
                    type="button"
                    onClick={() =>
                      saveMutation.mutate({
                        ...addr,
                        isDefault: true,
                      })
                    }
                    className="text-xs text-neutral-500 hover:text-neutral-900 font-medium transition flex items-center gap-1"
                  >
                    <Check className="size-3.5" />
                    Đặt làm mặc định
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Add / Edit Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-lg bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900">
              {editingAddress ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ nhận hàng'}
            </DialogTitle>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-2">
            {/* HỘP HIỂN THỊ LỖI KHI MẤT MẠNG HOẶC GỌI API THẤT BẠI */}
            {formError && (
              <div className="flex items-start gap-2.5 rounded-xl border border-rose-200 bg-rose-50/80 p-3.5 text-xs text-rose-800 animate-in fade-in-50">
                <WifiOff className="size-4 shrink-0 text-rose-600 mt-0.5" />
                <div className="flex-1">
                  <p className="font-semibold">Lỗi gửi dữ liệu</p>
                  <p className="mt-0.5 text-rose-700 leading-relaxed">{formError}</p>
                </div>
              </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <FormField
                control={control}
                name="receiverName"
                label="Tên người nhận"
                placeholder="Nguyễn Văn A"
                error={errors.receiverName?.message}
              />
              <FormField
                control={control}
                name="phoneNumber"
                label="Số điện thoại"
                placeholder="0912345678"
                error={errors.phoneNumber?.message}
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <FormField
                control={control}
                name="province"
                label="Tỉnh / Thành"
                placeholder="TP. Hồ Chí Minh"
                error={errors.province?.message}
              />
              <FormField
                control={control}
                name="district"
                label="Quận / Huyện"
                placeholder="Quận 1"
                error={errors.district?.message}
              />
              <FormField
                control={control}
                name="ward"
                label="Phường / Xã"
                placeholder="Bến Nghé"
                error={errors.ward?.message}
              />
            </div>

            <FormField
              control={control}
              name="detailAddress"
              label="Địa chỉ chi tiết (Số nhà, tên đường)"
              placeholder="123 Đường Lê Lợi"
              error={errors.detailAddress?.message}
            />

            <label className="flex items-center gap-2 cursor-pointer pt-1">
              <input
                type="checkbox"
                checked={isDefaultWatched}
                onChange={(e) => setValue('isDefault', e.target.checked)}
                className="rounded border-[#EAE6DF] accent-neutral-900 size-4"
              />
              <span className="text-xs text-neutral-700">Đặt làm địa chỉ giao hàng mặc định</span>
            </label>

            <DialogFooter className="mt-6 flex flex-row justify-end gap-2.5 pt-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsDialogOpen(false)}
                className="rounded-xl border-[#EAE6DF]"
              >
                Hủy
              </Button>
              <Button
                type="submit"
                disabled={saveMutation.isPending}
                className="rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium"
              >
                {saveMutation.isPending ? (
                  <>
                    <Loader2 className="size-4 animate-spin mr-2" />
                    Đang lưu...
                  </>
                ) : (
                  'Lưu địa chỉ'
                )}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Delete Confirm */}
      <ConfirmDialog
        open={deleteTargetId !== null}
        onOpenChange={(open) => !open && setDeleteTargetId(null)}
        title="Xóa địa chỉ"
        description="Bạn có chắc chắn muốn xóa địa chỉ nhận hàng này? Thao tác này không thể hoàn tác."
        variant="destructive"
        confirmText="Xóa vĩnh viễn"
        isLoading={deleteMutation.isPending}
        onConfirm={() => {
          if (deleteTargetId) deleteMutation.mutate(deleteTargetId);
        }}
      />
    </div>
  );
}
```

# user\components\profile-form.tsx

| Property | Value |
|----------|-------|
| Path | `user\components\profile-form.tsx` |
| Size | 7,394 bytes |
| Extension | `.tsx` |
| Modified | 2026-10-13 02:26:52.966907 |

```tsx
'use client';

import * as React from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';
import { Loader2, Sparkles, CheckCircle2 } from 'lucide-react';
import { profileSchema, type ProfileFormData } from '@/modules/auth/schemas';
import { useAuth } from '@/components/providers/auth-provider';
import { apiClient } from '@/api/client';
import { API_ENDPOINTS } from '@/api/endpoints';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';
import { Avatar } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import type { ApiResponse, UserProfile } from '@/types';

export function ProfileForm() {
  const { user } = useAuth();

  const {
    control,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isDirty },
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      fullName: user?.fullName || '',
      phoneNumber: user?.phoneNumber || '',
      avatarUrl: user?.avatarUrl || '',
    },
  });

  React.useEffect(() => {
    if (user) {
      setValue('fullName', user.fullName || '');
      setValue('phoneNumber', user.phoneNumber || '');
      setValue('avatarUrl', user.avatarUrl || '');
    }
  }, [user, setValue]);

  const currentAvatarUrl = watch('avatarUrl');

  const updateProfileMutation = useMutation({
    mutationFn: async (data: ProfileFormData) => {
      if (!user?.id) throw new Error('Không tìm thấy thông tin người dùng');
      const res = await apiClient.put<ApiResponse<UserProfile>>(
        `${API_ENDPOINTS.IDENTITY.USERS}/${user.id}`,
        {
          fullName: data.fullName,
          phoneNumber: data.phoneNumber || undefined,
          avatarUrl: data.avatarUrl || null,
        }
      );
      return res.data;
    },
    onSuccess: (res) => {
      if (res.data) {
        // Merge với dữ liệu user hiện tại để không mất các field API không trả về
        const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
        const updatedUser = { ...currentUser, ...res.data };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        window.dispatchEvent(new Event('local-storage-change'));
        toast.success('Cập nhật hồ sơ thành công!');
      }
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật thất bại, vui lòng thử lại');
    },
  });

  const onSubmit = (data: ProfileFormData) => {
    updateProfileMutation.mutate(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Avatar Preview & URL */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-6 p-4 rounded-xl bg-neutral-50/80 border border-[#EAE6DF]">
        <Avatar
          src={currentAvatarUrl || user?.avatarUrl}
          alt={user?.fullName || 'User'}
          size="xl"
          className="size-20 ring-4 ring-white shadow-xs"
        />
        <div className="space-y-1.5 flex-1">
          <h4 className="text-sm font-semibold text-neutral-900">Ảnh đại diện</h4>
          <p className="text-xs text-neutral-500">
            Dán đường dẫn ảnh đại diện (URL) của bạn để cập nhật ảnh hiển thị.
          </p>
          <div className="max-w-md pt-1">
            <FormField
              control={control}
              name="avatarUrl"
              label=""
              placeholder="https://example.com/avatar.jpg"
              error={errors.avatarUrl?.message}
            />
          </div>
        </div>
      </div>

      {/* Rewards & Role Summary */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div className="p-4 rounded-xl border border-[#EAE6DF] bg-neutral-50/40 flex items-center justify-between">
          <div>
            <p className="text-xs text-neutral-500">Điểm thưởng thành viên</p>
            <p className="text-xl font-bold text-neutral-900 flex items-center gap-1.5 mt-0.5">
              <Sparkles className="size-4 text-amber-500 fill-amber-500" />
              {user?.rewardPoints?.toLocaleString('vi-VN') || 0} điểm
            </p>
          </div>
          <div className="flex flex-col items-end gap-1.5">
            <Badge variant="lime" className="text-[11px]">Thành viên</Badge>
            <Link
              href="/points"
              className="text-[11px] font-semibold text-neutral-800 hover:text-neutral-950 underline underline-offset-2"
            >
              Lịch sử điểm &rarr;
            </Link>
          </div>
        </div>

        <div className="p-4 rounded-xl border border-[#EAE6DF] bg-neutral-50/40 flex items-center justify-between">
          <div>
            <p className="text-xs text-neutral-500">Trạng thái tài khoản</p>
            <p className="text-sm font-semibold text-emerald-600 flex items-center gap-1.5 mt-1">
              <CheckCircle2 className="size-4" />
              {user?.status === 'ACTIVE' ? 'Đang hoạt động' : user?.status}
            </p>
          </div>
          <Badge variant="success" className="text-[11px]">Đã kích hoạt</Badge>
        </div>
      </div>

      {/* Input Fields */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
        <FormField
          control={control}
          name="fullName"
          label="Họ và tên"
          placeholder="Nguyễn Văn A"
          error={errors.fullName?.message}
        />

        <FormField
          control={control}
          name="phoneNumber"
          label="Số điện thoại"
          placeholder="0912345678"
          error={errors.phoneNumber?.message}
        />
      </div>

      {/* Readonly Email */}
      <div className="space-y-1">
        <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
          Địa chỉ Email
        </label>
        <input
          type="email"
          value={user?.email || ''}
          disabled
          className="w-full rounded-xl border border-[#EAE6DF] bg-neutral-100/70 px-4 py-2.5 text-sm text-neutral-500 cursor-not-allowed outline-none"
        />
        <p className="text-[11px] text-neutral-400">
          Email đăng nhập được cố định để bảo vệ an toàn danh tính tài khoản của bạn.
        </p>
      </div>

      <div className="flex justify-end pt-4 border-t border-[#EAE6DF]">
        <Button
          type="submit"
          disabled={updateProfileMutation.isPending || !isDirty}
          className="h-11 px-8 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium transition active:scale-[0.99]"
        >
          {updateProfileMutation.isPending ? (
            <>
              <Loader2 className="size-4 animate-spin mr-2" />
              Đang lưu thay đổi...
            </>
          ) : (
            'Lưu thay đổi'
          )}
        </Button>
      </div>
    </form>
  );
}


```

# user\types.ts

| Property | Value |
|----------|-------|
| Path | `user\types.ts` |
| Size | 78 bytes |
| Extension | `.ts` |
| Modified | 2026-10-02 09:57:03.162979 |

```typescript
export interface User {
  id: string;
  name: string;
  email: string;
}

```

