package com.task.minidoodle.repository;

import com.task.minidoodle.domain.SlotStatus;
import com.task.minidoodle.domain.TimeSlot;
import com.task.minidoodle.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TimeSlotRepositoryTest {

    @Autowired
    private TimeSlotRepository slotRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindOverlappingSlots() {
        // given
        var user = entityManager.persist(
                User.builder()
                        .name("John")
                        .email("john@test.com")
                        .build());

        var startTime = LocalDateTime.of(2025, 1, 1, 10, 0);
        var timeSlot = TimeSlot.builder()
                .user(user)
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .status(SlotStatus.FREE)
                .build();

        entityManager.persist(timeSlot);

        // when
        var result = slotRepository.findOverlappingSlots(
                user.getId(),
                startTime.plusMinutes(30),
                startTime.plusMinutes(90));

        // then
        assertEquals(1, result.size());
    }
}

