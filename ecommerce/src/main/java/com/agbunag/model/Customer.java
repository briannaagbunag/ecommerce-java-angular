package com.agbunag.model;

import lombok.Data;

@Data
public class Customer {
    int id;
    int customerId;
    String firstname;
    String middlename;
    String lastname;
    String DateOfBirth;
    String Gender;
    String username;  // Added field
    String password;  // Added field
}
