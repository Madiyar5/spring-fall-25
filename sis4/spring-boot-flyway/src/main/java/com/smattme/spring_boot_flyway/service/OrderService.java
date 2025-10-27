package com.smattme.spring_boot_flyway.service;

import com.smattme.spring_boot_flyway.entity.Order;
import com.smattme.spring_boot_flyway.entity.Order.OrderStatus;
import com.smattme.spring_boot_flyway.entity.User;
import com.smattme.spring_boot_flyway.repository.OrderRepository;
import com.smattme.spring_boot_flyway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all orders with pagination
     */
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Get all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    /**
     * Create new order
     */
    public Order createOrder(Order order) {
        validateOrder(order);

        // Generate order number if not provided
        if (order.getOrderNumber() == null || order.getOrderNumber().trim().isEmpty()) {
            order.setOrderNumber(generateOrderNumber());
        }

        // Check if order number already exists
        if (orderRepository.existsByOrderNumber(order.getOrderNumber())) {
            throw new IllegalArgumentException("Order with number " + order.getOrderNumber() + " already exists");
        }

        // Validate user exists
        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + order.getUser().getId()));

        order.setUser(user);

        return orderRepository.save(order);
    }

    /**
     * Update existing order
     */
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        validateOrder(orderDetails);

        // Check if order number is being changed and if it already exists
        if (!order.getOrderNumber().equals(orderDetails.getOrderNumber()) &&
                orderRepository.existsByOrderNumber(orderDetails.getOrderNumber())) {
            throw new IllegalArgumentException("Order with number " + orderDetails.getOrderNumber() + " already exists");
        }

        // Validate user exists if being changed
        if (!order.getUser().getId().equals(orderDetails.getUser().getId())) {
            User user = userRepository.findById(orderDetails.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + orderDetails.getUser().getId()));
            order.setUser(user);
        }

        order.setOrderNumber(orderDetails.getOrderNumber());
        order.setTotalAmount(orderDetails.getTotalAmount());
        order.setStatus(orderDetails.getStatus());
        order.setOrderDate(orderDetails.getOrderDate());

        return orderRepository.save(order);
    }

    /**
     * Update order status
     */
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * Delete order by ID
     */
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    /**
     * Get orders by user ID
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Get orders by date range
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findByOrderDateBetweenOrderByOrderDateDesc(startDate, endDate);
    }

    /**
     * Get orders by minimum amount
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByMinAmount(BigDecimal minAmount) {
        return orderRepository.findByTotalAmountGreaterThanEqual(minAmount);
    }

    /**
     * Get orders by user email
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserEmail(String email) {
        return orderRepository.findByUserEmail(email);
    }

    /**
     * Get total amount for user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByUserId(Long userId) {
        BigDecimal total = orderRepository.getTotalAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get order count by status
     */
    @Transactional(readOnly = true)
    public long getOrderCountByStatus(OrderStatus status) {
        return orderRepository.getOrderCountByStatus(status);
    }

    /**
     * Get recent orders (last 30 days)
     */
    @Transactional(readOnly = true)
    public List<Order> getRecentOrders() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return orderRepository.findRecentOrders(thirtyDaysAgo);
    }

    /**
     * Generate unique order number
     */
    private String generateOrderNumber() {
        String prefix = "ORD-" + LocalDate.now().getYear() + "-";
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + suffix;
    }

    /**
     * Validate order data
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.getUser() == null || order.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required");
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }

        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
    }
}