package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.groot.backend.dto.response.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String password;

    @Column
    private String profile;

    @Column
    private String token;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<PotEntity> potEntities;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<DiaryEntity> diaryEntities;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<ArticleEntity> articleEntities;

    public UserDTO toUserDTO(){
        Long date = Duration.between(this.getCreatedDate(), LocalDateTime.now()).toDays() +1;
        UserDTO userDTO = UserDTO.builder()
                .id(this.id)
                .userId(this.userId)
                .nickName(this.nickName)
                .profile(this.profile)
                .registerDate(date)
                .build();
        return userDTO;
    }
}
