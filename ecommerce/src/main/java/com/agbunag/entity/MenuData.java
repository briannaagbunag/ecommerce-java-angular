package com.agbunag.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "menu_data")
public class MenuData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String description;
    String routerPath;
    String categoryName;
    String icon;
}
