package com.Project.EcommerceApp.model;

import java.util.List;

public class OrderRequest {
    private int userId;
    private List<CartItem> cartItems;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}