package com.pm.clothingshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartRequest {
    private Long productId;
    private Integer quantity;
    private String selectedSize;
}
