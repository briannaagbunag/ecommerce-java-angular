package com.agbunag.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "product_data")
public class    ProductData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String description;
    String categoryName;
    String unitOfMeasure;
    String price;
    String imageFile;
    int quantityStock;
}
