package com.example.backend.service;

import com.example.backend.dto.MemorialRequest;
import com.example.backend.dto.MemorialResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.entity.MemorialAlbum;
import com.example.backend.entity.Pet;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.MemorialRepository;
import com.example.backend.repository.PetRepository;
import com.example.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class MemorialService {

    private final MemorialRepository memorialRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public List<MemorialResponse> getPetMemorials(Long petId) {
        List<MemorialAlbum> albums = memorialRepository.findByPetIdAndDeletedOrderByCreatedAtDesc(petId, 0);
        return albums.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<MemorialResponse> getPublicMemorials(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createdAt").descending());
        Page<MemorialAlbum> page = memorialRepository.findByIsPublicAndDeletedOrderByCreatedAtDesc(1, 0, pageable);
        
        List<MemorialResponse> list = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    public MemorialResponse getMemorialById(Long id) {
        MemorialAlbum album = memorialRepository.findByIdAndDeleted(id, 0)
                .orElseThrow(() -> new ResourceNotFoundException("纪念相册", "id", id));
        
        album.setViewCount(album.getViewCount() + 1);
        memorialRepository.save(album);
        
        return convertToResponse(album);
    }

    @Transactional
    public MemorialResponse createMemorial(Long userId, MemorialRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("宠物", "id", request.getPetId()));
        
        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此宠物");
        }
        
        MemorialAlbum album = new MemorialAlbum();
        album.setPetId(request.getPetId());
        album.setUserId(userId);
        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        album.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : 1);
        
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            try {
                album.setPhotos(objectMapper.writeValueAsString(request.getPhotos()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("照片数据处理失败");
            }
        }
        
        MemorialAlbum savedAlbum = memorialRepository.save(album);
        return convertToResponse(savedAlbum);
    }

    @Transactional
    public MemorialResponse updateMemorial(Long id, Long userId, MemorialRequest request) {
        MemorialAlbum album = memorialRepository.findByIdAndDeleted(id, 0)
                .orElseThrow(() -> new ResourceNotFoundException("纪念相册", "id", id));
        
        if (!album.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此相册");
        }
        
        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        album.setIsPublic(request.getIsPublic());
        
        if (request.getPhotos() != null) {
            try {
                album.setPhotos(objectMapper.writeValueAsString(request.getPhotos()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("照片数据处理失败");
            }
        }
        
        MemorialAlbum savedAlbum = memorialRepository.save(album);
        return convertToResponse(savedAlbum);
    }

    @Transactional
    public void deleteMemorial(Long id, Long userId) {
        MemorialAlbum album = memorialRepository.findByIdAndDeleted(id, 0)
                .orElseThrow(() -> new ResourceNotFoundException("纪念相册", "id", id));
        
        if (!album.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此相册");
        }
        
        album.setDeleted(1);
        memorialRepository.save(album);
    }

    private MemorialResponse convertToResponse(MemorialAlbum album) {
        MemorialResponse response = new MemorialResponse();
        response.setId(album.getId());
        response.setPetId(album.getPetId());
        response.setUserId(album.getUserId());
        response.setTitle(album.getTitle());
        response.setDescription(album.getDescription());
        response.setIsPublic(album.getIsPublic());
        response.setViewCount(album.getViewCount());
        response.setCreatedAt(album.getCreatedAt());
        response.setUpdatedAt(album.getUpdatedAt());
        
        if (album.getPhotos() != null && !album.getPhotos().isEmpty()) {
            try {
                response.setPhotos(objectMapper.readValue(album.getPhotos(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                response.setPhotos(List.of());
            }
        }
        
        petRepository.findById(album.getPetId()).ifPresent(pet -> {
            response.setPetName(pet.getName());
        });
        
        userRepository.findById(album.getUserId()).ifPresent(user -> {
            response.setUserName(user.getName());
        });
        
        return response;
    }
}
