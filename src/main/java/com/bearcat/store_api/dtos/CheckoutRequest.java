package com.bearcat.store_api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CheckoutRequest {
    private String shippingAddress;
}
