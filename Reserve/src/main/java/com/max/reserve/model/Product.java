package com.max.reserve.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products", schema = "reserve")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name; //название продукта
    @Column(name = "available")
    private int available; // количество
    @Column(name = "price")
    private int price; //цена на штуку
    @Column(name = "reserved")
    private int reserved; // резерв

}
