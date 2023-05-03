package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="plants")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kr_name", nullable = false)
    private String krName;

    @Column(name = "sci_name", nullable = false, unique = true)
    private String sciName;

    @Column(name = "grw_type", nullable = false)
    private String grwType;

    @Column(name = "grw_speed", nullable = false)
    @ColumnDefault(value = "'보통'")
    private String grwSpeed;

    @Column(name = "min_grw_temp", nullable = false)
    private Integer minGrwTemp;

    @Column(name = "max_grw_temp", nullable = false)
    private Integer maxGrwTemp;

    @Column(name = "winter_min_temp", nullable = false)
    @ColumnDefault(value = "10")
    private Integer winterMinTemp;

    @Column(name = "min_humidity", nullable = false)
    @ColumnDefault(value = "25")
    private Integer minHumidity;

    @Column(name = "max_humidity", nullable = false)
    private Integer maxHumidity;

    @Column(name = "light_demand", nullable = false)
    private Integer lightDemand;

    @Column(name = "water_cycle", nullable = false)
    @ColumnDefault(value = "53003")
    private Integer waterCycle;

    @Column(name = "mgmt_level", nullable = false)
    private Integer mgmtLevel;

    @Column(name = "mgmt_demand", nullable = false)
    private String mgmtDemand;

    @Column
    private String place;

    @Column(name = "mgmt_tip", length = 500)
    private String mgmtTip;

    @Column(name = "grw_season")
    private String grwSeason;

    @Column
    private String characteristics;

    @Column(name = "insect_info")
    private String insectInfo;


    @Column(name = "toxic_info", nullable = true)
    private String toxicInfo;

    @Column(name = "smell_degree")
    private Integer smellDegree;

    @Column
    private Integer height;

    @Column
    private Integer area;

    @Column(nullable = true, length = 2000)
    private String description;

    @OneToMany(mappedBy = "plantEntity", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<PotEntity> potEntities;
}
