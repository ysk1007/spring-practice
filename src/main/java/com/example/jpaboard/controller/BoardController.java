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
import com.example.jpaboard.entity.Board;
import com.example.jpaboard.repository.BoardRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository; 
	
	// 보드 상세
	@GetMapping("/board/boardOne")
	public String boardOne(Model model, @RequestParam int boardNo) {
		
		// findById id(key)값으로 하나 가져오기 orElse는 객채가 비어있으면 반환할 기본값(null)을 직접 전달함
		Board board = boardRepository.findById(boardNo).orElse(null);
		model.addAttribute("board",board);
		
		return "board/boardOne";
	}
	
	// 보드 수정 페이지
	@GetMapping("/board/modifyBoard")
	public String modifyBoard(Model model, @RequestParam int boardNo) {
		
		// findById id(key)값으로 하나 가져오기 orElse는 객채가 비어있으면 반환할 기본값(null)을 직접 전달함
		Board board = boardRepository.findById(boardNo).orElse(null);
		model.addAttribute("board",board);
		
		return "board/modifyBoard";
	}
	
	// 보드 수정 기능
	@PostMapping("/board/updateBoard")
	public String updateBoard(BoardForm form, @RequestParam int boardNo) {
		Board board = form.toEntity();		// DTO를 Entity로 변환
		boardRepository.save(board); 		// 데이터 저장
		
		return "redirect:/board/boardList?boardNo="+boardNo;
	}
	
	// 보드 추가 페이지
	@GetMapping("/board/addBoard")
	public String addBoard() {
		return "board/addBoard";
	}
	
	// 보드 추가 기능
	@PostMapping("/board/createBoard")
	public String createBoard(BoardForm form) {
		Board board = form.toEntity();		// DTO를 Entity로 변환
		boardRepository.save(board); 		// 데이터 저장
		return "redirect:/board/boardList";
	}
	
	// 보드 삭제 기능
	@GetMapping("/board/deleteBoard")
	public String deleteBoard(@RequestParam int boardNo, RedirectAttributes rda) {
		Board board = boardRepository.findById(boardNo).orElse(null);
		
		if(board == null) {
			rda.addFlashAttribute("msg","삭제 실패");	// redirect의 뷰의 모델에서 자동으로 출력가능
			return "redirect:/board/boardOne?boardNo=" + boardNo;
		}
		
		boardRepository.delete(board); 		// 데이터 삭제
		return "redirect:/board/boardList";
	}
	
	// 리스트 출력
	@GetMapping("/board/boardList")
	public String boardList(Model model
						, @RequestParam(value = "currentPage", defaultValue = "0") int currentPage
						, @RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage
						, @RequestParam(value = "word", defaultValue = "") String word) {
		
		// boardNo으로 정렬
		Sort sort = Sort.by("boardNo").descending();
		
		// 페이징
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage, sort); // 현재 페이지, 컬럼 개수, 정렬 옵션
		
		// findByBoardTitleContaining(페이징 옵션, 특정 단어)
		Page<Board> list = boardRepository.findByBoardTitleContaining(pageable, word);

		// 넘기기
		model.addAttribute("list", list);						// 게시글 리스트
		model.addAttribute("prePage", list.getNumber()-1);		// 이전 페이지
		model.addAttribute("currentPage", list.getNumber());	// 현재 페이지
		model.addAttribute("nextPage", list.getNumber()+1);		// 다음 페이지
		model.addAttribute("word", word);						// 특정 단어
		model.addAttribute("hasNext",list.hasNext());			// 다음 페이지가 있는지
		model.addAttribute("isFirst",list.isFirst());			// 처음 페이지 인지
		
		return "board/boardList";
	}
	
}
