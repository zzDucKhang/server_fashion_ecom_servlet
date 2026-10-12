# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\presentation\controller`

**Generated:** 2026-10-12 17:13:39.767842

**Files:** 21 | **Total size:** 105,247 bytes

---

# cartorder\CartServlet.java

| Property | Value |
|----------|-------|
| Path | `cartorder\CartServlet.java` |
| Size | 5,408 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.006548 |

```java
package com.fashionshop.presentation.controller.cartorder;

import com.fashionshop.business.entity.Cart;
import com.fashionshop.business.service.cartorder.CartService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.cartorder.AddCartItemDTO;
import com.fashionshop.presentation.dto.cartorder.UpdateCartItemDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fashionshop.common.util.ValidationUtil;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/cart/*", "/api/cart/*"})
public class CartServlet extends HttpServlet {
    private final CartService cartService = new CartService();

    private Integer getUserId(HttpServletRequest req) {
        return (Integer) req.getAttribute("currentUserId"); // Lấy từ optionalAuth (nếu có)
    }

    private String getSessionId(HttpServletRequest req) {
    String sessionId = req.getHeader("x-session-id");
    // Chỉ ném lỗi nếu CẢ userId và sessionId đều không có
    if ((sessionId == null || sessionId.trim().isEmpty()) && getUserId(req) == null) {
        throw new com.fashionshop.common.exception.BadRequestException("Session ID không hợp lệ hoặc không tồn tại");
    }
    return sessionId != null ? sessionId.trim() : null;
}

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, jakarta.servlet.ServletException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cart cart = cartService.getCart(getUserId(req), getSessionId(req));
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "OK", cart));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = getUserId(req);
        String sessionId = getSessionId(req);

        if ("/items".equals(pathInfo)) {
            AddCartItemDTO dto = JsonUtil.fromJson(req.getReader(), AddCartItemDTO.class);
            ValidationUtil.validate(dto); 
            Cart cart = cartService.add(userId, sessionId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Đã thêm vào giỏ hàng", cart));
        } else if ("/merge".equals(pathInfo)) {
            if (userId == null) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Bạn cần đăng nhập để gộp giỏ hàng"));
                return;
            }
            Cart cart = cartService.merge(userId, sessionId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Gộp giỏ hàng thành công", cart));
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/items/")) {
            try {
                Integer itemId = Integer.parseInt(pathInfo.substring(7));
                UpdateCartItemDTO dto = JsonUtil.fromJson(req.getReader(), UpdateCartItemDTO.class);
                ValidationUtil.validate(dto);
                Cart cart = cartService.update(getUserId(req), getSessionId(req), itemId, dto.getQuantity());
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Cập nhật giỏ hàng thành công", cart));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã sản phẩm không hợp lệ"));
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = getUserId(req);
        String sessionId = getSessionId(req);

        if (pathInfo == null || "/".equals(pathInfo)) {
            Cart cart = cartService.clear(userId, sessionId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Đã xóa toàn bộ giỏ hàng", cart));
        } else if (pathInfo.startsWith("/items/")) {
            try {
                Integer itemId = Integer.parseInt(pathInfo.substring(7));
                Cart cart = cartService.remove(userId, sessionId, itemId);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Đã xóa sản phẩm khỏi giỏ hàng", cart));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã sản phẩm không hợp lệ"));
            }
        }
    }
}
```

# cartorder\OrderServlet.java

| Property | Value |
|----------|-------|
| Path | `cartorder\OrderServlet.java` |
| Size | 6,524 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.009548 |

```java
package com.fashionshop.presentation.controller.cartorder;

import com.fashionshop.business.entity.Order;
import com.fashionshop.business.service.cartorder.OrderService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.cartorder.CreateOrderDTO;
import com.fashionshop.presentation.dto.cartorder.OrderQueryDTO;
import com.fashionshop.presentation.dto.cartorder.UpdateOrderStatusDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fashionshop.common.util.ValidationUtil;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/orders/*", "/api/orders/*"})
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    private Integer getUserId(HttpServletRequest req) {
        Integer id = (Integer) req.getAttribute("currentUserId");
        if (id == null) {
            id = (Integer) req.getAttribute("userId");
        }
        return id;
    }

    private boolean isStaffOrAdmin(HttpServletRequest req) {
        Integer roleId = (Integer) req.getAttribute("currentRoleId");
        return roleId != null && (roleId == 1 || roleId == 2);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.error(401, "Chưa xác thực"));
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            // GET / - danh sách đơn hàng
            OrderQueryDTO query = new OrderQueryDTO();
            String pageStr = req.getParameter("page");
            String limitStr = req.getParameter("limit");
            String status = req.getParameter("status");

            if (pageStr != null && !pageStr.isBlank()) query.setPage(Integer.parseInt(pageStr));
            if (limitStr != null && !limitStr.isBlank()) query.setLimit(Integer.parseInt(limitStr));
            query.setStatus(status);

            PagedResponse<Order> result = orderService.getOrders(userId, query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Lấy danh sách đơn hàng thành công", result));
        } else {
            // GET /{id} - chi tiết đơn hàng
            try {
                String cleanPath = pathInfo.replaceFirst("^/", "");
                Integer orderId = Integer.parseInt(cleanPath);
                Order order = orderService.getOrderById(orderId, userId);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Chi tiết đơn hàng", order));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.error(401, "Chưa xác thực"));
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            // POST / - Tạo đơn hàng mới
            CreateOrderDTO dto = JsonUtil.fromJson(req.getReader(), CreateOrderDTO.class);
            ValidationUtil.validate(dto);

            Order order = orderService.createOrder(userId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Tạo đơn hàng thành công", order));
        } else if (pathInfo.endsWith("/cancel")) {
            // POST /{id}/cancel - Hủy đơn hàng
            try {
                String idPart = pathInfo.replaceFirst("^/", "").replace("/cancel", "");
                Integer orderId = Integer.parseInt(idPart);
                Order cancelled = orderService.cancelOrder(orderId, userId);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Hủy đơn hàng thành công", cancelled));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
            }
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền thực hiện thao tác này"));
            return;
        }

        // PUT /{id}/status - Cập nhật trạng thái (Admin/Staff)
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith("/status")) {
            try {
                String idPart = pathInfo.replaceFirst("^/", "").replace("/status", "");
                Integer orderId = Integer.parseInt(idPart);
                UpdateOrderStatusDTO dto = JsonUtil.fromJson(req.getReader(), UpdateOrderStatusDTO.class);
                ValidationUtil.validate(dto);
                Order updated = orderService.updateOrderStatus(orderId, dto.getStatus());
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Cập nhật trạng thái thành công", updated));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
            }
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }
}
```

