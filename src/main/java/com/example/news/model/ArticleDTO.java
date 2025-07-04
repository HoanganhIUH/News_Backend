package com.example.news.model;

import java.time.LocalDateTime;

public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String url;
    private LocalDateTime publishedAt;
    private String categoryName;
    private String urlImage;
    private String urlVideo;

    public ArticleDTO() {}

    public ArticleDTO(Long id, String title, String content, String url, LocalDateTime publishedAt, String categoryName, String urlImage, String urlVideo) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.publishedAt = publishedAt;
        this.categoryName = categoryName;
        this.urlImage = urlImage;
        this.urlVideo = urlVideo;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }
} 