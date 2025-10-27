package com.smattme.spring_boot_flyway.repository;

import com.smattme.spring_boot_flyway.entity.Order;
import com.smattme.spring_boot_flyway.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find all orders for a specific user
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find orders by status
     */
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    /**
     * Find orders by date range
     */
    List<Order> findByOrderDateBetweenOrderByOrderDateDesc(LocalDate startDate, LocalDate endDate);

    /**
     * Find orders with total amount greater than specified value
     */
    List<Order> findByTotalAmountGreaterThanEqual(BigDecimal minAmount);

    /**
     * Find orders by user email
     */
    @Query("SELECT o FROM Order o JOIN o.user u WHERE u.email = :email")
    List<Order> findByUserEmail(@Param("email") String email);

    /**
     * Get total amount for all orders by user
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.id = :userId")
    BigDecimal getTotalAmountByUserId(@Param("userId") Long userId);

    /**
     * Get order count by status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long getOrderCountByStatus(@Param("status") OrderStatus status);

    /**
     * Find recent orders (last 30 days)
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("date") LocalDate date);

    /**
     * Find top customers by order count
     */
    @Query("SELECT o.user, COUNT(o) as orderCount FROM Order o " +
            "GROUP BY o.user ORDER BY COUNT(o) DESC")
    List<Object[]> findTopCustomersByOrderCount();

    /**
     * Check if order number exists
     */
    boolean existsByOrderNumber(String orderNumber);
}
