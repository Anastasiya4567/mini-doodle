package com.task.minidoodle.service;

import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.domain.User;
import com.task.minidoodle.dto.CreateSlotRequest;
import com.task.minidoodle.exception.InvalidTimeRangeException;
import com.task.minidoodle.exception.SlotOverlapException;
import com.task.minidoodle.repository.TimeSlotRepository;
import com.task.minidoodle.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private TimeSlotRepository slotRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private SlotService slotService;

    @Test
    void shouldCreateSlot() {
        // given
        var userId = 1L;
        var user = User.builder()
                .id(userId)
                .build();

        var request = new CreateSlotRequest();
        var startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        request.setStartTime(startTime);
        request.setEndTime(startTime.plusHours(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(slotRepository.findOverlappingSlots(anyLong(), any(), any())).thenReturn(List.of());

        when(meterRegistry.counter("slots.created")).thenReturn(counter);

        // when
        slotService.create(userId, request);

        // then
        verify(slotRepository).save(any(TimeSlot.class));
    }

    @Test
    void shouldRejectOverlappingSlot() {
        // given
        var userId = 1L;
        var user = User.builder()
                .id(userId)
                .build();

        var request = new CreateSlotRequest();
        var startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        request.setStartTime(startTime);
        request.setEndTime(startTime.plusHours(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(slotRepository.findOverlappingSlots(anyLong(), any(), any())).thenReturn(List.of(TimeSlot.builder().build()));

        // when / then
        assertThrows(SlotOverlapException.class, () -> slotService.create(userId, request));
    }

    @Test
    void shouldRejectInvalidTimeRange() {
        // given
        var userId = 1L;

        var request = new CreateSlotRequest();
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().minusHours(1));

        // when / then
        assertThrows(InvalidTimeRangeException.class, () -> slotService.create(userId, request));
    }

    @Test
    void shouldChangeStatus() {
        // given
        var slot = TimeSlot.builder()
                .id(1L)
                .status(SlotStatus.FREE)
                .build();

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        // when
        var result = slotService.changeStatus(1L, SlotStatus.BUSY);

        // then
        assertEquals(SlotStatus.BUSY, result.getStatus());
    }
}
