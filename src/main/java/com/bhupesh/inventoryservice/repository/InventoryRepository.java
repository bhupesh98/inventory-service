package com.bhupesh.inventoryservice.repository;

import com.bhupesh.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    boolean existsBySkuCodeAndQuantityGreaterThanEqual(String skuCode, Integer quantity);

    @Query("SELECT i.quantity FROM Inventory i WHERE i.skuCode = :skuCode")
    Integer findQuantityBySkuCode(@Param("skuCode") String skuCode);
}
