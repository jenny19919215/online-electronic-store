package com.jennyz.electronicstore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecirutyConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/product/**")
                .authenticated()
                .antMatchers(HttpMethod.DELETE, "/product/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/product/**").authenticated()
                .anyRequest().permitAll();
               /* .authorizeHttpRequests((requests) -> requests.antMatchers(HttpMethod.POST, "/product/**")
                        .authenticated()
                        .antMatchers(HttpMethod.DELETE, "/product/**").authenticated()
                        .antMatchers(HttpMethod.PUT, "/product/**").authenticated()
                        .anyRequest().permitAll()

                );*/

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("123456")
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }

}
