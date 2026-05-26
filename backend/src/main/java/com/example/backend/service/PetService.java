package com.example.backend.service;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.PetRequest;
import com.example.backend.dto.PetResponse;
import com.example.backend.entity.Pet;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.PetRepository;
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
public class PetService {

    private final PetRepository petRepository;

    public List<PetResponse> getUserPets(Long userId) {
        List<Pet> pets = petRepository.findByUserIdAndDeletedOrderByCreatedAtDesc(userId, 0);
        return pets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<PetResponse> getUserPets(Long userId, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createdAt").descending());
        Page<Pet> page = petRepository.findByUserIdAndDeleted(userId, 0, pageable);
        
        List<PetResponse> list = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    public PetResponse getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("宠物", "id", id));
        return convertToResponse(pet);
    }

    @Transactional
    public PetResponse createPet(Long userId, PetRequest request) {
        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName(request.getName());
        pet.setType(request.getType());
        pet.setBreed(request.getBreed());
        pet.setGender(request.getGender());
        pet.setBirthday(request.getBirthday());
        pet.setPassDate(request.getPassDate());
        pet.setPhoto(request.getPhoto());
        pet.setColor(request.getColor());
        pet.setWeight(request.getWeight());
        pet.setDescription(request.getDescription());
        pet.setMemorialText(request.getMemorialText());
        
        Pet savedPet = petRepository.save(pet);
        return convertToResponse(savedPet);
    }

    @Transactional
    public PetResponse updatePet(Long id, Long userId, PetRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("宠物", "id", id));
        
        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此宠物信息");
        }
        
        pet.setName(request.getName());
        pet.setType(request.getType());
        pet.setBreed(request.getBreed());
        pet.setGender(request.getGender());
        pet.setBirthday(request.getBirthday());
        pet.setPassDate(request.getPassDate());
        pet.setPhoto(request.getPhoto());
        pet.setColor(request.getColor());
        pet.setWeight(request.getWeight());
        pet.setDescription(request.getDescription());
        pet.setMemorialText(request.getMemorialText());
        
        Pet savedPet = petRepository.save(pet);
        return convertToResponse(savedPet);
    }

    @Transactional
    public void deletePet(Long id, Long userId) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("宠物", "id", id));
        
        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此宠物信息");
        }
        
        pet.setDeleted(1);
        petRepository.save(pet);
    }

    public boolean isOwner(Long petId, Long userId) {
        return petRepository.existsByIdAndUserId(petId, userId);
    }

    private PetResponse convertToResponse(Pet pet) {
        PetResponse response = new PetResponse();
        response.setId(pet.getId());
        response.setUserId(pet.getUserId());
        response.setName(pet.getName());
        response.setType(pet.getType());
        response.setBreed(pet.getBreed());
        response.setGender(pet.getGender());
        response.setBirthday(pet.getBirthday());
        response.setPassDate(pet.getPassDate());
        response.setPhoto(pet.getPhoto());
        response.setColor(pet.getColor());
        response.setWeight(pet.getWeight());
        response.setDescription(pet.getDescription());
        response.setMemorialText(pet.getMemorialText());
        response.setCreatedAt(pet.getCreatedAt());
        response.setUpdatedAt(pet.getUpdatedAt());
        return response;
    }
}
