package com.Project.EcommerceApp.service;

import com.Project.EcommerceApp.entity.Order;
import com.Project.EcommerceApp.entity.OrderProduct;
import com.Project.EcommerceApp.entity.Product;
import com.Project.EcommerceApp.entity.User;
import com.Project.EcommerceApp.model.CartItem;
import com.Project.EcommerceApp.model.OrderRequest;
import com.Project.EcommerceApp.repository.OrderRepository;
import com.Project.EcommerceApp.repository.ProductRepository;
import com.Project.EcommerceApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    OrderService() {

    }

    public Object placeOrder(OrderRequest orderRequest) {

        Optional<User> existUser = userRepository.findById(orderRequest.getUserId());
        if (existUser.isEmpty()) {
            return "USER NOT FOUND";
        }
        User user = existUser.get();

        Order order = new Order();
        order.setUser(user);
        order.setOrder_status("Pending");
        order.setLocalDateTime(LocalDateTime.now());
        double total_amount = 0;
        List<OrderProduct> orderProductList = new ArrayList<>();

        for (CartItem items : orderRequest.getCartItems()) {
            Optional<Product> productOpt = productRepository.findById(items.getProductId());
            if (productOpt.isEmpty()) {
                return "PRODUCT IS NOT PRESENT IN INVENTORY";
            }
            Product product = productOpt.get();

            if (product.getStock_quantity() < items.getQuantity()) {
                return "INSUFFICIENT PRODUCT IN INVENTORY";
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(items.getQuantity());
            orderProduct.setPriceAtPurchase(product.getPrice());

            total_amount += product.getPrice() * items.getQuantity();
            orderProductList.add(orderProduct);
            product.setStock_quantity(product.getStock_quantity() - items.getQuantity());
            productRepository.save(product);
        }

        order.setTotal_amount(total_amount);
        order.setOrderProducts(orderProductList);
        Order savedOrder = orderRepository.save(order);

        Map<String, Object> invoice = new LinkedHashMap<>();
        invoice.put("orderId", savedOrder.getOrder_id());
        invoice.put("customerName", savedOrder.getUser().getUser_name());
        invoice.put("orderStatus", savedOrder.getOrder_status());
        invoice.put("orderDate", savedOrder.getLocalDateTime());
        invoice.put("finalAmount", savedOrder.getTotal_amount());

        List<Map<String, Object>> invoiceItems = new ArrayList<>();
        for (OrderProduct op : savedOrder.getOrderProducts()) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("productName", op.getProduct().getProduct_name());
            itemMap.put("quantity", op.getQuantity());
            itemMap.put("priceAtPurchase", op.getPriceAtPurchase());
            invoiceItems.add(itemMap);
        }
        invoice.put("items", invoiceItems);

        return invoice;
    }


    public Object getOrderDetails(int orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return "ORDER NOT FOUND";
        }
        Order order = orderOpt.get();


        Map<String, Object> invoice = new LinkedHashMap<>();
        invoice.put("orderId", order.getOrder_id());
        invoice.put("customerName", order.getUser().getUser_name());
        invoice.put("orderStatus", order.getOrder_status());
        invoice.put("orderDate", order.getLocalDateTime());
        invoice.put("finalAmount", order.getTotal_amount());

        List<Map<String, Object>> invoiceItems = new ArrayList<>();
        for (OrderProduct op : order.getOrderProducts()) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("productName", op.getProduct().getProduct_name());
            itemMap.put("quantity", op.getQuantity());
            itemMap.put("priceAtPurchase", op.getPriceAtPurchase());
            invoiceItems.add(itemMap);
        }
        invoice.put("items", invoiceItems);

        return invoice;
    }


    public Object getUserOrderHistory(int userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "USER NOT FOUND";
        }

        List<Order> userOrders = orderRepository.findByUserId(userId);
        List<Map<String, Object>> historyList = new ArrayList<>();

        for (Order order : userOrders) {
            Map<String, Object> orderSummary = new LinkedHashMap<>();
            orderSummary.put("orderId", order.getOrder_id());
            orderSummary.put("orderStatus", order.getOrder_status());
            orderSummary.put("orderDate", order.getLocalDateTime());
            orderSummary.put("totalAmount", order.getTotal_amount());
            historyList.add(orderSummary);
        }

        return historyList;
    }

    // === METHOD 3: Order Cancel Karna Aur Inventory Revert Karna ===
    public String cancelOrder(int orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return "ORDER NOT FOUND";
        }
        Order order = orderOpt.get();


        if (order.getOrder_status().equalsIgnoreCase("Cancelled")) {
            return "ORDER IS ALREADY CANCELLED";
        }
        if (order.getOrder_status().equalsIgnoreCase("Delivered")) {
            return "DELIVERED ORDER CANNOT BE CANCELLED";
        }

        for (OrderProduct op : order.getOrderProducts()) {
            Product product = op.getProduct();
            product.setStock_quantity(product.getStock_quantity() + op.getQuantity());
            productRepository.save(product);
        }

        order.setOrder_status("Cancelled");
        orderRepository.save(order);

        return "ORDER CANCELLED SUCCESSFULLY";
    }
}