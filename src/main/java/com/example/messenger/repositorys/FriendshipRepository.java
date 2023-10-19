package com.example.messenger.repositorys;


import com.example.messenger.entity.Friendship;
import com.example.messenger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship,Long> {
    List<Friendship> findByUser(User user);
    Optional<Friendship> findByUserAndFriend(User user, User friend);
}
