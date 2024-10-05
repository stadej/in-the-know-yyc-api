package com.intheknowyyc.api.config;

import com.intheknowyyc.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuration class for Spring Security.
 * Enables web security and defines the security filter chain.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    @Autowired
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }


    /**
     * Defines the security filter chain.
     * Disables CSRF protection, allows GET requests for all users,
     * requires authentication for POST requests (restricted to ADMIN role),
     * and enables form login and HTTP Basic authentication.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.GET, "/events").permitAll()
                                        .requestMatchers(HttpMethod.POST).permitAll()
                                        .anyRequest()
                                        .authenticated())
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    /**
     * Configures the authentication provider.
     *
     * @return the configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(4));
        return provider;
    }

}
