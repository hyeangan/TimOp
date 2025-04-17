package kangnamUni.TimOp.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Date;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey; // Java 내장 암호화 키 타입
    private JwtParser jwtParser;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        // Base64 디코딩하여 키 생성
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
        // 토큰 검증 파서 초기화
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
        /*
        HMAC-SHA256 -> 입력: 메시지(=header.payload) + 비밀 키(=SecretKey) ->
        	payload가 중간에 바뀌었는지 감지(무결성) / 서명 키를 아는 사람만 유효한 토큰을 생성(인증)
        * */

    }
    //signature?????????????????
    //JWT 생성 / header payload signature
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration()); //만료

        return Jwts.builder()
                .subject(username) //로그인 식별자(ID, username 등)payload에 "sub":"username"
                .claim("role", role) //커스텀 데이터
                .issuedAt(now) //발급 시간
                .expiration(expiry) //토큰만료 시간
                .signWith(secretKey) //토큰 서명을 위한 HMAC-SHA256 시크릿 키
                .compact(); //설정 끝 문자열로 직렬화
    }

    //토큰에서 username 추출
    public String extractUsername(String token) {
        //secretKey로???????????????
        return jwtParser.parseSignedClaims(token) //JWT 파싱 서버의 secretKey로 서명 검증-> 유효하면 Jws<Claims> 객체 반환
                        .getPayload().getSubject();
    }

     //토큰에서 role 추출
    public String extractRole(String token) {
        return jwtParser.parseSignedClaims(token).getPayload().get("role", String.class);
    }

    //토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        Date expiration = jwtParser.parseSignedClaims(token).getPayload().getExpiration();
        return expiration.before(new Date());
    }


    //토큰 유효성 검사 (서명, 만료 등 포함)
    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token); //??
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰입니다.");
        } catch (JwtException e) {
            System.out.println("유효하지 않은 토큰입니다: " + e.getMessage());
        }
        return false;
    }
}
