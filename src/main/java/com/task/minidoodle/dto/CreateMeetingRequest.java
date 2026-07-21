package com.task.minidoodle.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateMeetingRequest {

    private String title;

    private String description;

    private Long slotId;

    private List<Long> participantIds;
}