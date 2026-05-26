package com.example.backend.service;

import com.example.backend.dto.request.OrderRequest;
import com.example.backend.dto.response.OrderDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Notification;
import com.example.backend.entity.Order;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public PageResponse<OrderDTO> getOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Order> orderPage = orderRepository.findByUserIdWithAllRelations(userId, pageable);
        Page<OrderDTO> dtoPage = orderPage.map(OrderDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public OrderDTO getOrderDetail(Long id, Long userId) {
        Order order = orderRepository.findByIdWithBuyerAndSellerAndProduct(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (!order.getBuyer().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new BusinessException("无权查看该订单");
        }

        return OrderDTO.fromEntity(order);
    }

    @Transactional
    public OrderDTO createOrder(Long buyerId, OrderRequest request) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", buyerId));

        Product product = productRepository.findByIdWithSeller(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (product.getStatus() != Product.ProductStatus.ON_SALE) {
            throw new BusinessException("该商品不可购买");
        }

        if (product.getSeller().getId().equals(buyerId)) {
            throw new BusinessException("不能购买自己的商品");
        }

        if (orderRepository.existsByProductIdAndStatusIn(
                product.getId(), 
                List.of(Order.OrderStatus.PENDING, Order.OrderStatus.SHIPPED)
        )) {
            throw new BusinessException("该商品已有待处理的订单");
        }

        User seller = product.getSeller();

        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setProduct(product);
        order.setTradeType(request.getTradeType());
        order.setAmount(product.getPrice());
        order.setAddress(request.getAddress());
        order.setStatus(Order.OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        product.setStatus(Product.ProductStatus.SOLD);
        productRepository.save(product);

        createNotification(seller.getId(), "新订单", 
                "您的商品「" + product.getTitle() + "」有新的购买订单", 
                Notification.NotificationType.ORDER, savedOrder.getId());

        return OrderDTO.fromEntity(savedOrder);
    }

    @Transactional
    public OrderDTO shipOrder(Long id, Long userId, String expressNo) {
        Order order = orderRepository.findByIdWithBuyerAndSellerAndProduct(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (!order.getSeller().getId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(Order.OrderStatus.SHIPPED);
        order.setExpressNo(expressNo);
        order.setShipTime(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);

        createNotification(order.getBuyer().getId(), "订单已发货", 
                "您的订单「" + order.getProduct().getTitle() + "」已发货，快递单号：" + expressNo, 
                Notification.NotificationType.ORDER, updatedOrder.getId());

        return OrderDTO.fromEntity(updatedOrder);
    }

    @Transactional
    public OrderDTO confirmOrder(Long id, Long userId) {
        Order order = orderRepository.findByIdWithBuyerAndSellerAndProduct(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        if (order.getStatus() != Order.OrderStatus.SHIPPED) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setCompleteTime(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);

        User seller = order.getSeller();
        seller.setCreditScore(seller.getCreditScore() + 5);
        userRepository.save(seller);

        createNotification(order.getSeller().getId(), "订单已完成", 
                "您的订单「" + order.getProduct().getTitle() + "」买家已确认收货", 
                Notification.NotificationType.ORDER, updatedOrder.getId());

        return OrderDTO.fromEntity(updatedOrder);
    }

    @Transactional
    public OrderDTO cancelOrder(Long id, Long userId, String reason) {
        Order order = orderRepository.findByIdWithBuyerAndSellerAndProduct(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (!order.getBuyer().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        if (order.getStatus() == Order.OrderStatus.COMPLETED || order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BusinessException("订单无法取消");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelReason(reason);

        Product product = order.getProduct();
        product.setStatus(Product.ProductStatus.ON_SALE);
        productRepository.save(product);

        Order updatedOrder = orderRepository.save(order);

        Long notifyUserId = order.getBuyer().getId().equals(userId) 
                ? order.getSeller().getId() 
                : order.getBuyer().getId();
        
        createNotification(notifyUserId, "订单已取消", 
                "订单「" + order.getProduct().getTitle() + "」已被取消，原因：" + reason, 
                Notification.NotificationType.ORDER, updatedOrder.getId());

        return OrderDTO.fromEntity(updatedOrder);
    }

    private void createNotification(Long userId, String title, String content, 
                                    Notification.NotificationType type, Long relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notificationRepository.save(notification);
    }
}
