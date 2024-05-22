package com.example.todo.userapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter @ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // 계정명이 아니라 식별 코드

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @CreationTimestamp
    private LocalDateTime joinDate;
    
    @Enumerated(EnumType.STRING) // 상수를 문자열로 저장할것임
    @Builder.Default // 나중에 빌더 객체를 이용해서 생성
    private Role role = Role.COMMON; // 유저 권한


    // 등급 수정 메서드 (엔터티에 @setter 를 설정하지않고 변경 가능성있는 필드를 직접 수정하는 메서드를 작성하는것이 일반적)
    public void changeRole(Role role) {
        this.role = role;
    }
}





















