package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tags_counts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String tag;
    @Column
    private Double count;
    @Column
    private String category;
}
