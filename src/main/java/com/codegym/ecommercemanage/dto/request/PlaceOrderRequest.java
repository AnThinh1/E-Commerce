package com.codegym.ecommercemanage.dto.request;

import com.codegym.ecommercemanage.model.CartItem;
import lombok.Data;

import java.util.List;
@Data
public class PlaceOrderRequest {
        private List<CartItem> cartItems;
        private String voucherCode;


}
