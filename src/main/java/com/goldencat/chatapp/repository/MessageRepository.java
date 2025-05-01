package com.goldencat.chatapp.repository;

import com.goldencat.chatapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);
}