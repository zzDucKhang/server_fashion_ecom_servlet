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
    @com.fasterxml.jackson.annotation.JsonAlias({"comment", "message", "review"})
    private String content;

    @com.fasterxml.jackson.annotation.JsonAlias({"media", "images", "videos", "mediaList"})
    private List<MediaItemDTO> mediaUrls = new ArrayList<>();

    public static class MediaItemDTO {
        private String url;
        private String type;
        private String mediaUrl;
        private String mediaType;

        public String getUrl() {
            return url != null ? url : mediaUrl;
        }
        public void setUrl(String url) {
            this.url = url;
            if (this.mediaUrl == null) this.mediaUrl = url;
        }
        public String getMediaUrl() { return getUrl(); }
        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            if (this.url == null) this.url = mediaUrl;
        }

        public String getType() {
            if (type != null) return type;
            if (mediaType != null) return mediaType;
            return "image";
        }
        public void setType(String type) {
            this.type = type;
            if (this.mediaType == null) this.mediaType = type;
        }
        public String getMediaType() { return getType(); }
        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
            this.type = mediaType;
        }
    }

    @Override
    public Map<String, List<String>> validate() {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        if (productId == null || productId <= 0) {
            ValidationUtil.addError(errors, "productId", "Mã sản phẩm phải là số nguyên dương");
        }

        if (orderId != null && orderId <= 0) {
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
    public String getComment() { return content; }
    public void setComment(String comment) { this.content = comment; }
    public List<MediaItemDTO> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<MediaItemDTO> mediaUrls) { this.mediaUrls = mediaUrls; }
    public List<MediaItemDTO> getMedia() { return mediaUrls; }
    public void setMedia(List<MediaItemDTO> media) { this.mediaUrls = media; }
}