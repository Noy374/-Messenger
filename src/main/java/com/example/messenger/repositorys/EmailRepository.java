package com.example.messenger.repositorys;

import com.example.messenger.entity.Email;
import com.example.messenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface EmailRepository extends JpaRepository<Email,Long> {


    Optional<Email> getEmailByEmailAddress(String email);

    @Modifying
    @Query("UPDATE Email e SET e.token = :token WHERE e.emailAddress = :email")
    void updateTokenByEmailAddress(@Param("email") String email, @Param("token") String token);


    @Modifying
    @Transactional
    @Query("UPDATE Email e SET e.status = :status WHERE e.token = :token")
    void updateStatusByToken(@Param("token") String token,@Param("status")Boolean status);

    @Modifying
    @Transactional
    @Query("UPDATE Email e SET e.emailAddress = :email WHERE e.user = :user")
    void updateEmailByUser(@Param("user") User user, @Param("email") String email);

}
