package com.BuyerData.service;

import com.BuyerData.entity.Products;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SellerRepository extends MongoRepository<Products,Integer>{

}
