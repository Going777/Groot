package com.groot.backend.repository;

import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    List<ArticleEntity> findAllByCategory(String category);
}
