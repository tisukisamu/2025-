package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.PetRequest;
import com.example.backend.dto.PetResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PetController {

    private final PetService petService;

    @GetMapping
    public Result<List<PetResponse>> getUserPets() {
        Long userId = getCurrentUserId();
        List<PetResponse> pets = petService.getUserPets(userId);
        return Result.success(pets);
    }

    @GetMapping("/page")
    public Result<PageResponse<PetResponse>> getUserPetsPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getCurrentUserId();
        PageResponse<PetResponse> result = petService.getUserPets(userId, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<PetResponse> getPetById(@PathVariable Long id) {
        PetResponse pet = petService.getPetById(id);
        return Result.success(pet);
    }

    @PostMapping
    public Result<PetResponse> createPet(@Valid @RequestBody PetRequest request) {
        Long userId = getCurrentUserId();
        PetResponse pet = petService.createPet(userId, request);
        return Result.success("创建成功", pet);
    }

    @PutMapping("/{id}")
    public Result<PetResponse> updatePet(@PathVariable Long id, @Valid @RequestBody PetRequest request) {
        Long userId = getCurrentUserId();
        PetResponse pet = petService.updatePet(id, userId, request);
        return Result.success("更新成功", pet);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePet(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        petService.deletePet(id, userId);
        return Result.success("删除成功", null);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return Long.parseLong(authentication.getName());
    }
}
