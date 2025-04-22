package com.example.jpaboard.controller;

import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.jpaboard.dto.MemberForm;
import com.example.jpaboard.entity.Member;
import com.example.jpaboard.repository.MemberRepository;
import com.example.jpaboard.util.SHA256Util;

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
		return "/member/login";
	}
	
	// 로그아웃
	
	// 회원정보수정
	
	// 회원목록
}
