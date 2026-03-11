package com.nscc.onlinestore.controller;


import com.nscc.onlinestore.dto.CategoryCreateDTO;
import com.nscc.onlinestore.dto.ProductCreateDTO;
import com.nscc.onlinestore.entity.Category;
import com.nscc.onlinestore.entity.Product;
import com.nscc.onlinestore.repository.CategoryRepository;
import com.nscc.onlinestore.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping

public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    //constructor
    public ProductController(ProductService productService, CategoryRepository categoryRepository){
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    //GET: /dolls
    @GetMapping("/")
    public List<Product> GetAllProducts(){
        return productService.getAllProducts();
    }

    //GET: /dolls/4
    @GetMapping("/{id}")
    public Product GetProductByID(@PathVariable long id) {
        //Optional<Product> allows us to throw an exception if not found
        return productService.getProductByID(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    //POST: /dolls
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)//HTTP Status Code: 201
    public Product CreateProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO){
        // use DTO to receive input in API and plugin to a product object
        Product product = new Product();
        product.setProdName(productCreateDTO.getProdName());
        product.setProdStory(productCreateDTO.getProdStory());
        product.setProdImgURL(productCreateDTO.getProdImgURL());
        product.setProdHeight(productCreateDTO.getProdHeight());
        product.setProdWeight(productCreateDTO.getProdWeight());
        product.setProdBirthday(productCreateDTO.getProdBirthday());
        product.setProdPrice(productCreateDTO.getProdPrice());
        product.setProdStockCount(productCreateDTO.getProdStockCount());

        // assign category by ID (foreign key)
        Category category = categoryRepository.findById(productCreateDTO.getCategoryID())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        product.setCategory(category);
        category.addProduct(product);

        return productService.createProduct(product);
    }

}
