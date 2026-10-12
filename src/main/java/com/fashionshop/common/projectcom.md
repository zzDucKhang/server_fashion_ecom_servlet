# Project Dump

**Root:** `C:\Programing\Lap_trinh_Web\fashion_ecom\src\main\java\com\fashionshop\common`

**Generated:** 2026-10-12 17:12:03.936904

**Files:** 18 | **Total size:** 21,593 bytes

---

# exception\AppException.java

| Property | Value |
|----------|-------|
| Path | `exception\AppException.java` |
| Size | 328 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:10:32.164000 |

```java
package com.fashionshop.common.exception;


public class AppException extends RuntimeException {
    private final int statusCode;

    public AppException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return statusCode; }
}

```

# exception\BadRequestException.java

| Property | Value |
|----------|-------|
| Path | `exception\BadRequestException.java` |
| Size | 303 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:30:31.066000 |

```java
package com.fashionshop.common.exception;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException() {
        super("Dữ liệu đầu vào không hợp lệ", 400);
    }
}

```

# exception\ConflictException.java

| Property | Value |
|----------|-------|
| Path | `exception\ConflictException.java` |
| Size | 310 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:31:35.548000 |

```java
package com.fashionshop.common.exception;

public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(message, 409);
    }

    public ConflictException() {
        super("Dữ liệu đã tồn tại hoặc xảy ra xung đột", 409);
    }
}
```

# exception\ForbiddenException.java

| Property | Value |
|----------|-------|
| Path | `exception\ForbiddenException.java` |
| Size | 314 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:31:03.214000 |

```java
package com.fashionshop.common.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(message, 403);
    }

    public ForbiddenException() {
        super("Bạn không có quyền thực hiện hành động này", 403);
    }
}
```

# exception\NotFoundException.java

| Property | Value |
|----------|-------|
| Path | `exception\NotFoundException.java` |
| Size | 303 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:31:16.544000 |

```java
package com.fashionshop.common.exception;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(message, 404);
    }

    public NotFoundException() {
        super("Bản ghi không tồn tại trong hệ thống", 404);
    }
}

```

# exception\UnauthorizedException.java

| Property | Value |
|----------|-------|
| Path | `exception\UnauthorizedException.java` |
| Size | 334 bytes |
| Extension | `.java` |
| Modified | 2026-10-07 12:30:46.277000 |

```java
package com.fashionshop.common.exception;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException() {
        super("Chưa xác thực danh tính hoặc phiên đăng nhập hết hạn", 401);
    }
}
```

# exception\ValidationException.java

| Property | Value |
|----------|-------|
| Path | `exception\ValidationException.java` |
| Size | 734 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.338785 |

```java
package com.fashionshop.common.exception;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidationException extends AppException {
    private final Map<String, List<String>> errors;

    public ValidationException(Map<String, List<String>> errors) {
        super("Dữ liệu đầu vào không hợp lệ", 422);
        this.errors = errors != null ? errors : Collections.emptyMap();
    }

    public ValidationException(String field, String message) {
        super("Dữ liệu đầu vào không hợp lệ", 422);
        this.errors = Map.of(field, List.of(message));
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}

```

# response\ApiResponse.java

| Property | Value |
|----------|-------|
| Path | `response\ApiResponse.java` |
| Size | 1,464 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.380153 |

```java
package com.fashionshop.common.response;


public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.statusCode = 200;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.statusCode = 201;
        res.message = message;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.statusCode = statusCode;
        res.message = message;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.statusCode = statusCode;
        res.message = message;
        return res;
    }

    // Getters and Setters...
    public boolean isSuccess() { return success; }
    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}

```

# response\PagedResponse.java

| Property | Value |
|----------|-------|
| Path | `response\PagedResponse.java` |
| Size | 1,170 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:30:00.424000 |

```java
package com.fashionshop.common.response;

import java.util.List;

public class PagedResponse<T> {
    private List<T> data;
    private long totalItems;
    private int page;
    private int limit;
    private int totalPages;

    public PagedResponse() {}

    public PagedResponse(List<T> data, long totalItems, int page, int limit) {
        this.data = data;
        this.totalItems = totalItems;
        this.page = page;
        this.limit = limit;
        this.totalPages = (limit > 0) ? (int) Math.ceil((double) totalItems / limit) : 1;
    }

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
```

# util\JPAUtil.java

| Property | Value |
|----------|-------|
| Path | `util\JPAUtil.java` |
| Size | 1,467 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:28.382151 |

```java
package com.fashionshop.common.util;

import com.fashionshop.config.AppConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            Map<String, Object> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", AppConfig.DB_URL);
            props.put("jakarta.persistence.jdbc.user", AppConfig.DB_USER);
            props.put("jakarta.persistence.jdbc.password", AppConfig.DB_PASSWORD);

            System.out.println(">>> [JPAUtil] Đang kết nối DB: " + AppConfig.DB_URL);
            System.out.println(">>> [JPAUtil] User DB: " + AppConfig.DB_USER);

            emf = Persistence.createEntityManagerFactory("FashionShopPU", props);
        } catch (Throwable ex) {
            System.err.println("❌ LỖI KHỞI TẠO ENTITY MANAGER FACTORY CHI TIẾT:");
            ex.printStackTrace(); // Bắt buộc phải có dòng này để thấy nguyên nhân gốc
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
```

