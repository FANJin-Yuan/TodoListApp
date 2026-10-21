package com.natixis.controller;

import com.natixis.dto.TodoResponse;
import com.natixis.exception.TodoNotFoundException;
import com.natixis.service.TodoMapper;
import com.natixis.model.Todo;
import com.natixis.service.TodoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @MockitoBean
    private TodoMapper todoMapper;

    private final Todo todo1 = new Todo(1L, "Learn Java", false);
    private final Todo todo2 = new Todo(2L, "Learn Angular", true);
    private final TodoResponse response1 = new TodoResponse(1L, "Learn Java", false);
    private final TodoResponse response2 = new TodoResponse(2L, "Learn Angular", true);

    @Test
    void shouldGetAllTodos() throws Exception {
        when(todoService.findAll(null)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].label").value("Learn Java"))
                .andExpect(jsonPath("$[0].complete").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].label").value("Learn Angular"))
                .andExpect(jsonPath("$[1].complete").value(true));

        verify(todoService).findAll(null);
    }

    @Test
    void shouldGetIncompleteTodos() throws Exception {
        when(todoService.findAll(false)).thenReturn(List.of(response1));

        mockMvc.perform(get("/api/todos").param("complete", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].label").value("Learn Java"))
                .andExpect(jsonPath("$[0].complete").value(false));

        verify(todoService).findAll(false);
    }

    @Test
    void shouldGetCompletedTodos() throws Exception {
        when(todoService.findAll(true)).thenReturn(List.of(response2));

        mockMvc.perform(get("/api/todos").param("complete", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].label").value("Learn Angular"))
                .andExpect(jsonPath("$[0].complete").value(true));

        verify(todoService).findAll(true);
    }

    @Test
    void shouldGetTodoById() throws Exception {
        when(todoService.findById(1L))
                .thenReturn(response1);

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.label").value("Learn Java"))
                .andExpect(jsonPath("$.complete").value(false));

        verify(todoService).findById(1L);
    }

    @Test
    void shouldReturn404WhenTodoNotFound() throws Exception {
        when(todoService.findById(99L))
                .thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateTodo() throws Exception {
        TodoResponse response = new TodoResponse(3L, "New task", false);

        when(todoService.create("New task")).thenReturn(response);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "label": "New task"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.label").value("New task"))
                .andExpect(jsonPath("$.complete").value(false));

        verify(todoService).create("New task");
    }

    @Test
    void shouldRejectEmptyLabel() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "label": ""
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(todoService);
    }

    @Test
    void shouldRejectBlankLabel() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "label": "   "
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(todoService);
    }

    @Test
    void shouldRejectMissingLabel() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(todoService);
    }

    @Test
    void shouldUpdateTodoStatus() throws Exception {
        TodoResponse response = new TodoResponse(1L, "Learn Java", true);

        when(todoService.updateStatus(1L, true)).thenReturn(response);

        mockMvc.perform(patch("/api/todos/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "complete": true
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.complete").value(true));

        verify(todoService).updateStatus(1L, true);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingTodo() throws Exception {
        when(todoService.updateStatus(99L, true))
                .thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(patch("/api/todos/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "complete": true
                            }
                            """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(todoService).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingTodo() throws Exception {
        doThrow(new TodoNotFoundException(99L)).when(todoService).delete(99L);

        mockMvc.perform(delete("/api/todos/99"))
                .andExpect(status().isNotFound());

        verify(todoService).delete(99L);
    }
}

