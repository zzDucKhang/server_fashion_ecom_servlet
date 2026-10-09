package com.fashionshop.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filter cấu hình Cross-Origin Resource Sharing (CORS) toàn cục cho ứng dụng.
 * Đảm bảo các client (React, Vue, mobile apps,...) có thể gọi API mà không bị chặn bởi trình duyệt.
 * Tự động chặn và phản hồi 200 OK cho các request Preflight (OPTIONS).
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = {"/*"})
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không có cấu hình đặc biệt cần khởi tạo
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Thiết lập các Header CORS theo yêu cầu
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        res.setHeader("Access-Control-Max-Age", "3600");

        // Xử lý Preflight request (OPTIONS): Trả về HTTP 200 OK ngay lập tức
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Chuyển tiếp tới filter / servlet tiếp theo trong chuỗi FilterChain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Giải phóng tài nguyên khi Filter bị hủy
    }
}
