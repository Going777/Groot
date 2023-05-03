package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column
    private String page;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name="user_id", insertable = false, updatable = false)
    private Long userPK;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private UserEntity receiver;

    @Builder
    public NotificationEntity(UserEntity receiver, String content, String url, String page, Long contentId, Boolean isRead) {
        this.receiver = receiver;
        this.content = content;
        this.contentId = contentId;
        this.page = page;
        this.url = url;
        this.isRead = isRead;
    }
}
