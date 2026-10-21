package com.natixis.service;

import com.natixis.dto.TodoResponse;
import com.natixis.model.Todo;
import com.natixis.repository.TodoRepository;
import com.natixis.exception.TodoNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository repository;

    @InjectMocks
    private TodoService todoService;

    private Todo todo1;
    private Todo todo2;

    @BeforeEach
    void setUp() {
        todo1 = new Todo(1L, "Learn Java", false);
        todo2 = new Todo(2L, "Learn Angular", true);
    }

    @Test
    void shouldFindAllTodos() {
        // Given
        when(repository.findAll()).thenReturn(List.of(todo1, todo2));

        // When
        List<TodoResponse> result = todoService.findAll(null);

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(TodoResponse::id, TodoResponse::label, TodoResponse::complete)
                .containsExactlyInAnyOrder(tuple(1L, "Learn Java", false), tuple(2L, "Learn Angular", true));

        verify(repository).findAll();
    }

    @Test
    void shouldFindAllCompletedTodos() {
        // Given
        when(repository.findAll()).thenReturn(List.of(todo1, todo2));

        // When
        List<TodoResponse> result = todoService.findAll(true);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(TodoResponse::id, TodoResponse::label, TodoResponse::complete)
                .containsExactly(tuple(2L, "Learn Angular", true));

        verify(repository).findAll();
    }

    @Test
    void shouldFindAllIncompletedTodos() {
        // Given
        when(repository.findAll()).thenReturn(List.of(todo1, todo2));

        // When
        List<TodoResponse> result = todoService.findAll(false);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(TodoResponse::id, TodoResponse::label, TodoResponse::complete)
                .containsExactly(tuple(1L, "Learn Java", false));

        verify(repository).findAll();
    }

    @Test
    void shouldFindTodoById() {
        // Given
        when(repository.findById(1L))
                .thenReturn(Optional.of(todo1));

        // When
        TodoResponse result = todoService.findById(1L);

        // Then
        assertThat(result)
                .extracting(TodoResponse::id, TodoResponse::label, TodoResponse::complete)
                .containsExactly(1L, "Learn Java", false);

        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTodoNotFound() {
        // Given
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> todoService.findById(99L))
                .isInstanceOf(TodoNotFoundException.class);

        verify(repository).findById(99L);
    }

    @Test
    void shouldCreateTodo() {
        // Given
        Todo created = new Todo(3L, "New task", false);

        when(repository.create("New task"))
                .thenReturn(created);

        // When
        TodoResponse result = todoService.create("New task");

        // Then
        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.label()).isEqualTo("New task");
        assertThat(result.complete()).isFalse();

        verify(repository).create("New task");
    }

    @Test
    void shouldUpdateTodoStatus() {
        // Given
        Todo updatedTodo = new Todo(1L, "Learn Java", true);

        when(repository.updateStatus(1L, true))
                .thenReturn(Optional.of(updatedTodo));

        // When
        TodoResponse result = todoService.updateStatus(1L, true);

        // Then
        assertThat(result.complete()).isTrue();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.label()).isEqualTo("Learn Java");

        verify(repository).updateStatus(1L, true);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingTodo() {
        // Given
        when(repository.updateStatus(99L, true))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() ->
                todoService.updateStatus(99L, true))
                .isInstanceOf(TodoNotFoundException.class);

        verify(repository).updateStatus(99L, true);
    }

    @Test
    void shouldDeleteTodo() {
        // Given
        when(repository.delete(1L)).thenReturn(true);

        // When
        todoService.delete(1L);

        // Then
        verify(repository).delete(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTodo() {
        // When & Then
        assertThatThrownBy(() ->
                todoService.delete(99L))
                .isInstanceOf(TodoNotFoundException.class);

        verify(repository).delete(99L);
    }
}
