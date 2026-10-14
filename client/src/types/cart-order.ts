import { OrderStatus, PaymentMethod, PaymentStatus } from './enums';
import { ProductVariant, Product } from './catalog';

export interface CartItem {
  id: number;
  cartId: number;
  variantId: number;
  quantity: number;
  variant: ProductVariant & {
    product: Product;
  };
}

export interface Cart {
  id: number;
  userId?: number | null;
  sessionId?: string | null;
  cartItems: CartItem[];
}

export interface OrderItem {
  id: number;
  orderId: number;
  variantId?: number | null;
  productName: string;
  skuCode: string;
  variantValue: string;
  productImageUrl?: string | null;
  unitPrice: number | string;
  quantity: number;
  productId?: number | null;
  productSlug?: string | null;
}

export interface Order {
  id: number;
  orderCode: string;
  userId: number;
  // Chuỗi địa chỉ snapshot chuẩn Backend Prisma
  shippingAddress: string;
  
  // Các trường parse mềm phục vụ hiển thị UI
  receiverName?: string;
  receiverPhone?: string;
  province?: string;
  district?: string;
  ward?: string;
  detailAddress?: string;

  subTotal: number | string;
  shippingFee: number | string;
  shippingMethod: string;
  shippingDiscount: number | string;
  voucherCode?: string | null;
  voucherDiscount: number | string;
  pointUsed: number;
  pointDiscount: number | string;
  pointEarned: number;
  totalAmount: number | string;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  status: OrderStatus;
  customerNote?: string | null;
  orderDate?: string;
  createdAt?: string;
  orderItems: OrderItem[];
  shipment?: unknown;
  paymentTransactions?: unknown[];
}

export interface AddToCartPayload {
  variantId: number;
  quantity: number;
}

export interface UpdateCartItemPayload {
  quantity: number;
}

export interface CreateOrderPayload {
  cartItemIds?: number[];
  shippingAddressId?: number;
  // Nhận dạng Object để backend tạo snapshot hoặc truyền string trực tiếp
  shippingAddress?: string | {
    receiverName: string;
    receiverPhone: string;
    province: string;
    district: string;
    ward: string;
    detailAddress: string;
  };
  paymentMethod: PaymentMethod;
  shippingMethod?: string;
  customerNote?: string;
  voucherCode?: string | null;
  pointUsed?: number;
}

export interface UpdateOrderStatusPayload {
  status: OrderStatus;
}