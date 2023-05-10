package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import com.example.demo.domain.*;
import com.example.demo.service.*;

import jakarta.servlet.http.*;

@Controller
@RequestMapping("member")
public class MemberController {

	@Autowired
	private MemberService service;

	@GetMapping("signup")
	@PreAuthorize("isAnonymous()") //로그인안한 사람만 접근할수 있도록 처리
	public void signupFrom() {

	}

	@PostMapping("signup")
	@PreAuthorize("isAnonymous()")
	public String signupProcess(Member member, RedirectAttributes rttr) {
		try {
			service.signup(member);
			rttr.addFlashAttribute("message", "회원가입에 성공했습니다.");
			return "redirect:/list";

		} catch (Exception e) {
			e.printStackTrace();
			rttr.addFlashAttribute("member", member);
			rttr.addFlashAttribute("message", "회원가입에 실패했습니다.");
			return "redirect:/member/signup";
		}

	}
	
	
	@GetMapping("list")
	@PreAuthorize("hasAuthority('admin')")
	public void list(Model model) {
		
		List<Member> memberList = service.memberList();		
		model.addAttribute("memberList", memberList);
	}	

	
	
	
//	@GetMapping("info")
//	@PreAuthorize("isAuthenticated() and (authentication.name eq #id)")
//	public void info(String id, Model model) {
//		Member member = service.get(id);
//		System.out.println(member);
//		model.addAttribute("member", member);
//	}

	@GetMapping("info")
	@PreAuthorize("hasAuthority('admin') or (isAuthenticated() and (authentication.name eq #id))")
	//들어가지 못하는 이유 
	public void info(String id, Model model) {
		Member member = service.get(id);
		System.out.println(member);
		model.addAttribute("member", member);
	}

	@PostMapping("remove")
	@PreAuthorize("isAuthenticated() and (authentication.name eq #member.id)")
	public String remove(Member member, RedirectAttributes rttr,
			HttpServletRequest request) throws Exception {
		
		
		boolean ok = service.remove(member);
		
		if(ok) {
			rttr.addFlashAttribute("message", "회원 탈퇴하였습니다.");	
			
			// 로그아웃 하는 기능 
			request.logout();
			return "redirect:/list";
		} else {
			rttr.addFlashAttribute("message", "회원탈퇴시 문제가 발생하였습니다.");
			return "redirect:/member/info?id";
		}
		
	}
	
	
	@GetMapping("modify")
	@PreAuthorize("isAuthenticated() and (authentication.name eq #id)")
	public void modifyForm(String id, Model model) {
		Member member = service.get(id);
		model.addAttribute("member", member);
//		model.addAttribute(service.get(id));
		
	}
	
	
	// 2.
	@PostMapping("modify")
	@PreAuthorize("isAuthenticated() and (authentication.name eq #member.id)")
	public String modifyProcess(Member member, String oldPassword, RedirectAttributes rttr) {
		boolean ok = service.modify(member, oldPassword);	
		if(ok) {
			rttr.addFlashAttribute("message", member.getId()+"번 수정 완료");
			return "redirect:/member/info?id="+member.getId();
		}else {
			rttr.addFlashAttribute("message", "회원정보 수정시 문제가 발생하였습니다.");
			return "redirect:/member/modify?id="+member.getId();
		}
		
	}
	
	
	@GetMapping("login")
	public void loginForm() {
		//로그인 페이지
	}
	
}
