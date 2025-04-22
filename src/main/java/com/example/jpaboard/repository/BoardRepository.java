package com.example.jpaboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jpaboard.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
	Page<Board> findByBoardTitleContaining(Pageable page, String searchTitle);
	//Page<Board> findByBoardTitleContaining(PageRequest page, String searchTitle);
}
