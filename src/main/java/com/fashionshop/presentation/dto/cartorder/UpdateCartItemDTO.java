package com.fashionshop.presentation.dto.cartorder;

public class UpdateCartItemDTO implements Validatable{
    private Integer quantity;

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (defaultName == null || defaultName.trim().isEmpty()) {
            ValidationUtil.addError(errors, "defaultName", "Tên danh mục không được để trống");
        } else if (defaultName.trim().length() > 500) {
            ValidationUtil.addError(errors, "defaultName", "Tên danh mục tối đa 500 ký tự");
        }

        if (parentCategoryId != null && parentCategoryId <= 0) {
            ValidationUtil.addError(errors, "parentCategoryId", "ID danh mục cha phải là số nguyên dương");
        }

        return errors;
    }


    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}