package com.shopsphere.shipping.enums;


public enum ShipmentStatus {

	CREATED,            // Shipment record created (label generated)

    PICKED_UP,          // Courier picked parcel → Order becomes SHIPPED

    IN_TRANSIT,         // Moving between hubs

    OUT_FOR_DELIVERY,   // Out with delivery agent

    DELIVERED
}