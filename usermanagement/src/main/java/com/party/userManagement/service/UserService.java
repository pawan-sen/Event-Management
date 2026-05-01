package com.party.userManagement.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.party.userManagement.db.UserRepo;
import com.party.userManagement.entity.PasswordUserRequest;
import com.party.userManagement.entity.ProfileUserRequest;
import com.party.userManagement.entity.UserDetails;
import com.party.userManagement.entity.UserRequest;

@Service
public class UserService {

	@Autowired
	private UserRepo repo;

	public UserDetails getUserInfo(UUID uuid) {
		return Optional.of(repo.findById(uuid)).orElse(null).get();
	}

	@Transactional
	public boolean addUser(UserRequest request) {
		try {
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

	public String isPasswordCorrect(String password, String username) {
		String ret = "";
		try {
			ret = repo.isPasswordCorrect(username, password);

			System.out.println("Password check result for user " + username + ": " + ret);

		} catch (Exception e) {
			ret = "Error in checking password";
		}

		return ret;
	}

	@Transactional
	public boolean editUserPassWord(PasswordUserRequest request, String username) {
		try {
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
