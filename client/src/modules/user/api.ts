import { apiClient } from '@/api/client';
import type { User } from './types';

export function getUser(id: string) {
  return apiClient.get<User>(`/users/${id}`);
}
