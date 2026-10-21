package com.natixis.repository;

import com.natixis.model.Todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class TodoRepositoryTest {

    private TodoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TodoRepository();
    }

    @Test
    void shouldCreateTodo() {
        // Given & When
        Todo result = repository.create("Learn Java");

        // Then
        assertThat(result.id()).isNotNull();
        assertThat(result.label()).isEqualTo("Learn Java");
        assertThat(result.complete()).isFalse();
    }

    @Test
    void shouldFindAllTodos() {
        // Given
        Todo todo1 = repository.create("Task 1");
        Todo todo2 = repository.create("Task 2");

        // When
        List<Todo> result = repository.findAll();

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(todo1, todo2);
    }

    @Test
    void shouldFindTodoById() {
        // Given
        Todo created = repository.create("Learn Angular");

        // When
        Optional<Todo> result = repository.findById(created.id());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().label()).isEqualTo("Learn Angular");
        assertThat(result.get().complete()).isFalse();
    }

    @Test
    void shouldUpdateTodoStatus() {
        // Given
        Todo created = repository.create("Learn Java");
        assertThat(created.complete()).isFalse();

        // When
        Optional<Todo> updated = repository.updateStatus(created.id(), true);

        // Then
        assertThat(updated).isPresent();
        assertThat(updated.get().id()).isEqualTo(created.id());
        assertThat(updated.get().label()).isEqualTo("Learn Java");
        assertThat(updated.get().complete()).isTrue();
    }

    @Test
    void shouldDeleteTodo() {
        // Given
        Todo todo1 = repository.create("Task 1");
        Todo todo2 = repository.create("Task 2");

        // When
        repository.delete(todo1.id());

        // Then
        assertThat(repository.findById(todo1.id())).isEmpty();
        assertThat(repository.findAll()).containsExactly(todo2);
    }

    @Test
    void shouldGenerateUniqueIds() {
        // Given & When
        Todo todo1 = repository.create("Task 1");
        Todo todo2 = repository.create("Task 2");
        Todo todo3 = repository.create("Task 3");

        // Then
        assertThat(todo1.id()).isNotEqualTo(todo2.id());
        assertThat(todo2.id()).isNotEqualTo(todo3.id());
        assertThat(todo1.id()).isNotEqualTo(todo3.id());
    }

    @Test
    void shouldGenerateUniqueIdsConcurrently() throws Exception {
        int taskCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {
            List<Callable<Long>> tasks = IntStream.range(0, taskCount)
                    .<Callable<Long>>mapToObj(i -> () -> repository.create("Task " + i).id())
                    .toList();

            List<Future<Long>> futures = executor.invokeAll(tasks);
            Set<Long> ids = new HashSet<>();

            for (Future<Long> future : futures) {
                ids.add(future.get());
            }

            assertThat(ids).hasSize(taskCount);
            assertThat(repository.findAll()).hasSize(taskCount);
        } finally {
            executor.shutdownNow();
        }
    }
}

