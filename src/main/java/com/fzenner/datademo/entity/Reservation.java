package com.fzenner.datademo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reservation {

    private Long id;
    private Customer customer;

    private Integer tableNr;

    private LocalDateTime dateTime;

    public Reservation() {

    }

    public Reservation(int id, Customer customer, int tableNr, LocalDateTime dateTime) {
        this.id = Long.valueOf(id);
        this.customer = customer;
        this.tableNr = tableNr;
        this.dateTime = dateTime;
    }
}
