package com.shopsphere.cart.repo;

import java.util.Optional;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.shopsphere.cart.document.CartDocument;


public interface CartRepo extends MongoRepository<CartDocument, ObjectId>{
	Optional<CartDocument>  findByUserId(UUID userId);
	void deleteByUserId(UUID userId);

}
