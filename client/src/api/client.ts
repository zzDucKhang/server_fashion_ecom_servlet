// client/src/api/client.ts
import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { API_ENDPOINTS } from './endpoints';

const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000/api/v1';

export const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

const getClientCookie = (name: string): string | null => {
  if (typeof document === 'undefined') return null;
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  return match ? decodeURIComponent(match[2]) : null;
};

// 1. Request Interceptor
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('accessToken') || getClientCookie('accessToken');
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }

      let sessionId = localStorage.getItem('sessionId');
      if (!sessionId) {
        sessionId = crypto.randomUUID();
        localStorage.setItem('sessionId', sessionId);
      }
      if (config.headers) {
        config.headers['x-session-id'] = sessionId;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ====================================================================
// CƠ CHẾ HÀNG ĐỢI REFRESH TOKEN (QUEUE MECHANISM)
// ====================================================================
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: any) => void;
}> = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token as string);
    }
  });
  failedQueue = [];
};

// 2. Response Interceptor
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Bắt lỗi 401 (Hết hạn Token)
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      
      // NẾU ĐANG CÓ REQUEST ĐI REFRESH TOKEN RỒI -> CÁC REQUEST KHÁC XẾP HÀNG CHỜ
      if (isRefreshing) {
        return new Promise(function (resolve, reject) {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = 'Bearer ' + token;
            }
            return apiClient(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      // ĐÁNH DẤU REQUEST ĐẦU TIÊN BẮT ĐẦU ĐI REFRESH
      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = typeof window !== 'undefined' ? localStorage.getItem('refreshToken') : null;

      if (!refreshToken) {
        if (typeof window !== 'undefined') {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }

      try {
        // GỌI API REFRESH TOKEN BẰNG AXIOS GỐC (TRÁNH BỊ INTERCEPTOR CHẶN VÒNG LẶP LỖI 401)
        const response = await axios.post(`${BASE_URL}${API_ENDPOINTS.IDENTITY.REFRESH}`, {
          refreshToken,
        });
        
        const newAccessToken = response.data.data.accessToken;
        const newRefreshToken = response.data.data.refreshToken; // Đã lưu cả refresh token mới

        if (typeof window !== 'undefined') {
          localStorage.setItem('accessToken', newAccessToken);
          localStorage.setItem('refreshToken', newRefreshToken);
          document.cookie = `accessToken=${newAccessToken}; path=/; max-age=3600`;
        }

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        }

        // BÁO CHO TẤT CẢ REQUEST TRONG HÀNG ĐỢI RẰNG ĐÃ CÓ TOKEN MỚI -> HÃY CHẠY LẠI ĐI
        processQueue(null, newAccessToken);
        
        // Chạy lại request đầu tiên
        return apiClient(originalRequest);
      } catch (err) {
        // NẾU REFRESH TOKEN CŨNG HẾT HẠN HOẶC BỊ LỖI -> ĐÁNH SẬP TẤT CẢ VÀ ĐẨY RA TRANG LOGIN
        processQueue(err, null);
        
        if (typeof window !== 'undefined') {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user'); // Xóa sách bóng session
          window.location.href = '/login?session_expired=true';
        }
        return Promise.reject(err);
      } finally {
        // MỞ LẠI CỔNG CHO CÁC LẦN REFRESH TƯƠNG LAI
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);