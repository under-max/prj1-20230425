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

	public boolean createProcess(Board board) {
		
		int cnt = mapper.create(board);
		return cnt == 1;
	}

	public Map<String, Object> listBoard(Integer page, String search, String type) { //pagenation용 SELECT
		Integer rowPerPage = 15; // 페이지당 행의 수
		Integer startIndex = (page - 1) * rowPerPage; //20개 보여줄꺼면 *20 10개라면 *10
		// 페이지네이션 필요한 정보
		Integer numOfRecords = mapper.countAll(search, type);//전체 레코드 갯수
		//마지막 페이지 번호 
		Integer lastPageNumber = (numOfRecords - 1) / rowPerPage + 1;
		
		// 페이지네이션 왼쪽 번호 
		Integer leftPageNum = page - 5;		
		
		// 1보다 작을수 없음 
		leftPageNum = Math.max(leftPageNum, 1);
		// 페이지네이션 오른쪽 번호 
		Integer rightPageNum = page + 4;
		rightPageNum = Math.min(rightPageNum, lastPageNumber);
		
		
		//현재 페이지 확인 
//		Integer currentPageNumber = page;
//		System.out.println(page);
		// 빈 혹은 맵에 만들어서 토스 
		
		Map<String, Object> pageInfo = new HashMap<>();
		pageInfo.put("rightPageNum", rightPageNum);
		pageInfo.put("leftPageNum", leftPageNum);
		pageInfo.put("currentPageNum", page);
		pageInfo.put("lastPageNumber", lastPageNumber);
		// 게시물 목록
		List<Board> list = mapper.selectAllPaging(startIndex, rowPerPage, search, type);
		// 게시물 목록 
		return Map.of("pageInfo", pageInfo,
				"boardList", list);
		
	}

}
