package com.task.minidoodle.service;

import com.task.minidoodle.domain.Meeting;
import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.dto.CreateMeetingRequest;
import com.task.minidoodle.repository.MeetingRepository;
import com.task.minidoodle.repository.TimeSlotRepository;
import com.task.minidoodle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final TimeSlotRepository slotRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public Meeting create(CreateMeetingRequest request) {

        var slot = slotRepository.findById(request.getSlotId()).orElseThrow();

        if (SlotStatus.FREE != slot.getStatus()) {
            throw new RuntimeException("Slot not available");
        }

        var participants = userRepository.findAllById(request.getParticipantIds());

        var meeting = Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .participants(participants)
                .build();

        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        return meetingRepository.save(meeting);
    }
}
