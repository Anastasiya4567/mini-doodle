package com.task.minidoodle.controller;

import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.dto.CreateSlotRequest;
import com.task.minidoodle.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping("/users/{userId}")
    public TimeSlot create(@PathVariable Long userId, @RequestBody CreateSlotRequest request) {
        return slotService.create(userId, request);
    }

    @GetMapping("/users/{userId}")
    public List<TimeSlot> getUserSlots(@PathVariable Long userId) {
        return slotService.getUserSlots(userId);
    }

    @GetMapping("/{slotId}")
    public TimeSlot getById(@PathVariable Long slotId) {
        return slotService.getById(slotId);
    }

    @PutMapping("/{slotId}")
    public TimeSlot update(@PathVariable Long slotId, @RequestBody CreateSlotRequest request) {
        return slotService.update(slotId, request);
    }

    @PatchMapping("/{slotId}/status")
    public TimeSlot changeStatus(@PathVariable Long slotId, @RequestParam SlotStatus status) {
        return slotService.changeStatus(slotId, status);
    }

    @DeleteMapping("/{slotId}")
    public void delete(@PathVariable Long slotId) {
        slotService.delete(slotId);
    }

}
