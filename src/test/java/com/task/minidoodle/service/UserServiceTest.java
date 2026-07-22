package com.task.minidoodle.service;

import com.task.minidoodle.domain.User;
import com.task.minidoodle.exception.NotFoundException;
import com.task.minidoodle.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        // given
        var user = User.builder()
                .name("John Doe")
                .email("john@test.com")
                .build();

        when(userRepository.save(user)).thenReturn(user);

        // when
        var result = userService.create(user);

        // then
        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnUserById() {
        // given
        var user = User.builder()
                .id(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        User result = userService.getById(1L);

        // then
        assertEquals(user, result);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void shouldDeleteUser() {

        // given
        var user = User.builder()
                .id(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        userService.delete(1L);

        // then
        verify(userRepository).delete(user);
    }
}

