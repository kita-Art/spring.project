package com.canesblack.spring_project11.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.canesblack.spring_project11.controller.PageController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
//SpringSecurity 기능을 사용할려면 이 어노테이션을 써줘야함
public class SecurityConfig {

    private final PageController pageController;

    private final CorsConfigurationSource corsCorsfigurationSource;

    SecurityConfig(CorsConfigurationSource corsCorsfigurationSource, PageController pageController) {
        this.corsCorsfigurationSource = corsCorsfigurationSource;
        this.pageController = pageController;
    }

    @Bean
    SecurityFilterChain securityFiterChain(HttpSecurity http) throws Exception{
		//스프링시큐리티 기능을 사용하고자 할떄 이 메소드안에 작성한다.
    	http
    	.csrf(csrf->csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
    	//csrf해킹기법으로 보호조치를 하는 코드방법,=> 나중에 따로 자바스크립트에다가 csrf보호기능도 넣어놓을것
    	.cors(cors->cors.configurationSource(corsCorsfigurationSource()))
    	//cors는 특정서버로만 데이터로 넘길수 잇도록 설정할수 있습니다.
    	.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
    	//세션설정
    	.authorizeHttpRequests(authz->authz.requestMatchers("/","/loginPage","logout","/noticeCheckPage","/register","/menu/all")
    			.permitAll()
    			.requestMatchers(HttpMethod.POST,"/login").permitAll()
    			.requestMatchers("/resources/**","/WEB-INF/**").permitAll()
    			.requestMatchers("/noticerAdd","noticeModifyPage").hasAnyAuthority("ADMIN","MANAGER")
    			.requestMatchers(HttpMethod.POST,"/menu/add").hasAnyAuthority("ADMIN","MANAGER")
    			.requestMatchers(HttpMethod.POST,"/menu/update").hasAnyAuthority("ADMIN","MANAGER")
    			.requestMatchers(HttpMethod.DELETE,"menu/delete").hasAnyAuthority("ADMIN","MANAGER")
    			.anyRequest().authenticated()
    			)
   
    	
    		.formLogin(
		     login->login.loginPage("/loginPage")//url을 작성해서 로그인페이지로 이동할떄
		     .loginProcessingUrl("/login")
		     .failureUrl("/loginPage?error=true")
		     .usernameParameter("username")
		     .passwordParameter("password")
		     .successHandler(authenticationSuccessHandler())
		     .permitAll()
		     )
    	
    	.logout(logout->logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))// logout URL을 통해서 진행됨
    		.logoutSuccessUrl("/")//로그아웃성공후 이 url로 리다이렉팅
    		.invalidateHttpSession(true)//세션무효화
    		.deleteCookies("JSESSIONID")//쿠키삭제
    		.permitAll()
    	    );
    	
    	
    	
    	return http.build();
		
	}
	
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new SimpleUrlAuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// 로그인이 성공했을때 우리가 특별기능을 넣고싶을때(세션,권한기능)
				HttpSession session = request.getSession();//세션 기능을 가지고 온 것
				boolean isManager = authentication.getAuthorities().stream()
						.anyMatch(grantedAuthoirity -> 
						grantedAuthoirity.getAuthority().equals("ADMIN")||
						grantedAuthoirity.getAuthority().equals("MANAGER"));
			    if(isManager) {
			    	session.setAttribute("MANAGER", true);
			    }
				session.setAttribute("username", authentication.getName());
				session.setAttribute("isAuthenticatied", true);
				//localhost: request 8080
				response.sendRedirect(request.getContextPath()+"/");
				super.onAuthenticationSuccess(request, response, authentication);
			}
			
			
	  };
	}
	
	@Bean
	public CorsConfigurationSource corsCorsfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080","https://localhost:8080"));
		//localhost:8080 서버에서는 프론트에서 백엔드단 혹은 백엔드단에서 프로트단인데 데이터를 주고받을 수 있게 만든 것
		configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
		org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	
	
}

	

