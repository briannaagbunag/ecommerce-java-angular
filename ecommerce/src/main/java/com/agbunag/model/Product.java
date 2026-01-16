package com.agbunag.model;

import lombok.Data;

@Data
public class Product {
    int id;
    String name;
    String description;
    String categoryName;
    String imageFile;
    String unitOfMeasure;
    String price;
    int quantityStock;

}
