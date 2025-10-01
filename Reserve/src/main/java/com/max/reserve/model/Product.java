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
    private String name;
    @Column(name = "available")
    private int available;
    @Column(name = "price")
    private int price;
    @Column(name = "reserved")
    private int reserved;
    @Version
    private long version;

}
