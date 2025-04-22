package com.example.jpaboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jpaboard.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer>{
	// member_id 중복검사
	// 로그인 하는 추상메서드
	boolean existsByMemberId(String memberId);
}
