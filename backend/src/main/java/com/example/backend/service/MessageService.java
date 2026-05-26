package com.example.backend.service;

import com.example.backend.dto.MessageDTO;
import com.example.backend.dto.request.MessageRequest;
import com.example.backend.entity.Message;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<MessageDTO> getConversation(Long userId, Long otherUserId) {
        List<Message> messages = messageRepository.findConversationWithUsers(userId, otherUserId);
        return messages.stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<MessageDTO> getMessages(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Message> messagePage = messageRepository.findByUserIdWithUsers(userId, pageable);
        return messagePage.map(MessageDTO::fromEntity);
    }

    @Transactional
    public MessageDTO sendMessage(Long senderId, MessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getReceiverId()));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setType(Message.MessageType.valueOf(request.getType()));

        Message saved = messageRepository.save(message);
        return MessageDTO.fromEntity(saved);
    }

    @Transactional
    public void markAsRead(Long userId, Long otherUserId) {
        List<Message> messages = messageRepository.findConversationWithUsers(userId, otherUserId);
        messages.stream()
                .filter(m -> m.getReceiver().getId().equals(userId) && !m.getIsRead())
                .forEach(m -> m.setIsRead(true));
        messageRepository.saveAll(messages);
    }

    public long getUnreadCount(Long userId) {
        return messageRepository.countUnreadByReceiverId(userId);
    }
}
