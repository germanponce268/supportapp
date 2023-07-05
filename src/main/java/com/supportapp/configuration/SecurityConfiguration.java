package com.supportapp.configuration;


import com.supportapp.constant.SecurityConstant;
import com.supportapp.filter.JWTAccessDeniedHandler;
import com.supportapp.filter.JWTAuthenticationEntryPoint;
import com.supportapp.filter.JWTAuthorizationFilter;
import com.supportapp.listeners.AuthenticationSuccessListener;
import com.supportapp.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    private JWTAuthorizationFilter jwtAuthorizationFilter;
    private JWTAccessDeniedHandler jwtAccessDeniedHandler;
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private UserDetailsService userDetailsService;



    public SecurityConfiguration(JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint){
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;

    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

            http
                     .authorizeHttpRequests(auth -> auth.requestMatchers(SecurityConstant.PUBLIC_URLS).permitAll())
                     .csrf(config -> config.disable())
                     .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                     .addFilterBefore(this.jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                     .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                     .authenticationEntryPoint(jwtAuthenticationEntryPoint);

                     return http.build();
    }
    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter(){
        return new JWTAuthorizationFilter();
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
        return new AuthenticationManagerFactoryBean();
    }


}
