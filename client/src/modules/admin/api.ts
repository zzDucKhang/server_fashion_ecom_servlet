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

