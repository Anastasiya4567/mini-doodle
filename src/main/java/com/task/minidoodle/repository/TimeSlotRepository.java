package com.task.minidoodle.repository;

import com.task.minidoodle.domain.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByUserId(Long userId);

    @Query("""
            select t
            from TimeSlot t
            where t.user.id = :userId
            and t.startTime < :endTime
            and t.endTime > :startTime
            """)
    List<TimeSlot> findOverlappingSlots(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
