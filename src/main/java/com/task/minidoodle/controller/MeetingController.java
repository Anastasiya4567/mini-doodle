package com.task.minidoodle.controller;

import com.task.minidoodle.domain.Meeting;
import com.task.minidoodle.dto.CreateMeetingRequest;
import com.task.minidoodle.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public Meeting create(@RequestBody CreateMeetingRequest request) {
        return meetingService.create(request);
    }

    @GetMapping
    public List<Meeting> getAll() {
        return meetingService.getAll();
    }

    @GetMapping("/{id}")
    public Meeting getById(@PathVariable Long id) {
        return meetingService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable Long id) {
        meetingService.cancel(id);
    }

}
