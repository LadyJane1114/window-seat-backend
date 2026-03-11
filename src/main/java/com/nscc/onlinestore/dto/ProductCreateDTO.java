package com.nscc.onlinestore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductCreateDTO {

    @NotNull
    @NotBlank(message = "Name is Required")
    private String prodName;

    @NotNull
    @NotBlank(message = "Story is Required")
    @Size(max=2000)
    private String prodStory;

    @NotNull
    @DecimalMin("0.0")
    private Double prodPrice;

    private String prodImgURL;


    private Double prodHeight;
    private Double prodWeight;
    private Integer prodStockCount;

    private String prodBirthday;

    //category foreign key
    private Long categoryID;

}
