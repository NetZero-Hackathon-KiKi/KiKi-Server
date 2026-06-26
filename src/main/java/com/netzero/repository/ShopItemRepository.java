package com.netzero.repository;

import com.netzero.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {

    List<ShopItem> findByCategory(ShopItem.ItemCategory category);
}
