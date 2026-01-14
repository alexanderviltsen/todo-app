package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {


    @GetMapping("/")
    public String showTasks() {
        return "tasks";
    }

    @GetMapping("/new/tasks")
    public String addTask() {
        return "new-tasks";
    }
}
