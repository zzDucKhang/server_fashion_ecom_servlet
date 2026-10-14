import { ShipmentStatus, PaymentStatus, ReturnStatus } from './enums'; //[cite: 1, 2]
import { OrderItem } from './cart-order';

export interface Shipment {
  id: number;
  orderId: number;
  carrier: string;
  trackingCode: string;
  actualShippingFee: number | string;
  status: ShipmentStatus; //[cite: 1, 2]
  estimatedDelivery?: string | null;
  createdAt: string;
  order?: {
    orderCode: string;
    receiverName: string;
    receiverPhone: string;
  };
}

export interface PaymentTransaction {
  id: number;
  orderId: number;
  provider: string;
  providerTransId: string;
  amount: number | string;
  status: PaymentStatus; //[cite: 1, 2]
  createdAt: string;
  order?: {
    orderCode: string;
    totalAmount: number | string;
    paymentMethod: string;
  };
}

export interface ReturnMedia {
  id: number;
  returnRequestId: number;
  mediaUrl: string;
  mediaType: string;
}

export interface ReturnRequest {
  id: number;
  orderItemId: number;
  quantity: number;
  reason: string;
  description?: string | null;
  status: ReturnStatus; //[cite: 1, 2]
  createdAt: string;
  orderItem?: OrderItem;
  medias?: ReturnMedia[];
}

// Payload tạo đơn vận chuyển (Admin)
export interface CreateShipmentPayload {
  orderId: number;
  carrier: string;
  trackingCode: string;
  actualShippingFee: number;
  status?: ShipmentStatus; //[cite: 1, 2]
  estimatedDelivery?: string;
}

// Payload khách gửi yêu cầu hoàn trả
export interface CreateReturnPayload {
  orderItemId: number;
  quantity: number;
  reason: string;
  description?: string;
  medias?: Array<{
    mediaUrl: string;
    mediaType: string;
  }>;
}