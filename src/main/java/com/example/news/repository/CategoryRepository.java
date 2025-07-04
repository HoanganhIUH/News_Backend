package com.example.news.repository;

import com.example.news.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface CategoryRepository extends JpaRepository<Category, Long> {
} 