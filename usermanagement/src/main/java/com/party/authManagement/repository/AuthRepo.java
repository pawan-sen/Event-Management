package com.party.authManagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.party.authManagement.entity.AuthEntity;

@Repository
public interface AuthRepo extends JpaRepository<AuthEntity, UUID> {

	@Query("Select a FROM AuthEntity a Where a.userId = :userId and a.isActive = true")
	Optional<AuthEntity> findByUserIdAndActiveTrue(UUID userId);

	@Modifying
	@Transactional
	@Query("DELETE FROM AuthEntity a WHERE a.tokenHash = :tokenHash")
	void deleteByTokenHash(String tokenHash);

	@Modifying
	@Transactional
	@Query("DELETE FROM AuthEntity a WHERE a.userId = :userId")
	void deleteByUserId(UUID userId);
}
