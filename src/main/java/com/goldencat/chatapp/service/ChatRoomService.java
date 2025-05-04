package com.goldencat.chatapp.service;

import com.goldencat.chatapp.model.ChatRoom;
import com.goldencat.chatapp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    /**
     * Retrieves or creates a chat room ID between two users.
     *
     * @param senderId The ID of the sender.
     * @param receiverId The ID of the receiver.
     * @param createRoomIfNotExist If true, creates a new chat room if one doesn't exist.
     * @return An Optional containing the chat room ID if found or created, empty otherwise.
     * @throws IllegalArgumentException if senderId or receiverId is invalid.
     */
    public Optional<String> getChatRoomId(
            String senderId,
            String receiverId,
            boolean createRoomIfNotExist
    ) {
        if (!StringUtils.hasText(senderId)) {
            throw new IllegalArgumentException("Sender ID cannot be null or empty");
        }
        if (!StringUtils.hasText(receiverId)) {
            throw new IllegalArgumentException("Receiver ID cannot be null or empty");
        }

        // Check if a chat room already exists in either direction
        Optional<String> existingChatId = chatRoomRepository
                .findBySenderIdAndReceiverId(senderId, receiverId)
                .or(() -> chatRoomRepository.findBySenderIdAndReceiverId(receiverId, senderId))
                .map(ChatRoom::getChatId);

        if (existingChatId.isPresent()) {
            return existingChatId;
        }

        if (createRoomIfNotExist) {
            return Optional.of(createChatRoom(senderId, receiverId));
        }

        return Optional.empty();
    }

    /**
     * Creates a new chat room between two users.
     *
     * @param senderId The ID of the sender.
     * @param receiverId The ID of the receiver.
     * @return The generated chat room ID.
     */
    private String createChatRoom(String senderId, String receiverId) {
        String chatId = generateChatId(senderId, receiverId);

        ChatRoom senderReceiver = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .receiverId(receiverId)
                .build();

        ChatRoom receiverSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(receiverId)
                .receiverId(senderId)
                .build();

        // Save both entries in a single transaction
        chatRoomRepository.saveAll(List.of(senderReceiver, receiverSender));

        return chatId;
    }

    /**
     * Generates a unique chat ID for a conversation between two users.
     * Uses UUID to ensure uniqueness regardless of sender/receiver order.
     */
    private String generateChatId(String senderId, String receiverId) {
        // Sort IDs to ensure consistency regardless of order
        String sortedIds = senderId.compareTo(receiverId) < 0
                ? senderId + "_" + receiverId
                : receiverId + "_" + senderId;

        return UUID.nameUUIDFromBytes(sortedIds.getBytes()).toString();
    }
}