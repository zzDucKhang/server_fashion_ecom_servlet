package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.ShippingAddress;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.ForbiddenException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.identity.DeviceSessionDAO;
import com.fashionshop.dataaccess.dao.identity.ShippingAddressDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.UpdateUserDTO;
import com.fashionshop.presentation.dto.identity.UpdateUserStatusDTO;
import com.fashionshop.presentation.dto.identity.UserQueryDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UserDAO userDAO;
    private final ShippingAddressDAO shippingAddressDAO;
    private final DeviceSessionDAO deviceSessionDAO;

    public UserService() {
        this(new UserDAO(), new ShippingAddressDAO(), new DeviceSessionDAO());
    }

    public UserService(UserDAO userDAO, ShippingAddressDAO shippingAddressDAO, DeviceSessionDAO deviceSessionDAO) {
        this.userDAO = userDAO;
        this.shippingAddressDAO = shippingAddressDAO;
        this.deviceSessionDAO = deviceSessionDAO;
    }

    public Map<String, Object> getUserById(Integer userId, Integer currentUserId, String currentRoleCode) {
        if (userId == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        // Anti-IDOR: Only ADMIN or the account owner can view profile
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền xem thông tin người dùng này");
        }

        User user = userDAO.findByIdWithRole(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        Map<String, Object> userMap = AuthService.toUserResponseMap(user);

        // Include addresses in profile
        List<ShippingAddress> addresses = shippingAddressDAO.findByUserId(userId);
        List<Map<String, Object>> addressMaps = new ArrayList<>();
        for (ShippingAddress addr : addresses) {
            addressMaps.add(toAddressMap(addr));
        }
        userMap.put("shippingAddresses", addressMaps);
        userMap.put("addresses", addressMaps);

        return userMap;
    }

    public PagedResponse<Map<String, Object>> getUsers(UserQueryDTO query, String currentRoleCode) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }

        List<User> users = userDAO.findFiltered(
                query.getSearch(),
                query.getStatus(),
                query.getRoleId(),
                query.getPage(),
                query.getLimit()
        );

        long total = userDAO.countFiltered(
                query.getSearch(),
                query.getStatus(),
                query.getRoleId()
        );

        List<Map<String, Object>> userMaps = new ArrayList<>();
        for (User user : users) {
            userMaps.add(AuthService.toUserResponseMap(user));
        }

        return new PagedResponse<>(userMaps, total, query.getPage(), query.getLimit());
    }

    public Map<String, Object> updateUser(Integer userId, UpdateUserDTO dto, Integer currentUserId, String currentRoleCode) {
        if (userId == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền cập nhật thông tin người dùng này");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        user.updateProfile(dto.getFullName(), dto.getPhoneNumber(), dto.getAvatarUrl());
        user.setUpdatedById(currentUserId);
        User updated = userDAO.update(user);

        return AuthService.toUserResponseMap(updated);
    }

    public Map<String, Object> updateUserStatus(Integer userId, UpdateUserStatusDTO dto, Integer currentUserId, String currentRoleCode) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        User.UserStatus newStatus = dto.toUserStatus();
        user.updateStatus(newStatus);
        user.setUpdatedById(currentUserId);
        User updated = userDAO.update(user);

        // If status changed to BLOCKED, revoke all active sessions immediately
        if (newStatus == User.UserStatus.BLOCKED || newStatus == User.UserStatus.INACTIVE) {
            deviceSessionDAO.deactivateAllByUserId(userId);
        }

        return AuthService.toUserResponseMap(updated);
    }

    public Map<String, Object> softDeleteUser(Integer userId, Integer currentUserId, String currentRoleCode) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentRoleCode);
        if (!isAdmin) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này");
        }

        if (userId != null && userId.equals(currentUserId)) {
            throw new BadRequestException("Bạn không thể tự xóa tài khoản của chính mình");
        }

        userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        userDAO.softDelete(userId, currentUserId);
        deviceSessionDAO.deactivateAllByUserId(userId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa người dùng thành công");
        return response;
    }

    private Map<String, Object> toAddressMap(ShippingAddress addr) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", addr.getId());
        map.put("userId", addr.getUser() != null ? addr.getUser().getId() : null);
        map.put("receiverName", addr.getReceiverName());
        map.put("phoneNumber", addr.getPhoneNumber());
        map.put("province", addr.getProvince());
        map.put("district", addr.getDistrict());
        map.put("ward", addr.getWard());
        map.put("detailAddress", addr.getDetailAddress());
        map.put("isDefault", addr.getIsDefault());
        map.put("createdAt", addr.getCreatedAt());
        map.put("updatedAt", addr.getUpdatedAt());
        return map;
    }
}
