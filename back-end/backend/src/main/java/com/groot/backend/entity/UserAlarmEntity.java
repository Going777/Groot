package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_alarm")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAlarmEntity implements Serializable {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column
    private Boolean waterAlarm;

    @Column
    private Boolean commentAlarm;

    @Column
    private Boolean chattingAlarm;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
