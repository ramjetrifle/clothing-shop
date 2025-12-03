package com.pm.clothingshop.dto.response;

import com.pm.clothingshop.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private List<String> sizes;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private Long quantity;
}
