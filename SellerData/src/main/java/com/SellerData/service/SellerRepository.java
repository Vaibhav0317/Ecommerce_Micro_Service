package com.SellerData.service;

import com.SellerData.entity.Products;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SellerRepository extends MongoRepository<Products,Integer>{

}
