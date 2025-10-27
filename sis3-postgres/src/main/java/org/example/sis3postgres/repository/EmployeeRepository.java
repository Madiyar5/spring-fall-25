package org.example.sis3postgres.repository;

import org.example.sis3postgres.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

// Employee - ваша сущность. Integer - тип ее первичного ключа (id).
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Spring автоматически предоставляет все методы CRUD (save, findAll, delete и т.д.).
    // Здесь вам не нужно ничего писать, пока не понадобятся кастомные запросы.

}