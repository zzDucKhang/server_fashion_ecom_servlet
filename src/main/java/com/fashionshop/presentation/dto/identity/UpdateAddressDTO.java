package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAddressDTO implements Validatable {

    private String receiverName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailAddress;
    private Boolean isDefault;

    public UpdateAddressDTO() {}

    public UpdateAddressDTO(String receiverName, String phoneNumber, String province, String district, String ward, String detailAddress, Boolean isDefault) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (receiverName != null) {
            String trimmed = receiverName.trim();
            if (trimmed.isEmpty() || trimmed.length() > 255) {
                ValidationUtil.addError(errors, "receiverName", "Tên người nhận từ 1 đến 255 ký tự");
            }
        }

        if (phoneNumber != null) {
            String trimmed = phoneNumber.trim();
            if (trimmed.isEmpty() || trimmed.length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(trimmed)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
            }
        }

        if (province != null) {
            String trimmed = province.trim();
            if (trimmed.isEmpty() || trimmed.length() > 150) {
                ValidationUtil.addError(errors, "province", "Tỉnh/Thành phố từ 1 đến 150 ký tự");
            }
        }

        if (district != null) {
            String trimmed = district.trim();
            if (trimmed.isEmpty() || trimmed.length() > 150) {
                ValidationUtil.addError(errors, "district", "Quận/Huyện từ 1 đến 150 ký tự");
            }
        }

        if (ward != null) {
            String trimmed = ward.trim();
            if (trimmed.isEmpty() || trimmed.length() > 150) {
                ValidationUtil.addError(errors, "ward", "Phường/Xã từ 1 đến 150 ký tự");
            }
        }

        if (detailAddress != null) {
            String trimmed = detailAddress.trim();
            if (trimmed.isEmpty() || trimmed.length() > 500) {
                ValidationUtil.addError(errors, "detailAddress", "Địa chỉ chi tiết từ 1 đến 500 ký tự");
            }
        }

        return errors;
    }

    public String getReceiverName() {
        return receiverName != null ? receiverName.trim() : null;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvince() {
        return province != null ? province.trim() : null;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district != null ? district.trim() : null;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward != null ? ward.trim() : null;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDetailAddress() {
        return detailAddress != null ? detailAddress.trim() : null;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
