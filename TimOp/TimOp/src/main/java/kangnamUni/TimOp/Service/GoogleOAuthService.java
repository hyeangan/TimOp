package kangnamUni.TimOp.Service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import kangnamUni.TimOp.dto.GoogleProfileDTO;
import kangnamUni.TimOp.dto.GoogleTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URL;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final WebClient webClient;

    @Value("${oauth.google.client-id}")     private String clientId;
    @Value("${oauth.google.client-secret}") private String clientSecret;
    @Value("${oauth.google.redirect-uri}")  private String redirectUri;


    // Authorization Code → 토큰 교환
    public GoogleTokenResponseDTO exchangeCode(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        // WebClient로 구글 토큰 엔드포인트 호출
        Map<?, ?> body = webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return new GoogleTokenResponseDTO(
                (String) body.get("access_token"),
                (String) body.get("id_token"),
                ((Number) body.get("expires_in")).intValue()
        );
    }

    //ID Token(JWT) 검증 + 기본 프로필 추출
    public GoogleProfileDTO verifyAndParseIdToken(String idToken) throws Exception {
        // 구글 JWKS(공개키 목록)에서 서명 검증에 필요한 키를 가져옴
        URL jwksURL = new URL("https://www.googleapis.com/oauth2/v3/certs");

        // JWKS 가져오는 네트워크 타임아웃(연결/읽기 3초)
        DefaultResourceRetriever retriever = new DefaultResourceRetriever(3000, 3000);
        JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(jwksURL, retriever);

        // RS256 서명 검증을 위한 Processor 설정
        ConfigurableJWTProcessor<SecurityContext> proc = new DefaultJWTProcessor<>();
        proc.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource));

        // 검증 + 파싱 (서명 OK 아니면 예외)
        JWTClaimsSet c = proc.process(idToken, null);

        // 필수 클레임 검증: iss(발급자), aud(내 clientId), exp(만료)
        String iss = c.getIssuer();
        if (!"https://accounts.google.com".equals(iss) && !"accounts.google.com".equals(iss)) {
            throw new RuntimeException("Invalid issuer: " + iss);
        }
        if (!c.getAudience().contains(clientId)) {
            throw new RuntimeException("Invalid audience");
        }
        Date exp = c.getExpirationTime();
        if (exp == null || exp.before(new Date())) {
            throw new RuntimeException("ID token expired");
        }

        // 필요한 사용자 정보 추출
        return new GoogleProfileDTO(
                c.getSubject(),                           // sub: 구글 사용자 고유 ID
                c.getStringClaim("email"),
                Boolean.TRUE.equals(c.getBooleanClaim("email_verified")),
                (String) c.getClaim("name"),
                (String) c.getClaim("picture")
        );
    }


}