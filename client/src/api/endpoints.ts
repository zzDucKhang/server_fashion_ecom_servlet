export const API_ENDPOINTS = {
  IDENTITY: {
    LOGIN: '/identity/auth/login',
    REGISTER: '/identity/auth/register',
    REFRESH: '/identity/auth/refresh',
    VERIFY_CODE: '/identity/auth/verify-code',
    FORGOT_PASSWORD: '/identity/auth/forgot-password',
    RESET_PASSWORD: '/identity/auth/reset-password',
    CHANGE_PASSWORD: '/identity/auth/change-password',
    LOGOUT: '/identity/auth/logout',
    ADDRESSES: '/identity/addresses',
    USERS: '/identity/users',
    ROLES: '/identity/roles',
    PERMISSIONS: '/identity/roles/permissions',
  },
  CATALOG: {
    PRODUCTS: '/catalog/products',
    CATEGORIES: '/catalog/categories',
    BRANDS: '/catalog/brands',
    TAGS: '/catalog/tags',
    VARIANTS: '/catalog/variants',
  },
  CART_ORDER: {
    CART: '/cart-order/cart',
    CART_ITEMS: '/cart-order/cart/items',
    CART_MERGE: '/cart-order/cart/merge',
    ORDERS: '/cart-order/orders',
  },
  FULFILLMENT: {
    SHIPMENTS: '/fulfillment/shipments',
    PAYMENTS: '/fulfillment/payments',
    RETURNS: '/fulfillment/returns',
  },
  INVENTORY_SALE: {
    INVENTORY: '/inventory-sale/inventory',
    FLASH_SALES: '/inventory-sale/flash-sales',
  },
  LOYALTY_ENGAGE: {
    VOUCHERS: '/loyalty-engage/vouchers',
    POINTS: '/loyalty-engage/points',
    REVIEWS: '/loyalty-engage/reviews',
  },
  CHAT: {
    ACTIVE: '/loyalty-engage/chat/messages',
    HISTORY: '/loyalty-engage/chat/messages',
    SEND: '/loyalty-engage/chat/messages',
    READ: '/loyalty-engage/chat/messages/read',
    UNREAD: '/loyalty-engage/chat/messages/unread',
    CONVERSATIONS: '/loyalty-engage/chat/conversations',
    HEARTBEAT: '/loyalty-engage/chat/sessions/heartbeat'
  },
  COMMON: {
    UPLOAD: '/common/upload', // hoặc endpoint thực tế của server bạn
  }
} as const;