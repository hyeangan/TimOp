package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kangnamUni.TimOp.Service.GoogleOAuthService;
import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.dto.GoogleProfileDTO;
import kangnamUni.TimOp.dto.GoogleTokenResponseDTO;
import kangnamUni.TimOp.dto.JoinMemberDTO;
import kangnamUni.TimOp.dto.JoinResponseDTO;
import kangnamUni.TimOp.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;

@Tag(name = "인증/인가", description = "인증 인가 실행 API")
@RestController
public class AuthController {
    private final MemberService memberService;
    private final GoogleOAuthService googleOAuthService;
    private final JwtUtil jwtUtil;
    @Autowired
    public AuthController(MemberService memberService, GoogleOAuthService googleOAuthService,
                          JwtUtil jwtUtil){
        this.memberService = memberService;
        this.googleOAuthService = googleOAuthService;
        this.jwtUtil = jwtUtil;
    }
    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    @Operation(
            summary = "회원가입",
            description = "학번, 비밀번호, 이름, 전공을 받아 새로운 사용자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = JoinResponseDTO.class),
                                    mediaType = "application/json"
                            )),
                    @ApiResponse(responseCode = "400", description = "이미 존재하는 학번 또는 잘못된 요청")
            }
    )

    @PostMapping("/join")
    public ResponseEntity<?> join( @Parameter(description = "회원가입 정보", required = true) @RequestBody JoinMemberDTO joinMemberDTO){
        try {
            Member member = memberService.join(joinMemberDTO);
            JoinResponseDTO joinMember = new JoinResponseDTO();
            joinMember.setStudentId(member.getStudentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(joinMember);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("회원가입 실패" + e.getMessage());

        }
    }

    @GetMapping("/auth/login/{provider}")
    public void loginStart(@PathVariable String provider, HttpServletResponse res) throws IOException {
        String redirect;
        switch (provider) {
            case "google":
                redirect = UriComponentsBuilder
                        .fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                        .queryParam("client_id", googleClientId)
                        .queryParam("redirect_uri", googleRedirectUri)
                        .queryParam("response_type", "code")
                        .queryParam("scope", "openid email profile")
                        .build().toUriString();
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown provider");
        }

        res.sendRedirect(redirect);
    }
    @GetMapping("/auth/callback/google")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        try {
            // code → token 교환 (WebClient)
            GoogleTokenResponseDTO googleTokenResponseDTO = googleOAuthService.exchangeCode(code);
            // id_token 검증 + 프로필 추출
            GoogleProfileDTO googleProfileDTO = googleOAuthService.verifyAndParseIdToken(googleTokenResponseDTO.getIdToken());
            Member member = memberService.upsertFromGoogle(googleProfileDTO);
            // JWT 발급
            String id = String.valueOf(member.getId());
            String jwt = jwtUtil.generateToken(id, member.getRole());
            //현재 header로 jwt 전달해서 localStorage 저장하는 방식 -> redirect 응답의 헤더/바디를 js가 못읽음
            String html =
                    "<!doctype html><html><body><script>"
                            + "localStorage.setItem('accessToken', '" + escapeForJs(jwt) + "');"
                            + "window.location.href='/home';"
                            + "</script></body></html>";
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.TEXT_HTML)
                    .body(html);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(401).body("google_login_failed");
        }
    }
    private String escapeForJs(String s) {
        return s.replace("\\", "\\\\").replace("'", "\\'");
    }

}
