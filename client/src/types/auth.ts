import { UserStatus } from './enums'; //[cite: 1, 2]

export interface Permission {
  id: number;
  code: string;
  module: string;
  description?: string | null;
}

export interface Role {
  id: number;
  code: 'ADMIN' | 'STAFF' | 'CUSTOMER' | string; //[cite: 1, 2]
  name: string;
  isActive: boolean;
  permissions?: Permission[];
}

export interface UserProfile {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  avatarUrl?: string | null;
  rewardPoints: number;
  status: UserStatus; //[cite: 1, 2]
  roleId: number;
  role?: Role;
  createdAt: string;
}

export interface ShippingAddress {
  id: number;
  userId: number;
  receiverName: string;
  phoneNumber: string;
  province: string;
  district: string;
  ward: string;
  detailAddress: string;
  isDefault: boolean;
  createdAt?: string;
}

export interface DeviceSession {
  id: number;
  userId: number;
  refreshToken: string;
  ipAddress: string;
  isActive: boolean;
  expiresAt: string;
  createdAt: string;
}

// Payload DTO gửi lên Backend
export interface RegisterPayload {
  fullName: string;
  email: string;
  phoneNumber?: string;
  password: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface VerifyCodePayload {
  email: string;
  type: 'REGISTER' | 'FORGOT_PASSWORD'; //[cite: 1, 2]
  code: string;
}

export interface ResetPasswordPayload {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ChangePasswordPayload {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface CreateAddressPayload {
  receiverName: string;
  phoneNumber: string;
  province: string;
  district: string;
  ward: string;
  detailAddress: string;
  isDefault?: boolean;
}