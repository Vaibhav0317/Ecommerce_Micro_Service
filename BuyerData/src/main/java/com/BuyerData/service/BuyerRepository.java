package com.BuyerData.service;

import com.BuyerData.entity.BuyerProducts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BuyerRepository extends MongoRepository<BuyerProducts,Integer> {
}
