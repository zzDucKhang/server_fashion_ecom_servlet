'use client';

import React, { createContext, useContext, useSyncExternalStore, useEffect } from 'react'; // <-- Thêm useEffect
import { UserProfile } from '@/types';
import { cartApi } from '@/api/cart.api';
import { apiClient } from '@/api/client'; // <-- Thêm apiClient

interface AuthContextType {
  user: UserProfile | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (accessToken: string, refreshToken: string, user: UserProfile) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

function subscribe(callback: () => void) {
  window.addEventListener('storage', callback);
  window.addEventListener('local-storage-change', callback);
  return () => {
    window.removeEventListener('storage', callback);
    window.removeEventListener('local-storage-change', callback);
  };
}

function getStoredUserSnapshot(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('user');
}

function getStoredUserServerSnapshot(): string | null {
  return null;
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const userString = useSyncExternalStore(
    subscribe,
    getStoredUserSnapshot,
    getStoredUserServerSnapshot
  );

  const user: UserProfile | null = React.useMemo(() => {
    if (!userString) return null;
    try {
      return JSON.parse(userString);
    } catch {
      return null;
    }
  }, [userString]);


  useEffect(() => {
    if (user?.id) {
      apiClient.get(`/identity/users/${user.id}`)
        .then(res => {
          const freshUser = res.data?.data;
          if (freshUser && typeof freshUser.rewardPoints === 'number') {
            // Nếu điểm trong DB khác với dưới LocalStorage -> Cập nhật đè lên LocalStorage
            if (freshUser.rewardPoints !== user.rewardPoints || freshUser.status !== user.status) {
              const updatedUser = { ...user, ...freshUser };
              localStorage.setItem('user', JSON.stringify(updatedUser));
              window.dispatchEvent(new Event('local-storage-change')); // Ép Header render lại số điểm ngay lập tức
            }
          }
        })
        .catch(() => {}); // Nuốt lỗi im lặng để không phiền khách nếu mất mạng
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id]); // Chỉ quét 1 lần khi load app

  const login = async (accessToken: string, refreshToken: string, newUser: UserProfile) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(newUser));
    document.cookie = `accessToken=${accessToken}; path=/; max-age=3600`;

    window.dispatchEvent(new Event('local-storage-change'));

    try {
      await cartApi.mergeCart();
    } catch (err) {
      console.error('Merge cart failed:', err);
    }
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    document.cookie = 'accessToken=; path=/; max-age=0';

    window.dispatchEvent(new Event('local-storage-change'));
    window.location.href = '/login';
  };

  const value = React.useMemo(
    () => ({
      user,
      isAuthenticated: !!user,
      isAdmin: user?.role?.code === 'ADMIN',
      login,
      logout,
    }),
    [user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);