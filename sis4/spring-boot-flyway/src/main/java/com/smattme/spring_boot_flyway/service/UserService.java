package com.smattme.spring_boot_flyway.service;

import com.smattme.spring_boot_flyway.entity.User;
import com.smattme.spring_boot_flyway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Create new user
     */
    public User createUser(User user) {
        validateUser(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        return userRepository.save(user);
    }

    /**
     * Update existing user
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        validateUser(userDetails);

        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDetails.getEmail() + " already exists");
        }

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());

        return userRepository.save(user);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Search users by name
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String searchTerm) {
        return userRepository.findByNameContaining(searchTerm);
    }

    /**
     * Find users by phone
     */
    @Transactional(readOnly = true)
    public List<User> findUsersByPhone(String phone) {
        return userRepository.findByPhoneContaining(phone);
    }

    /**
     * Get users with orders
     */
    @Transactional(readOnly = true)
    public List<User> getUsersWithOrders() {
        return userRepository.findUsersWithOrders();
    }

    /**
     * Get users without orders
     */
    @Transactional(readOnly = true)
    public List<User> getUsersWithoutOrders() {
        return userRepository.findUsersWithoutOrders();
    }

    /**
     * Get total user count
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.getUserCount();
    }

    /**
     * Check if user exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Validate user data
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Basic email validation
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}