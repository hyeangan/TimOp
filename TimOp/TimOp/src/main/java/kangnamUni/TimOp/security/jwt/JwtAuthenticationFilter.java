package kangnamUni.TimOp.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kangnamUni.TimOp.Service.MemberDetailsService;
import kangnamUni.TimOp.dto.MemberDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberDetailsService memberDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, MemberDetailsService memberDetailsService) {
        this.jwtUtil = jwtUtil;
        this.memberDetailsService = memberDetailsService;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return p.startsWith("/auth/login/")
                || p.startsWith("/auth/callback/")
                || p.equals("/") || p.equals("/home") || p.equals("/login") || p.equals("/join")
                || p.startsWith("/swagger-ui/") || p.startsWith("/v3/api-docs/") || p.startsWith("/webjars/")
                || p.equals("/error"); // ⬅️ 추가
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        if(requestHeader == null || !requestHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = requestHeader.split(" ")[1];
        if(!jwtUtil.validateToken(token)){
            filterChain.doFilter(request, response);
            return;
        }
        String pkId = jwtUtil.extractUsername(token);//sub 값이고 pk
        if(pkId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = memberDetailsService.loadUserByUsername(pkId);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        filterChain.doFilter(request, response);
    }
}