# catalog\BrandServlet.java

| Property | Value |
|----------|-------|
| Path | `catalog\BrandServlet.java` |
| Size | 4,074 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.009548 |

```java
package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.business.service.catalog.BrandService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.catalog.BrandQueryDTO;
import com.fashionshop.presentation.dto.catalog.CreateBrandDTO;
import com.fashionshop.presentation.dto.catalog.UpdateBrandDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/brands/*")
public class BrandServlet extends HttpServlet {
    private final BrandService brandService = new BrandService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            BrandQueryDTO query = new BrandQueryDTO();
            if (req.getParameter("page") != null) {
                try { query.setPage(Integer.parseInt(req.getParameter("page"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("limit") != null) {
                try { query.setLimit(Integer.parseInt(req.getParameter("limit"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("search") != null) {
                query.setSearch(req.getParameter("search"));
            }

            PagedResponse<Brand> result = brandService.list(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            Integer brandId = parseId(pathInfo);
            Brand brand = brandService.findById(brandId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", brand));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateBrandDTO dto = JsonUtil.fromJson(req, CreateBrandDTO.class);
        Brand created = brandService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo thương hiệu thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer brandId = parseId(req.getPathInfo());
        UpdateBrandDTO dto = JsonUtil.fromJson(req, UpdateBrandDTO.class);
        ValidationUtil.validate(dto);
        Brand updated = brandService.update(brandId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật thương hiệu thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer brandId = parseId(req.getPathInfo());
        brandService.delete(brandId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa thương hiệu thành công"
        ));
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã thương hiệu không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}


```

# catalog\CategoryServlet.java

| Property | Value |
|----------|-------|
| Path | `catalog\CategoryServlet.java` |
| Size | 3,879 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.016549 |

```java
package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Category;
import com.fashionshop.business.service.catalog.CategoryService;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.catalog.CreateCategoryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateCategoryDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/catalog/categories/*")
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            Integer parentCategoryId = null;
            if (req.getParameter("parentCategoryId") != null) {
                try {
                    parentCategoryId = Integer.parseInt(req.getParameter("parentCategoryId"));
                } catch (NumberFormatException ignored) {}
            }

            List<Category> list = categoryService.findAll(parentCategoryId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "data", list,
                    "totalItems", list.size()
            ));
        } else {
            Integer categoryId = parseId(pathInfo);
            Category category = categoryService.findById(categoryId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", category));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateCategoryDTO dto = JsonUtil.fromJson(req, CreateCategoryDTO.class);
        Category created = categoryService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo danh mục thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer categoryId = parseId(req.getPathInfo());
        UpdateCategoryDTO dto = JsonUtil.fromJson(req, UpdateCategoryDTO.class);
        ValidationUtil.validate(dto);
        Category updated = categoryService.update(categoryId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật danh mục thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer categoryId = parseId(req.getPathInfo());
        categoryService.delete(categoryId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa danh mục thành công"
        ));
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã danh mục không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}


```

# catalog\ProductServlet.java

| Property | Value |
|----------|-------|
| Path | `catalog\ProductServlet.java` |
| Size | 4,728 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.017548 |

```java
package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.business.service.catalog.ProductService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.catalog.CreateProductDTO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateProductDTO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/products/*")
public class ProductServlet extends HttpServlet {
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/catalog/products -> Trả về trực tiếp PagedResponse
            ProductQueryDTO queryDTO = new ProductQueryDTO();
            if (req.getParameter("page") != null) queryDTO.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) queryDTO.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("search") != null) queryDTO.setSearch(req.getParameter("search"));
            if (req.getParameter("categoryId") != null) queryDTO.setCategoryId(Integer.parseInt(req.getParameter("categoryId")));
            if (req.getParameter("brandId") != null) queryDTO.setBrandId(Integer.parseInt(req.getParameter("brandId")));
            if (req.getParameter("status") != null) queryDTO.setStatus(Product.ProductStatus.valueOf(req.getParameter("status")));

            PagedResponse<Product> result = productService.list(queryDTO);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            // GET /api/v1/catalog/products/:productId -> Trả về { statusCode: 200, data }
            Integer productId = parseProductId(pathInfo);
            Product product = productService.findById(productId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", product));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST /api/v1/catalog/products -> ADMIN ONLY
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateProductDTO dto = JsonUtil.fromJson(req, CreateProductDTO.class);
        Product created = productService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo sản phẩm thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /api/v1/catalog/products/:productId -> ADMIN ONLY
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer productId = parseProductId(req.getPathInfo());
        UpdateProductDTO dto = JsonUtil.fromJson(req, UpdateProductDTO.class);
        ValidationUtil.validate(dto);
        Product updated = productService.update(productId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật sản phẩm thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /api/v1/catalog/products/:productId -> ADMIN ONLY
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer productId = parseProductId(req.getPathInfo());
        productService.delete(productId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa sản phẩm thành công"
        ));
    }

    private Integer parseProductId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}
```

# catalog\TagServlet.java

| Property | Value |
|----------|-------|
| Path | `catalog\TagServlet.java` |
| Size | 4,023 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.018550 |

```java
package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.business.service.catalog.TagService;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.catalog.CreateTagDTO;
import com.fashionshop.presentation.dto.catalog.TagQueryDTO;
import com.fashionshop.presentation.dto.catalog.UpdateTagDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/catalog/tags/*")
public class TagServlet extends HttpServlet {
    private final TagService tagService = new TagService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            TagQueryDTO query = new TagQueryDTO();
            if (req.getParameter("page") != null) {
                try { query.setPage(Integer.parseInt(req.getParameter("page"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("limit") != null) {
                try { query.setLimit(Integer.parseInt(req.getParameter("limit"))); } catch (NumberFormatException ignored) {}
            }
            if (req.getParameter("search") != null) {
                query.setSearch(req.getParameter("search"));
            }

            PagedResponse<Tag> result = tagService.list(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            Integer tagId = parseId(pathInfo);
            Tag tag = tagService.findById(tagId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", tag));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        CreateTagDTO dto = JsonUtil.fromJson(req, CreateTagDTO.class);
        ValidationUtil.validate(dto);
        Tag created = tagService.create(adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo thẻ tag thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer tagId = parseId(req.getPathInfo());
        UpdateTagDTO dto = JsonUtil.fromJson(req, UpdateTagDTO.class);
        ValidationUtil.validate(dto);
        Tag updated = tagService.update(tagId, adminId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật thẻ tag thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        Integer tagId = parseId(req.getPathInfo());
        tagService.delete(tagId, adminId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa thẻ tag thành công"
        ));
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã thẻ tag không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}


```

