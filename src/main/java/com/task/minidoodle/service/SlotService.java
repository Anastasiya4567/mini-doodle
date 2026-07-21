package com.task.minidoodle.service;

import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.domain.User;
import com.task.minidoodle.dto.CreateSlotRequest;
import com.task.minidoodle.repository.TimeSlotRepository;
import com.task.minidoodle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final TimeSlotRepository slotRepository;
    private final UserRepository userRepository;

    public TimeSlot create(Long userId, CreateSlotRequest request) {
        var user = userRepository.findById(userId).orElseThrow();
        var overlaps = slotRepository.findOverlappingSlots(userId, request.getStartTime(), request.getEndTime());

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Overlapping slot");
        }

        var slot = buildFreeTimeSlot(request, user);
        return slotRepository.save(slot);
    }

    private TimeSlot buildFreeTimeSlot(CreateSlotRequest request, User user) {
        return TimeSlot.builder()
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(SlotStatus.FREE)
                .build();
    }
}
