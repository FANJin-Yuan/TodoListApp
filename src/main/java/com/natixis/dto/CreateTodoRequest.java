package com.natixis.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTodoRequest(@NotBlank String label) {
}
