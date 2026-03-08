package com.shopsphere.inventory.dto.request;

import java.util.List;
import java.util.UUID;

public class ConfirmStockRequest {

    private UUID orderId;
    private List<UUID> productIds;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public List<UUID> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<UUID> productIds) {
        this.productIds = productIds;
    }
}
