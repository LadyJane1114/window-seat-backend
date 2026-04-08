package com.nscc.onlinestore.controller;

import com.nscc.onlinestore.dto.CategoryCreateDTO;
import com.nscc.onlinestore.entity.Category;
import com.nscc.onlinestore.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody CategoryCreateDTO categoryCreateDTO) {
        Category category = new Category();
        category.setCatName(categoryCreateDTO.getCatName());
        return categoryRepository.save(category);
    }
}
