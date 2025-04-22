package com.example.jpaboard.controller;

import java.util.Map;

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

import com.example.jpaboard.dto.ArticleForm;
import com.example.jpaboard.entity.Article;
import com.example.jpaboard.repository.ArticleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ArticleController {
	
	@Autowired	// 의존성주입
	private ArticleRepository articleRepository;
	
	@GetMapping("/articles/sqlTest")
	public String sqlTest(Model model) {
		Map<String, Object> map = articleRepository.getMinMaxCount("%%");
		log.debug(map.toString());
		model.addAttribute("map", map);
		return "articles/sqlTest";
	}
	
	@GetMapping("/articles/new")
	public String newArticleForm() {
		return "articles/new";
	}
	
	@PostMapping("/articles/create")
	public String createArticle(ArticleForm form) {
		//System.out.println(form.toString());
		
		// DTO -> Entity
		Article entity = form.toEntity();
		articleRepository.save(entity);			// 레포지토리 호출할 때 Entity가 필요
		
		return "redirect:/articles/index";		// articles/list 리다이렉트 "redirect : /article/list "
	}
	
	@GetMapping("/articles/index")
	public String articleList(Model model
								, @RequestParam(value = "currentPage", defaultValue = "0") int currentPage
								, @RequestParam(value = "rowPerPage", defaultValue = "10") int rowPerPage
								, @RequestParam(value = "word", defaultValue = "") String word) {
		
		Sort sort = Sort.by("id").descending();
		//Sort s1 = Sort.by("title").ascending();
		//Sort s2 = Sort.by("content").descending(); 
		//Sort sort = s1.and(s2);
		
		PageRequest pageable = PageRequest.of(currentPage, rowPerPage, sort);	// 0 페이지, 10개
		Page<Article> list = articleRepository.findByTitleContaining(pageable,word);
		
		// Page의 추가 속성
		log.debug("list.getTotalElements() : "+list.getTotalElements());	// 전체행의 사이즈
		log.debug("list.getTotalPages() : "+list.getTotalPages());			// 전체 사이즈
		log.debug("list.getNumber() : "+list.getNumber());					// 현재 페이지
		log.debug("list.getSize() : "+list.getSize());						// rowPerPage
		log.debug("list.isFirst() : "+list.isFirst());						// 1페이지인지 : 이전링크유무
		log.debug("list.hasNext() : "+list.hasNext());						// 다음이 있는지 : 다음링크유무
		
		model.addAttribute("list",list);
		model.addAttribute("nextPage",list.getNumber() + 1);
		model.addAttribute("prePage",list.getNumber() - 1);
		model.addAttribute("word", word);
		// redirect로 호출되었다면 + RedirectAttributes.addAttribute() 같이 포함
		
		return "articles/index";
	}
	
	@GetMapping("/articles/show")
	public String show(Model model, @RequestParam Long id) {
		Article article = articleRepository.findById(id).orElse(null);
		model.addAttribute("article",article);
		return "articles/show";
	}
	
	@GetMapping("/articles/edit")
	public String edit(Model model, @RequestParam Long id) {
		Article article = articleRepository.findById(id).orElse(null);
		model.addAttribute("article",article);
		return "articles/edit";
	}
	
	@PostMapping("/articles/update")
	public String update(ArticleForm form, @RequestParam Long id) {
		//System.out.println(form.toString());
		
		// DTO -> Entity
		Article entity = form.toEntity();	// 저장하면 새로운 행에 저장X, 키(ID)
		// entity가 키값을 가지고 있으면 존재하는 키값의 행을 수정
		articleRepository.save(entity);		//  
		
		return "redirect:/articles/show?id=" + id;		// articles/list 리다이렉트 "redirect : /article/list "
	}
	
	@GetMapping("/articles/delete")
	public String delete(@RequestParam long id, RedirectAttributes rda) {
		Article article = articleRepository.findById(id).orElse(null);
		
		if(article == null) {
			rda.addFlashAttribute("msg","삭제 실패");	// redirect의 뷰의 모델에서 자동으로 출력가능
			return "redirect:/articles/show?id=" + id;
		}
		
		articleRepository.delete(article);

		return "redirect:/articles/index";
	}
}
