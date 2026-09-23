package com.project.aura.Repository;

import com.project.aura.Entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepo extends JpaRepository<InventoryItem, Integer> {

    Optional<InventoryItem> findByName(String name);

    List<InventoryItem> findByCategory(String category);

    boolean existsByName(String name);
}
