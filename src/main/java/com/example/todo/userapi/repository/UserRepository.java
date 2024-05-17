package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    // 이메일 중복 체크
    // 1. JPQL
    /*
    @Query(value = "SELECT COUNT(*) FROM User u WHERE u.email = :em")
    User emailOverCheck(@Param("em") String email);
     */
    // 2. 쿼리 메서드로
    boolean existsByEmail(String email);

}
