package kangnamUni.TimOp.security.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kangnamUni.TimOp.dto.MemberDetails;
import kangnamUni.TimOp.security.jwt.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    public LoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        String username = memberDetails.getUsername();
        String role = memberDetails.getMember().getRole();

        String token = jwtUtil.generateToken(username, role);

        response.addHeader("Authorization", "Bearer " + token);

        response.setStatus(HttpServletResponse.SC_OK);

        System.out.println("로그인 성공!");
    }
}
