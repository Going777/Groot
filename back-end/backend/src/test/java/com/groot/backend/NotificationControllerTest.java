package com.groot.backend;

import com.groot.backend.controller.NotificationController;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.UserRepository;
import com.groot.backend.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WebAppConfiguration
@Slf4j
public class NotificationControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository memberRepository;
    @Autowired
    JwtTokenProvider accessTokenHelper;
    @Autowired
    NotificationController notificationController;
    @Autowired
    TestDB testDB;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
//        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        log.info("here"+context);
//        testDB.init();
    }

    @Test
    @DisplayName("SSE에 연결을 진행한다.")
    public void subscribe() throws Exception {
        //given
        UserEntity member = testDB.findGeneralMember();
        String accessToken = accessTokenHelper.createAccessToken(member);

        //when, then
        mockMvc.perform(get("/notifications/subscribe"))
//                        .header("X-AUTH-TOKEN", accessToken))
//                        .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }
}
