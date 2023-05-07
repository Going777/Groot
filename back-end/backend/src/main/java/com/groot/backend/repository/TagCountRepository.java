package com.groot.backend.repository;

import com.groot.backend.entity.TagCountEntity;
import com.groot.backend.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagCountRepository extends JpaRepository<TagCountEntity, Long> {
}