# util\JsonUtil.java

| Property | Value |
|----------|-------|
| Path | `util\JsonUtil.java` |
| Size | 1,779 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 16:02:02.492000 |

```java
package com.fashionshop.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Reader;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Hibernate6Module())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static <T> T fromJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        return mapper.readValue(req.getReader(), clazz);
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) throws IOException {
        return mapper.readValue(reader, clazz);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    public static String toJson(Object obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }

    public static void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getOutputStream(), data);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
```

# util\JwtUtil.java

| Property | Value |
|----------|-------|
| Path | `util\JwtUtil.java` |
| Size | 4,033 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.404792 |

```java
package com.fashionshop.common.util;


import com.fashionshop.config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private static final SecretKey ACCESS_KEY = Keys.hmacShaKeyFor(
            AppConfig.ACCESS_TOKEN_SECRET.getBytes(StandardCharsets.UTF_8)
    );

    private static final SecretKey REFRESH_KEY = Keys.hmacShaKeyFor(
            AppConfig.REFRESH_TOKEN_SECRET.getBytes(StandardCharsets.UTF_8)
    );

    private static final SecretKey RESET_KEY = Keys.hmacShaKeyFor(
            AppConfig.get("JWT_RESET_SECRET", AppConfig.ACCESS_TOKEN_SECRET).getBytes(StandardCharsets.UTF_8)
    );

    private static final long RESET_TOKEN_EXPIRATION = 15 * 60 * 1000L; // 15 minutes

    public static String generateAccessToken(Integer userId, Integer roleId, String roleCode, String email) {
        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("roleId", roleId)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + AppConfig.ACCESS_TOKEN_EXPIRATION));

        if (roleCode != null) {
            builder.claim("roleCode", roleCode);
        }

        return builder.signWith(ACCESS_KEY).compact();
    }

    public static String generateAccessToken(Integer userId, Integer roleId, String email) {
        return generateAccessToken(userId, roleId, null, email);
    }

    public static Claims verifyToken(String token) {
        return verifyAccessToken(token);
    }

    public static Claims verifyAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(ACCESS_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String generateRefreshToken(Integer userId, String email, String roleCode) {
        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + AppConfig.REFRESH_TOKEN_EXPIRATION));

        if (roleCode != null) {
            builder.claim("roleCode", roleCode);
        }

        return builder.signWith(REFRESH_KEY).compact();
    }

    public static Claims verifyRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(REFRESH_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String generateResetToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .claim("purpose", "RESET_PASSWORD")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + RESET_TOKEN_EXPIRATION))
                .signWith(RESET_KEY)
                .compact();
    }

    public static Claims verifyResetToken(String token) {
        return Jwts.parser()
                .verifyWith(RESET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static long getRemainingMillis(Claims claims) {
        if (claims == null || claims.getExpiration() == null) {
            return 0;
        }
        long diff = claims.getExpiration().getTime() - System.currentTimeMillis();
        return Math.max(diff, 0);
    }

    public static long getRemainingMillis(String token) {
        try {
            Claims claims = verifyAccessToken(token);
            return getRemainingMillis(claims);
        } catch (Exception e) {
            return 0;
        }
    }
}
```

# util\MailUtil.java

| Property | Value |
|----------|-------|
| Path | `util\MailUtil.java` |
| Size | 2,459 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 15:00:59.442805 |

```java
package com.fashionshop.common.util;

import com.fashionshop.config.AppConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {
    public static void sendVerificationEmail(String toEmail, String otpCode) {
        String mailUser = AppConfig.get("MAIL_USER", "your-email@gmail.com");
        String mailPass = AppConfig.get("MAIL_PASS", "your-app-password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUser, mailPass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUser, "Fashion Store"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã xác thực OTP – Fashion Store");

            String htmlContent = String.format("""
                <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;padding:24px;border:1px solid #e5e7eb;border-radius:8px">
                    <h2 style="color:#111827">Mã xác thực của bạn</h2>
                    <p style="color:#374151">Sử dụng mã OTP dưới đây để xác thực tài khoản:</p>
                    <div style="font-size:36px;font-weight:bold;letter-spacing:8px;text-align:center;color:#4f46e5;padding:16px 0">%s</div>
                    <p style="color:#6b7280;font-size:14px">⏱ Mã có hiệu lực trong <strong>10 phút</strong> kể từ khi nhận được email này.</p>
                    <p style="color:#6b7280;font-size:13px">⚠️ Không chia sẻ mã này với bất kỳ ai.</p>
                </div>
                """, otpCode);

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }
}
```

# util\PasswordUtil.java

