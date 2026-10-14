package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApplyVoucherDTO implements Validatable {
    private String code;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (code == null || code.isBlank()) {
            ValidationUtil.addError(errors, "code", "Mã voucher không được để trống");
        } else if (code.trim().length() > 100) {
            ValidationUtil.addError(errors, "code", "Mã voucher tối đa 100 ký tự");
        }

        return errors;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}