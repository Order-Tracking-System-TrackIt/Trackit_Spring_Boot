package com.trackit.admin.repository.sql;

import com.trackit.admin.entity.sql.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    
    // Find all support agents
    List<Authentication> findByRole(String role);
    
    // Find support agents by role (case insensitive)
    @Query("SELECT a FROM Authentication a WHERE LOWER(a.role) = LOWER(:role)")
    List<Authentication> findByRoleIgnoreCase(String role);
    
    // Count by role
    Long countByRole(String role);
}