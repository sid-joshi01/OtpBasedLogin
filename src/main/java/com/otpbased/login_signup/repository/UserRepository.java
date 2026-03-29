package com.otpbased.login_signup.repository;

import com.otpbased.login_signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    <T> Optional<T> findByEmail(String email);
}
