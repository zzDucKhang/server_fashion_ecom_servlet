import { Metadata } from 'next';
import { ProfileForm } from '@/modules/user/components/profile-form';

export const metadata: Metadata = {
  title: 'Hồ sơ cá nhân — Fashion Store',
  description: 'Quản lý thông tin cá nhân và điểm thưởng tài khoản của bạn.',
};

export default function ProfilePage() {
  return (
    <div className="bg-white rounded-2xl border border-[#EAE6DF] p-6 sm:p-8 shadow-xs">
      <div className="pb-6 border-b border-[#EAE6DF] mb-6">
        <h2 className="text-xl font-semibold text-neutral-900">
          Thông tin cá nhân
        </h2>
        <p className="text-xs text-neutral-500 mt-1">
          Cập nhật thông tin chi tiết để hỗ trợ giao hàng và liên hệ nhanh chóng.
        </p>
      </div>

      <ProfileForm />
    </div>
  );
}

