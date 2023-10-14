package com.example.toDoList.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface iTaskRepository extends JpaRepository<taskModel, UUID> {

    List<taskModel> findByUserId(UUID userId);

}
