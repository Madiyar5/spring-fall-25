package com.smattme.spring_boot_flyway.controller;

import com.smattme.spring_boot_flyway.entity.Order;
import com.smattme.spring_boot_flyway.entity.Order.OrderStatus;
import com.smattme.spring_boot_flyway.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // Enable CORS for frontend integration
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /api/orders - Get all orders with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderService.getAllOrders(pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/all - Get all orders without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrdersNoPagination() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/{id} - Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/orders/number/{orderNumber} - Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByOrderNumber(@PathVariable String orderNumber) {
        Optional<Order> order = orderService.getOrderByOrderNumber(orderNumber);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/orders - Create new order
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/orders/{id} - Update existing order
     */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody Order orderDetails) {
        try {
            Order updatedOrder = orderService.updateOrder(id, orderDetails);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /api/orders/{id}/status - Update order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/orders/{id} - Delete order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/orders/user/{userId} - Get orders by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/status/{status} - Get orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/date-range - Get orders by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/min-amount/{amount} - Get orders with minimum amount
     */
    @GetMapping("/min-amount/{amount}")
    public ResponseEntity<List<Order>> getOrdersByMinAmount(@PathVariable BigDecimal amount) {
        List<Order> orders = orderService.getOrdersByMinAmount(amount);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/user/email/{email} - Get orders by user email
     */
    @GetMapping("/user/email/{email}")
    public ResponseEntity<List<Order>> getOrdersByUserEmail(@PathVariable String email) {
        List<Order> orders = orderService.getOrdersByUserEmail(email);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/user/{userId}/total - Get total amount for user
     */
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getTotalAmountByUserId(@PathVariable Long userId) {
        BigDecimal total = orderService.getTotalAmountByUserId(userId);
        return ResponseEntity.ok(total);
    }

    /**
     * GET /api/orders/count/status/{status} - Get order count by status
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getOrderCountByStatus(@PathVariable OrderStatus status) {
        long count = orderService.getOrderCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/orders/recent - Get recent orders (last 30 days)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        List<Order> orders = orderService.getRecentOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
