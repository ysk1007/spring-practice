package com.example.jpaboard.dto;

import com.example.jpaboard.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberForm {

	private int memberNo;
	private String memberId;
	private String memberPw;
	private String memberRole;
	
	public Member toEntity() {
		Member member = new Member();
		
		member.setMemberNo(this.memberNo);
		member.setMemberId(this.memberId);
		member.setMemberPw(this.memberPw);
		
		return member;
	}
}
