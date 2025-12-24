package com.popjub.aiservice.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.popjub.aiservice.domain.entity.Ai;

public interface AiRepository extends JpaRepository<Ai, UUID> {
}
