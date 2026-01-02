package com.thiru.BookMyShow.userMgmt;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.thiru.BookMyShow.bookingMgmt.BookingEntity;

@Entity
@Table(name = "users", indexes = {

        // frequent profile / auth lookup
        @Index(name = "idx_user_username", columnList = "user_name")

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Setter(AccessLevel.NONE)
    @Column(name = "hashedPwd", nullable = false)
    private String hashedPwd;

    @Column(name = "userName", nullable = false)
    private String userName;

    @Column(name = "mailId", nullable = false, unique = true)
    private String mailId;

    @Column(name = "phNo", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BookingEntity> bookings;
}
