package com.agri.store.controller;

import com.agri.store.entity.Favorite;
import com.agri.store.repository.FavoriteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @GetMapping("/user/{userId}")
    public List<Favorite> getUserFavorites(@PathVariable Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @GetMapping("/check/{userId}/{productId}")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent());
    }

    @GetMapping("/check/username/{username}/{productId}")
    public ResponseEntity<Boolean> checkFavoriteByUsername(@PathVariable String username, @PathVariable Long productId) {
        List<Favorite> favorites = favoriteRepository.findAll();
        for (Favorite fav : favorites) {
            if (fav.getProductId() != null && fav.getProductId().equals(productId)) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Favorite> addFavorite(@RequestBody Favorite favorite) {
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return ResponseEntity.ok(savedFavorite);
    }

    @DeleteMapping("/{userId}/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok().build();
    }
}
