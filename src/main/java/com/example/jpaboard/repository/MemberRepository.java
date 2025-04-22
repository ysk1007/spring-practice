package com.example.jpaboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.jpaboard.entity.Member;
import com.example.jpaboard.entity.MemberOnlyMemberId;

import jakarta.transaction.Transactional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer>{
	Page<MemberOnlyMemberId> findAllBy(Pageable pageable);
	
	Page<MemberOnlyMemberId> findByMemberIdContains(Pageable pageable, String word);
	
	// member_id 중복검사 : existsBy엔티티컬럼필드
	boolean existsByMemberId(String memberId);
	
	// 로그인 하는 추상 메서드 : findBy엔티티컬럼필드 ... And엔티티컬럼필드
	// findBy컬럼명And컬럼명And...
	MemberOnlyMemberId findByMemberIdAndMemberPw(String memberId, String memberPw);
	
	// 비밀번호 수정
	@Transactional	// @Transactional은 메서드 실행 중 문제가 생기면 자동으로 롤백
	@Modifying		// 수정 작업이라고 알려주기 위해 @Modifying을 붙임.
	@Query(nativeQuery = true,
			value = "UPDATE member SET"
					+ " member_pw = :newPw"
					+ " WHERE member_id = :memberId AND member_pw = :memberPw")
	int modifyPw(String memberId, String memberPw, String newPw);
	
	// 멤버 계정 삭제
	@Transactional	// @Transactional은 메서드 실행 중 문제가 생기면 자동으로 롤백
	@Modifying		// 수정 작업이라고 알려주기 위해 @Modifying을 붙임.
	@Query(nativeQuery = true,
			value = "DELETE FROM member"
					+ " WHERE member_id = :memberId AND member_pw = :memberPw")
	int removeMember(String memberId, String memberPw);
}
