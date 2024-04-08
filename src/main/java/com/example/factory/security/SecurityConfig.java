package com.example.factory.security;

import com.example.factory.jwt.AuthTokenFilter;
import com.example.factory.jwt.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private UserDetailsService userDetailsService;
    private AuthenticationEntryPoint authEntryPoint;
    private JwtUtils jwtUtils;
    private AuthenticationSuccessHandler successHandler;

    public SecurityConfig(UserDetailsService userDetailsService, AuthenticationEntryPoint authEntryPoint, JwtUtils jwtUtils, AuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
        this.jwtUtils = jwtUtils;
        this.successHandler = successHandler;
    }

    @Bean
    public AuthTokenFilter authJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public AuthenticationManager registerGlobalAuthentication(AuthenticationManagerBuilder builder) throws Exception {
//        builder.userDetailsService(userDetailsService);
//        return builder.build();
//    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(c -> c.disable())
                .cors(c -> c.disable())
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/login", "/register",  "/js/**", "/css/**", "/favicon.ico", "/**.html", "/", "/send-message").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(o -> o
                        .successHandler(successHandler))
                .formLogin(l -> l
                        .loginPage("/login.html"))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedPage("/access-denied.html"))
                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request,response,authentication) -> authJwtTokenFilter().deactivateToken(request))
                        .logoutSuccessUrl("/"))
                .addFilterBefore(authJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
