package com.party.userManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;

	private String password;
}
