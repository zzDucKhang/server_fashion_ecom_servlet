package com.fashionshop.presentation.dto.fulfillment;

public class ReturnMediaDTO {
    private String mediaUrl;
    private String mediaType = "image";

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaType() { return mediaType != null ? mediaType : "image"; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getUrl() { return getMediaUrl(); }
    public void setUrl(String url) { setMediaUrl(url); }
    public String getType() { return getMediaType(); }
    public void setType(String type) { setMediaType(type); }
}
