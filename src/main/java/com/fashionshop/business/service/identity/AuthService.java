package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.DeviceSession;
import com.fashionshop.business.entity.Role;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.*;
import com.fashionshop.common.util.JwtUtil;
import com.fashionshop.common.util.PasswordUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.config.AppConfig;
import com.fashionshop.dataaccess.dao.identity.DeviceSessionDAO;
import com.fashionshop.dataaccess.dao.identity.RoleDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.*;
import io.jsonwebtoken.Claims;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final DeviceSessionDAO deviceSessionDAO;
    private final RedissonClient redisson;
    private final SecureRandom random = new SecureRandom();

    public AuthService() {
        this(new UserDAO(), new RoleDAO(), new DeviceSessionDAO(), RedisUtil.getClient());
    }

    public AuthService(UserDAO userDAO, RoleDAO roleDAO, DeviceSessionDAO deviceSessionDAO, RedissonClient redisson) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.deviceSessionDAO = deviceSessionDAO;
        this.redisson = redisson;
    }

    public Map<String, Object> register(RegisterDTO dto) {
        String email = dto.getEmail();

        Optional<User> existingUserOpt = userDAO.findByEmail(email);
        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();
            if (existing.getStatus() != User.UserStatus.INACTIVE) {
                throw new ConflictException("Email đã được sử dụng");
            }
        }

        // Check Redis cooldown: otp_cooldown:REGISTER:{email}
        checkOtpCooldown("REGISTER", email);

        // Find CUSTOMER role
        Role customerRole = roleDAO.findByCode("CUSTOMER")
                .orElseThrow(() -> new AppException("Hệ thống chưa cấu hình vai trò CUSTOMER", 500));

        String passwordHash = PasswordUtil.hashPassword(dto.getPassword());
        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            user.setFullName(dto.getFullName());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPasswordHash(passwordHash);
            user.setRole(customerRole);
            user.setStatus(User.UserStatus.INACTIVE);
            user = userDAO.update(user);
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(dto.getFullName());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPasswordHash(passwordHash);
            user.setRole(customerRole);
            user.setStatus(User.UserStatus.INACTIVE);
            user.setRewardPoints(0);
            user = userDAO.save(user);
        }

        // Generate and store OTP
        String otpCode = generateAndStoreOtp("REGISTER", email);
        System.out.printf("[OTP-REGISTER] Email: %s, Code: %s%n", email, otpCode);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user", toUserResponseMap(user));
        response.put("message", "Đăng ký thành công. Vui lòng kiểm tra email để lấy mã xác thực.");
        return response;
    }

    public Map<String, Object> login(LoginDTO dto, String clientIp) {
        String email = dto.getEmail();

        User user = userDAO.findByEmailWithRole(email)
                .orElseThrow(() -> new UnauthorizedException("Email hoặc mật khẩu không chính xác"));

        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new ForbiddenException("Tài khoản chưa được kích hoạt. Vui lòng xác thực mã OTP.");
        }
        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new ForbiddenException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ CSKH.");
        }

        if (!PasswordUtil.checkPassword(dto.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Email hoặc mật khẩu không chính xác");
        }

        String roleCode = user.getRole() != null ? user.getRole().getCode() : "CUSTOMER";
        Integer roleId = user.getRole() != null ? user.getRole().getId() : null;

        String accessToken = JwtUtil.generateAccessToken(user.getId(), roleId, roleCode, user.getEmail());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId(), user.getEmail(), roleCode);

        // Create DeviceSession
        DeviceSession session = new DeviceSession();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setIpAddress(clientIp != null && !clientIp.isBlank() ? clientIp : "127.0.0.1");
        session.setIsActive(true);
        session.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(AppConfig.REFRESH_TOKEN_EXPIRATION)));
        deviceSessionDAO.save(session);

        // Cache session in Redis
        setRedisSession(user.getId(), refreshToken);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", toUserResponseMap(user));
        return response;
    }

    public Map<String, Object> verifyCode(VerifyOtpDTO dto) {
        String email = dto.getEmail();
        String type = dto.getType();

        String redisKey = "otp:" + type + ":" + email;
        String cachedOtp = getRedisValue(redisKey);

        if (cachedOtp == null) {
            throw new AppException("Mã xác thực đã hết hạn hoặc không tồn tại. Vui lòng gửi lại mã mới.", 410);
        }

        if (!cachedOtp.equals(dto.getCode())) {
            throw new BadRequestException("Mã xác thực không chính xác");
        }

        // Delete OTP immediately to prevent replay attacks
        deleteRedisKey(redisKey);

        Map<String, Object> response = new LinkedHashMap<>();

        if ("REGISTER".equalsIgnoreCase(type)) {
            User user = userDAO.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
            user.setStatus(User.UserStatus.ACTIVE);
            userDAO.update(user);
            response.put("message", "Xác thực tài khoản thành công!");
        } else if ("FORGOT_PASSWORD".equalsIgnoreCase(type)) {
            userDAO.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
            String resetToken = JwtUtil.generateResetToken(email);
            response.put("resetToken", resetToken);
            response.put("message", "Xác thực mã OTP thành công. Vui lòng đặt lại mật khẩu mới.");
        }

        return response;
    }

    public Map<String, Object> forgotPassword(ForgotPasswordDTO dto) {
        String email = dto.getEmail();

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với địa chỉ email này"));

        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new ForbiddenException("Tài khoản này đã bị khóa");
        }

        checkOtpCooldown("FORGOT_PASSWORD", email);

        String otpCode = generateAndStoreOtp("FORGOT_PASSWORD", email);
        System.out.printf("[OTP-FORGOT_PASSWORD] Email: %s, Code: %s%n", email, otpCode);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Mã xác thực đã được gửi về email của bạn.");
        return response;
    }

    public Map<String, Object> resetPassword(ResetPasswordDTO dto) {
        Claims claims;
        try {
            claims = JwtUtil.verifyResetToken(dto.getToken());
        } catch (Exception e) {
            throw new UnauthorizedException("Mã token đặt lại mật khẩu đã hết hạn hoặc không hợp lệ");
        }

        String purpose = claims.get("purpose", String.class);
        if (!"RESET_PASSWORD".equals(purpose)) {
            throw new BadRequestException("Token không đúng mục đích");
        }

        String email = claims.get("email", String.class);
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản với địa chỉ email này"));

        user.setPasswordHash(PasswordUtil.hashPassword(dto.getNewPassword()));
        userDAO.update(user);

        // Deactivate all DeviceSessions
        deviceSessionDAO.deactivateAllByUserId(user.getId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.");
        return response;
    }

    public Map<String, Object> changePassword(ChangePasswordDTO dto, Integer currentUserId) {
        if (currentUserId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }

        User user = userDAO.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        if (!PasswordUtil.checkPassword(dto.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác");
        }

        user.setPasswordHash(PasswordUtil.hashPassword(dto.getNewPassword()));
        userDAO.update(user);

        // Deactivate all active device sessions
        deviceSessionDAO.deactivateAllByUserId(currentUserId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
        return response;
    }

    public Map<String, Object> refreshToken(RefreshTokenDTO dto, String clientIp) {
        String token = dto.getRefreshToken();
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Vui lòng cung cấp Refresh Token");
        }

        try {
            JwtUtil.verifyRefreshToken(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        DeviceSession session = deviceSessionDAO.findByRefreshToken(token)
                .orElseThrow(() -> new UnauthorizedException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."));

        if (!Boolean.TRUE.equals(session.getIsActive()) || session.isTokenExpired()) {
            throw new UnauthorizedException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        User user = session.getUser();
        if (user == null || user.getStatus() != User.UserStatus.ACTIVE) {
            throw new ForbiddenException("Tài khoản không hợp lệ hoặc đã bị khóa");
        }

        String roleCode = user.getRole() != null ? user.getRole().getCode() : "CUSTOMER";
        Integer roleId = user.getRole() != null ? user.getRole().getId() : null;

        String newAccessToken = JwtUtil.generateAccessToken(user.getId(), roleId, roleCode, user.getEmail());
        String newRefreshToken = JwtUtil.generateRefreshToken(user.getId(), user.getEmail(), roleCode);

        // Rotate session in database
        session.revokeSession();
        deviceSessionDAO.update(session);

        DeviceSession newSession = new DeviceSession();
        newSession.setUser(user);
        newSession.setRefreshToken(newRefreshToken);
        newSession.setIpAddress(clientIp != null && !clientIp.isBlank() ? clientIp : session.getIpAddress());
        newSession.setIsActive(true);
        newSession.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(AppConfig.REFRESH_TOKEN_EXPIRATION)));
        deviceSessionDAO.save(newSession);

        // Rotate Redis session
        deleteRedisKey("session:" + user.getId() + ":" + token);
        setRedisSession(user.getId(), newRefreshToken);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        return response;
    }

    public Map<String, Object> logout(String accessToken, String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            deviceSessionDAO.deactivateByRefreshToken(refreshToken);
        }

        if (accessToken != null && !accessToken.isBlank()) {
            long remainingMillis = JwtUtil.getRemainingMillis(accessToken);
            if (remainingMillis > 0) {
                setRedisBlacklist(accessToken, remainingMillis);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Đăng xuất thành công");
        return response;
    }

    public static Map<String, Object> toUserResponseMap(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("email", user.getEmail());
        map.put("fullName", user.getFullName());
        map.put("phoneNumber", user.getPhoneNumber());
        map.put("avatarUrl", user.getAvatarUrl());
        map.put("rewardPoints", user.getRewardPoints());
        map.put("status", user.getStatus() != null ? user.getStatus().name() : null);

        if (user.getRole() != null) {
            map.put("roleId", user.getRole().getId());
            Map<String, Object> roleMap = new LinkedHashMap<>();
            roleMap.put("id", user.getRole().getId());
            roleMap.put("code", user.getRole().getCode());
            roleMap.put("name", user.getRole().getName());
            map.put("role", roleMap);
        }

        map.put("createdAt", user.getCreatedAt());
        map.put("updatedAt", user.getUpdatedAt());
        return map;
    }

    private void checkOtpCooldown(String type, String email) {
        try {
            if (redisson != null) {
                RBucket<String> cooldownBucket = redisson.getBucket("otp_cooldown:" + type + ":" + email);
                if (cooldownBucket.isExists()) {
                    long ttlMillis = cooldownBucket.remainTimeToLive();
                    long seconds = ttlMillis > 0 ? (ttlMillis / 1000) : 60;
                    throw new AppException(String.format("Vui lòng chờ %d giây trước khi yêu cầu gửi lại mã", Math.max(seconds, 1)), 429);
                }
            }
        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            System.err.println("Warning: Redis cooldown check failed: " + e.getMessage());
        }
    }

    private String generateAndStoreOtp(String type, String email) {
        int code = 100000 + random.nextInt(900000);
        String codeStr = String.valueOf(code);

        try {
            if (redisson != null) {
                RBucket<String> otpBucket = redisson.getBucket("otp:" + type + ":" + email);
                otpBucket.set(codeStr, Duration.ofSeconds(600)); // 10 minutes

                RBucket<String> cooldownBucket = redisson.getBucket("otp_cooldown:" + type + ":" + email);
                cooldownBucket.set("1", Duration.ofSeconds(60)); // 60 seconds cooldown
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis store OTP failed: " + e.getMessage());
        }

        return codeStr;
    }

    private String getRedisValue(String key) {
        try {
            if (redisson != null) {
                RBucket<String> bucket = redisson.getBucket(key);
                return bucket.get();
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis get failed: " + e.getMessage());
        }
        return null;
    }

    private void deleteRedisKey(String key) {
        try {
            if (redisson != null) {
                redisson.getBucket(key).delete();
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis delete failed: " + e.getMessage());
        }
    }

    private void setRedisSession(Integer userId, String refreshToken) {
        try {
            if (redisson != null) {
                RBucket<String> sessionBucket = redisson.getBucket("session:" + userId + ":" + refreshToken);
                sessionBucket.set("active", Duration.ofMillis(AppConfig.REFRESH_TOKEN_EXPIRATION));
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis set session failed: " + e.getMessage());
        }
    }

    private void setRedisBlacklist(String accessToken, long remainingMillis) {
        try {
            if (redisson != null) {
                RBucket<String> blacklistBucket = redisson.getBucket("blacklist:token:" + accessToken);
                blacklistBucket.set("revoked", Duration.ofMillis(remainingMillis));
            }
        } catch (Exception e) {
            System.err.println("Warning: Redis set blacklist failed: " + e.getMessage());
        }
    }
}
