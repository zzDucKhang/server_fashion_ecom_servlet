package com.fashionshop.presentation.controller.identity;

import com.fashionshop.business.service.identity.AddressService;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.identity.CreateAddressDTO;
import com.fashionshop.presentation.dto.identity.UpdateAddressDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/v1/identity/addresses",
    "/api/v1/identity/addresses/*",
    "/api/v1/addresses",
    "/api/v1/addresses/*",
    "/api/addresses",
    "/api/addresses/*"
})
public class AddressServlet extends BaseHttpServlet {

    private final AddressService addressService = new AddressService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/identity/addresses -> list current user's addresses
            List<Map<String, Object>> addresses = addressService.getAddressesByUserId(currentUserId);
            sendSuccess(resp, null, addresses);
        } else {
            // GET /api/v1/identity/addresses/:addressId -> detail (Anti-IDOR)
            Integer addressId = parseId(pathInfo);
            Map<String, Object> address = addressService.getAddressById(addressId, currentUserId);
            sendSuccess(resp, null, address);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);

        CreateAddressDTO dto = parseBody(req, CreateAddressDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> created = addressService.createAddress(dto, currentUserId);
        sendJson(resp, HttpServletResponse.SC_CREATED, Map.of(
                "statusCode", 201,
                "message", "Tạo địa chỉ thành công",
                "data", created
        ));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);
        Integer addressId = parseId(req.getPathInfo());

        UpdateAddressDTO dto = parseBody(req, UpdateAddressDTO.class);
        ValidationUtil.validate(dto);

        Map<String, Object> updated = addressService.updateAddress(addressId, dto, currentUserId);
        sendSuccess(resp, "Cập nhật địa chỉ thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer currentUserId = getUserId(req);
        Integer addressId = parseId(req.getPathInfo());

        Map<String, Object> result = addressService.deleteAddress(addressId, currentUserId);
        sendSuccess(resp, null, result);
    }
}
