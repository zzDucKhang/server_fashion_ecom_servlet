package com.fashionshop.presentation.controller.cartorder;

import com.fashionshop.business.entity.Cart;
import com.fashionshop.business.service.cartorder.CartService;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.cartorder.AddCartItemDTO;
import com.fashionshop.presentation.dto.cartorder.UpdateCartItemDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {
    "/api/v1/cart-order/cart/*",
    "/api/v1/cart/*",
    "/api/cart/*"
})
public class CartServlet extends BaseHttpServlet {
    private final CartService cartService = new CartService();

    private String getSessionId(HttpServletRequest req) {
        String sessionId = req.getHeader("x-session-id");
        if ((sessionId == null || sessionId.trim().isEmpty()) && getOptionalUserId(req) == null) {
            throw new BadRequestException("Session ID không hợp lệ hoặc không tồn tại");
        }
        return sessionId != null ? sessionId.trim() : null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cart cart = cartService.getCart(getOptionalUserId(req), getSessionId(req));
        sendSuccess(resp, "Lấy giỏ hàng thành công", cart);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = getOptionalUserId(req);
        String sessionId = getSessionId(req);

        if ("/items".equals(pathInfo)) {
            AddCartItemDTO dto = parseBody(req, AddCartItemDTO.class);
            ValidationUtil.validate(dto);
            Cart cart = cartService.add(userId, sessionId, dto);
            sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.success(201, "Đã thêm vào giỏ hàng", cart));
        } else if ("/merge".equals(pathInfo)) {
            if (userId == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Bạn cần đăng nhập để gộp giỏ hàng");
                return;
            }
            Cart cart = cartService.merge(userId, sessionId);
            sendSuccess(resp, "Gộp giỏ hàng thành công", cart);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPatch(req, resp);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/items/")) {
            Integer itemId = parseId(pathInfo.substring(6));
            UpdateCartItemDTO dto = parseBody(req, UpdateCartItemDTO.class);
            ValidationUtil.validate(dto);
            Cart cart = cartService.update(getOptionalUserId(req), getSessionId(req), itemId, dto.getQuantity());
            sendSuccess(resp, "Cập nhật giỏ hàng thành công", cart);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Integer userId = getOptionalUserId(req);
        String sessionId = getSessionId(req);

        if (pathInfo == null || "/".equals(pathInfo)) {
            Cart cart = cartService.clear(userId, sessionId);
            sendSuccess(resp, "Đã xóa toàn bộ giỏ hàng", cart);
        } else if (pathInfo.startsWith("/items/")) {
            Integer itemId = parseId(pathInfo.substring(6));
            Cart cart = cartService.remove(userId, sessionId, itemId);
            sendSuccess(resp, "Đã xóa sản phẩm khỏi giỏ hàng", cart);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint không tồn tại");
        }
    }
}