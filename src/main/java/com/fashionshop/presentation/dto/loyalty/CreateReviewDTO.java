package com.fashionshop.presentation.dto.loyalty;

import com.fashionshop.common.util.Validatable;
import com.fashionshop.common.util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateReviewDTO implements Validatable {
    private Integer productId;
    private Integer orderId;
    private Short rating;
    private String content;
    private List<MediaItemDTO> mediaUrls = new ArrayList<>();

    public static class MediaItemDTO {
        private String url;
        private String type = "image";

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (productId == null || productId <= 0) {
            ValidationUtil.addError(errors, "productId", "Mã sản phẩm phải là số nguyên dương");
        }

        if (orderId == null || orderId <= 0) {
            ValidationUtil.addError(errors, "orderId", "Mã đơn hàng phải là số nguyên dương");
        }

        if (rating == null || rating < 1 || rating > 5) {
            ValidationUtil.addError(errors, "rating", "Số sao đánh giá phải từ 1 đến 5");
        }

        if (content == null || content.isBlank()) {
            ValidationUtil.addError(errors, "content", "Nội dung đánh giá không được để trống");
        } else if (content.trim().length() > 2000) {
            ValidationUtil.addError(errors, "content", "Nội dung đánh giá tối đa 2000 ký tự");
        }

        if (mediaUrls != null) {
            for (int i = 0; i < mediaUrls.size(); i++) {
                MediaItemDTO media = mediaUrls.get(i);
                if (media == null) {
                    ValidationUtil.addError(errors, "mediaUrls[" + i + "]", "Media không được để trống");
                    continue;
                }
                if (media.getUrl() == null || media.getUrl().isBlank()) {
                    ValidationUtil.addError(errors, "mediaUrls[" + i + "].url", "Đường dẫn media không được để trống");
                } else if (media.getUrl().trim().length() > 1000) {
                    ValidationUtil.addError(errors, "mediaUrls[" + i + "].url", "Đường dẫn media tối đa 1000 ký tự");
                }
                if (media.getType() != null && media.getType().trim().length() > 50) {
                    ValidationUtil.addError(errors, "mediaUrls[" + i + "].type", "Loại media tối đa 50 ký tự");
                }
            }
        }

        return errors;
    }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Short getRating() { return rating; }
    public void setRating(Short rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<MediaItemDTO> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<MediaItemDTO> mediaUrls) { this.mediaUrls = mediaUrls; }
}