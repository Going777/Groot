package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tags_counts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCountEntity {
    @Id
    @Column(unique = true)
    private String tag;
    @Column
    private Integer count;
}
