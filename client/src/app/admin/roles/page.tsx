'use client';

import * as React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import {
  Shield,
  Plus,
  Edit3,
  Trash2,
  CheckCircle2,
  XCircle,
  KeyRound,
  Loader2,
  Lock,
} from 'lucide-react';
import { adminApi } from '@/modules/admin/api';
import { SearchInput } from '@/components/ui/search-input';
import { DataTable, type Column } from '@/modules/admin/components/data-table';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import type { Role, Permission } from '@/types';

interface RoleFormData {
  name: string;
  code: string;
  isActive: boolean;
  permissionIds: number[];
}

export default function AdminRolesPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = React.useState(1);
  const [search, setSearch] = React.useState('');
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [editingRole, setEditingRole] = React.useState<Role | null>(null);
  const [deleteTargetRole, setDeleteTargetRole] = React.useState<Role | null>(null);

  // Fetch Roles
  const { data: rolesData, isLoading } = useQuery({
    queryKey: ['admin-roles', page, search],
    queryFn: () =>
      adminApi.getRoles({
        page,
        limit: 10,
        search: search || undefined,
      }),
  });

  // Fetch Permissions
  const { data: permissionsData } = useQuery({
    queryKey: ['admin-permissions'],
    queryFn: () => adminApi.getPermissions({ limit: 100 }),
  });

  const permissions = Array.isArray(permissionsData?.data) ? permissionsData.data : [];

  // Group permissions by module
  const permissionsByModule = React.useMemo(() => {
    const groups: Record<string, Permission[]> = {};
    if (Array.isArray(permissions)) {
      permissions.forEach((perm) => {
        const mod = perm.module || 'CHUNG';
        if (!groups[mod]) groups[mod] = [];
        groups[mod].push(perm);
      });
    }
    return groups;
  }, [permissions]);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    reset,
    formState: { errors },
  } = useForm<RoleFormData>({
    defaultValues: {
      name: '',
      code: '',
      isActive: true,
      permissionIds: [],
    },
  });

  const selectedPermissionIds = watch('permissionIds') || [];
  const isActiveWatched = watch('isActive');

  const openCreateDialog = () => {
    setEditingRole(null);
    reset({
      name: '',
      code: '',
      isActive: true,
      permissionIds: [],
    });
    setIsDialogOpen(true);
  };

  const openEditDialog = (role: Role) => {
    setEditingRole(role);
    reset({
      name: role.name,
      code: role.code,
      isActive: role.isActive ?? true,
      permissionIds: role.permissions?.map((p) => p.id) || [],
    });
    setIsDialogOpen(true);
  };

  const togglePermission = (id: number) => {
    const current = new Set(selectedPermissionIds);
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    setValue('permissionIds', Array.from(current));
  };

  const toggleAllInModule = (modulePermissions: Permission[]) => {
    const current = new Set(selectedPermissionIds);
    const allModuleSelected = modulePermissions.every((p) => current.has(p.id));

    if (allModuleSelected) {
      modulePermissions.forEach((p) => current.delete(p.id));
    } else {
      modulePermissions.forEach((p) => current.add(p.id));
    }
    setValue('permissionIds', Array.from(current));
  };

  // Mutation save role
  const saveRoleMutation = useMutation({
    mutationFn: async (data: RoleFormData) => {
      if (editingRole) {
        return adminApi.updateRole(editingRole.id, {
          name: data.name,
          isActive: data.isActive,
          permissionIds: data.permissionIds,
        });
      } else {
        return adminApi.createRole({
          name: data.name,
          code: data.code.toUpperCase(),
          permissionIds: data.permissionIds,
        });
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-roles'] });
      toast.success(editingRole ? 'Cập nhật vai trò thành công!' : 'Tạo vai trò mới thành công!');
      setIsDialogOpen(false);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Lưu vai trò thất bại');
    },
  });

  // Mutation delete role
  const deleteRoleMutation = useMutation({
    mutationFn: (roleId: number) => adminApi.deleteRole(roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-roles'] });
      toast.success('Xóa vai trò thành công!');
      setDeleteTargetRole(null);
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Xóa vai trò thất bại');
    },
  });

  const onSubmit = (data: RoleFormData) => {
    saveRoleMutation.mutate(data);
  };

  const isCoreRole = (code: string) => {
    return ['ADMIN', 'CUSTOMER'].includes(code?.toUpperCase());
  };

  const columns: Column<Role>[] = [
    {
      header: 'ID',
      className: 'w-16 text-center text-xs text-neutral-400 font-mono',
      cell: (role) => `#${role.id}`,
    },
    {
      header: 'Tên vai trò',
      cell: (role) => (
        <div className="flex items-center gap-2.5">
          <div className="size-8 rounded-xl bg-neutral-100 text-neutral-700 flex items-center justify-center">
            <Shield className="size-4" />
          </div>
          <div>
            <p className="font-semibold text-xs text-neutral-900">{role.name}</p>
            <p className="text-[11px] text-neutral-400 font-mono">{role.code}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Số quyền hạn',
      cell: (role) => (
        <span className="text-xs font-semibold text-neutral-700 flex items-center gap-1.5">
          <KeyRound className="size-3.5 text-neutral-400" />
          {role.permissions?.length || 0} quyền
        </span>
      ),
    },
    {
      header: 'Trạng thái',
      cell: (role) =>
        role.isActive ? (
          <Badge variant="success" className="gap-1 text-[11px]">
            <CheckCircle2 className="size-3" /> Hoạt động
          </Badge>
        ) : (
          <Badge variant="destructive" className="gap-1 text-[11px]">
            <XCircle className="size-3" /> Vô hiệu
          </Badge>
        ),
    },
    {
      header: 'Hành động',
      className: 'text-right',
      cell: (role) => (
        <div className="flex items-center justify-end gap-1">
          <button
            type="button"
            onClick={() => openEditDialog(role)}
            title="Chỉnh sửa vai trò"
            className="p-1.5 rounded-lg text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 transition"
          >
            <Edit3 className="size-4" />
          </button>

          {!isCoreRole(role.code) ? (
            <button
              type="button"
              onClick={() => setDeleteTargetRole(role)}
              title="Xóa vai trò"
              className="p-1.5 rounded-lg text-neutral-400 hover:text-rose-600 hover:bg-rose-50 transition"
            >
              <Trash2 className="size-4" />
            </button>
          ) : (
            <span
              title="Vai trò hệ thống cốt lõi không thể xóa"
              className="p-1.5 text-neutral-300 cursor-not-allowed"
            >
              <Lock className="size-4" />
            </span>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="eyebrow block mb-1">PHÂN QUYỀN HỆ THỐNG</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Vai trò & Quyền hạn
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Thiết lập danh mục vai trò người dùng (Roles) và ma trận phân quyền tính năng (RBAC).
          </p>
        </div>

        <Button
          onClick={openCreateDialog}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2"
        >
          <Plus className="size-4" />
          <span>Thêm vai trò mới</span>
        </Button>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white rounded-2xl border border-[#EAE6DF] p-4 flex items-center justify-between shadow-xs">
        <SearchInput
          value={search}
          onChange={(val) => {
            setSearch(val);
            setPage(1);
          }}
          placeholder="Tìm kiếm theo tên hoặc mã vai trò..."
          className="max-w-md"
        />
      </div>

      {/* Roles Data Table */}
      <DataTable
        columns={columns}
        data={Array.isArray(rolesData?.data) ? rolesData.data : []}
        isLoading={isLoading}
        page={page}
        totalPages={rolesData?.totalPages || 1}
        totalItems={rolesData?.totalItems}
        onPageChange={setPage}
        emptyMessage="Không tìm thấy vai trò nào."
      />

      {/* Create / Edit Role Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-2xl bg-white border border-[#EAE6DF] rounded-2xl p-6 max-h-[90vh] flex flex-col">
          <DialogHeader>
            <DialogTitle className="text-lg font-semibold text-neutral-900">
              {editingRole ? `Chỉnh sửa vai trò "${editingRole.name}"` : 'Tạo vai trò mới'}
            </DialogTitle>
          </DialogHeader>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 mt-2 flex-1 overflow-y-auto pr-1">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                  Tên vai trò
                </label>
                <input
                  {...register('name', { required: 'Tên vai trò là bắt buộc' })}
                  placeholder="Ví dụ: Quản trị viên chi nhánh"
                  className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2 text-sm text-neutral-800 outline-none focus:border-neutral-900 shadow-xs"
                />
              </div>

              <div className="space-y-1">
                <label className="text-xs font-semibold uppercase tracking-wider text-neutral-600">
                  Mã vai trò (Code)
                </label>
                <input
                  {...register('code', { required: 'Mã vai trò là bắt buộc' })}
                  disabled={!!editingRole}
                  placeholder="Ví dụ: BRANCH_MANAGER"
                  className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2 text-sm text-neutral-800 outline-none focus:border-neutral-900 shadow-xs uppercase font-mono disabled:bg-neutral-100 disabled:text-neutral-500"
                />
              </div>
            </div>

            <label className="flex items-center gap-2 cursor-pointer pt-1">
              <input
                type="checkbox"
                checked={isActiveWatched}
                onChange={(e) => setValue('isActive', e.target.checked)}
                className="rounded border-[#EAE6DF] accent-neutral-900 size-4"
              />
              <span className="text-xs font-medium text-neutral-700">Kích hoạt vai trò này</span>
            </label>

            {/* Matrix Permissions By Module */}
            <div className="space-y-3 pt-2 border-t border-[#EAE6DF]">
              <div className="flex items-center justify-between">
                <h4 className="text-xs font-bold uppercase tracking-wider text-neutral-700 flex items-center gap-1.5">
                  <KeyRound className="size-3.5" />
                  Danh mục quyền hạn ({selectedPermissionIds.length} đã chọn)
                </h4>
              </div>

              {Object.keys(permissionsByModule).length === 0 ? (
                <div className="text-center py-6 text-xs text-neutral-400">
                  Đang tải danh sách quyền hạn...
                </div>
              ) : (
                <div className="space-y-4 max-h-[300px] overflow-y-auto pr-2">
                  {Object.entries(permissionsByModule).map(([mod, perms]) => {
                    const allSelected = perms.every((p) =>
                      selectedPermissionIds.includes(p.id)
                    );
                    return (
                      <div
                        key={mod}
                        className="rounded-xl border border-[#EAE6DF] p-3.5 bg-neutral-50/50 space-y-2.5"
                      >
                        <div className="flex items-center justify-between pb-2 border-b border-[#EAE6DF]">
                          <span className="text-xs font-bold text-neutral-900 uppercase tracking-wide">
                            {mod}
                          </span>
                          <button
                            type="button"
                            onClick={() => toggleAllInModule(perms)}
                            className="text-[11px] text-neutral-500 hover:text-neutral-900 font-medium"
                          >
                            {allSelected ? 'Bỏ chọn module' : 'Chọn tất cả module'}
                          </button>
                        </div>

                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                          {perms.map((p) => {
                            const isChecked = selectedPermissionIds.includes(p.id);
                            return (
                              <label
                                key={p.id}
                                className={`flex items-start gap-2 p-2 rounded-lg border transition cursor-pointer text-xs ${
                                  isChecked
                                    ? 'border-neutral-900 bg-white font-medium'
                                    : 'border-[#EAE6DF] bg-white/70 hover:border-neutral-300'
                                }`}
                              >
                                <input
                                  type="checkbox"
                                  checked={isChecked}
                                  onChange={() => togglePermission(p.id)}
                                  className="rounded border-[#EAE6DF] accent-neutral-900 size-3.5 mt-0.5"
                                />
                                <div className="min-w-0">
                                  <p className="text-neutral-900 truncate font-mono text-[11px]">
                                    {p.code}
                                  </p>
                                  {p.description && (
                                    <p className="text-[10px] text-neutral-500 line-clamp-1">
                                      {p.description}
                                    </p>
                                  )}
                                </div>
                              </label>
                            );
                          })}
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>

            <DialogFooter className="mt-6 flex flex-row justify-end gap-2.5 pt-3 border-t border-[#EAE6DF]">
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
                disabled={saveRoleMutation.isPending}
                className="rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium"
              >
                {saveRoleMutation.isPending ? (
                  <>
                    <Loader2 className="size-4 animate-spin mr-2" />
                    Đang lưu...
                  </>
                ) : (
                  'Lưu vai trò'
                )}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Confirm Delete Role */}
      <ConfirmDialog
        open={!!deleteTargetRole}
        onOpenChange={(open) => !open && setDeleteTargetRole(null)}
        title="Xóa vai trò"
        description={`Bạn có chắc chắn muốn xóa vai trò "${deleteTargetRole?.name}" (${deleteTargetRole?.code})? Hành động này sẽ loại bỏ quyền hạn của những người dùng đang giữ vai trò này.`}
        variant="destructive"
        confirmText="Xóa vai trò"
        isLoading={deleteRoleMutation.isPending}
        onConfirm={() => {
          if (deleteTargetRole) deleteRoleMutation.mutate(deleteTargetRole.id);
        }}
      />
    </div>
  );
}

