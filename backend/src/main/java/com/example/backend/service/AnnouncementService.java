package com.example.backend.service;

import com.example.backend.dto.AnnouncementDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Announcement;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AnnouncementRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public List<AnnouncementDTO> getTopAnnouncements() {
        List<Announcement> announcements = announcementRepository.findTop5WithAuthor();
        return announcements.stream()
                .map(AnnouncementDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PageResponse<AnnouncementDTO> getAnnouncements(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Announcement> announcementPage = announcementRepository.findAllWithAuthor(pageable);
        Page<AnnouncementDTO> dtoPage = announcementPage.map(AnnouncementDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public AnnouncementDTO getDetail(Long id) {
        Announcement announcement = announcementRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcement.setViewCount(announcement.getViewCount() + 1);
        announcementRepository.save(announcement);
        return AnnouncementDTO.fromEntity(announcement);
    }

    @Transactional
    public AnnouncementDTO create(Long authorId, String title, String content,
                               Announcement.AnnouncementType type, Boolean isTop) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setType(type);
        announcement.setIsTop(isTop != null ? isTop : false);
        announcement.setAuthor(author);

        Announcement saved = announcementRepository.save(announcement);
        return AnnouncementDTO.fromEntity(saved);
    }

    @Transactional
    public AnnouncementDTO update(Long id, String title, String content,
                               Announcement.AnnouncementType type, Boolean isTop) {
        Announcement announcement = announcementRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));

        if (title != null) announcement.setTitle(title);
        if (content != null) announcement.setContent(content);
        if (type != null) announcement.setType(type);
        if (isTop != null) announcement.setIsTop(isTop);

        Announcement saved = announcementRepository.save(announcement);
        return AnnouncementDTO.fromEntity(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Announcement", "id", id);
        }
        announcementRepository.deleteById(id);
    }
}
