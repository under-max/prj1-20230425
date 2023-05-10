package com.example.demo.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.access.expression.*;

import jakarta.annotation.*;
import jakarta.servlet.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.*;

@Configuration
@EnableMethodSecurity //특정 메소드에 시큐리티 걸어줌 귀찮게 filterchain 설정 안해도 됨
public class CustomConfiguration {

	
	// aws 설정들
	@Value("${aws.accessKeyId}")
	private String accessKeyId;
	
	@Value("${aws.secretAccessKey}")
	private String secretAccessKey;
	
	
	@Value("${aws.bucketUrl}")
	private String bucketUrl;	
	
	@Autowired
	private ServletContext application;
	
	@PostConstruct
	public void init() {
		application.setAttribute("bucketUrl", bucketUrl);
		
	}
	
	
	//이하 spring security
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();				
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
		http.csrf().disable(); // 일단 csrf토큰 막아둠 
		
		// 로그인
//		http.formLogin(Customizer.withDefaults()); // 기본로그인
		http.formLogin().loginPage("/member/login");
		http.logout().logoutUrl("/member/logout"); //로그아웃 적용
		
//		http.authorizeHttpRequests().requestMatchers("/add").authenticated(); //로그인 유저만 /add 경로 이용 가능 
//		http.authorizeHttpRequests().requestMatchers("/member/signup").anonymous(); //로그인 안한 사람만 signup이용 가능
//		http.authorizeHttpRequests().requestMatchers("/**").permitAll();

//		이하 spring security expression
//		http.authorizeHttpRequests()
//		.requestMatchers("/add")
//		.access(new WebExpressionAuthorizationManager("isAuthenticated()"));// 첫번째 줄과 같은 코드
//		
//		http.authorizeHttpRequests()
//		.requestMatchers("/member/signup")
//		.access(new WebExpressionAuthorizationManager("isAnonymous()")); //두번째 줄과 같은 코드
//		
//		http.authorizeHttpRequests()
//		.requestMatchers("/**")
//		.access(new WebExpressionAuthorizationManager("permitAll"));
		
		return http.build(); //해당 빌드 적용
	}
	
	// aws 설정
	@Bean
	public S3Client s3client() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
		AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
		
		S3Client s3client = S3Client.builder()
				.credentialsProvider(provider)
				.region(Region.AP_NORTHEAST_2)
				.build();
				
		return s3client;
	}
	
	
}
