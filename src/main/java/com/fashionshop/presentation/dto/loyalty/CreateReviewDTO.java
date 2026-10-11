package com.fashionshop.presentation.dto.loyalty;

import java.util.ArrayList;
import java.util.List;

public class CreateReviewDTO {
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