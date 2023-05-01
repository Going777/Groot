//package com.groot.backend.entity;
//
//import lombok.*;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//import javax.persistence.*;
//
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@EqualsAndHashCode(of = "id")
//@Getter
//public class NotificationEntity extends BaseEntity{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(insertable = false, updatable = false)
//    private Long id;
//
//    @Embedded
//    private String content;
//
//    @Embedded
//    private String url;
//
//    @Column(nullable = false)
//    private Boolean isRead;
//
//    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", insertable=false, updatable=false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private UserEntity receiver;
//
//    @Builder
//    public NotificationEntity(UserEntity receiver, String content, String url, Boolean isRead) {
//        this.receiver = receiver;
//        this.content = content;
//        this.url = url;
//        this.isRead = isRead;
//    }
//}
