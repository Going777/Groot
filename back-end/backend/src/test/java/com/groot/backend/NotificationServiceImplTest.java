package com.groot.backend;

import com.groot.backend.entity.UserEntity;
import com.groot.backend.service.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class NotificationServiceImplTest {
    @Autowired
    NotificationService notificationService;
    @Autowired
    TestDB testDB;

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @BeforeEach
    void beforeEach() {
        testDB.init();
    }

    @Test
    @DisplayName("알림 구독을 진행한다.")
    public void subscribe() throws Exception {
        //given
        UserEntity member = testDB.findGeneralMember();
        String lastEventId = "";

        //when, then
        Assertions.assertDoesNotThrow(() -> notificationService.subscribe(member.getId(), lastEventId));
    }

    @Test
    @DisplayName("알림 메세지를 전송한다.")
    public void send() throws Exception {
        //given
        UserEntity member = testDB.findGeneralMember();
        String lastEventId = "";
        notificationService.subscribe(member.getId(), lastEventId);

        //when, then
//        Assertions.assertDoesNotThrow(() -> notificationService.send(member, "스터디 신청에 지원하셨습니다.", "localhost:8080/comments"));
    }
}
