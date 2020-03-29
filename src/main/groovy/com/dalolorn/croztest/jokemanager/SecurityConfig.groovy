package com.dalolorn.croztest.jokemanager

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers('/','/like','/dislike').permitAll()
                .and()
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .and()
            .logout()
                .clearAuthentication(true)
                .logoutUrl('/logout')
                .logoutSuccessUrl('/')
    }

    @Bean
    @Override
    UserDetailsService userDetailsService() {
        Collection<UserDetails> users =
        [
                // I wonder if I should've extracted this into a new function...
            User.withDefaultPasswordEncoder()
                .username("pperic")
                .password("pero")
                .roles("USER")
                .build(),
            User.withDefaultPasswordEncoder()
                .username("iivic")
                .password("ivan")
                .roles("USER")
                .build()
        ]

        return new InMemoryUserDetailsManager(users)
    }
}
