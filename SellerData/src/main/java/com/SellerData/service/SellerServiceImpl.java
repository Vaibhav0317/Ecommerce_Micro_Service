package com.SellerData.service;

import com.SellerData.entity.BuyerProducts;
import com.SellerData.entity.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SellerServiceImpl{
    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserLoginDetails userLoginDetails;

    @Autowired
    private MongoOperations mongoTemplate;


    //validation methods
    public boolean isPresent(int id)
    {
        boolean flag=this.sellerRepository.existsById(id);
        return flag;
    }

    public boolean isContains(List<Products> list,int id) {
        boolean isPresent = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                isPresent = true;
                break;
            } else {
                isPresent = false;
            }
        }
        return isPresent;
    }

    public boolean isContainsProduct(List<BuyerProducts> list, int id) {
        boolean isPresent = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                isPresent = true;
                break;
            } else {
                isPresent = false;
            }
        }
        return isPresent;
    }
//---------------------------------------------------------------------------------------------------



//Seller services

// add seller product
    public Products addProduct(Products product)
    {
        System.out.println("inside add ");
        String details=this.userLoginDetails.details();
        product.setSellerName(details);
        UUID uuid = UUID.randomUUID();
        int positiveInt = Math.abs(uuid.hashCode());
        product.setId(positiveInt);
        this.sellerRepository.save(product);
        System.out.println("product>=="+product);
        return product;
    }
// show all seller products
    public List<Products> getAll()
    {
        List<Products> list = this.sellerRepository.findAll();
        System.out.println("All Database>--- "+list);
        return list;
    }

    public Products getById(int id){
        Products p= this.sellerRepository.findById(id).get();
        System.out.println("get by id === "+p);
        return p;
    }

    public Products deleteProductById(int id)
    {
        Products p=this.sellerRepository.findById(id).get();
        this.sellerRepository.deleteById(id);
        return p;
    }

    public void deleteAllProduct()
    {
        String details=this.userLoginDetails.details();
        Query query = new Query();
        query.addCriteria(Criteria.where("sellerName").is(details));
        query.addCriteria(Criteria.where("sellerName").is("abc"));


        mongoTemplate.findAllAndRemove(query,Products.class);
    }

    public Products modify(Products product,int id)
    {

        Products p=this.sellerRepository.findById(id).get();

        Products temp=new Products();
        System.out.println("update product>=="+p);

        if(product.getId()==0)
        {
            temp.setId(p.getId());
        }
        else {
            temp.setId(product.getId());
        }
        if(product.getName()==null)
        {
            temp.setName(p.getName());
        }
        else {
            temp.setName(product.getName());
        }
        if(product.getPrice()==null)
        {
            temp.setPrice(p.getPrice());
        }
        else {
            temp.setPrice(product.getPrice());
        }

        if(product.getStatus()==null)
        {
            temp.setStatus(p.getStatus());
        }
        else {
            temp.setStatus(product.getStatus());
        }
        if(product.getNoOfProduct()==0)
        {
            temp.setNoOfProduct(p.getNoOfProduct());
        }
        else {
            temp.setNoOfProduct(product.getNoOfProduct());
        }

        if(product.getSellerName()==null)
        {
            temp.setSellerName(p.getSellerName());
        }
        else {
            temp.setSellerName(product.getSellerName());
        }

        System.out.println("Updated product>=="+temp);
        return temp;
    }


}
