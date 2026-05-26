package com.example.backend.service;

import com.example.backend.dto.MessageDTO;
import com.example.backend.entity.Message;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Page<Message> getMessages(Long receiverId, Pageable pageable) {
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId, pageable);
    }

    public List<Message> getUnreadMessages(Long receiverId) {
        return messageRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(receiverId);
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("消息不存在"));
    }

    public long getUnreadCount(Long receiverId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(receiverId);
    }

    @Transactional
    public Message sendMessage(Long senderId, MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(messageDTO.getReceiverId());
        message.setTitle(messageDTO.getTitle());
        message.setContent(messageDTO.getContent());
        message.setType(Message.Type.valueOf(messageDTO.getType()));
        message.setRelatedId(messageDTO.getRelatedId());
        message.setIsRead(false);
        return messageRepository.save(message);
    }

    @Transactional
    public void markAsRead(Long id, Long userId) {
        Message message = getMessageById(id);
        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权操作此消息");
        }
        message.setIsRead(true);
        messageRepository.save(message);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Message> unreadMessages = getUnreadMessages(userId);
        unreadMessages.forEach(m -> m.setIsRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    @Transactional
    public void deleteMessage(Long id, Long userId) {
        Message message = getMessageById(id);
        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权删除此消息");
        }
        messageRepository.delete(message);
    }
}
