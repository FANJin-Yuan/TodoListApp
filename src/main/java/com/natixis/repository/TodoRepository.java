package com.natixis.repository;

import com.natixis.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TodoRepository {
    private final Map<Long, Todo> data = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<Todo> findAll() {
        return data.values().stream().sorted(Comparator.comparing(Todo::id)).toList();
    }

    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    public Todo create(String label) {
        Long id = sequence.incrementAndGet();
        Todo todo = new Todo(id, label, false);
        data.put(id, todo);
        return todo;
    }

    public Optional<Todo> updateStatus(Long id, boolean complete) {
        return Optional.ofNullable(data.computeIfPresent(id, (key, old) -> new Todo(old.id(), old.label(), complete)));
    }

    public boolean delete(Long id) {
        return data.remove(id) != null;
    }
}
