package com.stayconnected4.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {
	private final String STUDENT_ROLE = "STUDENT";
	private final String ALUMNUS_ROLE = "ALUMNUS";
	private final String FACULTY_ROLE = "FACULTY";
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
					.antMatchers("/login", "/register/**", "/code/new", "/email", "/**.css").permitAll()
					.antMatchers("/job/**", "/postjob", "/jobconfirm", "/profile", "/profileInfo", "/profileinformation/confirm","/update/profile").hasAnyRole(STUDENT_ROLE, ALUMNUS_ROLE, FACULTY_ROLE)
					.anyRequest().authenticated()
				.and()
					.formLogin().loginPage("/login")
					.defaultSuccessUrl("/")
				.and()
					.exceptionHandling().accessDeniedPage("/403");
		return http.build();
	}
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
			.usersByUsernameQuery("SELECT email, password, active FROM user_account WHERE email=?")
			.authoritiesByUsernameQuery("SELECT email, authority FROM user_authority WHERE email=?");
		
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

