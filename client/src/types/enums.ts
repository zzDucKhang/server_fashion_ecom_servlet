// Trạng thái người dùng
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'BLOCKED'; //[cite: 1, 2]

// Loại mã xác thực OTP
export type VerificationCodeType = 'REGISTER' | 'FORGOT_PASSWORD' | 'LOGIN' | 'DISABLE_2FA'; //[cite: 1, 2]

// Trạng thái mở bán của sản phẩm
export type ProductStatus = 'DRAFT' | 'PUBLISHED' | 'OUT_OF_STOCK' | 'ARCHIVED'; //[cite: 1, 2]

// Loại biến động kho hàng
export type InventoryTransType = 'IMPORT' | 'EXPORT_ORDER' | 'RETURN_INBOUND' | 'ADJUSTMENT'; //[cite: 1, 2]

// Phương thức thanh toán được hỗ trợ
export type PaymentMethod = 'COD' | 'VNPAY' | 'MOMO' | 'BANK_TRANSFER'; //[cite: 1, 2]

// Trạng thái thanh toán của hóa đơn
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED'; //[cite: 1, 2]

// Vòng đời trạng thái đơn hàng
export type OrderStatus = 
  | 'PENDING_PAYMENT' 
  | 'CONFIRMED' 
  | 'PROCESSING' 
  | 'SHIPPED' 
  | 'DELIVERED' 
  | 'CANCELLED' 
  | 'RETURNED'; //[cite: 1, 2]

// Tiến trình vận đơn của đối tác giao hàng
export type ShipmentStatus = 'PENDING' | 'PICKED_UP' | 'IN_TRANSIT' | 'DELIVERED' | 'FAILED'; //[cite: 1, 2]

// Trạng thái duyệt khiếu nại đổi trả
export type ReturnStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED'; //[cite: 1, 2]

// Cách thức giảm giá của Voucher
export type DiscountType = 'PERCENTAGE' | 'FIXED_AMOUNT'; //[cite: 1, 2]

// Biến động điểm thưởng thành viên
export type PointTransType = 'EARN' | 'REDEEM' | 'REFUND' | 'EXPIRE'; //[cite: 1, 2]