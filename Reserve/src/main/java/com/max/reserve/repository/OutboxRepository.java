package com.max.reserve.repository;

import com.max.reserve.model.OutboxMessage;
import com.max.reserve.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    List<OutboxMessage> findTop20ByStatusOrderByCreatedAtAsc(Status status);
}

