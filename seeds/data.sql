

-- ==============================================================================
-- 1. LÀM SẠCH DỮ LIỆU CŨ (CLEANUP)
-- ==============================================================================
TRUNCATE TABLE
    "_RolePermissions",
    "Message",
    "Chat",
    "ReviewMedia",
    "ProductReview",
    "PointTransaction",
    "FlashSaleItem",
    "FlashSaleCampaign",
    "ReturnMedia",
    "ReturnRequest",
    "PaymentTransaction",
    "Shipment",
    "OrderItem",
    "Order",
    "CartItem",
    "Cart",
    "ProductImage",
    "ProductVariant",
    "Product",
    "Voucher",
    "Tag",
    "Brand",
    "Category",
    "DeviceSession",
    "ShippingAddress",
    "User",
    "Permission",
    "Role"
CASCADE;

-- ==============================================================================
-- 2. SEED ROLE & PERMISSION (Phân quyền RBAC)
-- ==============================================================================
-- Danh sách Vai trò (Role)
INSERT INTO "Role" ("id", "code", "name", "isActive", "createdAt", "updatedAt") VALUES
                                                                                    (1, 'ADMIN', 'Quản trị viên tối cao', true, NOW(), NOW()),
                                                                                    (2, 'STAFF', 'Nhân viên quản lý cửa hàng', true, NOW(), NOW()),
                                                                                    (3, 'CUSTOMER', 'Khách hàng', true, NOW(), NOW());