# catalog\VariantServlet.java

| Property | Value |
|----------|-------|
| Path | `catalog\VariantServlet.java` |
| Size | 4,828 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.018550 |

```java
package com.fashionshop.presentation.controller.catalog;

import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.business.service.catalog.VariantService;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.catalog.SyncVariantsDTO;
import com.fashionshop.presentation.dto.catalog.UpdateStockDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/catalog/variants/*")
public class VariantServlet extends HttpServlet {
    private final VariantService variantService = new VariantService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("Vui lòng cung cấp productId hoặc variantId");
        }

        // GET /api/v1/catalog/variants/products/:productId
        if (pathInfo.startsWith("/products/")) {
            String[] parts = pathInfo.split("/");
            if (parts.length < 3) throw new IllegalArgumentException("productId không hợp lệ");
            Integer productId = Integer.parseInt(parts[2]);

            List<ProductVariant> variants = variantService.getVariantsByProductId(productId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", variants
            ));
            return;
        }

        // GET /api/v1/catalog/variants/:variantId
        Integer variantId = parseId(pathInfo);
        ProductVariant variant = variantService.getVariantById(variantId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", variant));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // POST /api/v1/catalog/variants/sync
        if (pathInfo != null && pathInfo.equals("/sync")) {
            Integer adminId = SecurityGuard.requireAdmin(req);
            SyncVariantsDTO dto = JsonUtil.fromJson(req, SyncVariantsDTO.class);
            ValidationUtil.validate(dto);
            List<ProductVariant> variants = variantService.syncProductVariants(dto, adminId);

            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đồng bộ biến thể thành công",
                    "data", variants
            ));
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint không hợp lệ");
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // PATCH /api/v1/catalog/variants/:variantId/stock
        if (pathInfo != null && pathInfo.endsWith("/stock")) {
            SecurityGuard.requireAdmin(req);
            String[] parts = pathInfo.split("/");
            if (parts.length < 2) throw new IllegalArgumentException("variantId không hợp lệ");
            Integer variantId = Integer.parseInt(parts[1]);

            UpdateStockDTO dto = JsonUtil.fromJson(req, UpdateStockDTO.class);
            ValidationUtil.validate(dto);
            ProductVariant updated = variantService.updateStock(variantId, dto.getQuantityChange());

            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Cập nhật tồn kho thành công",
                    "data", updated
            ));
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Endpoint không hợp lệ");
    }

    private Integer parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new IllegalArgumentException("Mã biến thể không hợp lệ");
        }
        return Integer.parseInt(pathInfo.substring(1).split("/")[0]);
    }
}


```

# fulfillment\PaymentServlet.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\PaymentServlet.java` |
| Size | 6,035 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.443155 |

```java
package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.PaymentTransaction;
import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.fulfillment.PaymentInfoDTO;
import com.fashionshop.presentation.dto.fulfillment.PaymentStatusDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/payments/*", "/api/payments/*"})
public class PaymentServlet extends HttpServlet {

    private final PaymentService paymentService = new PaymentService();

    private Integer getUserId(HttpServletRequest req) {
        Integer id = (Integer) req.getAttribute("currentUserId");
        if (id == null) {
            id = (Integer) req.getAttribute("userId");
        }
        return id;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Integer userId = getUserId(req);
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(401, "Chưa xác thực người dùng")));
            return;
        }

        String pathInfo = req.getPathInfo();

        if ("/info".equals(pathInfo)) {
            // GET /api/payments/info?orderId=123
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Thiếu tham số orderId")));
                return;
            }

            Integer orderId;
            try {
                orderId = Integer.parseInt(orderIdStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Mã đơn hàng không hợp lệ")));
                return;
            }

            PaymentInfoDTO paymentInfo = paymentService.getPaymentInfo(orderId, userId);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "Lấy thông tin thanh toán thành công", paymentInfo)));

        } else if ("/status".equals(pathInfo)) {
            // GET /api/payments/status?orderId=123 (Dùng cho Frontend Polling)
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Thiếu tham số orderId")));
                return;
            }

            Integer orderId;
            try {
                orderId = Integer.parseInt(orderIdStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Mã đơn hàng không hợp lệ")));
                return;
            }

            PaymentStatusDTO status = paymentService.getPaymentStatus(orderId, userId);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "Trạng thái thanh toán", status)));

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(404, "Endpoint không tồn tại")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Integer userId = getUserId(req);
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(401, "Chưa xác thực người dùng")));
            return;
        }

        String pathInfo = req.getPathInfo();

        if ("/initiate".equals(pathInfo)) {
            // POST /api/payments/initiate?orderId=123
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr == null || orderIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Thiếu tham số orderId")));
                return;
            }

            Integer orderId;
            try {
                orderId = Integer.parseInt(orderIdStr.trim());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(400, "Mã đơn hàng không hợp lệ")));
                return;
            }

            PaymentTransaction tx = paymentService.initiatePayment(orderId, userId);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "Khởi tạo giao dịch thành công", tx)));

        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(404, "Endpoint không tồn tại")));
        }
    }
}
```

# fulfillment\ReturnServlet.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\ReturnServlet.java` |
| Size | 4,236 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.019549 |

