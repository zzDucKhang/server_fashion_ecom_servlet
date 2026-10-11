# server_fashion_ecom_servlet

C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\
│
├── config/                          # [CẤU HÌNH HỆ THỐNG]
│   ├── AppConfig.java               # Đọc biến môi trường .env (Đã có)
│   └── AppContextListener.java      # Lifecycle server (Khởi động JPA & Redisson) (Đã có)
│
├── common/                          # [DÙNG CHUNG TOÀN HỆ THỐNG]
│   ├── exception/                   # Custom AppException, BadRequest, NotFound,... (Đã có)
│   ├── response/                    # ApiResponse<T>, PagedResponse<T> (Đã có)
│   └── util/                        # Tiện ích chung
│       ├── JPAUtil.java             # Quản lý EntityManagerFactory (Đã có)
│       ├── RedisUtil.java           # Redisson client (Đã có)
│       ├── PasswordUtil.java        # BCrypt hash/verify (Đã có)
│       ├── JwtUtil.java             # Tạo & parse Access/Refresh token (Bổ sung)
│       └── JsonUtil.java            # ObjectMapper wrapper cho parse request/response (Bổ sung)
│
├── presentation/                    # [LAYER 1: PRESENTATION LAYER]
│   ├── filter/                      # Interceptor / Middleware tương đương Node.js
│   │   ├── CorsFilter.java          # Cấu hình CORS Header
│   │   ├── GlobalExceptionFilter.java # Bắt Exception toàn cục -> JSON (Đã có)
│   │   ├── AuthFilter.java          # Thay requireAuth: Parse Bearer JWT token
│   │   └── RoleFilter.java          # Thay roleGuard/permissionGuard
│   │
│   ├── dto/                         # Data Transfer Objects (Thay thế Zod Schema)
│   │   ├── identity/                # RegisterDTO, LoginDTO, VerifyOtpDTO,...
│   │   ├── catalog/                 # CreateProductDTO, UpdateStockDTO,...
│   │   ├── cartorder/               # AddCartItemDTO, CreateOrderDTO,...
│   │   ├── inventory/               # FlashSaleDTO,...
│   │   ├── fulfillment/             # PaymentWebhookDTO, ReturnRequestDTO,...
│   │   └── loyalty/                 # ApplyVoucherDTO, ReviewDTO, SendMessageDTO,...
│   │
│   ├── controller/                  # Servlets (Thay thế Express Controllers + Routes)
│   │   ├── identity/                # AuthServlet, UserServlet, AddressServlet, RoleServlet
│   │   ├── catalog/                 # CategoryServlet, BrandServlet, ProductServlet, VariantServlet
│   │   ├── cartorder/               # CartServlet, OrderServlet
│   │   ├── inventory/               # FlashSaleServlet
│   │   ├── fulfillment/             # PaymentServlet, ShipmentServlet, ReturnServlet, SepayWebhookServlet
│   │   └── loyalty/                 # VoucherServlet, PointServlet, ReviewServlet, ChatServlet
│   │
│   └── websocket/                   # Realtime WebSocket (Jakarta WebSocket)
│       ├── ChatEndpoint.java        # @ServerEndpoint("/ws/chat")
│       └── WsSessionManager.java    # Quản lý session socket trên Redis (tương đương chat.repo)
│
├── business/                        # [LAYER 2: BUSINESS LOGIC LAYER]
│   ├── entity/                      # JPA Entities (Đã có đầy đủ 22 entity của bạn)
│   │   ├── BaseEntity.java
│   │   ├── User.java, Order.java, Product.java,...
│   │   └── enums/
│   │
│   └── service/                     # Nghiệp vụ hệ thống (Chứa các Service logic)
│       ├── identity/
│       │   ├── AuthService.java     # Luồng OTP Redis, JWT token, Register, Login
│       │   ├── UserService.java
│       │   └── RolePermissionService.java
│       ├── catalog/
│       │   ├── ProductService.java
│       │   └── CategoryService.java
│       ├── cartorder/
│       │   ├── CartService.java     # Thêm/bớt giỏ hàng, merge guest cart
│       │   └── OrderService.java    # Checkout, Optimistic lock tồn kho, Snapshot đơn
│       ├── inventory/
│       │   └── FlashSaleService.java
│       ├── fulfillment/
│       │   ├── PaymentService.java  # Verify SePay HMAC, cập nhật đơn
│       │   └── ShipmentService.java
│       └── loyalty/
│           ├── VoucherService.java  # Atomic apply/release voucher qua Redisson
│           ├── PointService.java    # Pessimistic lock cộng/trừ điểm thưởng
│           ├── ReviewService.java   # Hook cập nhật rating cache
│           └── ChatService.java
│
└── dataaccess/                      # [LAYER 3: DATA ACCESS LAYER (DAO)]
├── base/
│   ├── GenericDAO.java          # Interface CRUD cơ bản (findById, save, delete,...)
│   └── AbstractJpaDAO.java      # Class abstract dùng chung JPA EntityManager
│
└── dao/                         # DAO chi tiết cho từng đối tượng (Thay thế Prisma Repo)
├── identity/                # UserDAO, RoleDAO, AddressDAO, SessionDAO
├── catalog/                 # ProductDAO, VariantDAO, CategoryDAO, BrandDAO
├── cartorder/               # CartDAO, OrderDAO, OrderItemDAO
├── inventory/               # FlashSaleDAO
├── fulfillment/             # PaymentDAO, ShipmentDAO, ReturnDAO
└── loyalty/                 # VoucherDAO, PointDAO, ReviewDAO, ChatDAO