package com.netzero.service;

import com.netzero.entity.*;
import com.netzero.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    public List<ShopItem> getAllItems() {
        return shopItemRepository.findAll();
    }

    @Transactional
    public UserItem purchase(Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow();
        ShopItem item = shopItemRepository.findById(itemId).orElseThrow();

        if (user.getGp() < item.getPrice()) {
            throw new RuntimeException("GP가 부족합니다.");
        }

        user.setGp(user.getGp() - item.getPrice());
        userRepository.save(user);

        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .purchasedAt(LocalDateTime.now())
                .build();

        return userItemRepository.save(userItem);
    }

    public List<UserItem> getMyItems(Long userId) {
        return userItemRepository.findByUserId(userId);
    }
}
