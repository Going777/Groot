package com.groot.backend.entity;

import lombok.*;

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

    @Column(name = "kr_name")
    private String krName;

    @Column(name = "bot_name")
    private String botName;

    @Column(name = "flr_lang")
    private String flrLang;

    @Column(name = "grow_speed")
    private String grwSpeed;

    @Column(name = "min_grw_temp")
    private Integer minGrwTemp;

    @Column(name = "max_grw_temp")
    private Integer maxGrwTemp;

    @Column(name = "winter_temp")
    private Integer winterTemp;

    @Column(name = "light_demand")
    private Integer lightDemand;

    @Column(name = "water_circle")
    private Integer waterCircle;

    @Column(name = "mgmt_level")
    private Integer mgmtLevel;

    @Column(name = "mgmt_demand")
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

    @Column(name = "min_humidity")
    private Integer minHumidity;

    @Column(name = "toxic_info")
    private String toxicInfo;

    @Column(name = "smell_degree")
    private String smellDegree;

    @Column
    private Integer height;

    @Column
    private Integer area;

    @Column
    private String description;

    @OneToMany(mappedBy = "plantEntity", cascade = CascadeType.REMOVE)
    private List<PotEntity> potEntities;
}
