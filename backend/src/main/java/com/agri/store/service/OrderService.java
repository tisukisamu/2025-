package com.agri.store.service;

import com.agri.store.entity.Order;
import com.agri.store.entity.OrderItem;
import com.agri.store.entity.Product;
import com.agri.store.entity.User;
import com.agri.store.repository.OrderRepository;
import com.agri.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(User user, Order orderRequest) {
        // Generate order number
        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        orderRequest.setOrderNo(orderNo);
        orderRequest.setUser(user);
        orderRequest.setStatus(0); // 待支付
        orderRequest.setCreateTime(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process order items and update stock
        for (OrderItem item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // Check stock
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Save product snapshot
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setImageUrl(product.getImageUrl());
            item.setStoreId(product.getStoreId());

            // Calculate item total
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        orderRequest.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(orderRequest);
        
        // 强制加载 items 避免懒加载问题
        savedOrder.getItems().size();
        
        return savedOrder;
    }

    @Transactional
    public Order payOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // 验证订单归属 - 使用 userId 字段避免懒加载问题
        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to pay for this order");
        }

        if (order.getStatus() != 0) {
            throw new RuntimeException("Order cannot be paid. Current status: " + order.getStatus());
        }

        // Update stock when payment is successful
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // Check stock again
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Update product stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(1); // 待发货
        order = orderRepository.save(order);
        
        // 强制加载 items 避免懒加载问题
        order.getItems().size();
        
        return order;
    }

    public void shipOrder(Long orderId, String trackingNo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != 1) {
            throw new RuntimeException("Order cannot be shipped. Current status: " + order.getStatus());
        }

        order.setStatus(2); // 已发货
        order.setTrackingNo(trackingNo);
        orderRepository.save(order);
    }
}
