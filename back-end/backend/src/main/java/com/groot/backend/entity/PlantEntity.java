package com.groot.backend.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="plant")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantEntity extends BaseEntity{
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(name = "kr_name", nullable = false)
    private String krName;

    @Column(name = "bot_name", nullable = false)
    private String botName;

    @Column(name = "flr_lang", nullable = false)
    private String flrLang;

    @Column(name = "grw_speed", nullable = false)
    private String grwSpeed;

    @Column(name = "min_grw_temp", nullable = false)
    private Integer minGrwTemp;

    @Column(name = "max_grw_temp", nullable = false)
    private Integer maxGrwTemp;

    @Column(name = "winter_temp", nullable = false)
    @ColumnDefault(value = "10")
    private Integer winterTemp;

    @Column(name = "light_demand", nullable = false)
    private Integer lightDemand;

    @Column(name = "water_cycle", nullable = false)
    private Integer waterCycle;

    @Column(name = "mgmt_level", nullable = false)
    private Integer mgmtLevel;

    @Column(name = "mgmt_demand", nullable = false)
    private Integer mgmtDemand;

    @Column
    private String place;

    @Column(name = "mgmt_tip")
    private String mgmtTip;

    @Column(name = "grw_season")
    private String grwSeason;

    @Column(name = "grw_season_cd")
    private Integer grwSeasonCd;

    @Column
    private String characteristics;

    @Column(name = "insect_info")
    private String insectInfo;

    @Column(name = "min_humidity", nullable = false)
    @ColumnDefault(value = "25")
    private Integer minHumidity;

    @Column(name = "toxic_info", nullable = false)
    private String toxicInfo;

    @Column(name = "smell_degree")
    private String smellDegree;

    @Column
    private Integer height;

    @Column
    private Integer area;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "plantEntity", cascade = CascadeType.REMOVE)
    private List<PotEntity> potEntities;
}
