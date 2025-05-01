package com.goldencat.chatapp.service;


import com.goldencat.chatapp.model.Message;
import com.goldencat.chatapp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;

    public Message save(Message message) {
        var chatId = chatRoomService
                .getChatRoomId(message.getSenderId(), message.getReceiverId(), true)
                .orElseThrow(() -> new RuntimeException("Chat room could not be found"));
        message.setChatId(chatId);
        message.setTimestamp(new Date());
        return messageRepository.save(message);
    }

    public List<Message> getChatMessage(String senderId, String receiverId) {
        // استرجاع كلا الاتجاهين للرسائل
        List<Message> messages = messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        messages.addAll(messageRepository.findBySenderIdAndReceiverId(receiverId, senderId));

        // ترتيب الرسائل حسب التاريخ
        messages.sort(Comparator.comparing(Message::getTimestamp));

        return messages;
    }
}