package com.party.userManagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.party.userManagement.entity.UserDetails;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepo extends JpaRepository<UserDetails, UUID> {
        @Modifying
        @Transactional
        @Query("UPDATE UserDetails a " +
                        "SET a.firstName = :firstName, " +
                        "    a.lastName = :lastName, " +
                        "    a.email = :email, " +
                        "    a.mobile = :mobile " +
                        "WHERE a.userName = :username")
        int updateUserProfile(@Param("firstName") String firstName,
                        @Param("lastName") String lastName,
                        @Param("email") String email,
                        @Param("mobile") String mobile,
                        @Param("username") String username);

        @Modifying
        @Query("UPDATE UserDetails a " +
                        "SET a.password = :newPassword " +
                        "WHERE a.userName  = :username and a.password = :oldPassword")
        int updateUserPassword(@Param("newPassword") String newPassword, @Param("oldPassword") String oldPassword,
                        @Param("username") String username);

        @Query("SELECT a.userId, a.role, a.password FROM UserDetails a WHERE a.userName = :username AND a.isUserActive = true")
        List<Object[]> isPasswordCorrect(@Param("username") String username);

        UUID findUserIdByEmail(String email);
}
