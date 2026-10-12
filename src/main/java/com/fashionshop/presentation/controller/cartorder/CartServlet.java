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

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/v1/cart/*", "/api/cart/*"})
public class CartServlet extends HttpServlet {
    private final CartService cartService = new CartService();

    private Integer getUserId(HttpServletRequest req) {
        return (Integer) req.getAttribute("currentUserId"); // Lấy từ optionalAuth (nếu có)
    }

    private String getSessionId(HttpServletRequest req) {
        String sessionId = req.getHeader("x-session-id");
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new com.fashionshop.common.exception.BadRequestException("Session ID không hợp lệ hoặc không tồn tại");
        }
        return sessionId.trim();
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