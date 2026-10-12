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
