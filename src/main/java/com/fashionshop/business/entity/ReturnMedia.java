package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"ReturnMedia\"")
public class ReturnMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"returnRequestId\"", nullable = false)
    private ReturnRequest returnRequest;

    @Column(name = "\"mediaUrl\"", nullable = false, length = 1000)
    private String mediaUrl;

    @Column(name = "\"mediaType\"", nullable = false, length = 50)
    private String mediaType;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ReturnRequest getReturnRequest() { return returnRequest; }
    public void setReturnRequest(ReturnRequest returnRequest) { this.returnRequest = returnRequest; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
}