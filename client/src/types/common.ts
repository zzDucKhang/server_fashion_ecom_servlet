// Khung phản hồi API chuẩn từ Express server
export interface ApiResponse<T = any> {
  statusCode: number;
  message?: string;
  data: T;
  success?: boolean;
}

// Khung phân trang chuẩn (Pagination metadata)
export interface PaginatedResult<T> {
  data: T[];
  totalItems: number;
  page: number;
  limit: number;
  totalPages: number;
}

// Tham số Query phân trang cơ sở
export interface BasePaginationQuery {
  page?: number;
  limit?: number;
  search?: string;
  lang?: string;
}

// Cấu trúc lỗi trả về từ Validation Pipe (Zod / Express errorFilter)
export interface ApiErrorResponse {
  statusCode: number;
  message: string;
  errors?: Record<string, string[]>;
  stack?: string;
}