package com.fashionshop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.dao.DeviceSessionDAO;
import com.fashionshop.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Filter bảo mật xác thực danh tính người dùng bằng JSON Web Token (JWT)
 * và kiểm soát phân quyền dựa trên vai trò (Role-Based Access Control - RBAC).
 * Áp dụng cho toàn bộ đường dẫn /api/* ngoại trừ các endpoint công khai (Login / Register).
 */
@WebFilter(filterName = "JwtAuthenticationFilter", urlPatterns = {"/api/*"})
public class JwtAuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private static final String BEARER_PREFIX = "Bearer ";

    // Danh sách các endpoint công khai không yêu cầu Access Token
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh",
        "/api/auth/logout"
    );

    // In-memory cache tối ưu hóa: lưu token hợp lệ trong 60s để giảm thiểu truy vấn DB lặp đi lặp lại
    private static final long SESSION_CACHE_TTL_MS = 60_000L;
    private final Map<String, Long> activeSessionCache = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;
    private final DeviceSessionDAO deviceSessionDAO;

    public JwtAuthenticationFilter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.deviceSessionDAO = new DeviceSessionDAO();
    }

    // Constructor phục vụ Mock Dependency Injection khi viết Unit Test
    public JwtAuthenticationFilter(DeviceSessionDAO deviceSessionDAO, ObjectMapper objectMapper) {
        this.deviceSessionDAO = deviceSessionDAO;
        this.objectMapper = objectMapper;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.info("JwtAuthenticationFilter da khoi tao thanh cong cho urlPatterns = [/api/*]");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1. Cho phép request Preflight CORS (OPTIONS) đi qua ngay lập tức
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Chuẩn hóa Request Path (bỏ contextPath nếu có)
        String path = getNormalizedPath(req);

        // 2. Bỏ qua xác thực cho các public endpoint (Login, Register)
        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Trích xuất Authorization Header
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: Yêu cầu cung cấp Authorization header hợp lệ dạng 'Bearer <token>'.");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: Token xác thực không được để trống.");
            return;
        }

        // 4. Kiểm tra chữ ký và tính hợp lệ của JWT qua JwtUtil (Thực hiện trên CPU, 0 DB Hit)
        if (!JwtUtil.validateToken(token)) {
            activeSessionCache.remove(token);
            writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: Token không hợp lệ hoặc đã hết hạn.");
            return;
        }

        // 5. Kiểm tra trạng thái thu hồi phiên (Revocation / Logout) qua DeviceSessionDAO
        if (!isSessionValidAndActive(token)) {
            activeSessionCache.remove(token);
            writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: Phiên đăng nhập đã bị thu hồi hoặc đã kết thúc.");
            return;
        }

        // 6. [RBAC Guard] Kiểm tra quyền hạn truy cập dựa trên Role trong Token
        String role = JwtUtil.getRoleFromToken(token);
        if (isAdminEndpoint(path)) {
            if (role == null || (!"ADMIN".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role))) {
                writeErrorResponse(res, HttpServletResponse.SC_FORBIDDEN,
                        "Forbidden: Bạn không có quyền Quản trị viên (ADMIN) để truy cập tài nguyên này.");
                return;
            }
        }

        // 7. Gán thông tin danh tính người dùng vào HttpServletRequest attributes để các Servlet tầng sau sử dụng
        Integer userId = JwtUtil.getUserIdFromToken(token);
        req.setAttribute("userId", userId);
        req.setAttribute("userRole", role);
        req.setAttribute("authToken", token);

        // Chuyển tiếp request đến filter tiếp theo hoặc Servlet đích
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        activeSessionCache.clear();
    }

    /**
     * Kiểm tra phiên đăng nhập có hợp lệ và chưa bị thu hồi (với lớp cache 60s tránh quét DB liên tục).
     */
    private boolean isSessionValidAndActive(String token) {
        long now = System.currentTimeMillis();
        Long cachedExpiry = activeSessionCache.get(token);
        if (cachedExpiry != null && cachedExpiry > now) {
            return true;
        }

        // Truy vấn DB index qua DeviceSessionDAO
        boolean activeInDb = deviceSessionDAO.isSessionActive(token);
        if (activeInDb) {
            // Giới hạn kích thước cache để bảo vệ bộ nhớ
            if (activeSessionCache.size() > 5000) {
                activeSessionCache.clear();
            }
            activeSessionCache.put(token, now + SESSION_CACHE_TTL_MS);
            return true;
        }
        return false;
    }

    private boolean isPublicEndpoint(String path) {
        if (path == null) return false;
        for (String publicPath : PUBLIC_ENDPOINTS) {
            if (path.equalsIgnoreCase(publicPath) || path.equalsIgnoreCase(publicPath + "/")) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdminEndpoint(String path) {
        if (path == null) return false;
        return path.equalsIgnoreCase("/api/admin") || path.startsWith("/api/admin/");
    }

    private String getNormalizedPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private void writeErrorResponse(HttpServletResponse res, int statusCode, String message) throws IOException {
        res.setStatus(statusCode);
        res.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(res.getOutputStream(), ApiResponse.error(statusCode, message));
    }
}
