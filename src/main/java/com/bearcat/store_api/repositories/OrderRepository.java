package com.bearcat.store_api.repositories;

import com.bearcat.store_api.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {


}