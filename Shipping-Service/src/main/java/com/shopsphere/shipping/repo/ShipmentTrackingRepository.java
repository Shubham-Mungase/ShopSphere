package com.shopsphere.shipping.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.shipping.entity.ShipmentTracking;

@Repository
public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, UUID> {

    List<ShipmentTracking> findByShipmentIdOrderByTimestampAsc(UUID shipmentId);

}