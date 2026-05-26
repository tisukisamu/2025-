package com.example.backend.service;

import com.example.backend.dto.MessageRequest;
import com.example.backend.dto.PageResponse;
import com.example.backend.entity.Message;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.MemorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MemorialRepository memorialRepository;

    public List<Message> getAlbumMessages(Long albumId) {
        return messageRepository.findByAlbumIdAndDeletedOrderByCreatedAtDesc(albumId, 0);
    }

    public PageResponse<Message> getAlbumMessages(Long albumId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createdAt").descending());
        Page<Message> page = messageRepository.findByAlbumIdAndDeleted(albumId, 0, pageable);
        
        return PageResponse.of(page.getContent(), page.getTotalElements(), pageNum, pageSize);
    }

    @Transactional
    public Message createMessage(MessageRequest request) {
        if (!memorialRepository.existsByIdAndUserId(request.getAlbumId(), request.getUserId())) {
            throw new ResourceNotFoundException("纪念相册", "id", request.getAlbumId());
        }
        
        Message message = new Message();
        message.setAlbumId(request.getAlbumId());
        message.setUserId(request.getUserId());
        message.setAuthorName(request.getAuthorName());
        message.setContent(request.getContent());
        
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long id, Long userId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("留言", "id", id));
        
        if (message.getUserId() != null && !message.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此留言");
        }
        
        message.setDeleted(1);
        messageRepository.save(message);
    }
}
