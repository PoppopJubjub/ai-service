package com.popjub.aiservice.domain.entity;

import java.util.UUID;

import com.popjub.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "p_ai")
public class Ai extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID aiId;

	@Column(nullable = false)
	private UUID reviewId;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String request;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String response;

	protected Ai() {}

	public Ai(UUID reviewId, String request, String response) {
		this.reviewId = reviewId;
		this.request = request;
		this.response = response;
	}
}
