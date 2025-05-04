package com.goldencat.chatapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date timestamp = new Date();

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String filePath;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }

    public enum MessageType {
        TEXT, IMAGE
    }
}