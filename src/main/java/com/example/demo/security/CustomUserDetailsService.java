package com.example.demo.security;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

import com.example.demo.domain.*;
import com.example.demo.mapper.*;

@Component //빈생성
public class CustomUserDetailsService implements UserDetailsService {
	//security user 설정
	@Autowired 
	private MemberMapper mapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		 SELECT FROM Member Where id = #{username}
		 
		 Member member = mapper.selectById(username);
		 
//		 System.out.println("***로그인 위한 정보***");
//		 System.out.println(member);
		 
		 if(member == null) {
			 throw new UsernameNotFoundException(username + "회원이 없습니다.");
		 }
		 
		 List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
		 for(String auth : member.getAuthority()) {
			 authorityList.add(new SimpleGrantedAuthority(auth));
		 }
		 //회원정보가 있다면?
		 UserDetails user = User.builder()
				 .username(member.getId())
				 .password(member.getPassword()) // 눈에보이지는 않으나 client에서의 입력된 부분은 Dao 에서 처리 Matches("평서문", "난수화") 처리함
				 .authorities(authorityList)
//				 .authorities(member.getAuthority().stream().map(SimpleGrantedAuthority::new).toList())
				 .build();
		 //view에 들어가 있는 login.jsp의 input method post/ name="username"/ name="password"로들어오는 부분이 각각
		 //username, password에서 db정보값과 비교하여 정보를 확인함
		 return user;
	}
	
}
