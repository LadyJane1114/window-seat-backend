package com.nscc.onlinestore.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CatID;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String CatName;

    //Relationships
    @OneToMany(mappedBy="category", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference
    private List<Product> products = new ArrayList<>();


    //Helpers
    public void addProduct(Product product){
        products.add(product);
        product.setCategory(this);
    }

}
