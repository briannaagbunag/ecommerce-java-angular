package com.agbunag.model;

import lombok.Data;

@Data
public class Menu {
    int id;
    String name;
    String description;
    String routerPath;
    String categoryName;
    String icon;
}
