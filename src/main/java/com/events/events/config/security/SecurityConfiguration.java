package com.events.events.config.security;

import com.events.events.config.security.Filters.JWTAuthenticationFilter;
import com.events.events.config.security.Filters.JWTAuthorizationFilter;
import com.events.events.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager(), userService);
        jwtAuthenticationFilter.setFilterProcessesUrl("/v1/account/login");
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/v1/account/login").permitAll()
                .antMatchers("/v1/account/register").permitAll()
                .antMatchers("/v1/account/confirmAccount").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(jwtAuthenticationFilter)
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));

    }
}
