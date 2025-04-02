package com.joayong.skillswap.controller;

import com.joayong.skillswap.domain.category.dto.response.CategoryResponse;
import com.joayong.skillswap.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/joayong/category")
@RestController
public class CategoryController {

    private  final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<?> getCategoryList(){

        CategoryResponse categoryResponse = categoryService.getCategories();

        return ResponseEntity.ok().body(categoryResponse);
    }
}