| Property | Value |
|----------|-------|
| Path | `util\PasswordUtil.java` |
| Size | 507 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:21:52.523000 |

```java
package com.fashionshop.common.util;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Mã hóa mật khẩu khi Register / Đổi mật khẩu
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
    // Kiểm tra mật khẩu khi Login
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
```

# util\RedisUtil.java

| Property | Value |
|----------|-------|
| Path | `util\RedisUtil.java` |
| Size | 793 bytes |
| Extension | `.java` |
| Modified | 2026-10-11 21:21:52.377000 |

```java
package com.fashionshop.common.util;


import com.fashionshop.config.AppConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisUtil {
    private static final RedissonClient redisson;

    static {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(AppConfig.REDIS_URL)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(20);
        redisson = Redisson.create(config);
    }

    public static RedissonClient getClient() {
        return redisson;
    }

    public static void shutdown() {
        if (redisson != null && !redisson.isShutdown()) {
            redisson.shutdown();
        }
    }
}

```

# util\SecurityGuard.java

| Property | Value |
|----------|-------|
| Path | `util\SecurityGuard.java` |
| Size | 2,293 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.428787 |

```java
package com.fashionshop.common.util;


import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

public class SecurityGuard {

    /**
     * Tương đương requireAuth trong Node.js
     */
    public static Integer requireAuth(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Vui lòng đăng nhập để thực hiện thao tác này");
        }

        try {
            String token = authHeader.substring(7).trim();
            Claims claims = JwtUtil.verifyToken(token);
            Integer userId = claims.get("userId", Integer.class);
            Integer roleId = claims.get("roleId", Integer.class);
            String roleCode = claims.get("roleCode", String.class);
            String email = claims.get("email", String.class);
            req.setAttribute("currentUserId", userId);
            req.setAttribute("currentRoleId", roleId);
            req.setAttribute("currentRoleCode", roleCode != null ? roleCode : (roleId != null && roleId == 1 ? "ADMIN" : "CUSTOMER"));
            req.setAttribute("currentUserEmail", email);
            return userId;
        } catch (Exception e) {
            throw new UnauthorizedException("Access token không hợp lệ hoặc đã hết hạn");
        }
    }

    /**
     * Tương đương requireAdmin trong Node.js (roleId = 1 hoặc roleCode = ADMIN)
     */
    public static Integer requireAdmin(HttpServletRequest req) {
        Integer userId = requireAuth(req);
        Integer roleId = (Integer) req.getAttribute("currentRoleId");
        String roleCode = (String) req.getAttribute("currentRoleCode");

        if ((roleId == null || roleId != 1) && !"ADMIN".equalsIgnoreCase(roleCode)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }
        return userId;
    }

    public static String getRoleCode(HttpServletRequest req) {
        return (String) req.getAttribute("currentRoleCode");
    }
}

```

# util\Validatable.java

| Property | Value |
|----------|-------|
| Path | `util\Validatable.java` |
| Size | 167 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.458786 |

```java
package com.fashionshop.common.util;

import java.util.List;
import java.util.Map;

public interface Validatable {
    Map<String, List<String>> validate();
}

```

# util\ValidationUtil.java

| Property | Value |
|----------|-------|
| Path | `util\ValidationUtil.java` |
| Size | 2,835 bytes |
| Extension | `.java` |
| Modified | 2026-10-12 14:35:40.460787 |

```java
package com.fashionshop.common.util;

import com.fashionshop.common.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0[35789])[0-9]{8}$"
    );

    private static final Pattern OTP_PATTERN = Pattern.compile(
            "^\\d{6}$"
    );

    private ValidationUtil() {}

    public static void validate(Validatable dto) {
        if (dto == null) {
            throw new ValidationException(Map.of("body", List.of("Dữ liệu đầu vào không được để trống")));
        }
        Map<String, List<String>> errors = dto.validate();
        if (errors != null && !errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static void addError(Map<String, List<String>> errors, String field, String message) {
        errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidOtp(String otp) {
        return otp != null && OTP_PATTERN.matcher(otp.trim()).matches();
    }

    public static void validatePasswordComplexity(String password, String fieldName, Map<String, List<String>> errors) {
        if (password == null || password.isBlank()) {
            addError(errors, fieldName, "Mật khẩu không được để trống");
            return;
        }
        if (password.length() < 8) {
            addError(errors, fieldName, "Mật khẩu tối thiểu 8 ký tự");
        }
        if (password.length() > 72) {
            addError(errors, fieldName, "Mật khẩu tối đa 72 ký tự");
        }
        if (!password.matches(".*[A-Z].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 chữ in hoa");
        }
        if (!password.matches(".*[a-z].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 chữ in thường");
        }
        if (!password.matches(".*[0-9].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 chữ số");
        }
        if (!password.matches(".*[^A-Za-z0-9].*")) {
            addError(errors, fieldName, "Mật khẩu cần ít nhất 1 ký tự đặc biệt");
        }
    }
}

```

