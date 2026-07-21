package com.task.minidoodle.repository;

import com.task.minidoodle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}