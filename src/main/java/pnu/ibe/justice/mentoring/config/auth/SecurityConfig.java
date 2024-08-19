package pnu.ibe.justice.mentoring.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pnu.ibe.justice.mentoring.model.Role;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(
                        (csrfConfig) -> csrfConfig.disable()


                )


                .headers(
                        (headerConfig) -> headerConfig.frameOptions(
                                frameOptionsConfig -> frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        //.requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/posts/new", "/comments/save").hasRole(Role.GUEST.name())
                        .requestMatchers("/","/h2-console/**", "/login/*","/MemberIfForm/**","/notice/**","/lectureList/**",
                                "/logout/*","/favicon.ico","/lib/**", "/css/**","/js/**","/images/**","/scss/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout( // 로그아웃 성공 시 / 주소로 이동
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")
                )

                // OAuth2 로그인 기능에 대한 여러 설정
                .oauth2Login(Customizer.withDefaults()) // 아래 코드와 동일한 결과

                .oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService))
                .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/");
                        })

                );



        /*
                .oauth2Login(
                        (oauth) ->
                            oauth.userInfoEndpoint(
                                    (endpoint) -> endpoint.userService(customOAuth2UserService)
                            )
                );
        */

        return http.build();
    }


}