-- Danh sách Quyền hạn (Permission)
INSERT INTO "Permission" ("id", "code", "module", "description", "createdAt", "updatedAt") VALUES
                                                                                               (1, 'USER:VIEW', 'IDENTITY', 'Xem danh sách và chi tiết người dùng', NOW(), NOW()),
                                                                                               (2, 'USER:UPDATE', 'IDENTITY', 'Cập nhật thông tin người dùng', NOW(), NOW()),
                                                                                               (3, 'USER:STATUS_UPDATE', 'IDENTITY', 'Khóa / Mở khóa tài khoản người dùng', NOW(), NOW()),
                                                                                               (4, 'USER:DELETE', 'IDENTITY', 'Xóa người dùng', NOW(), NOW()),
                                                                                               (5, 'ROLE:VIEW', 'IDENTITY', 'Xem danh sách và chi tiết vai trò', NOW(), NOW()),
                                                                                               (6, 'ROLE:CREATE', 'IDENTITY', 'Tạo vai trò mới', NOW(), NOW()),
                                                                                               (7, 'ROLE:UPDATE', 'IDENTITY', 'Cập nhật vai trò và gán quyền', NOW(), NOW()),
                                                                                               (8, 'ROLE:DELETE', 'IDENTITY', 'Xóa vai trò', NOW(), NOW()),
                                                                                               (9, 'PERMISSION:VIEW', 'IDENTITY', 'Xem danh sách quyền hệ thống', NOW(), NOW()),
                                                                                               (10, 'PERMISSION:CREATE', 'IDENTITY', 'Tạo quyền hệ thống mới', NOW(), NOW()),
                                                                                               (11, 'PERMISSION:UPDATE', 'IDENTITY', 'Cập nhật quyền hệ thống', NOW(), NOW()),
                                                                                               (12, 'PERMISSION:DELETE', 'IDENTITY', 'Xóa quyền hệ thống', NOW(), NOW()),
                                                                                               (13, 'CATEGORY:VIEW', 'CATALOG', 'Xem danh mục sản phẩm', NOW(), NOW()),
                                                                                               (14, 'CATEGORY:CREATE', 'CATALOG', 'Tạo danh mục sản phẩm', NOW(), NOW()),
                                                                                               (15, 'CATEGORY:UPDATE', 'CATALOG', 'Cập nhật danh mục sản phẩm', NOW(), NOW()),
                                                                                               (16, 'CATEGORY:DELETE', 'CATALOG', 'Xóa danh mục sản phẩm', NOW(), NOW()),
                                                                                               (17, 'BRAND:VIEW', 'CATALOG', 'Xem thương hiệu', NOW(), NOW()),
                                                                                               (18, 'BRAND:CREATE', 'CATALOG', 'Tạo thương hiệu', NOW(), NOW()),
                                                                                               (19, 'BRAND:UPDATE', 'CATALOG', 'Cập nhật thương hiệu', NOW(), NOW()),
                                                                                               (20, 'BRAND:DELETE', 'CATALOG', 'Xóa thương hiệu', NOW(), NOW()),
                                                                                               (21, 'PRODUCT:VIEW', 'CATALOG', 'Xem sản phẩm', NOW(), NOW()),
                                                                                               (22, 'PRODUCT:CREATE', 'CATALOG', 'Tạo sản phẩm mới', NOW(), NOW()),
                                                                                               (23, 'PRODUCT:UPDATE', 'CATALOG', 'Cập nhật thông tin sản phẩm', NOW(), NOW()),
                                                                                               (24, 'PRODUCT:DELETE', 'CATALOG', 'Xóa sản phẩm', NOW(), NOW()),
                                                                                               (25, 'VARIANT:MANAGE', 'CATALOG', 'Đồng bộ và quản lý biến thể, thuộc tính', NOW(), NOW()),
                                                                                               (26, 'TAG:MANAGE', 'CATALOG', 'Quản lý thẻ tag sản phẩm', NOW(), NOW()),
                                                                                               (27, 'ORDER:VIEW', 'CART_ORDER', 'Xem danh sách và chi tiết đơn hàng', NOW(), NOW()),
                                                                                               (28, 'ORDER:CREATE', 'CART_ORDER', 'Tạo đơn hàng mới', NOW(), NOW()),
                                                                                               (29, 'ORDER:CANCEL', 'CART_ORDER', 'Hủy đơn hàng', NOW(), NOW()),
                                                                                               (30, 'ORDER:STATUS_UPDATE', 'CART_ORDER', 'Cập nhật trạng thái đơn hàng (Duyệt, Giao hàng)', NOW(), NOW()),
                                                                                               (31, 'INVENTORY:VIEW', 'INVENTORY_SALE', 'Xem lịch sử biến động và tồn kho', NOW(), NOW()),
                                                                                               (32, 'INVENTORY:ADJUST', 'INVENTORY_SALE', 'Điều chỉnh nhập/xuất tồn kho thủ công', NOW(), NOW()),
                                                                                               (33, 'FLASH_SALE:VIEW', 'INVENTORY_SALE', 'Xem danh sách chiến dịch Flash Sale', NOW(), NOW()),
                                                                                               (34, 'FLASH_SALE:CREATE', 'INVENTORY_SALE', 'Tạo chiến dịch Flash Sale', NOW(), NOW()),
                                                                                               (35, 'FLASH_SALE:UPDATE', 'INVENTORY_SALE', 'Cập nhật chiến dịch Flash Sale', NOW(), NOW()),
                                                                                               (36, 'FLASH_SALE:DELETE', 'INVENTORY_SALE', 'Xóa chiến dịch Flash Sale', NOW(), NOW()),
                                                                                               (37, 'SHIPMENT:VIEW', 'FULFILLMENT', 'Tra cứu thông tin vận chuyển đơn hàng', NOW(), NOW()),
                                                                                               (38, 'SHIPMENT:MANAGE', 'FULFILLMENT', 'Tạo, cập nhật trạng thái lô hàng vận chuyển', NOW(), NOW()),
                                                                                               (39, 'PAYMENT:VIEW', 'FULFILLMENT', 'Xem lịch sử giao dịch thanh toán', NOW(), NOW()),
                                                                                               (40, 'RETURN:VIEW', 'FULFILLMENT', 'Xem danh sách yêu cầu đổi trả', NOW(), NOW()),
                                                                                               (41, 'RETURN:CREATE', 'FULFILLMENT', 'Tạo yêu cầu đổi trả hàng', NOW(), NOW()),
                                                                                               (42, 'RETURN:UPDATE', 'FULFILLMENT', 'Duyệt hoặc từ chối yêu cầu đổi trả hàng', NOW(), NOW()),
                                                                                               (43, 'VOUCHER:VIEW', 'LOYALTY_ENGAGE', 'Xem danh sách mã giảm giá', NOW(), NOW()),
                                                                                               (44, 'VOUCHER:CREATE', 'LOYALTY_ENGAGE', 'Tạo mã giảm giá mới', NOW(), NOW()),
                                                                                               (45, 'VOUCHER:UPDATE', 'LOYALTY_ENGAGE', 'Cập nhật mã giảm giá', NOW(), NOW()),
                                                                                               (46, 'VOUCHER:DELETE', 'LOYALTY_ENGAGE', 'Xóa mã giảm giá', NOW(), NOW()),
                                                                                               (47, 'POINT:VIEW', 'LOYALTY_ENGAGE', 'Xem lịch sử tích và dùng điểm thưởng', NOW(), NOW()),
                                                                                               (48, 'POINT:MANAGE', 'LOYALTY_ENGAGE', 'Cộng hoặc trừ điểm thưởng thủ công', NOW(), NOW()),
                                                                                               (49, 'REVIEW:VIEW', 'LOYALTY_ENGAGE', 'Xem đánh giá sản phẩm', NOW(), NOW()),
                                                                                               (50, 'REVIEW:CREATE', 'LOYALTY_ENGAGE', 'Đánh giá sản phẩm đã mua', NOW(), NOW()),
                                                                                               (51, 'REVIEW:REPLY', 'LOYALTY_ENGAGE', 'Phản hồi đánh giá của khách hàng', NOW(), NOW()),
                                                                                               (52, 'REVIEW:HIDE', 'LOYALTY_ENGAGE', 'Ẩn đánh giá vi phạm', NOW(), NOW()),
                                                                                               (53, 'CHAT:ACCESS', 'LOYALTY_ENGAGE', 'Tham gia nhắn tin tư vấn trực tiếp', NOW(), NOW());

-- Gán toàn bộ quyền cho ADMIN (Role ID = 1)
INSERT INTO "_RolePermissions" ("A", "B")
SELECT 1, id FROM "Permission";

-- Gán quyền cho STAFF (Role ID = 2, ngoại trừ quản lý vai trò và xoá người dùng)
INSERT INTO "_RolePermissions" ("A", "B")
SELECT 2, id FROM "Permission"
WHERE "code" NOT IN ('ROLE:CREATE', 'ROLE:UPDATE', 'ROLE:DELETE', 'PERMISSION:CREATE', 'PERMISSION:UPDATE', 'PERMISSION:DELETE', 'USER:DELETE');

-- Gán quyền cho CUSTOMER (Role ID = 3)
INSERT INTO "_RolePermissions" ("A", "B")
SELECT 3, id FROM "Permission"
WHERE "code" IN ('PRODUCT:VIEW', 'CATEGORY:VIEW', 'BRAND:VIEW', 'ORDER:CREATE', 'ORDER:VIEW', 'ORDER:CANCEL', 'FLASH_SALE:VIEW', 'VOUCHER:VIEW', 'POINT:VIEW', 'REVIEW:VIEW', 'REVIEW:CREATE', 'RETURN:CREATE', 'CHAT:ACCESS');

