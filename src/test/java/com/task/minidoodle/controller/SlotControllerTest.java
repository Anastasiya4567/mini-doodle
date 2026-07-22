package com.task.minidoodle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.dto.CreateSlotRequest;
import com.task.minidoodle.exception.NotFoundException;
import com.task.minidoodle.service.SlotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SlotController.class)
class SlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SlotService slotService;

    @Test
    void shouldCreateSlot() throws Exception {
        // given
        var request = new CreateSlotRequest();
        var startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        request.setStartTime(startTime);
        request.setEndTime(startTime.plusHours(1));

        var slot = TimeSlot.builder()
                .id(1L)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(SlotStatus.FREE)
                .build();

        when(slotService.create(eq(1L), any(CreateSlotRequest.class)))
                .thenReturn(slot);

        // when / then
        mockMvc.perform(post("/slots/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("FREE"));

        verify(slotService).create(eq(1L), any(CreateSlotRequest.class));
    }

    @Test
    void shouldGetUserSlots() throws Exception {
        // given
        var slots = List.of(
                TimeSlot.builder()
                        .id(1L)
                        .status(SlotStatus.FREE)
                        .build(),
                TimeSlot.builder()
                        .id(2L)
                        .status(SlotStatus.BUSY)
                        .build()
        );

        when(slotService.getUserSlots(1L)).thenReturn(slots);

        // when / then
        mockMvc.perform(get("/slots/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(slotService).getUserSlots(1L);
    }

    @Test
    void shouldGetSlotById() throws Exception {
        // given
        var slot = TimeSlot.builder()
                .id(1L)
                .status(SlotStatus.FREE)
                .build();

        when(slotService.getById(1L)).thenReturn(slot);

        // when / then
        mockMvc.perform(get("/slots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("FREE"));

        verify(slotService).getById(1L);
    }

    @Test
    void shouldUpdateSlot() throws Exception {
        // given
        var request = new CreateSlotRequest();
        request.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        request.setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0));

        var updatedSlot = TimeSlot.builder()
                .id(1L)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(SlotStatus.FREE)
                .build();

        when(slotService.update(eq(1L), any(CreateSlotRequest.class))).thenReturn(updatedSlot);

        // when / then
        mockMvc.perform(put("/slots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(slotService).update(eq(1L), any(CreateSlotRequest.class));
    }

    @Test
    void shouldChangeSlotStatus() throws Exception {
        // given
        var slot = TimeSlot.builder()
                .id(1L)
                .status(SlotStatus.BUSY)
                .build();

        when(slotService.changeStatus(1L, SlotStatus.BUSY)).thenReturn(slot);

        // when / then
        mockMvc.perform(patch("/slots/1/status")
                        .param("status", "BUSY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BUSY"));

        verify(slotService).changeStatus(1L, SlotStatus.BUSY);
    }

    @Test
    void shouldDeleteSlot() throws Exception {
        // given
        doNothing().when(slotService).delete(1L);

        // when / then
        mockMvc.perform(delete("/slots/1"))
                .andExpect(status().isOk());

        verify(slotService).delete(1L);
    }


    @Test
    void shouldReturn404WhenSlotNotFound() throws Exception {
        // given
        when(slotService.getById(999L))
                .thenThrow(new NotFoundException("Slot not found"));

        // when / then
        mockMvc.perform(get("/slots/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Slot not found"));
    }


}
