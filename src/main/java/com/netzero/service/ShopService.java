package com.netzero.service;

import com.netzero.dto.response.CharacterResponse;
import com.netzero.dto.response.ShopItemResponse;
import com.netzero.entity.ShopItem;
import com.netzero.entity.User;
import com.netzero.entity.UserItem;
import com.netzero.repository.ShopItemRepository;
import com.netzero.repository.UserItemRepository;
import com.netzero.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ShopItemResponse> getShopItems(Long userId, String category) {
        List<ShopItem> items;
        if (category != null && !category.isBlank()) {
            items = shopItemRepository.findByCategory(ShopItem.ItemCategory.valueOf(category));
        } else {
            items = shopItemRepository.findAll();
        }

        List<UserItem> userItems = userItemRepository.findByUserId(userId);
        Map<Long, UserItem> ownedMap = userItems.stream()
                .collect(Collectors.toMap(ui -> ui.getItem().getId(), ui -> ui));

        return items.stream()
                .map(item -> {
                    UserItem ui = ownedMap.get(item.getId());
                    return ShopItemResponse.builder()
                            .itemId(item.getId())
                            .name(item.getName())
                            .description(item.getDescription())
                            .imageUrl(item.getImageUrl())
                            .category(item.getCategory().name())
                            .price(item.getPrice())
                            .owned(ui != null)
                            .equipped(ui != null && ui.isEquipped())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> purchaseItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다."));

        if (userItemRepository.existsByUserIdAndItemId(userId, itemId)) {
            throw new IllegalStateException("이미 보유한 아이템입니다.");
        }
        if (user.getGreenPoint() < item.getPrice()) {
            throw new IllegalStateException("GP가 부족합니다.");
        }

        user.setGreenPoint(user.getGreenPoint() - item.getPrice());
        userRepository.save(user);

        UserItem userItem = UserItem.builder().user(user).item(item).build();
        userItemRepository.save(userItem);

        return Map.of("itemId", itemId, "remainingGp", user.getGreenPoint());
    }

    @Transactional(readOnly = true)
    public CharacterResponse getCharacterInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ShopItemResponse> equipped = userItemRepository.findByUserIdAndEquipped(userId, true).stream()
                .map(ui -> ShopItemResponse.builder()
                        .itemId(ui.getItem().getId())
                        .name(ui.getItem().getName())
                        .imageUrl(ui.getItem().getImageUrl())
                        .category(ui.getItem().getCategory().name())
                        .price(ui.getItem().getPrice())
                        .owned(true)
                        .equipped(true)
                        .build())
                .collect(Collectors.toList());

        return CharacterResponse.builder()
                .level(user.getLevel())
                .currentXp(user.getCurrentXp())
                .maxXp(user.getMaxXp())
                .greenPoint(user.getGreenPoint())
                .equippedItems(equipped)
                .build();
    }

    @Transactional
    public void equipItem(Long userId, Long itemId) {
        UserItem userItem = userItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new IllegalArgumentException("보유하지 않은 아이템입니다."));
        userItem.setEquipped(true);
        userItemRepository.save(userItem);
    }

    @Transactional
    public void unequipItem(Long userId, Long itemId) {
        UserItem userItem = userItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new IllegalArgumentException("보유하지 않은 아이템입니다."));
        userItem.setEquipped(false);
        userItemRepository.save(userItem);
    }

    @Transactional(readOnly = true)
    public List<ShopItemResponse> getMyItems(Long userId, String category) {
        List<UserItem> userItems = userItemRepository.findByUserId(userId);

        return userItems.stream()
                .filter(ui -> category == null || category.isBlank()
                        || ui.getItem().getCategory().name().equals(category))
                .map(ui -> ShopItemResponse.builder()
                        .itemId(ui.getItem().getId())
                        .name(ui.getItem().getName())
                        .description(ui.getItem().getDescription())
                        .imageUrl(ui.getItem().getImageUrl())
                        .category(ui.getItem().getCategory().name())
                        .price(ui.getItem().getPrice())
                        .owned(true)
                        .equipped(ui.isEquipped())
                        .build())
                .collect(Collectors.toList());
    }
}
