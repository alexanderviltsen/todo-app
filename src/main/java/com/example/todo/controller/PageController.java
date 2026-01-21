package com.example.todo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class PageController {

    @GetMapping("/")
    public String index() {
        log.info("GET / - Главная страница");
        return "index";
    }

    @GetMapping("/tasks")
    public String showTasks() {
        log.info("GET /tasks - Страница списка задач");
        return "tasks";
    }

    @GetMapping("/statistics")
    public String showStatistics() {
        log.info("GET /statistics - Страница статистики");
        return "statistics";
    }

    @GetMapping("/calendar")
    public String showCalendar() {
        log.info("GET /calendar - Календарь задач");
        return "calendar";
    }
}
