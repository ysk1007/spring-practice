package com.example.jpaboard.dto;

import com.example.jpaboard.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardForm {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	
	public Board toEntity() {
		Board b = new Board();
		b.setBoardNo(this.boardNo);
		b.setBoardTitle(this.boardTitle);
		b.setBoardContent(this.boardContent);
		return b;
	}
}
