package com.party.authManagement.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "AuthTable")
public class AuthEntity {

	@Id
	private UUID id;

	private UUID userId;
	private String tokenHash;
	private LocalDateTime issuedAt;
	private LocalDateTime expiryOn;
	private boolean isActive;
}
