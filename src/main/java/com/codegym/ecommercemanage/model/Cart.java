package com.codegym.ecommercemanage.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Cart {
    private Map<Long, CartItem> items = new HashMap<>();

    public void addItem(CartItem item) {
        if (items.containsKey(item.getProductId())) {
            CartItem existing = items.get(item.getProductId());
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        } else {
            items.put(item.getProductId(), item);
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
    }

    public void updateQuantity(Long productId, Integer quantity) {
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            items.get(productId).setQuantity(quantity);
        }
    }

    public double getTotalPrice() {
        return items.values().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }
}