
    package com.ridesync_v2.ridesync.Config;

    import com.ridesync_v2.ridesync.Service.MyUserDetailsService;
    import com.ridesync_v2.ridesync.Service.RideSyncOAuth2UserService;
    import com.ridesync_v2.ridesync.Service.RideSyncOidcUserService;

    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
    import org.springframework.security.web.context.SecurityContextRepository;
    import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
    import org.springframework.security.web.csrf.CsrfToken;
    import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
    import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
    import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
    import org.springframework.util.StringUtils;

    import java.util.function.Supplier;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        private final MyUserDetailsService myUserDetailsService;

        public SecurityConfig(MyUserDetailsService myUserDetailsService) {
            this.myUserDetailsService = myUserDetailsService;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(12);
        }

        @Bean
        public SecurityContextRepository securityContextRepository() {
            return new HttpSessionSecurityContextRepository();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                HttpSecurity http,
                SecurityContextRepository securityContextRepository,
                RideSyncOAuth2UserService rideSyncOAuth2UserService,
                RideSyncOidcUserService rideSyncOidcUserService
        ) throws Exception {

            CookieCsrfTokenRepository csrfTokenRepository =
                    CookieCsrfTokenRepository.withHttpOnlyFalse();

            http
                    .cors(Customizer.withDefaults())

                    .securityContext(context ->
                            context.securityContextRepository(
                                    securityContextRepository
                            )
                    )

                    .sessionManagement(session ->
                            session.sessionCreationPolicy(
                                    SessionCreationPolicy.IF_REQUIRED
                            )
                    )

                    .authorizeHttpRequests(requests -> requests
                            .requestMatchers(
                                    "/",
                                    "/index.html",
                                    "/assets/**",
                                    "/RideSyncLOgo.jpg",
                                    "/login",
                                    "/register",
                                    "/api/register",
                                    "/about",
                                    "/view",
                                    "/upload",
                                    "/health",
                                    "/csrf",
                                    "/api/login",
                                    "/oauth2/**",
                                    "/login/oauth2/**"
                            ).permitAll()
                            .anyRequest().authenticated()
                    )

                    .oauth2Login(oauth ->
                            oauth
                                    .userInfoEndpoint(userInfo ->
                                            userInfo
                                                    .userService(
                                                            rideSyncOAuth2UserService
                                                    )
                                                    .oidcUserService(
                                                            rideSyncOidcUserService
                                                    )
                                    )
                                    .successHandler(
                                            (request, response, authentication) ->
                                                    response.sendRedirect("/")
                                    )
                    )

                    .csrf(csrf -> csrf
                            .csrfTokenRepository(csrfTokenRepository)
                            .csrfTokenRequestHandler(
                                    new SpaCsrfTokenRequestHandler()
                            )
                    )

                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .deleteCookies("JSESSIONID")
                            .logoutSuccessHandler(
                                    (request, response, authentication) ->
                                            response.setStatus(
                                                    HttpServletResponse.SC_NO_CONTENT
                                            )
                            )
                    )

                    .exceptionHandling(exceptions -> exceptions

                            .authenticationEntryPoint(
                                    (request, response, exception) -> {
                                        response.setStatus(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        );
                                        response.setContentType("application/json");
                                        response.setCharacterEncoding("UTF-8");

                                        response.getWriter().write(
                                                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication is required or has expired\"}"
                                        );
                                    }
                            )

                            .accessDeniedHandler(
                                    (request, response, exception) -> {
                                        response.setStatus(
                                                HttpServletResponse.SC_FORBIDDEN
                                        );
                                        response.setContentType("application/json");
                                        response.setCharacterEncoding("UTF-8");

                                        response.getWriter().write(
                                                "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"You are not allowed to perform this action\"}"
                                        );
                                    }
                            )
                    )

                    .formLogin(form -> form.disable())
                    .httpBasic(basic -> basic.disable());

            return http.build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(
                PasswordEncoder passwordEncoder
        ) {

            DaoAuthenticationProvider provider =
                    new DaoAuthenticationProvider(myUserDetailsService);

            provider.setPasswordEncoder(passwordEncoder);

            return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                AuthenticationConfiguration configuration
        ) throws Exception {

            return configuration.getAuthenticationManager();
        }

        // Handles raw CSRF tokens from the SPA's XSRF-TOKEN cookie.
        private static final class SpaCsrfTokenRequestHandler
                implements CsrfTokenRequestHandler {

            private final CsrfTokenRequestHandler plain =
                    new CsrfTokenRequestAttributeHandler();

            private final CsrfTokenRequestHandler xor =
                    new XorCsrfTokenRequestAttributeHandler();

            @Override
            public void handle(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Supplier<CsrfToken> csrfToken
            ) {
                this.xor.handle(request, response, csrfToken);

                // Force deferred token loading so the cookie is generated.
                csrfToken.get();
            }

            @Override
            public String resolveCsrfTokenValue(
                    HttpServletRequest request,
                    CsrfToken csrfToken
            ) {
                String headerValue =
                        request.getHeader(csrfToken.getHeaderName());

                CsrfTokenRequestHandler handler =
                        StringUtils.hasText(headerValue)
                                ? this.plain
                                : this.xor;

                return handler.resolveCsrfTokenValue(request, csrfToken);
            }
        }
    }
