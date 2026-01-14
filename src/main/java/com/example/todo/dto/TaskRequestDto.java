package com.example.todo.dto;

public record TaskRequestDto(
        String description,
        String dayOfWeek
) {
}
