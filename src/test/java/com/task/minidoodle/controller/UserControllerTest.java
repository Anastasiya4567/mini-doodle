package com.task.minidoodle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.minidoodle.domain.User;
import com.task.minidoodle.exception.NotFoundException;
import com.task.minidoodle.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@test.com")
                .build();

        when(userService.create(any(User.class))).thenReturn(user);

        // when / then
        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@test.com"));

        verify(userService).create(any(User.class));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        // given
        var users = List.of(
                User.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john@test.com")
                        .build(),
                User.builder()
                        .id(2L)
                        .name("Jane Doe")
                        .email("jane@test.com")
                        .build()
        );

        when(userService.getAll())
                .thenReturn(users);

        // when / then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(userService).getAll();
    }

    @Test
    void shouldGetUserById() throws Exception {
        // given
        var user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@test.com")
                .build();

        when(userService.getById(1L)).thenReturn(user);

        // when / then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"));

        verify(userService).getById(1L);
    }

    @Test
    void shouldDeleteUser() throws Exception {
        // given
        doNothing().when(userService).delete(1L);

        // when / then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // given
        when(userService.getById(999L)).thenThrow(new NotFoundException("User not found"));

        // when / then
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("User not found"));

        verify(userService).getById(999L);
    }


}
