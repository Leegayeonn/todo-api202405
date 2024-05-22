package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.NoRegisteredArgumentException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.Role;
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
    private final TokenProvider tokenProvider;


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

    public UserSignUpResponseDTO create(final UserSignUpRequestDTO dto) {
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
    public LoginResponseDTO login(final LoginRequestDTO dto){
        // 이메일을 통해서 회원정보를 조회.
        String email = dto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));

        // 패스워드 검증
        String rawPassword = dto.getPassword();// 사용자가 입력한 비번
        String encodedPassword = user.getPassword();// DB에 저장된 암호화된 비번

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        log.info("{}님 로그인 성공!",user.getUserName());

        // 로그인 성공 후에 클라이언트에게 뭘 리턴해 줄 것인가?
        // -> JWT 를 클라이언트에게 발급해 주어야 한다! -> 로그인 유지를 위해!
        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user,token);

    }

    // 프리미엄으로 승격하는
    public LoginResponseDTO promoteToPremium(TokenUserInfo userInfo) {

        User user = userRepository.findById(userInfo.getUserId())
                .orElseThrow(() -> new NoRegisteredArgumentException("회원 조회에 실패 했습니다."));

        // 일반(COMMON) 회원이 아니라면 예외 발생
        if (userInfo.getRole() != Role.COMMON) {
            throw new IllegalArgumentException("일반 회원이 아니라면 등급을 상승시킬 수 없습니다.");
        }

        // 등급 변경
        user.changeRole(Role.PREMIUM);
        User saved = userRepository.save(user);

        // 토큰 재발급 (새롭게 변경된 정보가 반영된)
        // 토큰에는 -> 아이디, 이메일, 등급
        String token = tokenProvider.createToken(saved);

        return new LoginResponseDTO(user, token);


    }
}













