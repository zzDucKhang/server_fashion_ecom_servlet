package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.business.service.loyalty.VoucherService;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import com.fashionshop.presentation.dto.loyalty.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {
    "/api/v1/loyalty-engage/vouchers",
    "/api/v1/loyalty-engage/vouchers/*",
    "/api/v1/vouchers",
    "/api/v1/vouchers/*"
})
public class VoucherServlet extends BaseHttpServlet {
    private final VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/loyalty-engage/vouchers (Admin only)
            SecurityGuard.requireAdmin(req);
            VoucherQueryDTO query = new VoucherQueryDTO();
            query.setPage(getIntParameter(req, "page", 1));
            query.setLimit(getIntParameter(req, "limit", 10));
            if (req.getParameter("isActive") != null) query.setIsActive(Boolean.parseBoolean(req.getParameter("isActive")));

            var result = voucherService.listVouchers(query);
            sendSuccess(resp, null, result);
        } else {
            // GET /api/v1/loyalty-engage/vouchers/{id} (Admin only)
            SecurityGuard.requireAdmin(req);
            Integer id = parseId(pathInfo);
            Voucher voucher = voucherService.getVoucherById(id);
            sendSuccess(resp, null, voucher);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/validate".equals(pathInfo)) {
            // POST /validate (User Auth)
            Integer userId = getUserId(req);
            ValidateVoucherDTO dto = parseBody(req, ValidateVoucherDTO.class);
            ValidationUtil.validate(dto);
            var result = voucherService.validateAndCalculate(dto, userId);
            sendSuccess(resp, "Hợp lệ", result);
            return;
        }

        if ("/apply".equals(pathInfo)) {
            // POST /apply (Admin/Checkout Auth)
            Integer userId = getUserId(req);
            ApplyVoucherDTO dto = parseBody(req, ApplyVoucherDTO.class);
            ValidationUtil.validate(dto);
            voucherService.applyVoucher(dto.getCode(), userId);
            sendSuccess(resp, "Đã áp dụng voucher thành công", null);
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            // POST / (Create Voucher - Admin only)
            SecurityGuard.requireAdmin(req);
            CreateVoucherDTO dto = parseBody(req, CreateVoucherDTO.class);
            ValidationUtil.validate(dto);
            Voucher created = voucherService.createVoucher(dto);
            sendJson(resp, HttpServletResponse.SC_CREATED, java.util.Map.of(
                    "statusCode", 201,
                    "message", "Tạo voucher thành công",
                    "data", created
            ));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPatch(req, resp);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PATCH/PUT /{id} (Admin only)
        SecurityGuard.requireAdmin(req);
        Integer id = parseId(req.getPathInfo());
        UpdateVoucherDTO dto = parseBody(req, UpdateVoucherDTO.class);
        ValidationUtil.validate(dto);
        Voucher updated = voucherService.updateVoucher(id, dto);
        sendSuccess(resp, "Cập nhật voucher thành công", updated);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /{id} (Admin only)
        SecurityGuard.requireAdmin(req);
        Integer id = parseId(req.getPathInfo());
        voucherService.deleteVoucher(id);
        sendSuccess(resp, "Xoá voucher thành công", null);
    }
}