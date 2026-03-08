package com.shopsphere.inventory.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.inventory.entity.InventoryReservation;
import com.shopsphere.inventory.enums.ReservationStatus;

public interface InventoryReservationRepo extends JpaRepository<InventoryReservation, UUID>{

	 // Idempotency check
    Optional<InventoryReservation> findByOrderIdAndProductId(
            UUID orderId, UUID productId);

    // Get all reservations for an order
    List<InventoryReservation> findByOrderId(UUID orderId);

    // Find all active reserved entries
    List<InventoryReservation> findByStatus(ReservationStatus status);

    // Check if reservation already exists
    boolean existsByOrderIdAndProductId(UUID orderId, UUID productId);
}
