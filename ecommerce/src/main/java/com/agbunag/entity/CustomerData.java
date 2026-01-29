package com.agbunag.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "customer_data")
public class CustomerData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id; // INTERNAL DB PK
    @GeneratedValue(strategy = GenerationType.AUTO)
    int customerId; // PUBLIC / BUSINESS ID

    String firstname;
    String middlename;
    String lastname;
    String dateOfBirth;
    String gender;
    String username;
    String password;
}
