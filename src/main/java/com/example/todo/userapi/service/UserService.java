package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public boolean emailOverCheck(String email) {
        // JPQL로
        // User user = userRepository.emailOverCheck(email);

        // 쿼리메서드로
        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복되었습니다. - {}", email);
            return true;
        }

        return false;
    }

    public UserSignUpResponseDTO create(final UserSignUpRequestDTO dto) throws Exception{
        String email = dto.getEmail();

        if (emailOverCheck(email)) {
            throw new RuntimeException("중복된 이메일 입니다!");
        }

        // 패스워드 인코딩
        String encoded = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encoded);

        // dto룰 User Entity 로 변환해서 저장.
        User saved = userRepository.save(dto.toEntity());
        log.info("회원 가입 정상수행됨! -saved user = {}", saved);

        return new UserSignUpResponseDTO(saved);
    }

    // 로그인 유효성 검사
    public void login(LoginRequestDTO dto) {
        String email = dto.getEmail();
        boolean existsByEmail = userRepository.existsByEmail(email);

        String pw = dto.getPassword();
        userRepository.ComparePw()

    }
}













