package com.example.todo.userapi.api;

import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // 이메일 중복 확인 요청 처리
    // GET: /api/auth/check?email=zzzz@xxx.com
    // jpa는 pk로 조회하는 메서드는 기본 제공되지만, 다른컬럼으로 조회하는 메서드는 제공되지 않습니다.
    @GetMapping("/check")
    public ResponseEntity<?> emailCheck(String email) {
        log.info("/api/auth/check GET: {}", email);
        if (email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다.");
        }

        boolean emailOverCheck = userService.emailOverCheck(email);
        log.info("중복?? - {}", emailOverCheck);

        return  ResponseEntity.ok().body(emailOverCheck);

    }

}





















