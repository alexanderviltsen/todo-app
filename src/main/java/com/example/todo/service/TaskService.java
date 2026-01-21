package com.example.todo.service;

import com.example.todo.dto.TaskRequestDto;
import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository repository;

    /**
     * Получить все задачи
     */
    public List<Task> getAllTasks() {
        log.info("[getAllTasks] Запрос всех задач");
        List<Task> tasks = repository.findAll();
        log.info("[getAllTasks] Найдено задач: {}", tasks.size());
        return tasks;
    }

    /**
     * Получить задачу по ID
     */
    public Optional<Task> getTaskById(Long id) {
        log.info("[getTaskById] Запрос задачи с ID: {}", id);
        Optional<Task> task = repository.findById(id);
        if (task.isPresent()) {
            log.info("[getTaskById] Задача найдена: {}", task.get());
        } else {
            log.warn("[getTaskById] Задача с ID {} не найдена", id);
        }
        return task;
    }

    /**
     * Получить все завершенные задачи
     */
    public List<Task> getCompletedTasks() {
        log.info("[getCompletedTasks] Запрос завершенных задач");
        List<Task> completedTasks = repository.findByCompleted(true);
        log.info("[getCompletedTasks] Найдено завершенных задач: {}", completedTasks.size());
        return completedTasks;
    }

    /**
     * Получить все незавершенные задачи
     */
    public List<Task> getIncompleteTasks() {
        log.info("[getIncompleteTasks] Запрос незавершенных задач");
        List<Task> incompleteTasks = repository.findByCompleted(false);
        log.info("[getIncompleteTasks] Найдено незавершенных задач: {}", incompleteTasks.size());
        return incompleteTasks;
    }

    /**
     * Получить задачи по дню недели
     */
    public List<Task> getTasksByDayOfWeek(String dayOfWeek) {
        log.info("[getTasksByDayOfWeek] Запрос задач для дня: {}", dayOfWeek);
        List<Task> tasks = repository.findByDayOfWeek(dayOfWeek);
        log.info("[getTasksByDayOfWeek] Найдено задач для {}: {}", dayOfWeek, tasks.size());
        return tasks;
    }

    /**
     * Добавить новую задачу
     */
    public Task addTask(TaskRequestDto dto) {
        log.info("[addTask] Создание новой задачи: description='{}', dayOfWeek='{}'", 
                dto.description(), dto.dayOfWeek());
        
        Task task = new Task();
        task.setDescription(dto.description());
        task.setDayOfWeek(dto.dayOfWeek());
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        
        Task savedTask = repository.save(task);
        log.info("[addTask] Задача успешно создана с ID: {}", savedTask.getId());
        return savedTask;
    }

    /**
     * Обновить описание задачи
     */
    public Task updateTaskDescription(Long id, String newDescription) {
        log.info("[updateTaskDescription] Обновление описания задачи ID: {}", id);
        
        Task task = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("[updateTaskDescription] Задача с ID {} не найдена", id);
                    return new RuntimeException("Task not found with id: " + id);
                });
        
        String oldDescription = task.getDescription();
        task.setDescription(newDescription);
        task.setUpdatedAt(LocalDateTime.now());
        
        Task updatedTask = repository.save(task);
        log.info("[updateTaskDescription] Описание задачи ID {} изменено: '{}' -> '{}'", 
                id, oldDescription, newDescription);
        return updatedTask;
    }

    /**
     * Отметить задачу как выполненную
     */
    public void completeTask(Long id) {
        log.info("[completeTask] Отметка задачи ID {} как выполненной", id);
        
        Task task = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("[completeTask] Задача с ID {} не найдена", id);
                    return new RuntimeException("Task not found with id: " + id);
                });
        
        if (task.isCompleted()) {
            log.warn("[completeTask] Задача ID {} уже была выполнена", id);
        } else {
            task.setCompleted(true);
            task.setCompletedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            repository.save(task);
            log.info("[completeTask] Задача ID {} успешно отмечена как выполненная", id);
        }
    }

    /**
     * Отменить выполнение задачи
     */
    public void uncompleteTask(Long id) {
        log.info("[uncompleteTask] Отмена выполнения задачи ID {}", id);
        
        Task task = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("[uncompleteTask] Задача с ID {} не найдена", id);
                    return new RuntimeException("Task not found with id: " + id);
                });
        
        if (!task.isCompleted()) {
            log.warn("[uncompleteTask] Задача ID {} уже была не выполнена", id);
        } else {
            task.setCompleted(false);
            task.setCompletedAt(null);
            task.setUpdatedAt(LocalDateTime.now());
            repository.save(task);
            log.info("[uncompleteTask] Выполнение задачи ID {} успешно отменено", id);
        }
    }

    /**
     * Удалить задачу
     */
    public void deleteTask(Long id) {
        log.info("[deleteTask] Удаление задачи ID {}", id);
        
        if (!repository.existsById(id)) {
            log.error("[deleteTask] Задача с ID {} не найдена", id);
            throw new RuntimeException("Task not found with id: " + id);
        }
        
        repository.deleteById(id);
        log.info("[deleteTask] Задача ID {} успешно удалена", id);
    }

    /**
     * Удалить все выполненные задачи
     */
    public int deleteAllCompletedTasks() {
        log.info("[deleteAllCompletedTasks] Удаление всех выполненных задач");
        
        List<Task> completedTasks = repository.findByCompleted(true);
        int count = completedTasks.size();
        
        repository.deleteAll(completedTasks);
        log.info("[deleteAllCompletedTasks] Удалено задач: {}", count);
        return count;
    }

    /**
     * Получить статистику задач
     */
    public TaskStatistics getStatistics() {
        log.info("[getStatistics] Запрос статистики задач");
        
        long total = repository.count();
        long completed = repository.countByCompleted(true);
        long incomplete = repository.countByCompleted(false);
        
        TaskStatistics stats = new TaskStatistics(total, completed, incomplete);
        log.info("[getStatistics] Статистика: всего={}, выполнено={}, не выполнено={}", 
                total, completed, incomplete);
        return stats;
    }

    /**
     * Внутренний класс для статистики
     */
    public record TaskStatistics(
            long total,
            long completed,
            long incomplete
    ) {}
}
