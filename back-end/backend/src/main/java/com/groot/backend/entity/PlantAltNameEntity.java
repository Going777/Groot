package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="plants_alt_names")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantAltNameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alt_name", insertable = false, nullable = false)
    private String altName;

    @ManyToOne(targetEntity = PlantEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    @JsonBackReference
    private PlantEntity plantEntity;
}
