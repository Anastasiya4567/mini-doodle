package com.task.minidoodle.service;

import com.task.minidoodle.domain.Meeting;
import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.dto.CreateMeetingRequest;
import com.task.minidoodle.exception.NotFoundException;
import com.task.minidoodle.exception.SlotNotAvailableException;
import com.task.minidoodle.repository.MeetingRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private TimeSlotRepository slotRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private MeetingService meetingService;

    @Test
    void shouldCreateMeeting() {
        // given
        var request = new CreateMeetingRequest();
        request.setTitle("Sprint Planning");
        request.setDescription("Planning");
        request.setSlotId(1L);
        request.setParticipantIds(List.of(1L, 2L));

        var slot = TimeSlot.builder()
                .id(1L)
                .status(SlotStatus.FREE)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        when(userRepository.findAllById(any())).thenReturn(List.of());

        when(meetingRepository.save(any(Meeting.class))).thenAnswer(i -> i.getArgument(0));

        when(meterRegistry.counter("meetings.created")).thenReturn(counter);

        // when
        var meeting = meetingService.create(request);

        // then
        assertEquals(SlotStatus.BOOKED, slot.getStatus());
        assertEquals("Sprint Planning", meeting.getTitle());
    }

    @Test
    void shouldRejectBookingBusySlot() {
        // given
        var request = new CreateMeetingRequest();
        request.setSlotId(1L);

        var slot = TimeSlot.builder()
                .id(1L)
                .status(SlotStatus.BUSY)
                .build();

        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));

        // when / then
        assertThrows(SlotNotAvailableException.class, () -> meetingService.create(request));
    }

    @Test
    void shouldThrowWhenSlotNotFound() {
        // given
        var request = new CreateMeetingRequest();
        request.setSlotId(1L);

        when(slotRepository.findById(1L)).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> meetingService.create(request));
    }

    @Test
    void shouldCancelMeeting() {
        // given
        var slot = TimeSlot.builder()
                .status(SlotStatus.BOOKED)
                .build();

        var meeting = Meeting.builder()
                .id(1L)
                .slot(slot)
                .build();

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        // when
        meetingService.cancel(1L);

        // then
        assertEquals(SlotStatus.FREE, slot.getStatus());
        verify(meetingRepository).delete(meeting);
    }
}
