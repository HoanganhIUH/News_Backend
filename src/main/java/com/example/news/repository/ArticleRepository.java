package com.example.news.repository;

import com.example.news.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByUrl(String url);
} 