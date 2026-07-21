package com.task.minidoodle.controller;

import com.task.minidoodle.domain.Meeting;
import com.task.minidoodle.dto.CreateMeetingRequest;
import com.task.minidoodle.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public Meeting create(@RequestBody CreateMeetingRequest request) {
        return meetingService.create(request);
    }
}
