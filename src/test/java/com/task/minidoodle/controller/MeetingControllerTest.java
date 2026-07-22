package com.task.minidoodle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.minidoodle.domain.Meeting;
import com.task.minidoodle.dto.CreateMeetingRequest;
import com.task.minidoodle.service.MeetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeetingController.class)
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MeetingService meetingService;

    @Test
    void shouldCreateMeeting() throws Exception {
        // given
        var request = new CreateMeetingRequest();
        request.setTitle("Sprint Planning");
        request.setDescription("Planning session");
        request.setSlotId(1L);
        request.setParticipantIds(List.of(1L, 2L));

        var meeting = Meeting.builder()
                .id(1L)
                .title("Sprint Planning")
                .description("Planning session")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(meetingService.create(any(CreateMeetingRequest.class)))
                .thenReturn(meeting);

        // when / then
        mockMvc.perform(post("/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sprint Planning"))
                .andExpect(jsonPath("$.description").value("Planning session"));

        verify(meetingService).create(any(CreateMeetingRequest.class));
    }

    @Test
    void shouldReturnMeetingById() throws Exception {
        // given
        var meeting = Meeting.builder()
                .id(1L)
                .title("Daily")
                .description("Daily sync")
                .build();

        when(meetingService.getById(1L)).thenReturn(meeting);

        // when / then
        mockMvc.perform(get("/meetings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Daily"));

        verify(meetingService).getById(1L);
    }

    @Test
    void shouldReturnAllMeetings() throws Exception {
        // given
        var meetings = List.of(
                Meeting.builder().id(1L).title("Daily").build(),
                Meeting.builder().id(2L).title("Retro").build()
        );

        when(meetingService.getAll()).thenReturn(meetings);

        // when / then
        mockMvc.perform(get("/meetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(meetingService).getAll();
    }

    @Test
    void shouldCancelMeeting() throws Exception {
        // given
        doNothing().when(meetingService).cancel(1L);

        // when / then
        mockMvc.perform(delete("/meetings/1"))
                .andExpect(status().isOk());

        verify(meetingService).cancel(eq(1L));
    }
}

