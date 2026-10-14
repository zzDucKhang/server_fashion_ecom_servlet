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

