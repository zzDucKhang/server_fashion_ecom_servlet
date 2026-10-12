package com.fashionshop.presentation.dto.catalog;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateTagDTO implements Validatable {
    private String name;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (name == null || name.isBlank()) {
            ValidationUtil.addError(errors, "name", "Tên tag không được để trống");
        } else if (name.trim().length() > 100) {
            ValidationUtil.addError(errors, "name", "Tên tag tối đa 100 ký tự");
        }

        return errors;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

