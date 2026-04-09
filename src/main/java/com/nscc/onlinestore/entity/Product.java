package com.nscc.onlinestore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prodID;

    @NotBlank
    private String prodName;

    //Story = Description
    @NotBlank
    @Size(max=2000)
    @Column(length = 2000)
    private String prodStory;

    private Long prodPrice;

    private String prodImgURL;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDate prodCreateDate;

    //Additional items
    private Double prodHeight;
    private Double prodWeight;
    private Integer prodStockCount;
    //the doll's birthday to display on the site
    private String prodBirthday;


    //Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryID",nullable = false)
    @JsonIgnoreProperties("products")
    private Category category;
}
