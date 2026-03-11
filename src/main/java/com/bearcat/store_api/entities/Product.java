package com.bearcat.store_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private String category;

    @Column
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> images;

    @Column
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> sizes;

    @Column
    private boolean featured;

    @Column
    private boolean inStock;

}
