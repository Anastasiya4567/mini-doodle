package com.task.minidoodle.controller;

import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.dto.CreateSlotRequest;
import com.task.minidoodle.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping("/users/{userId}/slots")
    public TimeSlot create(@PathVariable Long userId, @RequestBody CreateSlotRequest request) {
        return slotService.create(userId, request);
    }
}
