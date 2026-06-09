package com.party.userManagement.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.party.userManagement.dto.PasswordUserRequest;
import com.party.userManagement.dto.ProfileUserRequest;
import com.party.userManagement.dto.UserRequest;
import com.party.userManagement.entity.UserDetails;
import com.party.userManagement.repository.UserRepo;

@Service
public class UserService {

	private final UserRepo repo;
	private final PasswordEncoder passwordEncoder;


	public UserService(UserRepo repo, PasswordEncoder passwordEncoder) {
		this.repo = repo;
		this.passwordEncoder = passwordEncoder;
	}

	public UserDetails getUserInfo(UUID uuid) {
		return Optional.of(repo.findById(uuid)).orElse(null).get();
	}

	@Transactional
	public boolean addUser(UserRequest request) {
		try {
			request.setPassword(passwordEncoder.encode(request.getPassword()));

			UserDetails userDetails = new UserDetails(request);
			repo.save(userDetails);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Transactional
	public boolean editUserProfile(ProfileUserRequest request, String username) {
		try {
			int ret = repo.updateUserProfile(request.getFirstName(), request.getLastName(), request.getEmail(),
					request.getMobile(), username);

			if (ret > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
	}

	public Map<String, String> isPasswordCorrect(String password, String username) {
		Map<String, String> ret = new HashMap<>();
		
		try {
			ret.put("result", "Success");

			List<Object[]> resultList = repo.isPasswordCorrect(username);
			if (!resultList.isEmpty()) {
				Object[] row = resultList.get(0);

				if (passwordEncoder.matches(password, row[2].toString())) {
					ret.put("userId", row[0].toString());
					ret.put("role", row[1].toString());
				} else {
					ret.put("result", "Incorrect Information");
				}
			} else {
				ret.put("result", "Incorrect Information");
			}

			System.out.println("Password check result for user " + username + ": " + ret);

		} catch (Exception e) {
			ret.put("result", "Error in checking Information");
		}

		return ret;
	}

	@Transactional
	public boolean editUserPassWord(PasswordUserRequest request, String username) {
		try {
			request.setNewPassword(passwordEncoder.encode(request.getNewPassword()));
			int ret = repo.updateUserPassword(request.getNewPassword(), request.getOldPassword(), username);

			if (ret > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
	}
}
