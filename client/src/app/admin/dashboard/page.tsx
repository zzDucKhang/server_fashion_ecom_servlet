'use client';

import * as React from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import {
  Users,
  ShoppingBag,
  TrendingUp,
  PackageCheck,
  ArrowUpRight,
  Shield,
  Layers,
  Clock,
  PackageX,
} from 'lucide-react';
import { adminApi } from '@/modules/admin/api';
import { orderApi } from '@/api/order.api';
import { DashboardCard } from '@/modules/admin/components/dashboard-card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { formatCurrency } from '@/lib/utils/format-currency';
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from '@/components/ui/table';
import type { Order } from '@/types';

function formatRelativeTime(dateInput?: string | Date): string {
  if (!dateInput) return 'Gần đây';
  const diffSec = Math.floor((Date.now() - new Date(dateInput).getTime()) / 1000);
  if (diffSec < 60) return 'Vừa xong';
  if (diffSec < 3600) return `${Math.floor(diffSec / 60)} phút trước`;
  if (diffSec < 86400) return `${Math.floor(diffSec / 3600)} giờ trước`;
  return `${Math.floor(diffSec / 86400)} ngày trước`;
}

export default function AdminDashboardPage() {
  // 1. Lấy dữ liệu KPI thống kê từ server
  const { data: statsData, isLoading: isLoadingStats } = useQuery({
    queryKey: ['admin-dashboard-stats'],
    queryFn: () => adminApi.getDashboardStats(),
  });

  // 2. Lấy danh sách 5 đơn hàng mới nhất từ server
  const { data: ordersData, isLoading: isLoadingOrders } = useQuery({
    queryKey: ['admin-recent-orders'],
    queryFn: () => orderApi.getOrders({ limit: 5, all: true }),
  });

  const stats = statsData?.data;

  // Trích xuất danh sách đơn hàng thực tế
  const rawOrders = ordersData?.data as unknown;
  const recentOrders: Order[] = Array.isArray(rawOrders)
    ? (rawOrders as Order[])
    : Array.isArray((rawOrders as { data?: Order[] })?.data)
    ? ((rawOrders as { data: Order[] }).data)
    : [];

  const getOrderStatusBadge = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return <Badge variant="success">Hoàn thành</Badge>;
      case 'CONFIRMED':
        return <Badge variant="lime">Đã xác nhận</Badge>;
      case 'PROCESSING':
        return <Badge variant="lime">Đang xử lý</Badge>;
      case 'SHIPPED':
        return <Badge variant="secondary">Đang giao</Badge>;
      case 'PENDING_PAYMENT':
        return <Badge variant="warning">Chờ thanh toán</Badge>;
      case 'CANCELLED':
        return <Badge variant="destructive">Đã hủy</Badge>;
      case 'RETURNED':
        return <Badge variant="outline">Trả hàng</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  return (
    <div className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">TỔNG QUAN HỆ THỐNG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Dashboard
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Theo dõi các chỉ số tăng trưởng, đơn hàng và hoạt động người dùng trong thời gian thực.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Link href="/admin/users">
            <Button variant="outline" className="rounded-xl border-[#EAE6DF] text-xs h-10">
              <Users className="size-4 mr-2" /> Quản lý Users
            </Button>
          </Link>
          <Link href="/admin/roles">
            <Button className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white text-xs h-10">
              <Shield className="size-4 mr-2" /> Phân quyền Roles
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <DashboardCard
          title="Tổng doanh thu"
          value={isLoadingStats ? '...' : formatCurrency(stats?.totalRevenue || 0)}
          icon={TrendingUp}
          change="+18.4%"
          trend="up"
          description="so với tháng trước"
        />
        <DashboardCard
          title="Tổng đơn hàng"
          value={isLoadingStats ? '...' : (stats?.totalOrders || 0).toLocaleString('vi-VN')}
          icon={PackageCheck}
          change={`${stats?.ordersPending || 0} đang chờ`}
          trend="neutral"
          description="tất cả giao dịch"
        />
        <DashboardCard
          title="Người dùng đăng ký"
          value={isLoadingStats ? '...' : (stats?.totalUsers || 0).toLocaleString('vi-VN')}
          icon={Users}
          change={`+${stats?.newUsersThisMonth || 0} mới`}
          trend="up"
          description="trong tháng hiện tại"
        />
        <DashboardCard
          title="Sản phẩm đang bán"
          value={isLoadingStats ? '...' : (stats?.totalProducts || 0).toLocaleString('vi-VN')}
          icon={ShoppingBag}
          change="Đang hoạt động"
          trend="up"
          description="danh mục toàn sàn"
        />
      </div>

      {/* Quick shortcuts & Status view */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs lg:col-span-1 space-y-4">
          <div className="flex items-center justify-between pb-4 border-b border-[#EAE6DF]">
            <h3 className="text-sm font-semibold text-neutral-900">
              Phím tắt nhanh
            </h3>
            <span className="text-[10px] text-neutral-400">Điều hướng</span>
          </div>

          <div className="space-y-2">
            <Link
              href="/admin/users"
              className="flex items-center justify-between p-3 rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition group"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-neutral-100 text-neutral-700">
                  <Users className="size-4" />
                </div>
                <div>
                  <p className="text-xs font-semibold text-neutral-800">Danh sách người dùng</p>
                  <p className="text-[10px] text-neutral-400">Xem, khóa hoặc cập nhật tài khoản</p>
                </div>
              </div>
              <ArrowUpRight className="size-4 text-neutral-400 group-hover:text-neutral-900 transition" />
            </Link>

            <Link
              href="/admin/roles"
              className="flex items-center justify-between p-3 rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition group"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-neutral-100 text-neutral-700">
                  <Shield className="size-4" />
                </div>
                <div>
                  <p className="text-xs font-semibold text-neutral-800">Quản lý vai trò & quyền</p>
                  <p className="text-[10px] text-neutral-400">Thiết lập RBAC Admin, Staff, Customer</p>
                </div>
              </div>
              <ArrowUpRight className="size-4 text-neutral-400 group-hover:text-neutral-900 transition" />
            </Link>

            <Link
              href="/admin/products"
              className="flex items-center justify-between p-3 rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition group"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-neutral-100 text-neutral-700">
                  <Layers className="size-4" />
                </div>
                <div>
                  <p className="text-xs font-semibold text-neutral-800">Quản lý sản phẩm</p>
                  <p className="text-[10px] text-neutral-400">Kho hàng, thuộc tính và biến thể</p>
                </div>
              </div>
              <ArrowUpRight className="size-4 text-neutral-400 group-hover:text-neutral-900 transition" />
            </Link>
          </div>
        </div>

        {/* Recent orders table */}
        <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 shadow-xs lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between pb-4 border-b border-[#EAE6DF]">
            <div>
              <h3 className="text-sm font-semibold text-neutral-900">
                Đơn hàng gần đây
              </h3>
              <p className="text-xs text-neutral-500 mt-0.5">
                Các giao dịch thực tế vừa phát sinh trên hệ thống
              </p>
            </div>
            <Link href="/admin/orders">
              <Button variant="ghost" size="sm" className="text-xs text-neutral-600 hover:text-neutral-900">
                Xem tất cả đơn →
              </Button>
            </Link>
          </div>

          <div className="overflow-hidden rounded-xl border border-[#EAE6DF]">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã đơn</TableHead>
                  <TableHead>Khách hàng</TableHead>
                  <TableHead>Tổng tiền</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead className="text-right">Thời gian</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoadingOrders ? (
                  Array.from({ length: 5 }).map((_, i) => (
                    <TableRow key={i}>
                      <TableCell><Skeleton className="h-4 w-20" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-32" /></TableCell>
                      <TableCell><Skeleton className="h-4 w-24" /></TableCell>
                      <TableCell><Skeleton className="h-5 w-20 rounded-full" /></TableCell>
                      <TableCell className="text-right"><Skeleton className="h-4 w-16 ml-auto" /></TableCell>
                    </TableRow>
                  ))
                ) : recentOrders.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} className="py-10 text-center">
                      <PackageX className="mx-auto size-8 text-neutral-300 mb-2" />
                      <p className="text-xs text-neutral-500 font-medium">
                        Chưa có đơn hàng nào phát sinh trên hệ thống.
                      </p>
                    </TableCell>
                  </TableRow>
                ) : (
                  recentOrders.map((ord) => {
                    const itemsSummary =
                      ord.orderItems && ord.orderItems.length > 0
                        ? ord.orderItems.map((item) => `${item.productName} x${item.quantity}`).join(', ')
                        : '1 sản phẩm';

                    return (
                      <TableRow key={ord.id}>
                        <TableCell className="font-semibold text-neutral-900 text-xs">
                          {ord.orderCode || `ORD-${ord.id}`}
                        </TableCell>
                        <TableCell className="text-xs">
                          <p className="font-medium text-neutral-800">{ord.receiverName || 'Khách hàng'}</p>
                          <p className="text-[11px] text-neutral-400 truncate max-w-[200px]" title={itemsSummary}>
                            {itemsSummary}
                          </p>
                        </TableCell>
                        <TableCell className="font-medium text-xs text-neutral-900">
                          {formatCurrency(ord.totalAmount)}
                        </TableCell>
                        <TableCell>{getOrderStatusBadge(ord.status)}</TableCell>
                        <TableCell className="text-right text-xs text-neutral-400">
                          <span className="inline-flex items-center gap-1">
                            <Clock className="size-3" />
                            {formatRelativeTime(ord.createdAt || ord.orderDate)}
                          </span>
                        </TableCell>
                      </TableRow>
                    );
                  })
                )}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
    </div>
  );
}
