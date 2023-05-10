package com.example.demo.service;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.example.demo.domain.*;
import com.example.demo.mapper.*;

@Service
@Transactional(rollbackFor = Exception.class) // checked exception 시 롤백
public class MemberService {

	@Autowired
	private BoardService boardService;
	@Autowired
	private MemberMapper mapper;

	@Autowired
	private PasswordEncoder passwordencoder;

	public boolean signup(Member member) {
		String plain = member.getPassword();
		member.setPassword(passwordencoder.encode(plain));
		int cnt = mapper.insert(member);
		return cnt == 1;

	}

	public List<Member> memberList() {
		List<Member> memberList = mapper.memberList();
		return memberList;
	}

	public Member get(String id) {
		Member member = mapper.selectById(id);
		return member;
	}

	public boolean remove(Member member) {
		// 바로 지우지 않고 패스워드 확인
		Member oldMember = mapper.selectById(member.getId());
		int cnt = 0;
		if (passwordencoder.matches(member.getPassword(), oldMember.getPassword())) { // 평문, 암호화된 password
			
			// 암호가 같으면?

			// 이 회원이 작성한 게시물 row 삭제
			boardService.removeByWriter(member.getId());
			// 회원 테이블 삭제
			cnt = mapper.deleteById(member);

		} else {
			// 암호가 같지 않으면
		}

		return cnt == 1;
	}

	public boolean modify(Member member, String oldPassword) {

		// 패스워드를 바꾸기 위해서 입력
		if (!member.getPassword().isBlank()) {
			// 입력된 패스워드를 암호화
			String plain = member.getPassword();
			member.setPassword(passwordencoder.encode(plain)); // passwordencoder를 통한 난수화
		}
		Member oldMember = mapper.selectById(member.getId());

		int cnt = 0;

		if (passwordencoder.matches(oldPassword, oldMember.getPassword())) { // 평문 pw, 암호화 pw
			cnt = mapper.update(member);
		}

		return cnt == 1;
	}


}
