package com.example.todo.controller;

import com.example.todo.dto.TaskRequestDto;
import com.example.todo.model.Task;
import com.example.todo.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService service;

    /**
     * Получить все задачи
     */
    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        log.info("GET /tasks - Запрос всех задач");
        return ResponseEntity.ok(service.getAllTasks());
    }

    /**
     * Получить задачу по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        log.info("GET /tasks/{} - Запрос задачи", id);
        return service.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить завершенные задачи
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        log.info("GET /tasks/completed - Запрос завершенных задач");
        return ResponseEntity.ok(service.getCompletedTasks());
    }

    /**
     * Получить незавершенные задачи
     */
    @GetMapping("/incomplete")
    public ResponseEntity<List<Task>> getIncompleteTasks() {
        log.info("GET /tasks/incomplete - Запрос незавершенных задач");
        return ResponseEntity.ok(service.getIncompleteTasks());
    }

    /**
     * Получить задачи по дню недели
     */
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<Task>> getTasksByDay(@PathVariable String dayOfWeek) {
        log.info("GET /tasks/day/{} - Запрос задач для дня", dayOfWeek);
        return ResponseEntity.ok(service.getTasksByDayOfWeek(dayOfWeek));
    }

    /**
     * Создать новую задачу
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDto dto) {
        log.info("POST /tasks - Создание новой задачи");
        Task created = service.addTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновить описание задачи
     */
    @PatchMapping("/{id}/description")
    public ResponseEntity<Task> updateDescription(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        log.info("PATCH /tasks/{}/description - Обновление описания", id);
        String newDescription = body.get("description");
        Task updated = service.updateTaskDescription(id, newDescription);
        return ResponseEntity.ok(updated);
    }

    /**
     * Отметить задачу как выполненную
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long id) {
        log.info("PATCH /tasks/{}/complete - Отметка задачи как выполненной", id);
        service.completeTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Отменить выполнение задачи
     */
    @PatchMapping("/{id}/uncomplete")
    public ResponseEntity<Void> uncompleteTask(@PathVariable Long id) {
        log.info("PATCH /tasks/{}/uncomplete - Отмена выполнения задачи", id);
        service.uncompleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удалить задачу
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /tasks/{} - Удаление задачи", id);
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удалить все завершенные задачи
     */
    @DeleteMapping("/completed")
    public ResponseEntity<Map<String, Integer>> deleteCompletedTasks() {
        log.info("DELETE /tasks/completed - Удаление всех завершенных задач");
        int deleted = service.deleteAllCompletedTasks();
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }

    /**
     * Получить статистику
     */
    @GetMapping("/statistics")
    public ResponseEntity<TaskService.TaskStatistics> getStatistics() {
        log.info("GET /tasks/statistics - Запрос статистики");
        return ResponseEntity.ok(service.getStatistics());
    }
}
