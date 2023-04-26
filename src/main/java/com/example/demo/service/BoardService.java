package com.example.demo.service;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.example.demo.domain.*;
import com.example.demo.mapper.*;

// 큰서비스라면 인터페이스로 나눔
// Component여도 상관 없지만 Service안에 Component 포함되어있으므로 명시적으로 @Service 기입
@Service
public class BoardService {
	//mapper 한테 일 시키므로 mapper 주입
	@Autowired
	private BoardMapper mapper;

	public List<Board> listBoard() {
		List<Board> list = mapper.selectALL();
		return list;
	}

	public Board getBoard(Integer id) {
		Board board = mapper.selectById(id);		
		
		return board;
	}

	public boolean modify(Board board) { // 원래 void지만 boolean 은 지정자 마음 
		int cnt = mapper.update(board);
		
		return cnt == 1;
	}

	public boolean remove(Integer id) {
		int cnt = mapper.deleteById(id);
		return cnt == 1;
	}

}
