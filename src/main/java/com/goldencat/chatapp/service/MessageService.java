package com.goldencat.chatapp.service;

import com.goldencat.chatapp.exception.ChatRoomNotFoundException;
import com.goldencat.chatapp.model.Message;
import com.goldencat.chatapp.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;

    // مسار تخزين الصور
    private final String UPLOAD_DIR = "uploads/";
    // تأكد من إنشاء المجلد إذا لم يكن موجوداً
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    public Message save(Message message) {
        validateMessage(message);
        String chatId = chatRoomService
                .getChatRoomId(message.getSenderId(), message.getReceiverId(), true)
                .orElseThrow(() -> new ChatRoomNotFoundException("Chat room could not be created"));

        message.setChatId(chatId);
        // سيتم تعيين التاريخ تلقائياً عبر @PrePersist
        return messageRepository.save(message);
    }

    public Message saveImageMessage(String senderId, String receiverId, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setType(Message.MessageType.IMAGE);
        message.setFilePath(fileName);

        return save(message);
    }

    public List<Message> findChatMessages(String senderId, String receiverId) {
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                senderId, receiverId, senderId, receiverId);
    }

    private void validateMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        validateUserIds(message.getSenderId(), message.getReceiverId());

        if (message.getType() == Message.MessageType.TEXT && !StringUtils.hasText(message.getContent())) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
    }

    private void validateUserIds(String senderId, String receiverId) {
        if (!StringUtils.hasText(senderId)) {
            throw new IllegalArgumentException("Sender ID cannot be empty");
        }
        if (!StringUtils.hasText(receiverId)) {
            throw new IllegalArgumentException("Receiver ID cannot be empty");
        }
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver cannot be the same");
        }
    }
}