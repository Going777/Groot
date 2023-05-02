package com.groot.backend.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
//@EqualsAndHashCode(of = "id")
@Getter
public class NotificationEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private String content;

    @Column
    private String url;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(name="user_id", insertable = false, updatable = false)
    private Long userPK;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")//, insertable=false, updatable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity receiver;

    @Builder
    public NotificationEntity(UserEntity receiver, String content, String url, Boolean isRead) {
        this.receiver = receiver;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }
}