-- ==============================================================================
-- 3. SEED USER & ĐỊA CHỈ & PHIÊN ĐĂNG NHẬP
-- ==============================================================================
-- PasswordHash tương ứng với: Password123@
INSERT INTO "User" ("id", "fullName", "email", "phoneNumber", "passwordHash", "avatarUrl", "rewardPoints", "status", "roleId", "createdAt", "updatedAt") VALUES
                                                                                                                                                             (1, 'Nguyễn Quản Trị', 'admin@fashion.com', '0901000001', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 5000, 'ACTIVE', 1, NOW(), NOW()),
                                                                                                                                                             (2, 'Trần Nhân Viên', 'staff@fashion.com', '0901000002', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 200, 'ACTIVE', 2, NOW(), NOW()),
                                                                                                                                                             (3, 'Lê Hoàng Nam', 'nam.le@gmail.com', '0901000003', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 1200, 'ACTIVE', 2, NOW(), NOW()),
                                                                                                                                                             (4, 'Phạm Thị Thảo', 'thao.pham@gmail.com', '0901000004', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 450, 'ACTIVE', 2, NOW(), NOW()),
                                                                                                                                                             (5, 'Vũ Minh Tuấn', 'tuan.vu@gmail.com', '0901000005', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 0, 'ACTIVE', 3, NOW(), NOW()),
                                                                                                                                                             (6, 'Hoàng Mai Anh', 'maianh.hoang@gmail.com', '0901000006', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 3400, 'ACTIVE', 3, NOW(), NOW()),
                                                                                                                                                             (7, 'Đặng Quốc Bảo', 'bao.dang@gmail.com', '0901000007', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 80, 'ACTIVE', 3, NOW(), NOW()),
                                                                                                                                                             (8, 'Bùi Phương Ly', 'ly.bui@gmail.com', '0901000008', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 950, 'ACTIVE', 3, NOW(), NOW()),
                                                                                                                                                             (9, 'Đỗ Tiến Dũng', 'dung.do@gmail.com', '0901000009', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 0, 'INACTIVE', 3, NOW(), NOW()),
                                                                                                                                                             (10, 'Ngô Khánh Linh', 'linh.ngo@gmail.com', '0901000010', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkqMAtFA44.1yvO0V0L9m8f2aG6eq', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80', 10, 'BLOCKED', 3, NOW(), NOW());

