package com.groot.backend.repository;

import com.groot.backend.entity.TagCountEntity;
import com.groot.backend.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagCountRepository extends JpaRepository<TagCountEntity, Long> {
    @Query(value = "select * from tags_counts where category=:category order by count desc", nativeQuery = true)
    List<TagCountEntity> findbyCategory(String category);
}
