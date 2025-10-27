package com.smattme.spring_boot_flyway.controller;

import com.smattme.spring_boot_flyway.entity.User;
import com.smattme.spring_boot_flyway.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Enable CORS for frontend integration
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users - Get all users with pagination
     */
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/all - Get all users without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsersNoPagination() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id} - Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/users/email/{email} - Get user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/users - Create new user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/users/{id} - Update existing user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/users/{id} - Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/users/search - Search users by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String q) {
        List<User> users = userService.searchUsersByName(q);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/phone/{phone} - Find users by phone
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<List<User>> findUsersByPhone(@PathVariable String phone) {
        List<User> users = userService.findUsersByPhone(phone);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/with-orders - Get users who have orders
     */
    @GetMapping("/with-orders")
    public ResponseEntity<List<User>> getUsersWithOrders() {
        List<User> users = userService.getUsersWithOrders();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/without-orders - Get users without orders
     */
    @GetMapping("/without-orders")
    public ResponseEntity<List<User>> getUsersWithoutOrders() {
        List<User> users = userService.getUsersWithoutOrders();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/count - Get total user count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/users/exists/{email} - Check if user exists by email
     */
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}