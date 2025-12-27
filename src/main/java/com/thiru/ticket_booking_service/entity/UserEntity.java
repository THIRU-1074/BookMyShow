package com.thiru.ticket_booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Setter(AccessLevel.NONE)
    @Column(name = "hashed_pwd", nullable = false)
    private String hashedPwd;

    public void updatePassword(String hashedPwd) {
        this.hashedPwd = hashedPwd;
    }

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mail_id", nullable = false, unique = true)
    private String mailId;

    @Column(name = "ph_no", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BookingEntity> bookings;
}

/* 
EXAMPLE UAGE

UserEntity user = UserEntity.builder()
        .name("Thiru")
        .mailId("thiru@mail.com")
        .phoneNumber("9876543210")
        .hashedPwd(passwordEncoder.encode("secret"))
        .role(Role.USER)
        .build();

*/