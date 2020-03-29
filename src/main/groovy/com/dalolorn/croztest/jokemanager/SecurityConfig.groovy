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

/**
 * Spring Security configuration class for the joke manager.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests() // Authorize all requests trying to read the joke list... or use the like/dislike buttons.
                .antMatchers('/','/like','/dislike').permitAll()
                .and()
            .authorizeRequests() // Any requests away from the joke list must be put down with extreme prejudice.
                .anyRequest().authenticated()
                .and()
            .formLogin() // Use the default login form. It's so much nicer than what I would have cobbled together.
                .and()
            .logout() // /logout asks the user if they want to log out (using another default form!), then redirects to the joke list.
                .clearAuthentication(true) // I am not actually sure if this is strictly necessary. There was a spot of weirdness with showing/hiding a button based on login status, and I never got to check if I needed this.
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
