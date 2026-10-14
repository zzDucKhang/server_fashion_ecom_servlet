package com.fashionshop.business.service.identity;

import com.fashionshop.business.entity.ShippingAddress;
import com.fashionshop.business.entity.User;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.exception.UnauthorizedException;
import com.fashionshop.dataaccess.dao.identity.ShippingAddressDAO;
import com.fashionshop.dataaccess.dao.identity.UserDAO;
import com.fashionshop.presentation.dto.identity.CreateAddressDTO;
import com.fashionshop.presentation.dto.identity.UpdateAddressDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddressService {

    private final ShippingAddressDAO shippingAddressDAO;
    private final UserDAO userDAO;

    public AddressService() {
        this(new ShippingAddressDAO(), new UserDAO());
    }

    public AddressService(ShippingAddressDAO shippingAddressDAO, UserDAO userDAO) {
        this.shippingAddressDAO = shippingAddressDAO;
        this.userDAO = userDAO;
    }

    public List<Map<String, Object>> getAddressesByUserId(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }

        List<ShippingAddress> addresses = shippingAddressDAO.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ShippingAddress addr : addresses) {
            result.add(toAddressMap(addr));
        }
        return result;
    }

    public Map<String, Object> getAddressById(Integer addressId, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }
        if (addressId == null) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        // Anti-IDOR: findByIdAndUserId
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại"));

        return toAddressMap(address);
    }

    public Map<String, Object> createAddress(CreateAddressDTO dto, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }

        int currentCount = shippingAddressDAO.countByUserId(userId);
        if (currentCount >= 10) {
            throw new BadRequestException("Mỗi tài khoản chỉ được lưu tối đa 10 địa chỉ nhận hàng");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        boolean makeDefault = currentCount == 0 || Boolean.TRUE.equals(dto.getIsDefault());

        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setReceiverName(dto.getReceiverName());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setProvince(dto.getProvince());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(makeDefault);

        ShippingAddress saved = shippingAddressDAO.createWithDefault(address);
        return toAddressMap(saved);
    }

    public Map<String, Object> updateAddress(Integer addressId, UpdateAddressDTO dto, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }
        if (addressId == null) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        // Anti-IDOR check
        ShippingAddress address = shippingAddressDAO.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Địa chỉ không tồn tại"));

        if (dto.getReceiverName() != null) {
            address.setReceiverName(dto.getReceiverName());
        }
        if (dto.getPhoneNumber() != null) {
            address.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getProvince() != null) {
            address.setProvince(dto.getProvince());
        }
        if (dto.getDistrict() != null) {
            address.setDistrict(dto.getDistrict());
        }
        if (dto.getWard() != null) {
            address.setWard(dto.getWard());
        }
        if (dto.getDetailAddress() != null) {
            address.setDetailAddress(dto.getDetailAddress());
        }
        if (dto.getIsDefault() != null) {
            address.setIsDefault(dto.getIsDefault());
        }
        address.setUpdatedAt(LocalDateTime.now());

        ShippingAddress updated = shippingAddressDAO.updateWithDefault(address);
        return toAddressMap(updated);
    }

    public Map<String, Object> deleteAddress(Integer addressId, Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException("Chưa xác thực người dùng");
        }
        if (addressId == null) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        boolean deleted = shippingAddressDAO.deleteByIdAndUserId(addressId, userId);
        if (!deleted) {
            throw new NotFoundException("Địa chỉ không tồn tại");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Xóa địa chỉ thành công");
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