```java
package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.business.service.fulfillment.ReturnService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/fulfillment/returns/*", "/api/v1/returns/*"})
public class ReturnServlet extends HttpServlet {
    private final ReturnService returnService = new ReturnService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET / (Admin only)
            SecurityGuard.requireAdmin(req);
            ReturnQueryDTO query = new ReturnQueryDTO();
            if (req.getParameter("status") != null) {
                query.setStatus(ReturnRequest.ReturnStatus.valueOf(req.getParameter("status")));
            }
            if (req.getParameter("orderItemId") != null) {
                query.setOrderItemId(Integer.parseInt(req.getParameter("orderItemId")));
            }

            List<ReturnRequest> list = returnService.findAll(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("data", list, "totalItems", list.size()));
        } else {
            // GET /:returnId (Require Auth)
            SecurityGuard.requireAuth(req);
            int returnId = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            ReturnRequest returnRequest = returnService.findById(returnId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(returnRequest));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST / (Khách hàng tạo yêu cầu trả hàng)
        SecurityGuard.requireAuth(req);
        CreateReturnDTO dto = JsonUtil.fromJson(req, CreateReturnDTO.class);
        ValidationUtil.validate(dto);
        ReturnRequest created = returnService.create(dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo yêu cầu trả hàng thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /:returnId (Admin duyệt/cập nhật)
        SecurityGuard.requireAdmin(req);
        int returnId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        UpdateReturnDTO dto = JsonUtil.fromJson(req, UpdateReturnDTO.class);
        ValidationUtil.validate(dto);
        ReturnRequest updated = returnService.update(returnId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật yêu cầu trả hàng thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /:returnId (Admin xóa yêu cầu PENDING)
        SecurityGuard.requireAdmin(req);
        int returnId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        returnService.delete(returnId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Xóa yêu cầu trả hàng thành công"
        ));
    }
}
```

# fulfillment\SepayWebhookServlet.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\SepayWebhookServlet.java` |
| Size | 2,064 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.449152 |

```java
package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.service.fulfillment.PaymentService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.fulfillment.SepayWebhookDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/fulfillment/payments/webhook", "/api/sepay/webhook"})
public class SepayWebhookServlet extends HttpServlet {

    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String signature = req.getHeader("x-sepay-signature");
        String timestamp = req.getHeader("x-sepay-timestamp");

        InputStream is = req.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        byte[] rawBody = buffer.toByteArray();

        if (!paymentService.verifySePaySignature(rawBody, signature, timestamp)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error(401, "Chữ ký SePay không hợp lệ")));
            return;
        }

        SepayWebhookDTO payload = JsonUtil.fromJson(new String(rawBody, "UTF-8"), SepayWebhookDTO.class);
        paymentService.handleSePayWebhook(payload);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(200, "OK", null)));
    }
}
```

# fulfillment\ShipmentServlet.java

| Property | Value |
|----------|-------|
| Path | `fulfillment\ShipmentServlet.java` |
| Size | 7,293 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.019549 |

```java
package com.fashionshop.presentation.controller.fulfillment;

import com.fashionshop.business.entity.Shipment;
import com.fashionshop.business.service.fulfillment.ShipmentService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.presentation.dto.fulfillment.CreateShipmentDTO;
import com.fashionshop.presentation.dto.fulfillment.ShipmentQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateShipmentDTO;
import com.fashionshop.common.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/fulfillment/shipments/*", "/api/shipments/*"})
public class ShipmentServlet extends HttpServlet {

    private final ShipmentService shipmentService = new ShipmentService();

    private Integer getUserId(HttpServletRequest req) {
        Integer id = (Integer) req.getAttribute("currentUserId");
        if (id == null) {
            id = (Integer) req.getAttribute("userId");
        }
        return id;
    }

    private boolean isStaffOrAdmin(HttpServletRequest req) {
        Integer roleId = (Integer) req.getAttribute("currentRoleId");
        return roleId != null && (roleId == 1 || roleId == 2);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = getUserId(req);
        if (userId == null) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, ApiResponse.error(401, "Chưa xác thực"));
            return;
        }

        boolean admin = isStaffOrAdmin(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isBlank()) {
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr != null && !orderIdStr.isBlank()) {
                // GET ?orderId=123
                try {
                    Integer orderId = Integer.parseInt(orderIdStr.trim());
                    Shipment shipment = shipmentService.getShipmentByOrderId(orderId, userId, admin);
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Thông tin vận chuyển", shipment));
                } catch (NumberFormatException e) {
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã đơn hàng không hợp lệ"));
                }
            } else {
                // GET / - Danh sách phân trang chỉ dành cho Staff/Admin
                if (!admin) {
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền xem danh sách vận đơn"));
                    return;
                }

                ShipmentQueryDTO query = new ShipmentQueryDTO();
                String pageStr = req.getParameter("page");
                String limitStr = req.getParameter("limit");
                String status = req.getParameter("status");

                try {
                    if (pageStr != null && !pageStr.isBlank()) query.setPage(Integer.parseInt(pageStr.trim()));
                    if (limitStr != null && !limitStr.isBlank()) query.setLimit(Integer.parseInt(limitStr.trim()));
                } catch (NumberFormatException e) {
                    JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Tham số phân trang không hợp lệ"));
                    return;
                }

                if (status != null && !status.isBlank()) {
                    try {
                        Shipment.ShipmentStatus.valueOf(status.trim().toUpperCase());
                        query.setStatus(status.trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Trạng thái vận đơn không hợp lệ: " + status));
                        return;
                    }
                }

                PagedResponse<Shipment> result = shipmentService.listShipments(query);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Danh sách vận đơn", result));
            }
        } else {
            // GET /{id}
            try {
                String cleanPath = pathInfo.replaceFirst("^/", "");
                Integer id = Integer.parseInt(cleanPath.trim());
                Shipment shipment = shipmentService.getShipmentById(id, userId, admin);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Chi tiết vận đơn", shipment));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã vận đơn không hợp lệ"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền thực hiện thao tác này"));
            return;
        }

        CreateShipmentDTO dto = JsonUtil.fromJson(req.getReader(), CreateShipmentDTO.class);
        ValidationUtil.validate(dto);
        Shipment shipment = shipmentService.createShipment(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Tạo vận đơn thành công", shipment));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isStaffOrAdmin(req)) {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_FORBIDDEN, ApiResponse.error(403, "Bạn không có quyền thực hiện thao tác này"));
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.contains("/status")) {
            try {
                String idPart = pathInfo.replaceFirst("^/", "").replace("/status", "");
                Integer shipmentId = Integer.parseInt(idPart);
                UpdateShipmentDTO dto = JsonUtil.fromJson(req.getReader(), UpdateShipmentDTO.class);
                ValidationUtil.validate(dto);
                shipmentService.updateShipmentStatus(shipmentId, dto);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.success(200, "Cập nhật trạng thái vận đơn thành công", null));
            } catch (NumberFormatException e) {
                JsonUtil.sendJson(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Mã vận đơn không hợp lệ"));
            }
        } else {
            JsonUtil.sendJson(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Endpoint không tồn tại"));
        }
    }
}


```

