package com.example.todo.service;

import com.example.todo.dto.TaskRequestDto;
import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository repository;

    public List<Task> getAllTasks() {
        log.info("getAllTasks");
        return repository.findAll();
    }

    public Task addTask(TaskRequestDto dto) {
        log.info("addTask");
        Task task = new Task();
        task.setDescription(dto.description());
        task.setDayOfWeek(dto.dayOfWeek());
        task.setCompleted(false);
        return repository.save(task);
    }

    public void completeTask(Long id) {
        Task task = repository.findById(id).orElseThrow();
        task.setCompleted(true);
        repository.save(task);
    }
}
