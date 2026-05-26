package com.example.backend.service;

import com.example.backend.dto.ReviewDTO;
import com.example.backend.dto.request.ReviewRequest;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Notification;
import com.example.backend.entity.Order;
import com.example.backend.entity.Review;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ReviewRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public PageResponse<ReviewDTO> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Review> reviewPage = reviewRepository.findByProductIdWithUserAndOrder(productId, pageable);
        Page<ReviewDTO> dtoPage = reviewPage.map(ReviewDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public PageResponse<ReviewDTO> getUserReviews(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Review> reviewPage = reviewRepository.findBySellerIdWithUserAndOrder(userId, pageable);
        Page<ReviewDTO> dtoPage = reviewPage.map(ReviewDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public ReviewDTO createReview(Long userId, ReviewRequest request) {
        Order order = orderRepository.findByIdWithBuyerAndSellerAndProduct(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new BusinessException("无权评价该订单");
        }

        if (order.getStatus() != Order.OrderStatus.COMPLETED) {
            throw new BusinessException("订单未完成，无法评价");
        }

        if (reviewRepository.existsByOrderId(request.getOrderId())) {
            throw new BusinessException("该订单已评价");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Review review = new Review();
        review.setOrder(order);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);

        Review savedReview = reviewRepository.save(review);

        User seller = order.getSeller();
        Double avgRating = reviewRepository.getAverageRatingBySellerId(seller.getId());
        if (avgRating != null) {
            int creditChange = (int) ((avgRating - 3) * 5);
            seller.setCreditScore(Math.max(0, Math.min(200, seller.getCreditScore() + creditChange)));
            userRepository.save(seller);
        }

        createNotification(seller.getId(), "收到新评价",
                "您的商品「" + order.getProduct().getTitle() + "」收到了新的评价",
                Notification.NotificationType.REVIEW, savedReview.getId());

        return ReviewDTO.fromEntity(savedReview);
    }

    @Transactional
    public ReviewDTO updateReview(Long id, Long userId, ReviewRequest request) {
        Review review = reviewRepository.findByIdWithUserAndOrder(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException("无权修改该评价");
        }

        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setIsAnonymous(request.getIsAnonymous());

        Review saved = reviewRepository.save(review);
        return ReviewDTO.fromEntity(saved);
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
