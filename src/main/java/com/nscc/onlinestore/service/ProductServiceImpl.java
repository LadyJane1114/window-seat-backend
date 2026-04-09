package com.nscc.onlinestore.service;

import com.nscc.onlinestore.dto.ProductCreateDTO;
import com.nscc.onlinestore.entity.Category;
import com.nscc.onlinestore.entity.Product;
import com.nscc.onlinestore.repository.CategoryRepository;
import com.nscc.onlinestore.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    // inject the repository into this class
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    //add a constructor method
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategory();
    }

    @Override
    public Optional<Product> getProductByID(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}
