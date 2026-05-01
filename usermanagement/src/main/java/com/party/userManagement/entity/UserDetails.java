package com.party.userManagement.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UserInfo")
public class UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID userId;

	@Column(unique = true, nullable = false)
	private String userName;

	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;

	@Column(unique = true, nullable = false)
	private String email;
	@Column(unique = true, nullable = false)
	private String mobile;

	@Column(nullable = false)
	private String password;
	private boolean isUserActive;
	private LocalDate createDate;

	public UserDetails(UserRequest userRequest) {
		userId = UUID.randomUUID();

		this.userName = userRequest.getUserName();
		this.firstName = userRequest.getFirstName();
		this.lastName = userRequest.getLastName();
		this.email = userRequest.getEmail();
		this.mobile = userRequest.getMobile();
		this.password = userRequest.getPassword();

		this.isUserActive = true;
		this.setCreateDate(LocalDate.now());
	}
}
