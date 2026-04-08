package com.nscc.onlinestore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LineItemCreateDTO {

    @Positive
    private Integer quantity;

    private Long unitPrice;

    @NotNull
    private Long productId;
}
