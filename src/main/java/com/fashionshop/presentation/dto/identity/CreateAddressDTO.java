package com.fashionshop.presentation.dto.identity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAddressDTO implements Validatable {

    private String receiverName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailAddress;
    private Boolean isDefault = false;

    public CreateAddressDTO() {}

    public CreateAddressDTO(String receiverName, String phoneNumber, String province, String district, String ward, String detailAddress, Boolean isDefault) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (receiverName == null || receiverName.isBlank()) {
            ValidationUtil.addError(errors, "receiverName", "Tên người nhận là bắt buộc");
        } else if (receiverName.trim().length() > 255) {
            ValidationUtil.addError(errors, "receiverName", "Tên người nhận tối đa 255 ký tự");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại là bắt buộc");
        } else {
            String trimmedPhone = phoneNumber.trim();
            if (trimmedPhone.length() > 50) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại tối đa 50 ký tự");
            } else if (!ValidationUtil.isValidPhone(trimmedPhone)) {
                ValidationUtil.addError(errors, "phoneNumber", "Số điện thoại không đúng định dạng");
            }
        }

        if (province == null || province.isBlank()) {
            ValidationUtil.addError(errors, "province", "Tỉnh/Thành phố là bắt buộc");
        } else if (province.trim().length() > 150) {
            ValidationUtil.addError(errors, "province", "Tỉnh/Thành phố tối đa 150 ký tự");
        }

        if (district == null || district.isBlank()) {
            ValidationUtil.addError(errors, "district", "Quận/Huyện là bắt buộc");
        } else if (district.trim().length() > 150) {
            ValidationUtil.addError(errors, "district", "Quận/Huyện tối đa 150 ký tự");
        }

        if (ward == null || ward.isBlank()) {
            ValidationUtil.addError(errors, "ward", "Phường/Xã là bắt buộc");
        } else if (ward.trim().length() > 150) {
            ValidationUtil.addError(errors, "ward", "Phường/Xã tối đa 150 ký tự");
        }

        if (detailAddress == null || detailAddress.isBlank()) {
            ValidationUtil.addError(errors, "detailAddress", "Địa chỉ là bắt buộc");
        } else if (detailAddress.trim().length() > 500) {
            ValidationUtil.addError(errors, "detailAddress", "Địa chỉ chi tiết tối đa 500 ký tự");
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
        return isDefault != null ? isDefault : false;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault != null ? isDefault : false;
    }
}
