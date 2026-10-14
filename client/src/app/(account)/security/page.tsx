'use client';

import * as React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Eye, EyeOff, Loader2, ShieldCheck } from 'lucide-react';
import { changePasswordSchema, type ChangePasswordFormData } from '@/modules/auth/schemas';
import { useAuth } from '@/modules/auth/hooks/use-auth';
import { FormField } from '@/components/ui/form-field';
import { Button } from '@/components/ui/button';

export default function SecurityPage() {
  const [showOld, setShowOld] = React.useState(false);
  const [showNew, setShowNew] = React.useState(false);
  const [showConfirm, setShowConfirm] = React.useState(false);
  const { changePasswordMutation } = useAuth();

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ChangePasswordFormData>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: {
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
  });

  const onSubmit = (data: ChangePasswordFormData) => {
    changePasswordMutation.mutate(data, {
      onSuccess: () => {
        reset();
      },
    });
  };

  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="pb-6 border-b border-[#EAE6DF] mb-6 flex items-start justify-between">
        <div>
          <h2 className="text-xl font-semibold text-neutral-900">
            Bảo mật & Đổi mật khẩu
          </h2>
          <p className="text-xs text-neutral-500 mt-1">
            Định kỳ thay đổi mật khẩu để bảo vệ an toàn cho tài khoản và thông tin mua hàng.
          </p>
        </div>
        <div className="p-2.5 rounded-xl bg-neutral-100 text-neutral-800 hidden sm:block">
          <ShieldCheck className="size-5" />
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 max-w-lg">
        <div className="relative">
          <FormField
            control={control}
            name="oldPassword"
            label="Mật khẩu hiện tại"
            type={showOld ? 'text' : 'password'}
            placeholder="••••••••"
            error={errors.oldPassword?.message}
          />
          <button
            type="button"
            onClick={() => setShowOld(!showOld)}
            className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
            tabIndex={-1}
          >
            {showOld ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        </div>

        <div className="relative">
          <FormField
            control={control}
            name="newPassword"
            label="Mật khẩu mới"
            type={showNew ? 'text' : 'password'}
            placeholder="Tối thiểu 8 ký tự (hoa, thường, số, ký tự)"
            error={errors.newPassword?.message}
          />
          <button
            type="button"
            onClick={() => setShowNew(!showNew)}
            className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
            tabIndex={-1}
          >
            {showNew ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        </div>

        <div className="relative">
          <FormField
            control={control}
            name="confirmPassword"
            label="Xác nhận mật khẩu mới"
            type={showConfirm ? 'text' : 'password'}
            placeholder="Nhập lại mật khẩu mới"
            error={errors.confirmPassword?.message}
          />
          <button
            type="button"
            onClick={() => setShowConfirm(!showConfirm)}
            className="absolute right-3.5 top-[34px] text-neutral-400 hover:text-neutral-700 transition"
            tabIndex={-1}
          >
            {showConfirm ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        </div>

        <div className="pt-4">
          <Button
            type="submit"
            disabled={changePasswordMutation.isPending}
            className="h-11 px-8 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium shadow-sm transition active:scale-[0.99]"
          >
            {changePasswordMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-2" />
                Đang cập nhật mật khẩu...
              </>
            ) : (
              'Cập nhật mật khẩu'
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}

