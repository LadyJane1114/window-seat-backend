package com.nscc.onlinestore.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    List<CartItemDTO>items;
}


