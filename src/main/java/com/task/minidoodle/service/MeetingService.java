package com.task.minidoodle.service;

import com.task.minidoodle.domain.Meeting;
import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.domain.User;
import com.task.minidoodle.dto.CreateMeetingRequest;
import com.task.minidoodle.exception.NotFoundException;
import com.task.minidoodle.exception.SlotNotAvailableException;
import com.task.minidoodle.repository.MeetingRepository;
import com.task.minidoodle.repository.TimeSlotRepository;
import com.task.minidoodle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final TimeSlotRepository slotRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    @Transactional
    public Meeting create(CreateMeetingRequest request) {

        var slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new NotFoundException("Slot not found: " + request.getSlotId()));

        if (SlotStatus.FREE != slot.getStatus()) {
            throw new SlotNotAvailableException("Selected slot is not available");
        }

        var participants = userRepository.findAllById(request.getParticipantIds());
        var meeting = buildMeeting(request, slot, participants);
        slot.setStatus(SlotStatus.BOOKED);
        return meetingRepository.save(meeting);
    }

    public Meeting getById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Meeting not found: " + id));
    }

    public List<Meeting> getAll() {
        return meetingRepository.findAll();
    }

    @Transactional
    public void cancel(Long id) {
        var meeting = getById(id);
        releaseSlot(meeting);
        meetingRepository.delete(meeting);
    }

    private void releaseSlot(Meeting meeting) {
        meeting.getSlot().setStatus(SlotStatus.FREE);
    }

    private Meeting buildMeeting(CreateMeetingRequest request, TimeSlot slot, List<User> participants) {
        return Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .participants(participants)
                .slot(slot)
                .build();
    }

}
