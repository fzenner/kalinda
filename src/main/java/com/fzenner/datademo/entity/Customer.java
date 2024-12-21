package com.fzenner.datademo.entity;

import lombok.Data;

@Data
public class Customer {

    protected Long id;

    protected String title;
    protected String firstName;
    protected String lastName;

    public Customer() {

    }

    public Customer(String title, String firstName, String lastName) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
