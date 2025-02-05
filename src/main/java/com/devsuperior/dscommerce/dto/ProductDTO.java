package com.devsuperior.dscommerce.dto;


import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

public class ProductDTO {

    private long id;

    @Size(min=3,max = 80, message = "Field must be between 3 and 80 characters.")
    @NotBlank(message = "Required field!")
    private String name;

    @Size(min=10, message = "Field must be at least 10 characters.")
    @NotBlank(message = "Required field!")
    private String description;

    @NotNull(message = "Required Field")
    @Positive(message = "Price must be positive.")
    private double price;
    private String imgUrl;

    @NotEmpty(message = "Must be at least one category")
    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductDTO(){}

    public ProductDTO(long id, String name, String description, double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.price = entity.getPrice();
        this.imgUrl = entity.getImgUrl();
        for(Category entCat : entity.getCategories()){
            categories.add(new CategoryDTO(entCat));
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }
}
