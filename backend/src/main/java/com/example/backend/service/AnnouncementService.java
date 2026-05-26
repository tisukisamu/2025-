package com.example.backend.service;

import com.example.backend.dto.AnnouncementDTO;
import com.example.backend.entity.Announcement;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public List<Announcement> getPublishedAnnouncements() {
        return announcementRepository.findByStatus(Announcement.Status.PUBLISHED);
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("公告不存在"));
    }

    @Transactional
    public Announcement createAnnouncement(AnnouncementDTO announcementDTO, Long userId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDTO.getTitle());
        announcement.setContent(announcementDTO.getContent());
        announcement.setType(Announcement.Type.valueOf(announcementDTO.getType()));
        announcement.setStatus(Announcement.Status.DRAFT);
        announcement.setCreatedBy(userId);
        return announcementRepository.save(announcement);
    }

    @Transactional
    public Announcement updateAnnouncement(Long id, AnnouncementDTO announcementDTO) {
        Announcement announcement = getAnnouncementById(id);
        announcement.setTitle(announcementDTO.getTitle());
        announcement.setContent(announcementDTO.getContent());
        announcement.setType(Announcement.Type.valueOf(announcementDTO.getType()));
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void publishAnnouncement(Long id) {
        Announcement announcement = getAnnouncementById(id);
        announcement.setStatus(Announcement.Status.PUBLISHED);
        announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement announcement = getAnnouncementById(id);
        announcementRepository.delete(announcement);
    }
}
