package com.supportapp.configuration;


import com.supportapp.constant.CorsConstant;
import com.supportapp.constant.SecurityConstant;
import com.supportapp.filter.*;
import com.supportapp.utility.JWTTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    private JWTAuthorizationFilter jwtAuthorizationFilter;
    private JWTAccessDeniedHandler jwtAccessDeniedHandler;

    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

            http
                    .authorizeHttpRequests(auth -> auth.requestMatchers(SecurityConstant.PUBLIC_URLS).permitAll().anyRequest().authenticated())
                    .cors(Customizer.withDefaults())
                    .csrf(config -> config.disable())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                     .addFilterBefore(this.jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                     .exceptionHandling(handler -> {
                         handler.accessDeniedHandler(this.jwtAccessDeniedHandler);
                         handler.authenticationEntryPoint(this.jwtAuthenticationEntryPoint);
                     });

                     return http.build();
    }
    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter(){
        return new JWTAuthorizationFilter();
    }

    @Bean
    public JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint(){
        return new JWTAuthenticationEntryPoint();
    }
    @Bean
    public JWTTokenProvider jwtTokenProvider() {
        return new JWTTokenProvider();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManagerFactoryBean authenticationManagerFactoryBean(){
        return  new AuthenticationManagerFactoryBean();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(CorsConstant.SUPPORTAPPWEB_URL);
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(Arrays.asList(CorsConstant.ALLOWED_HEADERS));
        corsConfiguration.setExposedHeaders(Arrays.asList(CorsConstant.EXPOSED_HEADERS));
        corsConfiguration.setAllowedMethods(Arrays.asList(CorsConstant.HTTP_METHODS));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }
}
