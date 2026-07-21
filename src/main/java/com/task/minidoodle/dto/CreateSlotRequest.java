package com.task.minidoodle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSlotRequest {

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}