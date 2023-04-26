package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import com.example.demo.domain.*;
import com.example.demo.mapper.*;
import com.example.demo.service.*;

@Controller
@RequestMapping("/")
public class BoardController {
	
	@Autowired
	private BoardService service; // 3단레이어로 service에 일시킴
	//경로 : http:// localhost:8080
	// 경로: http:// localhost:8080/list 
	//게시물 목록
//	@RequestMapping(value = {"/", "list"}, method= RequestMethod.GET) 이하 같은 의미
	@GetMapping({"/", "list"})
	public String list(Model model) {
		// 1.request param 수집/ 가공
		// 2. bussiness logic 처리 (게시물 목록 보여주기) mapper이용
		List<Board> list = service.listBoard();
		
		// 3. add attribute (List추가)
		model.addAttribute("boardList", list);
		System.out.println(list);
		// 4. forward/redirect
		
		
		return "list";
	}
	
	@GetMapping("/id/{id}") //개별조회
	public String board(@PathVariable("id") Integer id, Model model){
		// 1.request param
		// 2.business logic
		// -> service 몫
		Board board = service.getBoard(id);
		// 3. add Attribute
		model.addAttribute("board", board);
		System.out.println(board);
		// 4.forward/ redirect 
		return "get";		
	}
	
	@GetMapping("/modify/{id}") // 개별수정 조회
	public String modifyForm(@PathVariable("id") Integer id, Model model) {
		// 1.request Param
		
		//2.business logic
		// 일단 조회
		model.addAttribute("board", service.getBoard(id));
		System.out.println(service.getBoard(id));
		//3 add Attribute
		
		return "modify";
	}
	
//	@RequestMapping(value="/modify/{id}", method = RequestMethod.POST)
	@PostMapping("/modify/{id}")//개별수정
	public String modifyProcess(Board board, RedirectAttributes rttr) { // form submit으로 들어온거 받음 
		
		boolean ok = service.modify(board);
		
		if(ok) {
			// 해당 게시물 보기로 리디렉션
			rttr.addAttribute("success", "success"); //쿼리스트링 붙어서 넘어감
			return "redirect:/id/" + board.getId();
		} else {
			// 수정폼으로 리디랙션
			rttr.addAttribute("fail", "fail");
			return "redirect:/modify/" + board.getId();
		}
	}
	
	@PostMapping("remove") //지우기
	public String remove(Integer id, RedirectAttributes rttr) {
		boolean ok = service.remove(id);
		if(ok) {
			rttr.addAttribute("success", "remove");
			return "redirect:/list";
		} else {
			return "redirect:/id/" + id;
		}
	}
	
}
