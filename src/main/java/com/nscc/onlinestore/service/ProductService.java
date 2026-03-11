package com.nscc.onlinestore.service;

import com.nscc.onlinestore.dto.ProductCreateDTO;
import com.nscc.onlinestore.entity.Category;
import com.nscc.onlinestore.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    //get all products
    List<Product> getAllProducts();

    // get products by ID
    Optional<Product> getProductByID(Long id);

    // create products
    Product createProduct(Product product);

}
