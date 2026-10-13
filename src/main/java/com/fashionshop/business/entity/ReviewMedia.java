package com.fashionshop.business.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"ReviewMedia\"")
public class ReviewMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"reviewId\"", nullable = false)
    private ProductReview review;

    @Column(name = "\"mediaUrl\"", nullable = false, length = 1000)
    private String mediaUrl;

    @Column(name = "\"mediaType\"", nullable = false, length = 50)
    private String mediaType;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ProductReview getReview() { return review; }
    public void setReview(ProductReview review) { this.review = review; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getUrl() { return mediaUrl; }
    public String getType() { return mediaType; }

    public String getDownloadLink() {
        return this.mediaUrl;
    }

    public String getDirectUrl() {
        return this.mediaUrl;
    }
}