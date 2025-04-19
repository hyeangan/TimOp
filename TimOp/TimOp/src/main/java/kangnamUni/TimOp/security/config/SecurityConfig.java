package kangnamUni.TimOp.security.config;

import jakarta.servlet.http.HttpServletResponse;
import kangnamUni.TimOp.security.filter.LoginFilter;
import kangnamUni.TimOp.security.handler.LoginFailureHandler;
import kangnamUni.TimOp.security.handler.LoginSuccessHandler;
import kangnamUni.TimOp.security.jwt.JwtAuthenticationFilter;
import kangnamUni.TimOp.security.jwt.JwtProperties;
import kangnamUni.TimOp.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    SecurityConfig( AuthenticationConfiguration authenticationConfiguration,
                    LoginSuccessHandler loginSuccessHandler,
                    LoginFailureHandler loginFailureHandler,
                    JwtAuthenticationFilter jwtAuthenticationFilter
                    )
    {
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    //비밀번호 암호화(해싱) 도구
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration));
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        loginFilter.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/login", "POST")
        );

        // 1. http는 Spring Security가 자동으로 주입
        // 2. http.xxx() 로 설정 조립
        // 3. http.build() 로 SecurityFilterChain 생성

        http
                .csrf(AbstractHttpConfigurer::disable) //.csrf() -> CsrfConfigurer<HttpSecurity> 람다 받음 -> AbstractHttpConfigurer는 모든 Configurer의 부모 method reference방식 csrf 비활성화 : 세션방식 보안 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Spring Security가 세션을 만들지 않음 Stateless 상태
                .formLogin(AbstractHttpConfigurer::disable) //session방식 form로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) //브라우저 지원 로그인 방식 비활성화
                //경로별 인가
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/join", "/login").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/member").hasRole("MEMBER")
                        .anyRequest().authenticated()
                )
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, LoginFilter.class);


        /*
        * // Spring CORS 설정
        http.cors().configurationSource(request -> {
            var config = new CorsConfiguration();
            config.addExposedHeader("Authorization"); //
            ...
            return config;
        });
        * */
        return http.build();
    }

}
