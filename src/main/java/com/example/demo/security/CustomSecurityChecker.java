package com.example.demo.security;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;

import com.example.demo.domain.*;
import com.example.demo.mapper.*;

@Component //bean등록
public class CustomSecurityChecker {
	//하는일 게시물을 작성한 사람이 현재 로그인 한 사람이 맞는지
	
	@Autowired
	private BoardMapper mapper;
	
	
	public boolean checkBoarderWriter(Authentication authentication, Integer boardId) {
		// 현재 페이지 정보에서 유저를 확인하고 db에서 그 유저에 대한 정보를 얻어와 같은 사람인지 판별하는 로직
		
		Board board = mapper.selectById(boardId); //board기준 db에서정보 확인
		String username = authentication.getName(); //현재 page에 접속한 사람의 이름 확인
		System.out.println(username); 
		String writer = board.getWriter(); //board로 가져온 db정보 중 글씬이 확인
		
		return username.equals(writer); //db상 글쓴이와 page상 글쓴이가 같은지 boolean 으로 확인
	}
	
}
