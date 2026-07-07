package com.Project.EcommerceApp.controller;

import com.Project.EcommerceApp.model.OrderRequest;
import com.Project.EcommerceApp.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<Object> placeOrder(@RequestBody OrderRequest orderRequest) {
        Object response = orderService.placeOrder(orderRequest);
        if (response instanceof String) {
            String errorMsg = (String) response;
            if (errorMsg.equals("USER NOT FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderDetails(@PathVariable int orderId) {
        Object response = orderService.getOrderDetails(orderId);
        if (response instanceof String) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getUserOrderHistory(@PathVariable int userId) {
        Object response = orderService.getUserOrderHistory(userId);
        if (response instanceof String) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable int orderId) {
        String status = orderService.cancelOrder(orderId);
        if (status.equals("ORDER NOT FOUND")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(status);
        } else if (status.contains("ALREADY") || status.contains("CANNOT")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
        }
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
}