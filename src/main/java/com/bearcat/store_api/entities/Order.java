package com.bearcat.store_api.entities;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table( name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer totalAmount;

    @Column(nullable = false)
    private String shippingAddress;

}
