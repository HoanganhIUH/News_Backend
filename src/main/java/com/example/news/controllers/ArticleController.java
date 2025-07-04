package com.example.news.controllers;

import com.example.news.model.Article;
import com.example.news.model.ArticleDTO;
import com.example.news.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping
    public List<ArticleDTO> getAllArticles() {
        return articleService.getAllArticles().stream().map(article -> {
            ArticleDTO dto = new ArticleDTO();
            dto.setId(article.getId());
            dto.setTitle(article.getTitle());
            dto.setContent(article.getContent());
            dto.setUrl(article.getUrl());
            dto.setPublishedAt(article.getPublishedAt());
            dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
            dto.setUrlImage(article.getUrlImage());
            dto.setUrlVideo(article.getUrlVideo());
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id)
            .map(article -> {
                ArticleDTO dto = new ArticleDTO();
                dto.setId(article.getId());
                dto.setTitle(article.getTitle());
                dto.setContent(article.getContent());
                dto.setUrl(article.getUrl());
                dto.setPublishedAt(article.getPublishedAt());
                dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
                dto.setUrlImage(article.getUrlImage());
                dto.setUrlVideo(article.getUrlVideo());
                return dto;
            })
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        return articleService.saveArticle(article);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
} 