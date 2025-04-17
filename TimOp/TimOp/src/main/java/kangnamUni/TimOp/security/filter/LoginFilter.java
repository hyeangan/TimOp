package kangnamUni.TimOp.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kangnamUni.TimOp.dto.LoginRequestDTO;
import kangnamUni.TimOp.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;

    public LoginFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            //JSON -> Object
            LoginRequestDTO loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getStudentId(), loginRequest.getPassword());
            return authenticationManager.authenticate(authToken);
        }
        catch (IOException e){
            throw new RuntimeException("로그인 요청 처리 중 오류 발생");
        }
    }
}
