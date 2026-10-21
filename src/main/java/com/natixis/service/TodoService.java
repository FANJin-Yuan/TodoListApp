package com.natixis.service;

import com.natixis.dto.TodoResponse;
import com.natixis.exception.TodoNotFoundException;
import com.natixis.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public List<TodoResponse> findAll(Boolean complete) {
        return repository.findAll()
                .stream()
                .filter(t -> complete == null || t.complete() == complete)
                .map(TodoMapper::toResponse)
                .toList();
    }

    public TodoResponse findById(Long id) {
        return repository.findById(id)
                .map(TodoMapper::toResponse)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    public TodoResponse create(String label) {
        return TodoMapper.toResponse(repository.create(label.trim()));
    }

    public TodoResponse updateStatus(Long id, boolean complete) {
        return repository.updateStatus(id, complete)
                .map(TodoMapper::toResponse)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    public void delete(Long id) {
        if (!repository.delete(id)) throw new TodoNotFoundException(id);
    }
}