# identity\AddressServlet.java

| Property | Value |
|----------|-------|
| Path | `identity\AddressServlet.java` |
| Size | 4,386 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.470788 |

```java
package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.AddressService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.CreateAddressDTO;
import com.fashionshop.presentation.dto.identity.UpdateAddressDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/identity/addresses/*")
public class AddressServlet extends HttpServlet {

    private final AddressService addressService = new AddressService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/addresses -> list current user's addresses
            List<Map<String, Object>> addresses = addressService.getAddressesByUserId(currentUserId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", addresses
            ));
        } else {
            // GET /api/v1/identity/addresses/:addressId -> detail (Anti-IDOR)
            Integer addressId = parseAddressId(pathInfo);
            Map<String, Object> address = addressService.getAddressById(addressId, currentUserId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", address
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = SecurityGuard.requireAuth(req);

        CreateAddressDTO dto = JsonUtil.fromJson(req, CreateAddressDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = addressService.createAddress(dto, currentUserId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo địa chỉ thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = SecurityGuard.requireAuth(req);
        Integer addressId = parseAddressId(req.getPathInfo());

        UpdateAddressDTO dto = JsonUtil.fromJson(req, UpdateAddressDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = addressService.updateAddress(addressId, dto, currentUserId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật địa chỉ thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = SecurityGuard.requireAuth(req);
        Integer addressId = parseAddressId(req.getPathInfo());

        Map<String, Object> result = addressService.deleteAddress(addressId, currentUserId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private Integer parseAddressId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Thiếu ID địa chỉ");
        }
        try {
            String idStr = pathInfo.substring(1);
            if (idStr.contains("/")) {
                idStr = idStr.substring(0, idStr.indexOf('/'));
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID địa chỉ không hợp lệ: " + pathInfo);
        }
    }
}

```

# identity\AuthServlet.java

| Property | Value |
|----------|-------|
| Path | `identity\AuthServlet.java` |
| Size | 6,310 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.472787 |

```java
package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.AuthService;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/identity/auth/*")
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            throw new NotFoundException("Đường dẫn không tồn tại");
        }

        switch (pathInfo) {
            case "/register" -> handleRegister(req, resp);
            case "/login" -> handleLogin(req, resp);
            case "/refresh" -> handleRefresh(req, resp);
            case "/forgot-password" -> handleForgotPassword(req, resp);
            case "/verify-code" -> handleVerifyCode(req, resp);
            case "/reset-password" -> handleResetPassword(req, resp);
            case "/change-password" -> handleChangePassword(req, resp);
            case "/logout" -> handleLogout(req, resp);
            default -> throw new NotFoundException("Đường dẫn không tồn tại: " + pathInfo);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegisterDTO dto = JsonUtil.fromJson(req, RegisterDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.register(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "data", result
        ));
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginDTO dto = JsonUtil.fromJson(req, LoginDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.login(dto, getClientIp(req));
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleRefresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RefreshTokenDTO dto = JsonUtil.fromJson(req, RefreshTokenDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.refreshToken(dto, getClientIp(req));
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ForgotPasswordDTO dto = JsonUtil.fromJson(req, ForgotPasswordDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.forgotPassword(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        VerifyOtpDTO dto = JsonUtil.fromJson(req, VerifyOtpDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.verifyCode(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResetPasswordDTO dto = JsonUtil.fromJson(req, ResetPasswordDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.resetPassword(dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer userId = SecurityGuard.requireAuth(req);
        ChangePasswordDTO dto = JsonUtil.fromJson(req, ChangePasswordDTO.class);
        ValidationUtil.validate(dto);
        Map<String, Object> result = authService.changePassword(dto, userId);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String authHeader = req.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7).trim() : null;

        String refreshToken = null;
        try {
            RefreshTokenDTO dto = JsonUtil.fromJson(req, RefreshTokenDTO.class);
            if (dto != null) {
                refreshToken = dto.getRefreshToken();
            }
        } catch (Exception ignored) {
            // Body may be empty on logout
        }

        Map<String, Object> result = authService.logout(accessToken, refreshToken);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private String getClientIp(HttpServletRequest req) {
        String xForwardedFor = req.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = req.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return req.getRemoteAddr();
    }
}

```

# identity\PermissionServlet.java

| Property | Value |
|----------|-------|
| Path | `identity\PermissionServlet.java` |
| Size | 4,913 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.473788 |

```java
package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.RolePermissionService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.CreatePermissionDTO;
import com.fashionshop.presentation.dto.identity.UpdatePermissionDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/api/v1/identity/roles/permissions",
        "/api/v1/identity/roles/permissions/*"
})
public class PermissionServlet extends HttpServlet {

    private final RolePermissionService rolePermissionService = new RolePermissionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/roles/permissions?module=...
            String module = req.getParameter("module");
            List<Map<String, Object>> permissions = rolePermissionService.getAllPermissions(module, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", permissions
            ));
        } else {
            // GET /api/v1/identity/roles/permissions/:permissionId
            Integer permissionId = parsePermissionId(pathInfo);
            Map<String, Object> permission = rolePermissionService.getPermissionById(permissionId, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", permission
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        CreatePermissionDTO dto = JsonUtil.fromJson(req, CreatePermissionDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = rolePermissionService.createPermission(dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo quyền hạn thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer permissionId = parsePermissionId(req.getPathInfo());

        UpdatePermissionDTO dto = JsonUtil.fromJson(req, UpdatePermissionDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = rolePermissionService.updatePermission(permissionId, dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật quyền hạn thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer permissionId = parsePermissionId(req.getPathInfo());

        Map<String, Object> result = rolePermissionService.softDeletePermission(permissionId, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private Integer parsePermissionId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Thiếu ID quyền hạn");
        }
        try {
            String idStr = pathInfo.substring(1);
            if (idStr.contains("/")) {
                idStr = idStr.substring(0, idStr.indexOf('/'));
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID quyền hạn không hợp lệ: " + pathInfo);
        }
    }
}

```

