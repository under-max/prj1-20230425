package com.example.demo.controller;

import java.util.*;

import javax.swing.border.*;

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
	// 경로 : http:// localhost:8080?page=?
	// 경로: http:// localhost:8080/list?page=?
	// 게시물 목록
//	@RequestMapping(value = {"/", "list"}, method= RequestMethod.GET) 이하 같은 의미

	@GetMapping({ "/", "list" })
	public String list(Model model,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value="search", defaultValue="") String search,
			@RequestParam(value="type", required = false) String type) { // null all title로 들어옴 
		// 1. request param 수집/가공
		// 2. business logic 처리
		// List<Board> list = service.listBoard(); // 페이지 처리 전
		Map<String, Object> result = service.listBoard(page, search, type); // 페이지 처리
		
		// 3. add attribute
//		model.addAttribute("boardList", result.get("boardList"));
//		model.addAttribute("pageInfo", result.get("pageInfo"));		
		model.addAllAttributes(result);
		// 4. forward/redirect
		return "list"; // 애는 그냥 view보내는거고 
	}

	@GetMapping("/id/{id}") // 개별조회
	public String board(@PathVariable("id") Integer id, Model model) {
		// 1.request param
		// 2.business logic
		// -> service 몫
		Board board = service.getBoard(id);
		System.out.println(board);
		// 3. add Attribute
		model.addAttribute("board", board);

		// 4.forward/ redirect
		return "get";
	}

	@GetMapping("/modify/{id}") // 개별수정 조회
	public String modifyForm(@PathVariable("id") Integer id, Model model) {
		// 1.request Param

		// 2.business logic
		// 일단 조회
		model.addAttribute("board", service.getBoard(id));

		// 3 add Attribute

		return "modify";
	}

//	@RequestMapping(value="/modify/{id}", method = RequestMethod.POST)
	@PostMapping("/modify/{id}") // 개별수정
	public String modifyProcess(Board board, RedirectAttributes rttr) { // form submit으로 들어온거 받음

		boolean ok = service.modify(board);

		if (ok) {
			// 해당 게시물 보기로 리디렉션
//			rttr.addAttribute("success", "success"); //쿼리스트링 붙어서 넘어감
			rttr.addFlashAttribute("message", board.getId() + "번 게시물이 수정되었습니다.");
			return "redirect:/id/" + board.getId();
		} else {
			// 수정폼으로 리디랙션
			rttr.addAttribute("fail", "fail");
			return "redirect:/modify/" + board.getId();
		}
	}

	@PostMapping("remove") // 지우기
	public String remove(Integer id, RedirectAttributes rttr) {
		boolean ok = service.remove(id);
		if (ok) {
			// query String에 추가
//			rttr.addAttribute("success", "remove");
			// model에 추가되서 나감
			rttr.addFlashAttribute("message", id + "번 게시물이 삭제되었습니다.");
			return "redirect:/list";
		} else {
			return "redirect:/id/" + id;
		}
	}

	// 이하 게시물 삭제
	// Insert 기능 마음대로 추가
	@GetMapping("add")
	public String addForm() {
		// 게시물 작성 form view로 포워드

		return "create";
	}

	@PostMapping("add")
	public String addProcess(Board board, RedirectAttributes rttr) {
		// 새 게시물 db에 추가 service로 보내야지
		System.out.println(board);

		boolean ok = service.createProcess(board);
		if (ok) {
//			rttr.addAttribute("createSuccess", "success");
			rttr.addFlashAttribute("message", board.getId() + "번 게시물이 등록되었습니다.");
			return "redirect:/list";
		} else {
//			rttr.addAttribute("createSuccess", board);//실패하면 썻던거 다시 받아 쓰렴
			rttr.addFlashAttribute("message", board.getId() + "번 등록 중 실패 되었습니다.");
			return "redirect:/add";
		}

	}

}
