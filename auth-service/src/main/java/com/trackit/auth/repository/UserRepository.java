package com.trackit.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.trackit.auth.entity.Authentication;

public interface UserRepository extends JpaRepository<Authentication, Long> {

    Optional<Authentication> findByEmail(String email);

}
