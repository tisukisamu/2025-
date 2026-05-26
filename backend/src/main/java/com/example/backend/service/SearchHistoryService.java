package com.example.backend.service;

import com.example.backend.entity.SearchHistory;
import com.example.backend.entity.User;
import com.example.backend.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public List<String> getRecentKeywords(Long userId, int limit) {
        return searchHistoryRepository.findKeywordsByUserId(userId, PageRequest.of(0, limit));
    }

    @Transactional
    public void saveSearchHistory(Long userId, User user, String keyword) {
        searchHistoryRepository.findByUserIdAndKeyword(userId, keyword)
                .ifPresentOrElse(
                        history -> {
                            history.setSearchCount(history.getSearchCount() + 1);
                            searchHistoryRepository.save(history);
                        },
                        () -> {
                            SearchHistory history = new SearchHistory();
                            history.setUser(user);
                            history.setKeyword(keyword);
                            searchHistoryRepository.save(history);
                        }
                );
    }

    @Transactional
    public void deleteSearchHistory(Long userId, String keyword) {
        searchHistoryRepository.deleteByUserIdAndKeyword(userId, keyword);
    }

    @Transactional
    public void clearSearchHistory(Long userId) {
        searchHistoryRepository.deleteAllByUserId(userId);
    }
}
