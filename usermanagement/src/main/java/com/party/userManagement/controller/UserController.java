package com.party.userManagement.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.party.userManagement.entity.PasswordUserRequest;
import com.party.userManagement.entity.ProfileUserRequest;
import com.party.userManagement.entity.UserRequest;
import com.party.userManagement.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	// 1. Get user info
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserInfo(@PathVariable(value = "userId") UUID userId) {
		try {
			return ResponseEntity.ok(service.getUserInfo(userId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error in getting user details");
		}
	}

	// 2. Register user info
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
		if (service.addUser(request)) {
			return ResponseEntity.ok("User added");
		} else {
			return ResponseEntity.ok("Failed to add user");
		}
	}

	// 3. Edit user info
	@PutMapping("/{username}")
	public ResponseEntity<?> editUserProfile(@RequestBody ProfileUserRequest request,
			@PathVariable(value = "username") String username) {
		if (service.editUserProfile(request, username)) {
			return ResponseEntity.ok("Profile Editted");
		} else {
			return ResponseEntity.badRequest().body("Error in updating user details");
		}
	}

	// 4. Change Password
	@PutMapping("/changePassword/{username}")
	public ResponseEntity<?> changeUserPassword(@RequestBody PasswordUserRequest request,
			@PathVariable(value = "username") String username) {
		if (service.editUserPassWord(request, username)) {
			return ResponseEntity.ok("Password Changed");
		} else {
			return ResponseEntity.badRequest().body("Error in changing user password");
		}
	}

	// 5. Check Password
	@PostMapping("/checkPassword/{username}")
	public ResponseEntity<?> isPasswordCorrect(@RequestBody String password,
			@PathVariable(value = "username") String username) {
		return ResponseEntity.ok(service.isPasswordCorrect(password, username));
	}

}
