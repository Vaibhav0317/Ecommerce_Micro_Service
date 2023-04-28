package com.BuyerData.service;

import com.BuyerData.entity.BuyerProducts;
import com.BuyerData.entity.Entity;
import com.BuyerData.entity.FinalPurchase;
import com.BuyerData.entity.Products;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyerServiceImpl {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private UserLoginDetails userLoginDetails;

    @Autowired
    private MongoOperations mongoTemplate;

    @Autowired
    private SellerRepository sellerRepository;

    //other service
    public boolean isPresent(int id)
    {
        boolean flag=this.sellerRepository.existsById(id);
        return flag;
    }

    public boolean isContains(List<Products> list, int id) {
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


    //-------------------------------------------------------------------------------------

    public BuyerProducts addBuyProduct(BuyerProducts product)
    {
        List<BuyerProducts> list=buyerRepository.findAll();
        int id=list.size()+1;
        product.setId(id);
        String details=this.userLoginDetails.details();
        product.setBuyerName(details);
        System.out.println("Buyer product >==="+product);
        buyerRepository.save(product);
        return product;
    }


    public FinalPurchase showPurches()
    {
        String details=this.userLoginDetails.details();
        List<BuyerProducts> list=buyerRepository.findAll();
       // System.out.println("list all buy product >==="+list);
        list=list.stream().filter(e->e.getBuyerName().equals(details)).collect(Collectors.toList());
        System.out.println("list of particular user>==="+list);

        // List<Products> UserList=new ArrayList<>();
        List<Entity> UserList1=new ArrayList<>();

        /*for(int i=0;i<list.size();i++)
        {
            Products p=new Products();
            p.setId(list.get(i).getId());
            p.setName(list.get(i).getProductName());
            p.setPrice(list.get(i).getPrice());
            p.setStatus(list.get(i).getStatus());
            p.setNoOfProduct(list.get(i).getPurchaseProduct());
            p.setSellerName(list.get(i).getSellerName());
            System.out.println("product>========"+p);
            UserList.add(p);
        }*/

        for(int i=0;i<list.size();i++)
        {
            Entity p=new Entity();
            p.setId(list.get(i).getId());
            p.setName(list.get(i).getProductName());
            p.setPrice(list.get(i).getPrice());
            p.setStatus(list.get(i).getStatus());
            p.setPurchaseProduct(list.get(i).getPurchaseProduct());
            p.setSellerName(list.get(i).getSellerName());
            p.setProductRating(list.get(i).getProductRating());
            p.setSellerRating(list.get(i).getSellerRating());
            System.out.println("product>========"+p);
            UserList1.add(p);
        }

        System.out.println("list of product buy by user >=="+UserList1);
        FinalPurchase finalPurchase=new FinalPurchase();
        finalPurchase.setBuyerName(details);
        finalPurchase.setPurchaseList(UserList1);


        return finalPurchase;

    }

    public HttpEntity<String> request()
    {
        String plainCreds = "abc:abc";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        //System.out.println("byte >===" + base64Creds);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        return request;
    }

}
