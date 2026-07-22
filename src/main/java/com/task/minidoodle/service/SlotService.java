package com.task.minidoodle.service;

import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.domain.User;
import com.task.minidoodle.dto.CreateSlotRequest;
import com.task.minidoodle.exception.InvalidTimeRangeException;
import com.task.minidoodle.exception.NotFoundException;
import com.task.minidoodle.exception.SlotOverlapException;
import com.task.minidoodle.repository.TimeSlotRepository;
import com.task.minidoodle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlotService {

    private final TimeSlotRepository slotRepository;
    private final UserRepository userRepository;

    @Transactional
    public TimeSlot create(Long userId, CreateSlotRequest request) {

        validateTimeRange(request.getStartTime(), request.getEndTime());

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        var overlaps = slotRepository.findOverlappingSlots(userId, request.getStartTime(), request.getEndTime());

        if (overlaps.isEmpty()) {
            var slot = buildFreeTimeSlot(request, user);
            return slotRepository.save(slot);
        }
        throw new SlotOverlapException("User already has a slot during this period");
    }

    public List<TimeSlot> getUserSlots(Long userId) {
        return slotRepository.findByUserId(userId);
    }

    public TimeSlot getById(Long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Slot not found: " + id));
    }

    @Transactional
    public TimeSlot update(Long slotId, CreateSlotRequest request) {

        validateTimeRange(request.getStartTime(), request.getEndTime());

        var slot = getById(slotId);
        validateNoOverlap(slot.getUser().getId(), request.getStartTime(), request.getEndTime(), slotId);
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        return slot;
    }

    @Transactional
    public TimeSlot changeStatus(Long slotId, SlotStatus status) {
        var slot = getById(slotId);
        slot.setStatus(status);
        return slot;
    }

    @Transactional
    public void delete(Long slotId) {
        slotRepository.delete(getById(slotId));
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (end.isAfter(start)) {
            throw new InvalidTimeRangeException("Start time must be before end time");
        }
    }

    private void validateNoOverlap(Long userId, LocalDateTime start, LocalDateTime end, Long currentSlotId) {
        boolean overlap = slotRepository
                .findOverlappingSlots(userId, start, end)
                .stream()
                .anyMatch(slot -> !currentSlotId.equals(slot.getId()));

        if (overlap) {
            throw new SlotOverlapException("Overlapping slot detected");
        }
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
