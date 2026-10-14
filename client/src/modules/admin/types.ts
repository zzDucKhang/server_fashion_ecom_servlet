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

