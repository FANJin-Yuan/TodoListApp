package com.natixis.service;

import com.natixis.dto.TodoResponse;
import com.natixis.model.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public static TodoResponse toResponse(Todo todo) {
        return new TodoResponse(todo.id(), todo.label(), todo.complete());
    }
}
