package com.example.todo.repository;

import com.example.todo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Найти все задачи по статусу завершения
     */
    List<Task> findByCompleted(boolean completed);
    
    /**
     * Найти все задачи для определенного дня недели
     */
    List<Task> findByDayOfWeek(String dayOfWeek);
    
    /**
     * Подсчитать количество задач по статусу
     */
    long countByCompleted(boolean completed);
    
    /**
     * Найти задачи по дню недели и статусу завершения
     */
    List<Task> findByDayOfWeekAndCompleted(String dayOfWeek, boolean completed);
}
