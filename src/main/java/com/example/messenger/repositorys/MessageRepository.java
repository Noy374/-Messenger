package com.example.messenger.repositorys;


import com.example.messenger.documens.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySenderIdAndRecipientId(Long id, Long id1);
    // Методы для работы с сообщениями в MongoDB
}