# identity\RoleServlet.java

| Property | Value |
|----------|-------|
| Path | `identity\RoleServlet.java` |
| Size | 4,544 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.476789 |

```java
package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.RolePermissionService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.CreateRoleDTO;
import com.fashionshop.presentation.dto.identity.UpdateRoleDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/identity/roles/*")
public class RoleServlet extends HttpServlet {

    private final RolePermissionService rolePermissionService = new RolePermissionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/roles
            List<Map<String, Object>> roles = rolePermissionService.getAllRoles(currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", roles
            ));
        } else {
            // GET /api/v1/identity/roles/:roleId
            Integer roleId = parseRoleId(pathInfo);
            Map<String, Object> role = rolePermissionService.getRoleById(roleId, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", role
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        CreateRoleDTO dto = JsonUtil.fromJson(req, CreateRoleDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = rolePermissionService.createRole(dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo vai trò thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer roleId = parseRoleId(req.getPathInfo());

        UpdateRoleDTO dto = JsonUtil.fromJson(req, UpdateRoleDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = rolePermissionService.updateRole(roleId, dto, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật vai trò thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer adminId = SecurityGuard.requireAdmin(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer roleId = parseRoleId(req.getPathInfo());

        Map<String, Object> result = rolePermissionService.softDeleteRole(roleId, adminId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private Integer parseRoleId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Thiếu ID vai trò");
        }
        try {
            String idStr = pathInfo.substring(1);
            if (idStr.contains("/")) {
                idStr = idStr.substring(0, idStr.indexOf('/'));
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID vai trò không hợp lệ: " + pathInfo);
        }
    }
}

```

# identity\UserServlet.java

| Property | Value |
|----------|-------|
| Path | `identity\UserServlet.java` |
| Size | 6,880 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.519787 |

```java
package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.UserService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.identity.UpdateUserDTO;
import com.fashionshop.presentation.dto.identity.UpdateUserStatusDTO;
import com.fashionshop.presentation.dto.identity.UserQueryDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/v1/identity/users/*")
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/users -> ADMIN ONLY
            UserQueryDTO query = parseQueryDTO(req);
            PagedResponse<Map<String, Object>> result = userService.getUsers(query, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            // GET /api/v1/identity/users/:userId -> Owner or ADMIN
            Integer userId = parseUserId(pathInfo);
            Map<String, Object> result = userService.getUserById(userId, currentUserId, currentRoleCode);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", result
            ));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /api/v1/identity/users/:userId -> Owner or ADMIN
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer userId = parseUserId(req.getPathInfo());

        UpdateUserDTO dto = JsonUtil.fromJson(req, UpdateUserDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> result = userService.updateUser(userId, dto, currentUserId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật thông tin thành công",
                "data", result
        ));
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PATCH /api/v1/identity/users/:userId/status -> ADMIN ONLY
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.endsWith("/status")) {
            throw new NotFoundException("Đường dẫn không tồn tại: " + pathInfo);
        }

        String idPart = pathInfo.substring(1, pathInfo.lastIndexOf('/'));
        Integer userId;
        try {
            userId = Integer.parseInt(idPart);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID người dùng không hợp lệ");
        }

        UpdateUserStatusDTO dto = JsonUtil.fromJson(req, UpdateUserStatusDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> result = userService.updateUserStatus(userId, dto, currentUserId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật trạng thái người dùng thành công",
                "data", result
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /api/v1/identity/users/:userId -> ADMIN ONLY
        Integer currentUserId = SecurityGuard.requireAuth(req);
        String currentRoleCode = SecurityGuard.getRoleCode(req);
        Integer userId = parseUserId(req.getPathInfo());

        Map<String, Object> result = userService.softDeleteUser(userId, currentUserId, currentRoleCode);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "data", result
        ));
    }

    private Integer parseUserId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Thiếu ID người dùng");
        }
        try {
            String idStr = pathInfo.substring(1);
            if (idStr.contains("/")) {
                idStr = idStr.substring(0, idStr.indexOf('/'));
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID người dùng không hợp lệ: " + pathInfo);
        }
    }

    private UserQueryDTO parseQueryDTO(HttpServletRequest req) {
        UserQueryDTO query = new UserQueryDTO();
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            try { query.setPage(Integer.parseInt(pageParam)); } catch (NumberFormatException ignored) {}
        }
        String limitParam = req.getParameter("limit");
        if (limitParam != null) {
            try { query.setLimit(Integer.parseInt(limitParam)); } catch (NumberFormatException ignored) {}
        }
        String searchParam = req.getParameter("search");
        if (searchParam != null) {
            query.setSearch(searchParam);
        }
        String statusParam = req.getParameter("status");
        if (statusParam != null) {
            query.setStatus(statusParam);
        }
        String roleIdParam = req.getParameter("roleId");
        if (roleIdParam != null) {
            try { query.setRoleId(Integer.parseInt(roleIdParam)); } catch (NumberFormatException ignored) {}
        }
        return query;
    }
}

```

# inventory\FlashSaleServlet.java

| Property | Value |
|----------|-------|
| Path | `inventory\FlashSaleServlet.java` |
| Size | 4,526 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.019549 |

