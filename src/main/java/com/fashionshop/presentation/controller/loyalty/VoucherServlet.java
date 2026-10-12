package com.fashionshop.presentation.controller.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.business.service.loyalty.VoucherService;
import com.fashionshop.common.response.ApiResponse;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.SecurityGuard;
import com.fashionshop.common.util.ValidationUtil;
import com.fashionshop.presentation.dto.loyalty.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/v1/loyalty-engage/vouchers/*", "/api/v1/vouchers/*"})
public class VoucherServlet extends HttpServlet {
    private final VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/v1/loyalty-engage/vouchers (Admin only)
            SecurityGuard.requireAdmin(req);
            VoucherQueryDTO query = new VoucherQueryDTO();
            if (req.getParameter("page") != null) query.setPage(Integer.parseInt(req.getParameter("page")));
            if (req.getParameter("limit") != null) query.setLimit(Integer.parseInt(req.getParameter("limit")));
            if (req.getParameter("isActive") != null) query.setIsActive(Boolean.parseBoolean(req.getParameter("isActive")));

            var result = voucherService.listVouchers(query);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
        } else {
            // GET /api/v1/loyalty-engage/vouchers/{id} (Admin only)
            SecurityGuard.requireAdmin(req);
            Integer id = Integer.parseInt(pathInfo.substring(1).split("/")[0]);
            Voucher voucher = voucherService.getVoucherById(id);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(voucher));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if ("/validate".equals(pathInfo)) {
            // POST /validate (User Auth)
            Integer userId = SecurityGuard.requireAuth(req);
            ValidateVoucherDTO dto = JsonUtil.fromJson(req, ValidateVoucherDTO.class);
            ValidationUtil.validate(dto);
            var result = voucherService.validateAndCalculate(dto, userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(result));
            return;
        }

        if ("/apply".equals(pathInfo)) {
            // POST /apply (Admin/Checkout Auth)
            Integer userId = SecurityGuard.requireAuth(req);
            ApplyVoucherDTO dto = JsonUtil.fromJson(req, ApplyVoucherDTO.class);
            ValidationUtil.validate(dto);
            voucherService.applyVoucher(dto.getCode(), userId);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok("Đã áp dụng voucher thành công"));
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            // POST / (Create Voucher - Admin only)
            SecurityGuard.requireAdmin(req);
            CreateVoucherDTO dto = JsonUtil.fromJson(req, CreateVoucherDTO.class);
            ValidationUtil.validate(dto);
            Voucher created = voucherService.createVoucher(dto);
            JsonUtil.sendJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.created(created, "Tạo voucher thành công"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPatch(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // PATCH/PUT /{id} (Admin only)
        SecurityGuard.requireAdmin(req);
        Integer id = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        UpdateVoucherDTO dto = JsonUtil.fromJson(req, UpdateVoucherDTO.class);
        ValidationUtil.validate(dto);
        Voucher updated = voucherService.updateVoucher(id, dto);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(updated));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // DELETE /{id} (Admin only)
        SecurityGuard.requireAdmin(req);
        Integer id = Integer.parseInt(req.getPathInfo().substring(1).split("/")[0]);
        voucherService.deleteVoucher(id);
        JsonUtil.sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok("Xoá voucher thành công"));
    }
}