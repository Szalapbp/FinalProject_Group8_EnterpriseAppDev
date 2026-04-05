package com.bearcat.store_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true) // Set null if product deleted
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName; // Snapshot of name at time of purchase

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Snapshot of price at time of purchase

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 20)
    private String size;
}