```java
package com.fashionshop.presentation.controller.inventory;

import com.fashionshop.business.entity.FlashSaleCampaign;
import com.fashionshop.business.service.inventory.FlashSaleService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.presentation.dto.inventory.CreateFlashSaleDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleQueryDTO;
import com.fashionshop.presentation.dto.inventory.UpdateFlashSaleDTO;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/inventory-sale/flash-sales/*", "/api/v1/flash-sales/*"})
public class FlashSaleServlet extends HttpServlet {
    private final FlashSaleService flashSaleService = new FlashSaleService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/active".equals(pathInfo)) {
            // GET /active -> Public
            List<FlashSaleCampaign> activeList = flashSaleService.findActiveCampaigns();
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", activeList));
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET / -> Public list
            FlashSaleQueryDTO query = new FlashSaleQueryDTO();
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("isActive") != null) query.setIsActive(Boolean.parseBoolean(req.getParameter("isActive")));

            PagedResponse<FlashSaleCampaign> result = flashSaleService.list(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, result);
        } else {
            // GET /:campaignId -> Public detail
            int campaignId = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            FlashSaleCampaign campaign = flashSaleService.findById(campaignId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", campaign));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST / -> requireAdmin
        SecurityGuard.requireAdmin(req);
        CreateFlashSaleDTO dto = JsonUtil.fromJson(req, CreateFlashSaleDTO.class);
        ValidationUtil.validate(dto);
        FlashSaleCampaign created = flashSaleService.create(dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo chiến dịch Flash Sale thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PUT /:campaignId -> requireAdmin
        SecurityGuard.requireAdmin(req);
        int campaignId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        UpdateFlashSaleDTO dto = JsonUtil.fromJson(req, UpdateFlashSaleDTO.class);
        ValidationUtil.validate(dto);
        FlashSaleCampaign updated = flashSaleService.update(campaignId, dto);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "statusCode", 200,
                "message", "Cập nhật chiến dịch Flash Sale thành công",
                "data", updated
        ));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /:campaignId -> requireAdmin
        SecurityGuard.requireAdmin(req);
        int campaignId = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        flashSaleService.delete(campaignId);

        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "message", "Xóa chiến dịch Flash Sale thành công"
        ));
    }
}
```

# loyalty\ChatServlet.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ChatServlet.java` |
| Size | 7,066 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.020548 |

```java
package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.Chat;
import com.fashionshop.business.entity.Message;
import com.fashionshop.business.service.loyalty.ChatService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.dataaccess.dao.loyalty.MessageDAO;
import com.fashionshop.presentation.dto.loyalty.*;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.websocket.WsSessionManager;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/chat/*", "/api/v1/chat/*"})
public class ChatServlet extends HttpServlet {
    private final ChatService chatService = new ChatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/messages/unread".equals(pathInfo)) {
            // GET /messages/unread
            Integer userId = SecurityGuard.requireAuth(req);
            int unread = chatService.getUnreadCount(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", Map.of("unreadCount", unread)));
            return;
        }

        if ("/messages/active".equals(pathInfo)) {
            // GET /messages/active
            Integer userId = SecurityGuard.requireAuth(req);
            Chat chat = chatService.getOrCreateActiveChat(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", chat));
            return;
        }

        if ("/messages".equals(pathInfo)) {
            // GET /messages?chatId=&cursor=&limit=&page=
            Integer userId = SecurityGuard.requireAuth(req);
            ChatHistoryQueryDTO query = new ChatHistoryQueryDTO();
            if (req.getParameter("chatId") != null) query.setChatId(Integer.parseInt(req.getParameter("chatId")));
            if (req.getParameter("cursor") != null) query.setCursor(Integer.parseInt(req.getParameter("cursor")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));

            MessageDAO.CursorResult result = chatService.getChatHistory(userId, query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "data", result.data(),
                    "pagination", Map.of(
                            "page", query.getPage(),
                            "limit", query.getLimit(),
                            "nextCursor", result.nextCursor() != null ? result.nextCursor() : false,
                            "hasMore", result.hasMore()
                    )
            ));
            return;
        }

        if ("/conversations".equals(pathInfo)) {
            // GET /conversations (Shop inbox)
            Integer userId = SecurityGuard.requireAuth(req);
            var list = chatService.getShopConversationList(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", list));
            return;
        }

        if (pathInfo != null && pathInfo.matches("/sessions/\\d+/online")) {
            // GET /sessions/:userId/online (Admin only)
            SecurityGuard.requireAdmin(req);
            int targetUserId = Integer.parseInt(pathInfo.split("/")[2]);
            boolean isOnline = WsSessionManager.isUserOnline(targetUserId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", Map.of("userId", targetUserId, "isOnline", isOnline)));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/messages".equals(pathInfo)) {
            // POST /messages
            Integer senderId = SecurityGuard.requireAuth(req);
            SendMessageDTO dto = JsonUtil.fromJson(req, SendMessageDTO.class);
            ValidationUtil.validate(dto);
            Message msg = chatService.sendMessage(senderId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                    "statusCode", 201,
                    "message", "Tin nhắn đã được gửi",
                    "data", msg
            ));
            return;
        }

        if ("/messages/read".equals(pathInfo)) {
            // POST /messages/read
            Integer receiverId = SecurityGuard.requireAuth(req);
            MarkReadDTO dto = JsonUtil.fromJson(req, MarkReadDTO.class);
            ValidationUtil.validate(dto);
            int count = chatService.markAsRead(receiverId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đã đọc " + count + " tin nhắn",
                    "updatedCount", count
            ));
            return;
        }

        if (pathInfo != null && pathInfo.matches("/conversations/\\d+/close")) {
            // POST /conversations/:chatId/close
            Integer staffId = SecurityGuard.requireAuth(req);
            int chatId = Integer.parseInt(pathInfo.split("/")[2]);
            Chat closed = chatService.closeConversation(staffId, chatId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đã đóng cuộc trò chuyện",
                    "data", closed
            ));
            return;
        }

        if ("/sessions/heartbeat".equals(pathInfo)) {
            // POST /sessions/heartbeat
            Map<?, ?> body = JsonUtil.fromJson(req, Map.class);
            String socketId = (String) body.get("socketId");
            if (socketId == null || socketId.isBlank()) throw new BadRequestException("socketId không được để trống");
            boolean extended = WsSessionManager.extendSocketSession(socketId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Gia hạn socket session thành công",
                    "data", Map.of("socketId", socketId, "extended", extended)
            ));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
```

# loyalty\PointServlet.java

| Property | Value |
|----------|-------|
| Path | `loyalty\PointServlet.java` |
| Size | 3,225 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.020548 |

