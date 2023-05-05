package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="characters")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="type")
    private Long type;
    @Column(nullable = false)
    private Integer level;
    @Column (name = "glb_path")
    private String glbPath;
    @Column(name = "png_path")
    private String pngPath;
}