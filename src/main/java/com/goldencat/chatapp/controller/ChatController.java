package com.goldencat.chatapp.controller;

import com.goldencat.chatapp.model.Message;
import com.goldencat.chatapp.model.Notification;
import com.goldencat.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        Message savedMessage = messageService.save(message);
        sendNotification(savedMessage);
    }

    @PostMapping("/images/{senderId}/{receiverId}")
    public ResponseEntity<Message> sendImage(
            @PathVariable String senderId,
            @PathVariable String receiverId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Message savedMessage = messageService.saveImageMessage(senderId, receiverId, file);
        sendNotification(savedMessage);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<Message>> getChatHistory(
            @PathVariable String senderId,
            @PathVariable String receiverId
    ) {
        List<Message> messages = messageService.findChatMessages(senderId, receiverId);
        return ResponseEntity.ok(messages);
    }

    private void sendNotification(Message message) {
        Notification notification = Notification.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getType() == Message.MessageType.IMAGE ? "Image" : message.getContent())
                .build();

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                notification
        );
    }
}