```java
package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.service.loyalty.PointService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.presentation.dto.loyalty.EarnPointsDTO;
import com.fashionshop.presentation.dto.loyalty.PointQueryDTO;
import com.fashionshop.presentation.dto.loyalty.RedeemPointsDTO;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/points/*", "/api/v1/points/*"})
public class PointServlet extends HttpServlet {
    private final PointService pointService = new PointService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = SecurityGuard.requireAuth(req);

        if ("/balance".equals(pathInfo)) {
            // GET /balance
            int rewardPoints = pointService.getBalance(userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(Map.of("rewardPoints", rewardPoints)));
            return;
        }

        if ("/history".equals(pathInfo)) {
            // GET /history
            PointQueryDTO query = new PointQueryDTO();
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));

            var paged = pointService.getHistory(userId, query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(paged));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/earn".equals(pathInfo)) {
            // POST /earn (Admin only)
            Integer adminId = SecurityGuard.requireAdmin(req);
            EarnPointsDTO dto = JsonUtil.fromJson(req, EarnPointsDTO.class);
            ValidationUtil.validate(dto);
            var result = pointService.earnPoints(adminId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
            return;
        }

        if ("/redeem".equals(pathInfo)) {
            // POST /redeem (User auth)
            Integer userId = SecurityGuard.requireAuth(req);
            RedeemPointsDTO dto = JsonUtil.fromJson(req, RedeemPointsDTO.class);
            ValidationUtil.validate(dto);
            var result = pointService.redeemPoints(userId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
```

# loyalty\ReviewServlet.java

| Property | Value |
|----------|-------|
| Path | `loyalty\ReviewServlet.java` |
| Size | 5,361 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.020548 |

```java
package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.ProductReview;
import com.fashionshop.business.service.loyalty.ReviewService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.loyalty.CreateReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReplyReviewDTO;
import com.fashionshop.presentation.dto.loyalty.ReviewQueryDTO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/reviews/*", "/api/v1/reviews/*"})
public class ReviewServlet extends HttpServlet {
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/loyalty-engage/reviews?productId=&page=&limit=&rating=
            ReviewQueryDTO query = new ReviewQueryDTO();
            if (req.getParameter("productId") != null) query.setProductId(Integer.parseInt(req.getParameter("productId")));
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("rating") != null) query.setRating(Integer.parseInt(req.getParameter("rating")));

            var result = reviewService.getReviewsByProduct(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", result.get("data"), "ratingStats", result.get("ratingStats"), "pagination", result.get("pagination")));
        } else {
            // GET /api/v1/loyalty-engage/reviews/{id}
            int id = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            ProductReview review = reviewService.getReviewById(id);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of("statusCode", 200, "data", review));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // POST / (Create review - requireAuth)
            Integer userId = SecurityGuard.requireAuth(req);
            CreateReviewDTO dto = JsonUtil.fromJson(req, CreateReviewDTO.class);
            ValidationUtil.validate(dto);
            ProductReview created = reviewService.createReview(userId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                    "statusCode", 201,
                    "message", "Đánh giá sản phẩm thành công",
                    "data", created
            ));
            return;
        }

        String[] parts = pathInfo.substring(1).split("/");
        int reviewId = Integer.parseInt(parts[0]);

        if (parts.length > 1 && "reply".equals(parts[1])) {
            // POST /{id}/reply (requireAdmin)
            SecurityGuard.requireAdmin(req);
            ReplyReviewDTO dto = JsonUtil.fromJson(req, ReplyReviewDTO.class);
            ValidationUtil.validate(dto);
            ProductReview updated = reviewService.replyToReview(reviewId, dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                    "statusCode", 200,
                    "message", "Đã gửi phản hồi thành công",
                    "data", updated
            ));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPatch(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            String[] parts = pathInfo.substring(1).split("/");
            if (parts.length > 1 && "hide".equals(parts[1])) {
                // PATCH /{id}/hide (requireAdmin)
                SecurityGuard.requireAdmin(req);
                int reviewId = Integer.parseInt(parts[0]);
                Map<?, ?> body = JsonUtil.fromJson(req, Map.class);
                boolean isHidden = Boolean.TRUE.equals(body.get("isHidden"));

                ProductReview updated = reviewService.toggleHideReview(reviewId, isHidden);
                JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                        "statusCode", 200,
                        "message", isHidden ? "Đã ẩn đánh giá" : "Đã hiện đánh giá",
                        "data", updated
                ));
                return;
            }
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
```

# loyalty\VoucherServlet.java

| Property | Value |
|----------|-------|
| Path | `loyalty\VoucherServlet.java` |
| Size | 4,944 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:49:46.026545 |

```java
package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.business.service.loyalty.VoucherService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.loyalty.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/vouchers/*", "/api/v1/vouchers/*"})
public class VoucherServlet extends HttpServlet {
    private final VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/loyalty-engage/vouchers (Admin only)
            SecurityGuard.requireAdmin(req);
            VoucherQueryDTO query = new VoucherQueryDTO();
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("isActive") != null) query.setIsActive(Boolean.parseBoolean(req.getParameter("isActive")));

            var result = voucherService.listVouchers(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
        } else {
            // GET /api/v1/loyalty-engage/vouchers/{id} (Admin only)
            SecurityGuard.requireAdmin(req);
            Integer id = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            Voucher voucher = voucherService.getVoucherById(id);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(voucher));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/validate".equals(pathInfo)) {
            // POST /validate (User Auth)
            Integer userId = SecurityGuard.requireAuth(req);
            ValidateVoucherDTO dto = JsonUtil.fromJson(req, ValidateVoucherDTO.class);
            ValidationUtil.validate(dto);
            var result = voucherService.validateAndCalculate(dto, userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
            return;
        }

        if ("/apply".equals(pathInfo)) {
            // POST /apply (Admin/Checkout Auth)
            Integer userId = SecurityGuard.requireAuth(req);
            ApplyVoucherDTO dto = JsonUtil.fromJson(req, ApplyVoucherDTO.class);
            ValidationUtil.validate(dto);
            voucherService.applyVoucher(dto.getCode(), userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok("Đã áp dụng voucher thành công"));
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            // POST / (Create Voucher - Admin only)
            SecurityGuard.requireAdmin(req);
            CreateVoucherDTO dto = JsonUtil.fromJson(req, CreateVoucherDTO.class);
            ValidationUtil.validate(dto);
            Voucher created = voucherService.createVoucher(dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.created(created, "Tạo voucher thành công"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPatch(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PATCH/PUT /{id} (Admin only)
        SecurityGuard.requireAdmin(req);
        Integer id = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        UpdateVoucherDTO dto = JsonUtil.fromJson(req, UpdateVoucherDTO.class);
        ValidationUtil.validate(dto);
        Voucher updated = voucherService.updateVoucher(id, dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(updated));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /{id} (Admin only)
        SecurityGuard.requireAdmin(req);
        Integer id = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        voucherService.deleteVoucher(id);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok("Xoá voucher thành công"));
    }
}
```

