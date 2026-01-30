package com.shopsphere.order.domain.messages;

import com.shopsphere.order.domain.entity.OrderEntity;

public class OrderEventMapper {
	
	public static OrderCreatedEvent createdEvent(OrderEntity entity)
	{
		return new OrderCreatedEvent(entity.getId(),entity.getUserId(), entity.getTotalAmount(), entity.getStatus().name());
	}
}