INSERT INTO "ShippingAddress" ("id", "userId", "receiverName", "phoneNumber", "province", "district", "ward", "detailAddress", "isDefault", "createdAt", "updatedAt") VALUES
                                                                                                                                                                          (1, 1, 'Nguyễn Quản Trị', '0901000001', 'Thành phố Hồ Chí Minh', 'Quận 1', 'Phường 01', '100 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (2, 2, 'Trần Nhân Viên', '0901000002', 'Thành phố Hồ Chí Minh', 'Quận 2', 'Phường 02', '115 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (3, 3, 'Lê Hoàng Nam', '0901000003', 'Thành phố Hồ Chí Minh', 'Quận 3', 'Phường 03', '130 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (4, 4, 'Phạm Thị Thảo', '0901000004', 'Thành phố Hồ Chí Minh', 'Quận 4', 'Phường 04', '145 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (5, 5, 'Vũ Minh Tuấn', '0901000005', 'Thành phố Hồ Chí Minh', 'Quận 5', 'Phường 05', '160 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (6, 6, 'Hoàng Mai Anh', '0901000006', 'Thành phố Hồ Chí Minh', 'Quận 6', 'Phường 06', '175 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (7, 7, 'Đặng Quốc Bảo', '0901000007', 'Thành phố Hồ Chí Minh', 'Quận 7', 'Phường 07', '190 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (8, 8, 'Bùi Phương Ly', '0901000008', 'Thành phố Hồ Chí Minh', 'Quận 8', 'Phường 08', '205 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (9, 9, 'Đỗ Tiến Dũng', '0901000009', 'Thành phố Hồ Chí Minh', 'Quận 9', 'Phường 09', '220 Đường Lê Lợi', true, NOW(), NOW()),
                                                                                                                                                                          (10, 10, 'Ngô Khánh Linh', '0901000010', 'Thành phố Hồ Chí Minh', 'Quận 10', 'Phường 10', '235 Đường Lê Lợi', true, NOW(), NOW());

INSERT INTO "DeviceSession" ("id", "userId", "refreshToken", "ipAddress", "isActive", "expiresAt", "createdAt", "updatedAt") VALUES
                                                                                                                                 (1, 1, 'refresh_token_sample_string_hash_1_uuid', '192.168.1.10', true, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (2, 2, 'refresh_token_sample_string_hash_2_uuid', '192.168.1.11', false, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (3, 3, 'refresh_token_sample_string_hash_3_uuid', '192.168.1.12', true, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (4, 4, 'refresh_token_sample_string_hash_4_uuid', '192.168.1.13', false, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (5, 5, 'refresh_token_sample_string_hash_5_uuid', '192.168.1.14', true, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (6, 6, 'refresh_token_sample_string_hash_6_uuid', '192.168.1.15', false, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (7, 7, 'refresh_token_sample_string_hash_7_uuid', '192.168.1.16', true, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (8, 8, 'refresh_token_sample_string_hash_8_uuid', '192.168.1.17', false, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (9, 9, 'refresh_token_sample_string_hash_9_uuid', '192.168.1.18', true, NOW() + INTERVAL '7 days', NOW(), NOW()),
                                                                                                                                 (10, 10, 'refresh_token_sample_string_hash_10_uuid', '192.168.1.19', false, NOW() + INTERVAL '7 days', NOW(), NOW());

-- ==============================================================================
-- 4. SEED CATALOG (Danh mục, Thương hiệu, Tag, Sản phẩm, Biến thể)
-- ==============================================================================
INSERT INTO "Category" ("id", "defaultName", "iconUrl", "isVisible", "createdAt", "updatedAt") VALUES
                                                                                                   (1, 'Áo Nam', 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (2, 'Quần Nam', 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (3, 'Áo Nữ', 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (4, 'Váy & Đầm', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (5, 'Áo Khoác & Blazer', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (6, 'Đồ Thể Thao', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (7, 'Phụ Kiện Thời Trang', 'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (8, 'Giày & Dép', 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (9, 'Nón & Mũ', 'https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW()),
                                                                                                   (10, 'Đồ Lót & Mặc Nhà', 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=800&q=80', true, NOW(), NOW());

INSERT INTO "Brand" ("id", "defaultName", "logoUrl", "createdAt", "updatedAt") VALUES
                                                                                   (1, 'Morrow Studio', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (2, 'Form & Field', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (3, 'House of Common', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (4, 'Sunday Objects', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (5, 'Lumen Goods', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (6, 'Urban Minimal', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (7, 'Nordic Clean', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (8, 'Cotton Atelier', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (9, 'Denim Craft', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW()),
                                                                                   (10, 'Silk & Linen', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=300&q=80', NOW(), NOW());

INSERT INTO "Tag" ("id", "name", "createdAt", "updatedAt") VALUES
                                                               (1, 'Hot Trend', NOW(), NOW()),
                                                               (2, 'Bestseller', NOW(), NOW()),
                                                               (3, 'Mùa Hè', NOW(), NOW()),
                                                               (4, 'Thu Đông', NOW(), NOW()),
                                                               (5, 'Vintage', NOW(), NOW()),
                                                               (6, 'Minimalist', NOW(), NOW()),
                                                               (7, 'Cotton 100%', NOW(), NOW()),
                                                               (8, 'Cao Cấp', NOW(), NOW()),
                                                               (9, 'Giảm Giá', NOW(), NOW()),
                                                               (10, 'Mới Về', NOW(), NOW());

INSERT INTO "Product" ("id", "defaultName", "thumbnailUrl", "status", "categoryId", "brandId", "createdAt", "updatedAt") VALUES
                                                                                                                             (1, 'Áo Sơ Mi Linen Form Rộng', 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 1, 1, NOW(), NOW()),
                                                                                                                             (2, 'Quần Tây Baggy Xếp Ly', 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 2, 2, NOW(), NOW()),
                                                                                                                             (3, 'Áo Thun Heavyweight 260gsm', 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 3, 3, NOW(), NOW()),
                                                                                                                             (4, 'Chân Váy Chữ A Lưng Cao', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 4, 4, NOW(), NOW()),
                                                                                                                             (5, 'Áo Khoác Blazer 2 Hàng Nút', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 5, 5, NOW(), NOW()),
                                                                                                                             (6, 'Quần Jeans Ống Suông Indigo', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 6, 6, NOW(), NOW()),
                                                                                                                             (7, 'Áo Len Dệt Kim Cổ Tròn', 'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 7, 7, NOW(), NOW()),
                                                                                                                             (8, 'Túi Canvas Đeo Vai Minimal', 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 8, 8, NOW(), NOW()),
                                                                                                                             (9, 'Mũ Bucket Cotton Washed', 'https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 9, 9, NOW(), NOW()),
                                                                                                                             (10, 'Áo Hoodie Nỉ Bông Unisex', 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=800&q=80', 'PUBLISHED', 10, 10, NOW(), NOW());

INSERT INTO "ProductImage" ("id", "productId", "imageUrl", "displayOrder", "createdAt") VALUES
                                                                                            (1, 1, 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (2, 2, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (3, 3, 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (4, 4, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (5, 5, 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (6, 6, 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (7, 7, 'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (8, 8, 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (9, 9, 'https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=800&q=80', 1, NOW()),
                                                                                            (10, 10, 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=800&q=80', 1, NOW());

INSERT INTO "ProductVariant" ("id", "productId", "sku", "variant", "variantValue", "stockQuantity", "originalPrice", "sellingPrice", "weightGram", "imageUrl", "createdAt", "updatedAt") VALUES
                                                                                                                                                                                             (1, 1, 'SKU-1-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 250000.00, 220000.00, 300, 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (2, 2, 'SKU-2-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 300000.00, 270000.00, 300, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (3, 3, 'SKU-3-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 350000.00, 320000.00, 300, 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (4, 4, 'SKU-4-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 400000.00, 370000.00, 300, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (5, 5, 'SKU-5-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 450000.00, 420000.00, 300, 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (6, 6, 'SKU-6-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 500000.00, 470000.00, 300, 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (7, 7, 'SKU-7-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 550000.00, 520000.00, 300, 'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (8, 8, 'SKU-8-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 600000.00, 570000.00, 300, 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (9, 9, 'SKU-9-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 650000.00, 620000.00, 300, 'https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=800&q=80', NOW(), NOW()),
                                                                                                                                                                                             (10, 10, 'SKU-10-DEN-L', 'Màu sắc - Kích cỡ', 'Đen - L', 100, 700000.00, 670000.00, 300, 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=800&q=80', NOW(), NOW());

-- ==============================================================================
-- 5. SEED CART & VOUCHER
-- ==============================================================================
INSERT INTO "Cart" ("id", "userId", "sessionId", "createdAt", "updatedAt") VALUES
                                                                               (1, 1, 'session_token_1', NOW(), NOW()),
                                                                               (2, 2, 'session_token_2', NOW(), NOW()),
                                                                               (3, 3, 'session_token_3', NOW(), NOW()),
                                                                               (4, 4, 'session_token_4', NOW(), NOW()),
                                                                               (5, 5, 'session_token_5', NOW(), NOW()),
                                                                               (6, 6, 'session_token_6', NOW(), NOW()),
                                                                               (7, 7, 'session_token_7', NOW(), NOW()),
                                                                               (8, 8, 'session_token_8', NOW(), NOW()),
                                                                               (9, 9, 'session_token_9', NOW(), NOW()),
                                                                               (10, 10, 'session_token_10', NOW(), NOW());

INSERT INTO "CartItem" ("id", "cartId", "variantId", "quantity", "createdAt", "updatedAt") VALUES
                                                                                               (1, 1, 1, 1, NOW(), NOW()),
                                                                                               (2, 2, 2, 2, NOW(), NOW()),
                                                                                               (3, 3, 3, 3, NOW(), NOW()),
                                                                                               (4, 4, 4, 1, NOW(), NOW()),
                                                                                               (5, 5, 5, 2, NOW(), NOW()),
                                                                                               (6, 6, 6, 3, NOW(), NOW()),
                                                                                               (7, 7, 7, 1, NOW(), NOW()),
                                                                                               (8, 8, 8, 2, NOW(), NOW()),
                                                                                               (9, 9, 9, 3, NOW(), NOW()),
                                                                                               (10, 10, 10, 1, NOW(), NOW());

INSERT INTO "Voucher" ("id", "code", "discountType", "discountValue", "minOrderValue", "maxDiscountAmt", "totalUsageLimit", "usedCount", "userUsageLimit", "startDate", "endDate", "isActive", "createdAt", "updatedAt") VALUES
                                                                                                                                                                                                                             (1, 'FASHION10K', 'FIXED_AMOUNT', 20000.00, 200000.00, 100000.00, 500, 0, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (2, 'FASHION20K', 'PERCENTAGE', 11.00, 200000.00, 100000.00, 500, 5, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (3, 'FASHION30K', 'FIXED_AMOUNT', 40000.00, 200000.00, 100000.00, 500, 10, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (4, 'FASHION40K', 'PERCENTAGE', 13.00, 200000.00, 100000.00, 500, 15, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (5, 'FASHION50K', 'FIXED_AMOUNT', 60000.00, 200000.00, 100000.00, 500, 20, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (6, 'FASHION60K', 'PERCENTAGE', 15.00, 200000.00, 100000.00, 500, 25, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (7, 'FASHION70K', 'FIXED_AMOUNT', 80000.00, 200000.00, 100000.00, 500, 30, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (8, 'FASHION80K', 'PERCENTAGE', 17.00, 200000.00, 100000.00, 500, 35, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (9, 'FASHION90K', 'FIXED_AMOUNT', 100000.00, 200000.00, 100000.00, 500, 40, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
                                                                                                                                                                                                                             (10, 'FASHION100K', 'PERCENTAGE', 19.00, 200000.00, 100000.00, 500, 45, 1, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW());

-- ==============================================================================
-- 6. SEED ORDER, SHIPMENT, PAYMENT
-- ==============================================================================
INSERT INTO "Order" ("id", "orderCode", "userId", "shippingAddress", "subTotal", "shippingFee", "shippingMethod", "shippingDiscount", "voucherCode", "voucherDiscount", "pointUsed", "pointDiscount", "pointEarned", "totalAmount", "paymentMethod", "paymentStatus", "status", "customerNote", "orderDate", "createdAt", "updatedAt") VALUES
                                                                                                                                                                                                                                                                                                                                           (1, 'ORD-20261001', 1, 'Nguyễn Quản Trị | 0901000001 | 100 Đường Lê Lợi, Phường 01, Quận 1, Thành phố Hồ Chí Minh', 440000.00, 30000.00, 'STANDARD', 0.00, 'FASHION10K', 20000.00, 0, 0.00, 4500, 450000.00, 'COD', 'PENDING', 'PENDING_PAYMENT', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (2, 'ORD-20261002', 2, 'Trần Nhân Viên | 0901000002 | 115 Đường Lê Lợi, Phường 02, Quận 2, Thành phố Hồ Chí Minh', 540000.00, 30000.00, 'STANDARD', 0.00, 'FASHION20K', 20000.00, 0, 0.00, 5500, 550000.00, 'BANK_TRANSFER', 'PENDING', 'CONFIRMED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (3, 'ORD-20261003', 3, 'Lê Hoàng Nam | 0901000003 | 130 Đường Lê Lợi, Phường 03, Quận 3, Thành phố Hồ Chí Minh', 640000.00, 30000.00, 'STANDARD', 0.00, 'FASHION30K', 20000.00, 0, 0.00, 6500, 650000.00, 'COD', 'PENDING', 'DELIVERED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (4, 'ORD-20261004', 4, 'Phạm Thị Thảo | 0901000004 | 145 Đường Lê Lợi, Phường 04, Quận 4, Thành phố Hồ Chí Minh', 740000.00, 30000.00, 'STANDARD', 0.00, 'FASHION40K', 20000.00, 0, 0.00, 7500, 750000.00, 'BANK_TRANSFER', 'PENDING', 'SHIPPED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (5, 'ORD-20261005', 5, 'Vũ Minh Tuấn | 0901000005 | 160 Đường Lê Lợi, Phường 05, Quận 5, Thành phố Hồ Chí Minh', 840000.00, 30000.00, 'STANDARD', 0.00, 'FASHION50K', 20000.00, 0, 0.00, 8500, 850000.00, 'COD', 'SUCCESS', 'DELIVERED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (6, 'ORD-20261006', 6, 'Hoàng Mai Anh | 0901000006 | 175 Đường Lê Lợi, Phường 06, Quận 6, Thành phố Hồ Chí Minh', 940000.00, 30000.00, 'STANDARD', 0.00, 'FASHION60K', 20000.00, 0, 0.00, 9500, 950000.00, 'BANK_TRANSFER', 'SUCCESS', 'DELIVERED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (7, 'ORD-20261007', 7, 'Đặng Quốc Bảo | 0901000007 | 190 Đường Lê Lợi, Phường 07, Quận 7, Thành phố Hồ Chí Minh', 1040000.00, 30000.00, 'STANDARD', 0.00, 'FASHION70K', 20000.00, 0, 0.00, 10500, 1050000.00, 'COD', 'SUCCESS', 'DELIVERED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (8, 'ORD-20261008', 8, 'Bùi Phương Ly | 0901000008 | 205 Đường Lê Lợi, Phường 08, Quận 8, Thành phố Hồ Chí Minh', 1140000.00, 30000.00, 'STANDARD', 0.00, 'FASHION80K', 20000.00, 0, 0.00, 11500, 1150000.00, 'BANK_TRANSFER', 'PENDING', 'CANCELLED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (9, 'ORD-20261009', 9, 'Đỗ Tiến Dũng | 0901000009 | 220 Đường Lê Lợi, Phường 09, Quận 9, Thành phố Hồ Chí Minh', 1240000.00, 30000.00, 'STANDARD', 0.00, 'FASHION90K', 20000.00, 0, 0.00, 12500, 1250000.00, 'COD', 'PENDING', 'RETURNED', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW()),
                                                                                                                                                                                                                                                                                                                                           (10, 'ORD-20261010', 10, 'Ngô Khánh Linh | 0901000010 | 235 Đường Lê Lợi, Phường 10, Quận 10, Thành phố Hồ Chí Minh', 1340000.00, 30000.00, 'STANDARD', 0.00, 'FASHION100K', 20000.00, 0, 0.00, 13500, 1350000.00, 'BANK_TRANSFER', 'PENDING', 'PROCESSING', 'Giao hàng giờ hành chính', NOW(), NOW(), NOW());

INSERT INTO "OrderItem" ("id", "orderId", "variantId", "productName", "skuCode", "variantValue", "productImageUrl", "unitPrice", "quantity", "createdAt") VALUES
                                                                                                                                                              (1, 1, 1, 'Áo Sơ Mi Linen Form Rộng', 'SKU-1-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?auto=format&fit=crop&w=800&q=80', 220000.00, 2, NOW()),
                                                                                                                                                              (2, 2, 2, 'Quần Tây Baggy Xếp Ly', 'SKU-2-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=800&q=80', 270000.00, 2, NOW()),
                                                                                                                                                              (3, 3, 3, 'Áo Thun Heavyweight 260gsm', 'SKU-3-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80', 320000.00, 2, NOW()),
                                                                                                                                                              (4, 4, 4, 'Chân Váy Chữ A Lưng Cao', 'SKU-4-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80', 370000.00, 2, NOW()),
                                                                                                                                                              (5, 5, 5, 'Áo Khoác Blazer 2 Hàng Nút', 'SKU-5-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80', 420000.00, 2, NOW()),
                                                                                                                                                              (6, 6, 6, 'Quần Jeans Ống Suông Indigo', 'SKU-6-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80', 470000.00, 2, NOW()),
                                                                                                                                                              (7, 7, 7, 'Áo Len Dệt Kim Cổ Tròn', 'SKU-7-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?auto=format&fit=crop&w=800&q=80', 520000.00, 2, NOW()),
                                                                                                                                                              (8, 8, 8, 'Túi Canvas Đeo Vai Minimal', 'SKU-8-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=800&q=80', 570000.00, 2, NOW()),
                                                                                                                                                              (9, 9, 9, 'Mũ Bucket Cotton Washed', 'SKU-9-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=800&q=80', 620000.00, 2, NOW()),
                                                                                                                                                              (10, 10, 10, 'Áo Hoodie Nỉ Bông Unisex', 'SKU-10-DEN-L', 'Đen - L', 'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=800&q=80', 670000.00, 2, NOW());

INSERT INTO "Shipment" ("id", "orderId", "carrier", "trackingCode", "actualShippingFee", "status", "estimatedDelivery", "createdAt", "updatedAt") VALUES
                                                                                                                                                      (1, 1, 'Giao Hàng Nhanh (GHN)', 'TRK-VN-9988770', 28000.00, 'PENDING', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (2, 2, 'Viettel Post', 'TRK-VN-9988771', 28000.00, 'PICKED_UP', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (3, 3, 'Giao Hàng Nhanh (GHN)', 'TRK-VN-9988772', 28000.00, 'IN_TRANSIT', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (4, 4, 'Viettel Post', 'TRK-VN-9988773', 28000.00, 'DELIVERED', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (5, 5, 'Giao Hàng Nhanh (GHN)', 'TRK-VN-9988774', 28000.00, 'DELIVERED', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (6, 6, 'Viettel Post', 'TRK-VN-9988775', 28000.00, 'DELIVERED', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (7, 7, 'Giao Hàng Nhanh (GHN)', 'TRK-VN-9988776', 28000.00, 'DELIVERED', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (8, 8, 'Viettel Post', 'TRK-VN-9988777', 28000.00, 'FAILED', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (9, 9, 'Giao Hàng Nhanh (GHN)', 'TRK-VN-9988778', 28000.00, 'DELIVERED', NOW() + INTERVAL '3 days', NOW(), NOW()),
                                                                                                                                                      (10, 10, 'Viettel Post', 'TRK-VN-9988779', 28000.00, 'IN_TRANSIT', NOW() + INTERVAL '3 days', NOW(), NOW());

INSERT INTO "PaymentTransaction" ("id", "orderId", "provider", "providerTransId", "amount", "status", "createdAt", "updatedAt") VALUES
                                                                                                                                    (1, 1, 'COD', 'TXN_SEPAY_202610_1000', 450000.00, 'PENDING', NOW(), NOW()),
                                                                                                                                    (2, 2, 'SEPAY_BANK', 'TXN_SEPAY_202610_1001', 550000.00, 'PENDING', NOW(), NOW()),
                                                                                                                                    (3, 3, 'COD', 'TXN_SEPAY_202610_1002', 650000.00, 'PENDING', NOW(), NOW()),
                                                                                                                                    (4, 4, 'SEPAY_BANK', 'TXN_SEPAY_202610_1003', 750000.00, 'PENDING', NOW(), NOW()),
                                                                                                                                    (5, 5, 'COD', 'TXN_SEPAY_202610_1004', 850000.00, 'SUCCESS', NOW(), NOW()),
                                                                                                                                    (6, 6, 'SEPAY_BANK', 'TXN_SEPAY_202610_1005', 950000.00, 'SUCCESS', NOW(), NOW()),
                                                                                                                                    (7, 7, 'COD', 'TXN_SEPAY_202610_1006', 1050000.00, 'SUCCESS', NOW(), NOW()),
                                                                                                                                    (8, 8, 'SEPAY_BANK', 'TXN_SEPAY_202610_1007', 1150000.00, 'PENDING', NOW(), NOW()),
                                                                                                                                    (9, 9, 'COD', 'TXN_SEPAY_202610_1008', 1250000.00, 'PENDING', NOW(), NOW()),
                                                                                                                                    (10, 10, 'SEPAY_BANK', 'TXN_SEPAY_202610_1009', 1350000.00, 'PENDING', NOW(), NOW());

-- ==============================================================================
-- 7. SEED ĐỔI TRẢ & MEDIA
-- ==============================================================================
INSERT INTO "ReturnRequest" ("id", "orderItemId", "quantity", "reason", "description", "status", "createdAt", "updatedAt") VALUES
                                                                                                                               (1, 1, 1, 'Mặc không vừa size, muốn đổi lớn hơn', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'PENDING', NOW(), NOW()),
                                                                                                                               (2, 2, 1, 'Sản phẩm có chỉ thừa / lỗi nhẹ', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'APPROVED', NOW(), NOW()),
                                                                                                                               (3, 3, 1, 'Mặc không vừa size, muốn đổi lớn hơn', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'REJECTED', NOW(), NOW()),
                                                                                                                               (4, 4, 1, 'Sản phẩm có chỉ thừa / lỗi nhẹ', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'COMPLETED', NOW(), NOW()),
                                                                                                                               (5, 5, 1, 'Mặc không vừa size, muốn đổi lớn hơn', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'PENDING', NOW(), NOW()),
                                                                                                                               (6, 6, 1, 'Sản phẩm có chỉ thừa / lỗi nhẹ', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'APPROVED', NOW(), NOW()),
                                                                                                                               (7, 7, 1, 'Mặc không vừa size, muốn đổi lớn hơn', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'PENDING', NOW(), NOW()),
                                                                                                                               (8, 8, 1, 'Sản phẩm có chỉ thừa / lỗi nhẹ', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'REJECTED', NOW(), NOW()),
                                                                                                                               (9, 9, 1, 'Mặc không vừa size, muốn đổi lớn hơn', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'COMPLETED', NOW(), NOW()),
                                                                                                                               (10, 10, 1, 'Sản phẩm có chỉ thừa / lỗi nhẹ', 'Vải đẹp nhưng form hơi rộng so với bảng size chuẩn.', 'PENDING', NOW(), NOW());

INSERT INTO "ReturnMedia" ("id", "returnRequestId", "mediaUrl", "mediaType") VALUES
                                                                                 (1, 1, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (2, 2, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (3, 3, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (4, 4, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (5, 5, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (6, 6, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (7, 7, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (8, 8, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (9, 9, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                                 (10, 10, 'https://images.unsplash.com/photo-1506629905607-d9b1f1f3d7a8?auto=format&fit=crop&w=400&q=80', 'IMAGE');

-- ==============================================================================
-- 8. SEED FLASH SALE & ĐIỂM THƯỞNG
-- ==============================================================================
INSERT INTO "FlashSaleCampaign" ("id", "name", "bannerUrl", "startTime", "endTime", "isActive", "createdAt", "updatedAt") VALUES
                                                                                                                              (1, 'Flash Sale Giờ Vàng Ngày 10/1', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', true, NOW(), NOW()),
                                                                                                                              (2, 'Flash Sale Giờ Vàng Ngày 10/2', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (3, 'Flash Sale Giờ Vàng Ngày 10/3', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (4, 'Flash Sale Giờ Vàng Ngày 10/4', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (5, 'Flash Sale Giờ Vàng Ngày 10/5', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (6, 'Flash Sale Giờ Vàng Ngày 10/6', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (7, 'Flash Sale Giờ Vàng Ngày 10/7', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (8, 'Flash Sale Giờ Vàng Ngày 10/8', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (9, 'Flash Sale Giờ Vàng Ngày 10/9', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW()),
                                                                                                                              (10, 'Flash Sale Giờ Vàng Ngày 10/10', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '24 hours', false, NOW(), NOW());

INSERT INTO "FlashSaleItem" ("id", "campaignId", "variantId", "flashSalePrice", "promoQuantity", "soldQuantity") VALUES
                                                                                                                     (1, 1, 1, 149000.00, 50, 0),
                                                                                                                     (2, 1, 2, 149000.00, 50, 3),
                                                                                                                     (3, 1, 3, 149000.00, 50, 6),
                                                                                                                     (4, 1, 4, 149000.00, 50, 9),
                                                                                                                     (5, 1, 5, 149000.00, 50, 12),
                                                                                                                     (6, 1, 6, 149000.00, 50, 15),
                                                                                                                     (7, 1, 7, 149000.00, 50, 18),
                                                                                                                     (8, 1, 8, 149000.00, 50, 21),
                                                                                                                     (9, 1, 9, 149000.00, 50, 24),
                                                                                                                     (10, 1, 10, 149000.00, 50, 27);

INSERT INTO "PointTransaction" ("id", "userId", "orderId", "pointChange", "currentPoint", "transactionType", "description", "createdAt") VALUES
                                                                                                                                             (1, 1, 1, 150, 5000, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261001', NOW()),
                                                                                                                                             (2, 2, 2, 150, 200, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261002', NOW()),
                                                                                                                                             (3, 3, 3, 150, 1200, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261003', NOW()),
                                                                                                                                             (4, 4, 4, 150, 450, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261004', NOW()),
                                                                                                                                             (5, 5, 5, 150, 0, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261005', NOW()),
                                                                                                                                             (6, 6, 6, 150, 3400, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261006', NOW()),
                                                                                                                                             (7, 7, 7, 150, 80, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261007', NOW()),
                                                                                                                                             (8, 8, 8, 150, 950, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261008', NOW()),
                                                                                                                                             (9, 9, 9, 150, 0, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261009', NOW()),
                                                                                                                                             (10, 10, 10, 150, 10, 'EARN', 'Tích điểm 1% từ đơn hàng ORD-20261010', NOW());

-- ==============================================================================
-- 9. SEED ĐÁNH GIÁ (REVIEW) & CHAT
-- ==============================================================================
INSERT INTO "ProductReview" ("id", "productId", "userId", "orderId", "rating", "content", "shopReply", "updateCount", "isHidden", "createdAt", "updatedAt") VALUES
                                                                                                                                                                (1, 1, 1, 1, 5, 'Chất vải thoáng mát, đường may rất kỹ lưỡng, rất đáng tiền!', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (2, 2, 2, 2, 5, 'Màu sắc y như hình chụp, giao hàng thần tốc trong 2 ngày.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (3, 3, 3, 3, 5, 'Form áo mặc tôn dáng, đóng gói bọc hộp sang trọng.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (4, 4, 4, 4, 5, 'Vải mềm mịn không xù lông khi giặt máy.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (5, 5, 5, 5, 5, 'Giá quá rẻ so với chất lượng nhận được!', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (6, 6, 6, 6, 5, 'Tư vấn viên nhiệt tình chọn size chuẩn vừa khít.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (7, 7, 7, 7, 5, 'Quần mặc đứng form, vải không bị nhăn nhiều.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (8, 8, 8, 8, 5, 'Rất thích phong cách tối giản này, sẽ ủng hộ shop tiếp.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (9, 9, 9, 9, 5, 'Sản phẩm đẹp 10/10, chấm shop 5 sao chất lượng.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW()),
                                                                                                                                                                (10, 10, 10, 10, 5, 'Mẫu mã hiện đại, rất dễ phối đồ đi làm hoặc đi chơi.', 'Dạ cảm ơn bạn đã tin yêu và ủng hộ sản phẩm của thương hiệu ạ!', 0, false, NOW(), NOW());

INSERT INTO "ReviewMedia" ("id", "reviewId", "mediaUrl", "mediaType") VALUES
                                                                          (1, 1, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (2, 2, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (3, 3, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (4, 4, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (5, 5, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (6, 6, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (7, 7, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (8, 8, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (9, 9, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE'),
                                                                          (10, 10, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80', 'IMAGE');

INSERT INTO "Chat" ("id", "userId", "status", "startedAt", "createdAt", "updatedAt") VALUES
                                                                                         (1, 1, 'OPEN', NOW(), NOW(), NOW()),
                                                                                         (2, 2, 'CLOSED', NOW(), NOW(), NOW()),
                                                                                         (3, 3, 'CLOSED', NOW(), NOW(), NOW()),
                                                                                         (4, 4, 'OPEN', NOW(), NOW(), NOW()),
                                                                                         (5, 5, 'CLOSED', NOW(), NOW(), NOW()),
                                                                                         (6, 6, 'CLOSED', NOW(), NOW(), NOW()),
                                                                                         (7, 7, 'OPEN', NOW(), NOW(), NOW()),
                                                                                         (8, 8, 'CLOSED', NOW(), NOW(), NOW()),
                                                                                         (9, 9, 'CLOSED', NOW(), NOW(), NOW()),
                                                                                         (10, 10, 'OPEN', NOW(), NOW(), NOW());

INSERT INTO "Message" ("id", "chatId", "senderId", "content", "sendDate", "isRead") VALUES
                                                                                        (1, 1, 1, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (2, 2, 2, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (3, 3, 3, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (4, 4, 4, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (5, 5, 5, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (6, 6, 6, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (7, 7, 7, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (8, 8, 8, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (9, 9, 9, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true),
                                                                                        (10, 10, 10, 'Chào shop, mình cao 1m75 nặng 68kg thì mặc size L của sản phẩm này có vừa không?', NOW(), true);

-- ==============================================================================
-- 10. ĐỒNG BỘ AUTO-INCREMENT SEQUENCES (Tránh lỗi duplicate key khi App tạo mới)
-- ==============================================================================
DO $$
DECLARE
r RECORD;
BEGIN
FOR r IN (
        SELECT table_name, column_name
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND column_default LIKE 'nextval%'
    ) LOOP
        EXECUTE format('SELECT setval(pg_get_serial_sequence(''"%s"'', ''%s''), COALESCE(MAX("%s"), 1)) FROM "%s"',
                       r.table_name, r.column_name, r.column_name, r.table_name);
END LOOP;
END $$;