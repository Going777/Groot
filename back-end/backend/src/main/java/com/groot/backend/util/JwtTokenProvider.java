package com.groot.backend.util;

import com.groot.backend.dto.response.UserDTO;
import com.groot.backend.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    private static Key KEY;
    private static Long ACCESSTOKEN_EXPIRED_PERIOD;
    private static Long REFRESHTOKEN_EXPIRED_PERIOD;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access_time}") Long accessTime,
                            @Value("${jwt.refresh_time}") Long refreshTime) {
        ACCESSTOKEN_EXPIRED_PERIOD = accessTime;
        REFRESHTOKEN_EXPIRED_PERIOD = refreshTime;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    // accessToken 생성
    public String createAccessToken(UserEntity userEntity){
        long now = (new Date()).getTime();
        // 1시간
        Date accessTokenExpiresIn = new Date(now + ACCESSTOKEN_EXPIRED_PERIOD);
        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(accessTokenExpiresIn)
                .claim("id", userEntity.getId())
                .claim("nickName", userEntity.getNickName())
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
        return accessToken;
    }

    // refreshToken 생성
    public String createRefreshToken(Long id){
        long now = (new Date()).getTime();
        // 3일
        Date refreshTokenExpiresIn = new Date(now + REFRESHTOKEN_EXPIRED_PERIOD);
        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(refreshTokenExpiresIn)
                .claim("id",id)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
        return refreshToken;
    }

    // JWT 토큰 복호화하여 토큰에 들어있는 정보 꺼냄
    public Authentication getAuthentication(String accessToken){
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if(claims.get("id") == null){
            throw new RuntimeException("정보가 없는 토큰입니다.");
        }
        Integer i = (Integer) claims.get("id");
        Long id = Long.valueOf(i);
        // 정보 담아서 Authentication 리턴
        UserDTO userDTO = UserDTO.builder()
                .userPK(id)
                .nickName((String)claims.get("nickName"))
                .build();
        return new UsernamePasswordAuthenticationToken(userDTO,"",new ArrayList<>());
    }

    // 토큰 정보 검증
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 토큰입니다.", e);
            throw new JwtException("유효하지 않은 토큰입니다.", HttpStatus.FORBIDDEN);
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.", e);
            throw new JwtException("만료된 토큰입니다.", HttpStatus.FORBIDDEN);
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 토큰입니다.", e);
            throw new JwtException("지원하지 않는 토큰입니다.", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            log.info("토큰의 클레임이 비어있습니다", e);
            throw new JwtException("토큰의 클레임이 비어있습니다", HttpStatus.PRECONDITION_FAILED);
        }
    }

    public static Long getIdByAccessToken(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization").substring(7);
        Integer i = (Integer) parseClaims(accessToken).get("id");
        Long id = Long.valueOf(i);
        return id;
    }

    public static Long getIdByAccessToken(String accessToken){
        Integer i = (Integer) parseClaims(accessToken).get("id");
        Long id = Long.valueOf(i);
        return id;
    }

    // 클레임에서 정보 가져오기
    public static Claims parseClaims(String accessToken){
        try {
            return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
