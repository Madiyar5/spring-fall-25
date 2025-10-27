package com.smattme.spring_boot_flyway.repository;

import com.smattme.spring_boot_flyway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find users by first name or last name containing the search term (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByNameContaining(@Param("searchTerm") String searchTerm);

    /**
     * Find users by phone number
     */
    List<User> findByPhoneContaining(String phone);

    /**
     * Find users who have orders
     */
    @Query("SELECT DISTINCT u FROM User u WHERE u.orders IS NOT EMPTY")
    List<User> findUsersWithOrders();

    /**
     * Find users without orders
     */
    @Query("SELECT u FROM User u WHERE u.orders IS EMPTY")
    List<User> findUsersWithoutOrders();

    /**
     * Get user count
     */
    @Query("SELECT COUNT(u) FROM User u")
    long getUserCount();
}