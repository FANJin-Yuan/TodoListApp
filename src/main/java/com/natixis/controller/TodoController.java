package com.natixis.controller;

import com.natixis.dto.CreateTodoRequest;
import com.natixis.dto.TodoResponse;
import com.natixis.dto.UpdateStatusRequest;
import com.natixis.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "http://localhost:4200")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TodoResponse> retrieveAll(@RequestParam(required = false) Boolean complete) {
        return service.findAll(complete);
    }

    @GetMapping("/{id}")
    public TodoResponse retrieve(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@Valid @RequestBody CreateTodoRequest req) {
        return service.create(req.label());
    }

    @PatchMapping("/{id}/status")
    public TodoResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest req) {
        return service.updateStatus(id, req.complete());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
