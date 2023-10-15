package com.example.messenger.repositorys;

import com.example.messenger.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {

}
