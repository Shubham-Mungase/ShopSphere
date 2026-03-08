package com.shopsphere.inventory.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.enums.InventoryStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, UUID>{
      // Lock row for update (Pessimistic Lock)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductIdForUpdate(@Param("productId") UUID productId);

    List<Inventory> findByStatus(InventoryStatus status);

    Optional<Inventory> findByProductId(UUID productId);
    boolean existsByProductId(UUID productId);

}
