'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import {
  Users,
  Search,
  Filter,
  MoreVertical,
  ShieldAlert,
  ShieldCheck,
  Ban,
  Trash2,
  Eye,
  CheckCircle2,
  Clock,
  Sparkles,
  Phone,
  Mail,
  UserCheck,
  Coins,
  Laptop,
  Circle,
} from 'lucide-react';
import { adminApi } from '@/modules/admin/api';
import { chatApi, earnPoints } from '@/modules/loyalty-engage/api';
import { SearchInput } from '@/components/ui/search-input';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { Badge } from '@/components/ui/badge';
import { Avatar } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import type { UserProfile, UserStatus } from '@/types';

export default function AdminUsersPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [statusFilter, setStatusFilter] = React.useState<UserStatus | ''>('');
  const [selectedUser, setSelectedUser] = React.useState<UserProfile | null>(null);
  const [statusChangeTarget, setStatusChangeTarget] = React.useState<{
    user: UserProfile;
    newStatus: UserStatus;
  } | null>(null);
  const [deleteTargetUser, setDeleteTargetUser] = React.useState<UserProfile | null>(null);
  const [onlineStatusMap, setOnlineStatusMap] = React.useState<Record<number, boolean>>({});
  const [selectedUserSessions, setSelectedUserSessions] = React.useState<
    Array<{ socketId: string; userId: number; connectedAt: string }>
  >([]);

  // Fetch Users
  const { data: usersData, isLoading } = useQuery({
    queryKey: ['admin-users', page, search, statusFilter],
    queryFn: () =>
      adminApi.getUsers({
        page,
        limit: 10,
        search: search || undefined,
        status: statusFilter || undefined,
      }),
  });

  // Check online status for users on the current page
  React.useEffect(() => {
    const list = usersData?.data || [];
    if (!list.length) return;

    const userIds = list.map((u) => u.id);
    Promise.all(
      userIds.map(async (uid) => {
        try {
          const res = await chatApi.getUserOnlineStatus(uid);
          return { userId: uid, isOnline: !!res?.data?.isOnline };
        } catch {
          return { userId: uid, isOnline: false };
        }
      })
    ).then((results) => {
      setOnlineStatusMap((prev) => {
        const next = { ...prev };
        results.forEach((r) => {
          next[r.userId] = r.isOnline;
        });
        return next;
      });
    });
  }, [usersData]);

  // When selectedUser changes, fetch their active socket sessions
  React.useEffect(() => {
    if (!selectedUser?.id) {
      setSelectedUserSessions([]);
      return;
    }
    chatApi
      .getUserSessions(selectedUser.id)
      .then((res) => {
        setSelectedUserSessions(res?.data || []);
      })
      .catch(() => {
        setSelectedUserSessions([]);
      });
  }, [selectedUser?.id]);

  // Mutation update status
  const updateStatusMutation = useMutation({
    mutationFn: ({ userId, status }: { userId: number; status: UserStatus }) =>
      adminApi.updateUserStatus(userId, { status }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      toast.success('Cập nhật trạng thái người dùng thành công!');
      setStatusChangeTarget(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Cập nhật trạng thái thất bại');
    },
  });

  // Mutation delete user
  const deleteUserMutation = useMutation({
    mutationFn: (userId: number) => adminApi.deleteUser(userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] });
      toast.success('Đã xóa tài khoản người dùng!');
      setDeleteTargetUser(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa người dùng thất bại');
    },
  });

  const getStatusBadge = (status: UserStatus) => {
    switch (status) {
      case 'ACTIVE':
        return <Badge variant="success">Hoạt động</Badge>;
      case 'BLOCKED':
        return <Badge variant="destructive">Bị khóa</Badge>;
      case 'INACTIVE':
        return <Badge variant="warning">Chưa kích hoạt</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  const columns: Column<UserProfile>[] = [
    {
      header: 'ID',
      className: 'w-16 text-center text-xs text-neutral-400 font-mono',
      cell: (user) => `#${user.id}`,
    },
    {
      header: 'Người dùng',
      cell: (user) => {
        const isOnline = !!onlineStatusMap[user.id];
        return (
          <div className="flex items-center gap-3">
            <div className="relative shrink-0">
              <Avatar
                src={user.avatarUrl}
                alt={user.fullName}
                size="sm"
                className="size-8"
              />
              <span
                className={`absolute bottom-0 right-0 size-2.5 rounded-full ring-2 ring-white ${
                  isOnline ? 'bg-emerald-500 animate-pulse' : 'bg-neutral-300'
                }`}
                title={isOnline ? 'Đang online' : 'Ngoại tuyến'}
              />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <p className="font-semibold text-xs text-neutral-900">{user.fullName}</p>
                {isOnline && (
                  <span className="size-1.5 rounded-full bg-emerald-500 shrink-0" />
                )}
              </div>
              <p className="text-[11px] text-neutral-400">{user.email}</p>
            </div>
          </div>
        );
      },
    },
    {
      header: 'Số điện thoại',
      cell: (user) => (
        <span className="text-xs text-neutral-600 font-mono">
          {user.phoneNumber || '—'}
        </span>
      ),
    },
    {
      header: 'Vai trò',
      cell: (user) => (
        <Badge
          variant={user.role?.code === 'ADMIN' ? 'lime' : 'secondary'}
          className="text-[11px]"
        >
          {user.role?.name || user.role?.code || 'Customer'}
        </Badge>
      ),
    },
    {
      header: 'Điểm thưởng',
      cell: (user) => (
        <span className="text-xs font-semibold text-neutral-800 flex items-center gap-1">
          <Sparkles className="size-3 text-amber-500 fill-amber-500" />
          {(user.rewardPoints || 0).toLocaleString('vi-VN')}
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (user) => getStatusBadge(user.status),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (user) => (
        <div className="flex items-center justify-end gap-1">
          <button
            type="button"
            onClick={() => setSelectedUser(user)}
            title="Xem chi tiết"
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 transition"
          >
            <Eye className="size-4" />
          </button>

          {user.status === 'ACTIVE' ? (
            <button
              type="button"
              onClick={() =>
                setStatusChangeTarget({ user, newStatus: 'BLOCKED' })
              }
              title="Khóa tài khoản"
              className="p-1.5 rounded-lg text-neutral-400 hover:text-amber-600 hover:bg-amber-50 transition"
            >
              <Ban className="size-4" />
            </button>
          ) : (
            <button
              type="button"
              onClick={() =>
                setStatusChangeTarget({ user, newStatus: 'ACTIVE' })
              }
              title="Kích hoạt lại"
              className="p-1.5 rounded-lg text-neutral-400 hover:text-emerald-600 hover:bg-emerald-50 transition"
            >
              <CheckCircle2 className="size-4" />
            </button>
          )}

          <button
            type="button"
            onClick={() => setDeleteTargetUser(user)}
            title="Xóa người dùng"
            className="p-1.5 rounded-lg text-neutral-400 hover:text-rose-600 hover:bg-rose-50 transition"
          >
            <Trash2 className="size-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">QUẢN TRỊ TÀI KHOẢN</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Người dùng
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Xem danh sách tài khoản khách hàng và nhân viên, cập nhật trạng thái hoạt động.
          </p>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-4 flex flex-col sm:flex-row items-center justify-between gap-4 shadow-xs">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm theo tên hoặc email người dùng..."
          className="max-w-md"
        />

        <div className="flex items-center gap-3 w-full sm:w-auto">
          <div className="flex items-center gap-1.5 text-xs text-neutral-500 shrink-0">
            <Filter className="size-3.5" />
            <span>Trạng thái:</span>
          </div>
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value as UserStatus | '');
              setPage(1);
            }}
            className="rounded-xl border border-[#EAE6DF] bg-white px-3 py-2 text-xs font-medium text-neutral-800 outline-none transition focus:border-neutral-900 shadow-xs cursor-pointer"
          >
            <option value="">Tất cả trạng thái</option>
            <option value="ACTIVE">Hoạt động (ACTIVE)</option>
            <option value="INACTIVE">Chưa kích hoạt (INACTIVE)</option>
            <option value="BLOCKED">Bị khóa (BLOCKED)</option>
          </select>
        </div>
      </div>

      {/* Data Table */}
      <DataTable
        columns={columns}
        data={Array.isArray(usersData?.data) ? usersData.data : []}
        isLoading={isLoading}
        page={page}
        totalPages={usersData?.totalPages || 1}
        totalItems={usersData?.totalItems}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy người dùng nào phù hợp với bộ lọc."
      />

      {/* User Details Modal */}
      <Dialog open={!!selectedUser} onOpenChange={(open) => !open && setSelectedUser(null)}>
        <DialogContent className="sm:max-w-md bg-white border border-[#EAE6DF] rounded-2xl p-6">
          <DialogHeader>
            <DialogTitle className="text-base font-semibold text-neutral-900">
              Chi tiết người dùng #{selectedUser?.id}
            </DialogTitle>
          </DialogHeader>

          {selectedUser && (
            <div className="space-y-5 pt-2">
              <div className="flex items-center gap-4 p-4 rounded-xl bg-neutral-50 border border-[#EAE6DF]">
                <Avatar
                  src={selectedUser.avatarUrl}
                  alt={selectedUser.fullName}
                  size="lg"
                  className="size-14"
                />
                <div>
                  <h3 className="font-semibold text-sm text-neutral-900">
                    {selectedUser.fullName}
                  </h3>
                  <p className="text-xs text-neutral-500">{selectedUser.email}</p>
                  <div className="mt-1 flex gap-2">
                    {getStatusBadge(selectedUser.status)}
                    <Badge variant="lime" className="text-[10px]">
                      {selectedUser.role?.name || 'Customer'}
                    </Badge>
                  </div>
                </div>
              </div>

              <div className="space-y-3 text-xs">
                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Laptop className="size-3.5" /> Trạng thái trực tuyến
                  </span>
                  <div className="flex items-center gap-2">
                    {onlineStatusMap[selectedUser.id] || selectedUserSessions.length > 0 ? (
                      <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-50 text-emerald-700 border border-emerald-200">
                        <span className="size-2 rounded-full bg-emerald-500 animate-pulse" />
                        Đang online ({selectedUserSessions.length} tab/thiết bị)
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-neutral-100 text-neutral-500 border border-neutral-200">
                        <span className="size-2 rounded-full bg-neutral-400" />
                        Ngoại tuyến
                      </span>
                    )}
                  </div>
                </div>

                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Phone className="size-3.5" /> Số điện thoại
                  </span>
                  <span className="font-medium text-neutral-800">
                    {selectedUser.phoneNumber || 'Chưa cung cấp'}
                  </span>
                </div>

                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Sparkles className="size-3.5" /> Điểm thưởng tích lũy
                  </span>
                  <span className="font-bold text-neutral-900">
                    {(selectedUser.rewardPoints || 0).toLocaleString('vi-VN')} điểm
                  </span>
                </div>

                <div className="flex items-center justify-between py-2 border-b border-[#EAE6DF]">
                  <span className="text-neutral-500 flex items-center gap-2">
                    <Clock className="size-3.5" /> Ngày tạo tài khoản
                  </span>
                  <span className="font-medium text-neutral-800">
                    {selectedUser.createdAt
                      ? new Date(selectedUser.createdAt).toLocaleDateString('vi-VN')
                      : '—'}
                  </span>
                </div>

                {selectedUserSessions.length > 0 && (
                  <div className="p-3 bg-neutral-50 rounded-xl border border-neutral-100 space-y-1.5">
                    <span className="font-bold text-[10px] text-neutral-400 uppercase tracking-wider block">
                      Các phiên Socket đang hoạt động ({selectedUserSessions.length})
                    </span>
                    {selectedUserSessions.map((s) => (
                      <div key={s.socketId} className="flex items-center justify-between text-[11px] font-mono text-neutral-600">
                        <span className="truncate max-w-[200px]">{s.socketId}</span>
                        <span className="text-[10px] text-neutral-400">
                          {new Date(s.connectedAt).toLocaleTimeString('vi-VN')}
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Confirm Change Status Dialog */}
      <ConfirmDialog
        open={!!statusChangeTarget}
        onOpenChange={(open) => !open && setStatusChangeTarget(null)}
        title={statusChangeTarget?.newStatus === 'BLOCKED' ? 'Khóa tài khoản người dùng' : 'Kích hoạt lại tài khoản'}
        description={`Bạn có chắc chắn muốn thay đổi trạng thái của tài khoản ${statusChangeTarget?.user.fullName} sang "${statusChangeTarget?.newStatus}"?`}
        variant={statusChangeTarget?.newStatus === 'BLOCKED' ? 'destructive' : 'default'}
        confirmText="Xác nhận thay đổi"
        isLoading={updateStatusMutation.isPending}
        onConfirm={() => {
          if (statusChangeTarget) {
            updateStatusMutation.mutate({
              userId: statusChangeTarget.user.id,
              status: statusChangeTarget.newStatus,
            });
          }
        }}
      />

      {/* Confirm Delete User Dialog */}
      <ConfirmDialog
        open={!!deleteTargetUser}
        onOpenChange={(open) => !open && setDeleteTargetUser(null)}
        title="Xóa vĩnh viễn người dùng"
        description={`Cảnh báo: Hành động này sẽ xóa người dùng "${deleteTargetUser?.fullName}" (${deleteTargetUser?.email}) khỏi cơ sở dữ liệu. Thao tác không thể hoàn tác.`}
        variant="destructive"
        confirmText="Xóa người dùng"
        isLoading={deleteUserMutation.isPending}
        onConfirm={() => {
          if (deleteTargetUser) {
            deleteUserMutation.mutate(deleteTargetUser.id);
          }
        }}
      />
    </div>
  );
}

