package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;


@Getter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
//로그인 성공 후 클라이언트에게 전송할 데이터 객체
public class LoginResponseDTO {

    private String email;
    private String userName;
    
    @JsonFormat(pattern = "yyyy년 MM월 dd일")
    private LocalDate joinDate;
    
    private Map<String, String> token; // 인증 토큰
    private String role;

    public LoginResponseDTO(User user, Map<String, String> token) {
        this.email = user.getEmail();
        this.userName = user.getUserName();
        this.joinDate = LocalDate.from(user.getJoinDate());
        this.token = token;
        this.role = String.valueOf(user.getRole());

    }
}










