package com.example.todo.auth;

import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
// 역할: 토큰을 발급하고, 서명 위조를 검사하는 객체
public class TokenProvider {

    // 서명에 사용할 값 (512비트 이상의 랜덤 문자열을 권장)
    // @Value: properties 형태의 파일 내용을 읽어서 변수에 대입해주는 아노테이션(yml)
    // (springframework!!로 임포트 해야함)
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.refresh-secret}")
    private String REFRESH_SECRET_KEY;

    /**
     * JSON Web Token 을 생성하는 메서드
     * @param userEntity - 토큰의 내용(클레임)에 포함될 유저 정보
     * @return - 생성된 JSON 을 암호화 한 토큰값
      */
    public String createToken(User userEntity, String secretKey, long duration, ChronoUnit unit) {
        // 토큰 만료 시간 생성
        Date expiry = Date.from(
                Instant.now().plus(duration, unit) //현재 시간으로 부터 1일
        );

        // 토큰 생성
         /*
            {
                "iss": "서비스 이름(발급자)",
                "exp": "2023-12-27(만료일자)",
                "iat": "2023-11-27(발급일자)",
                "email": "로그인한 사람 이메일",
                "role": "Premium"
                ...
                == 서명
            }
         */
        // 추가 클레임 정의
        Map<String, String> claims = new HashMap<>();
        claims.put("email", userEntity.getEmail());
        claims.put("role", userEntity.getRole().toString());

        return Jwts.builder()
                //token Header 에 들어갈 서명
                .signWith(
                        Keys.hmacShaKeyFor(secretKey.getBytes()), //암호화해주는 메서드
                        SignatureAlgorithm.HS512
                )
                // token payload 에 들어갈 클레임 설정
                .setClaims(claims) // 추가 클레임을 먼저 설정해야함.
                .setIssuer("Todo운영자") // iss: 발급자 정보
                .setIssuedAt(new Date()) // iat: 발급 시간
                .setExpiration(expiry) // exp: 만료 시간
                .setSubject(userEntity.getId()) // sub: 토큰을 식별할 수 있는 주요 데이터
                .compact();

    }

    public String createAccessKey(User userEntity) {
        return createToken(userEntity, SECRET_KEY, 30, ChronoUnit.SECONDS);
    }

    public String createRefreshKey(User userEntity) {
        return createToken(userEntity, REFRESH_SECRET_KEY, 10, ChronoUnit.MINUTES);

    }

    // 토큰에서 클레임을 추출하는 로직을 분리함!
    private Claims getClaims(String token, String secretKey) {
        Claims claims = Jwts.parserBuilder()
                // 토큰 발급자의 발급 당시의 서명을 넣어줌.
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                // 서명 위조 검사: 위조된 경우에는 예외가 발생합니다.
                // 위조가 되지 않은 경우 payload를 리턴
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    /**
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 위조 여부를 확인
     * 토큰을 json 으로 파싱해서 클레임(토큰정보)을 리턴
     *
     * @param token - 필터가 전달해 준 토큰
     * @return - 토큰 안에 있는 인증된 유저 정보를 반환
     */

    public TokenUserInfo validateAndGetTokenUserInfo(String token) {
        Claims claims = getClaims(token, SECRET_KEY);

        log.info("claims: {}", claims);

        return TokenUserInfo.builder()
                .userId(claims.getSubject())
                .email(claims.get("email", String.class))
                .role(Role.valueOf(claims.get("role", String.class)))
                .build();

    }

    // refresh token 의 유효성을 검사.
    public boolean validateRefreshToken(String token) {
        try {
            getClaims(token, REFRESH_SECRET_KEY);
            return true;
        } catch (Exception e) {
            log.warn("유효하지 않은 리프레스 토큰!");
            return false;
        }
    }




}










