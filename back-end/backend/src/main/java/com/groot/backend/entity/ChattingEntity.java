package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="chatting_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChattingEntityPK.class)
public class ChattingEntity extends BaseEntity implements Serializable{

    @Column(name = "room_id")
    private Long roomId;

    @Id
    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender")
    private UserEntity sender;

    @Id
    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver")
    private UserEntity receiver;

    @Id
    @Column(name = "sender", insertable = false, updatable = false)
    private Long senderId;

    @Id
    @Column(name = "receiver", insertable = false, updatable = false)
    private Long receiverId;

}
