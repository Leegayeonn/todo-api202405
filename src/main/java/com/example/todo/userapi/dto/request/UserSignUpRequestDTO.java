package com.example.todo.userapi.dto.request;

import com.example.todo.userapi.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequestDTO {

    //json 데이터랑 똑같이 셋팅 해야함

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 5)
    private String userName;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    // dto를 엔터티로 변경
    public User toEntity(String uploadedFilePath) {
        return User.builder()
                .email(email)
                .password(password)
                .userName(userName)
                .profileImg(uploadedFilePath)
                .build();
    }

}


















