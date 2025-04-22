package com.example.jpaboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jpaboard.dto.BoardForm;
import com.example.jpaboard.dto.MemberForm;
import com.example.jpaboard.entity.Board;
import com.example.jpaboard.entity.Member;
import com.example.jpaboard.entity.MemberOnlyMemberId;
import com.example.jpaboard.repository.MemberRepository;
import com.example.jpaboard.util.SHA256Util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
	@Autowired
	MemberRepository memberRepository;
	
	// 회원가입 + member_id 중복확인
	@GetMapping("/member/joinMember")
	public String joinMember() {
		return "member/joinMember";
	}
	
	@PostMapping("/member/joinMember")
	public String joinMember(MemberForm form, RedirectAttributes rda) {
		// form.getMemberId() DB에 존재한다면
		
		log.debug(form.toString());
		log.debug("isMemberId : "+ memberRepository.existsByMemberId(form.getMemberId()));
		
		if(memberRepository.existsByMemberId(form.getMemberId())) {
			rda.addFlashAttribute("msg", form.getMemberId()+" ID가 이미 존재합니다.");
			return "redirect:/member/joinMember";
		}
		
		// 회원가입 진행
		// form.getMemberPw()값을 SHA-256방식으로 암호화
		form.setMemberPw(SHA256Util.encoding(form.getMemberPw()));
		
		Member member = form.toEntity();
		memberRepository.save(member);		// entity 저장 -> 최종 커밋시 -> 테이블에 행이 추가(insert)
		
		return "redirect:/member/login";
	}
	
	// 로그인
	@GetMapping("/member/login")
	public String login() {
		return "member/login";
	}
	
	// 로그인 액션
	@PostMapping("/member/login")
	public String login(HttpSession session,MemberForm memberForm,RedirectAttributes rda) {
		// pw 암호화
		memberForm.setMemberPw(SHA256Util.encoding(memberForm.getMemberPw()));
		
		// 로그인 확인 메서드
		MemberOnlyMemberId loginMember
			= memberRepository.findByMemberIdAndMemberPw(memberForm.getMemberId(), memberForm.getMemberPw());
		
		if(loginMember == null) {
			log.debug("로그인 실패");
			rda.addFlashAttribute("msg", "로그인 실패");
			return "redirect:/member/login";
		}
		
		log.debug("디버깅 : 로그인 성공");
		// 로그인 성공 코드 구현
		session.setAttribute("loginMember", loginMember);	// ISSUE : pw 정보까지 세션에 저장
		return "redirect:/member/memberList";
	}
	
	// 로그아웃
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/member/login";
	}
	
	
	// 회원정보수정
	@GetMapping("/member/modifyMemberPw")
	public String memberModifyPw(HttpSession session) {
		if(session.getAttribute("loginMember")==null) {
			return "redirect:/member/login";
		}
		
		return "member/modifyMemberPw";
	}
	
	// 회원정보수정 기능
	@PostMapping("/member/modifyMemberPw")
	public String memberModifyPw(MemberForm memberForm, RedirectAttributes rda
			, @RequestParam String newPw) {		
		
		Member member = memberForm.toEntity();		// DTO를 Entity로 변환
		
		// 기존 비밀번호와 수정 비밀번호 인코딩
		String pw = SHA256Util.encoding(member.getMemberPw());
		String modifyPw = SHA256Util.encoding(newPw);
		
		int result = memberRepository.modifyPw(member.getMemberId(), pw, modifyPw);
		
		if(result == 1) {
			log.debug("수정 성공");
			return "redirect:/member/logout";
		}
		
		rda.addFlashAttribute("msg","수정 실패!");
		log.debug("수정 실패");
		return "redirect:/member/modifyMemberPw";
	}
	
	// 회원 탈퇴 폼
	@GetMapping("/member/removeMember")
	public String removeMember(HttpSession session) {
		
		if(session.getAttribute("loginMember")==null) {
			return "redirect:/member/login";
		}
		
		return "member/removeMember";
	}
	
	// 회원 탈퇴 기능
	@PostMapping("/member/removeMember")
	public String removeMember(MemberForm memberForm, RedirectAttributes rda) {
		Member member = memberForm.toEntity();		// DTO를 Entity로 변환
		
		// 기존 비밀번호 인코딩
		String pw = SHA256Util.encoding(member.getMemberPw());
		
		int result = memberRepository.removeMember(member.getMemberId(), pw);
		
		if(result == 1) {
			log.debug("삭제 성공");
			return "redirect:/member/logout";
		}
		
		rda.addFlashAttribute("msg","삭제 실패!");
		log.debug("삭제 실패");
		return "redirect:/member/removeMember";
	}
	
	// 회원목록
	@GetMapping("/member/memberList")
	public String memberList(HttpSession session, Model model
			, @RequestParam(value = "currentPage", defaultValue = "0") int currentPage
			, @RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage
			, @RequestParam(value = "word", defaultValue = "") String word) {
		
		// session 인증/인가 검사
		if(session.getAttribute("loginMember") == null) {
			return "redirect:/member/login";
		}
		
		Sort sort = Sort.by("memberNo").descending();
		
		log.debug("디버깅 : memberList 시작");
		
		// 사용자 목록 + 페이징 + id 검색
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage,sort);
		// Page<Member> = memberRepository.findByMemberIdContaining(Pageable page, String word);
		Page<MemberOnlyMemberId> list = memberRepository.findByMemberIdContains(pageable, word);
		
		log.debug("디버깅 : " + list.toString());
		
		// 넘기기
		model.addAttribute("list", list);				// 게시글 리스트
		model.addAttribute("prePage", list.getNumber()-1);	// 이전 페이지
		model.addAttribute("currentPage", list.getNumber());	// 현재 페이지
		model.addAttribute("nextPage", list.getNumber()+1);	// 다음 페이지
		model.addAttribute("word", word);							// 특정 단어
		model.addAttribute("hasNext",list.hasNext());			// 다음 페이지가 있는지
		model.addAttribute("isFirst",list.isFirst());			// 처음 페이지인지
		
		return "member/memberList";
	}
}
