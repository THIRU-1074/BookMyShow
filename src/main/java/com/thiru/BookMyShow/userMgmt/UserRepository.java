package com.thiru.BookMyShow.userMgmt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByMailId(String mailId);

    Optional<UserEntity> findByName(String userName);

    boolean existsByMailId(String mailId);

    boolean existsByPhoneNumber(String phoneNumber);
}
