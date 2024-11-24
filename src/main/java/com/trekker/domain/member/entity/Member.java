package com.trekker.domain.member.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.trekker.global.entity.AuditBaseEntity;
import com.trekker.global.entity.BaseEntity;
import jakarta.persistence.*;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "members")
public class Member extends BaseEntity {

    public static final int MAX_EMAIL_LENGTH = 256;
    public static final int MAX_NAME_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    // 계정의 아이디
    @Column(name = "email", nullable = false, length = MAX_EMAIL_LENGTH)
    private String email;

    //권한
    @Enumerated(STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // 회원의 이름
    @Column(name = "name", length = MAX_NAME_LENGTH)
    private String name;

}