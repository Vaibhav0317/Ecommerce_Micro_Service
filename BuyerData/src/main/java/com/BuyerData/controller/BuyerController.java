package com.BuyerData.controller;

import com.BuyerData.entity.BuyerProducts;
import com.BuyerData.entity.FinalPurchase;
import com.BuyerData.entity.Products;
import com.BuyerData.filter.JwtFilter;
import com.BuyerData.model.JWTRequest;
import com.BuyerData.model.JWTResponse;
import com.BuyerData.service.*;
import com.BuyerData.utility.JWTUtility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class BuyerController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BuyerServiceImpl buyerService;

    @Autowired
    private UserLoginDetails userLoginDetails;


    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtFilter jwtFilter;



    String MsgFalse = "There are no Product in your List";
    HttpStatus statusNotOk = HttpStatus.NOT_FOUND;
    HttpStatus statusOk = HttpStatus.OK;

    @PostMapping("/authenticate")
    public JWTResponse authenticate(@RequestBody JWTRequest jwtRequest) throws Exception
    {
        System.out.println("inside authenticate <====");
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        }catch (BadCredentialsException e)
        {
            throw  new Exception("INVALID USER NAME OR PASSWORD",e);
        }

        final UserDetails userDetails
                =userService.loadUserByUsername(jwtRequest.getUsername());
        final String token=
                jwtUtility.generateToken(userDetails);

        System.out.println("Jwt token>=="+token);
        return new JWTResponse(token);
    }


    @GetMapping("/abc")
    public String demo() {
        System.out.println("inside abc");
        String s = restTemplate.getForObject("http://localhost:9091/demo", String.class);
        System.out.println("s>===" + s);
        return s;
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyProduct(@RequestBody BuyerProducts product) {
        int buyProduct = product.getPurchaseProduct();


        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        String accessToken=jwtFilter.getToken();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        //http://localhost:9091/getall
        JsonNode stocks = restTemplate.exchange("http://localhost:9091/getall", HttpMethod.GET, entity, JsonNode.class).getBody();
        System.out.println("Stock>==="+stocks);
        ArrayList<Products> list = new ArrayList<>();

        List<Products> prod = mapper.convertValue(stocks, new TypeReference<List<Products>>() {
        });

        for (int i = 0; i < prod.size(); i++) {
            //System.out.println("prod element " + " " + i + " " + prod.get(i));
            list.add((Products) prod.get(i));

        }

        boolean isPresent = this.buyerService.isContains(list, product.getProductId());
        if (isPresent == true) {
            System.out.println("product id====" + product.getProductId());
            Products p = null;
            for (int i = 0; i < list.size(); i++) {
               // System.out.println("list element " + " " + i + " " + list.get(i));
                if (product.getProductId() == list.get(i).getId()) {
                    p = list.get(i);
                }

            }


            //Products p = this.sellerRepository.findById(product.getProductId()).get();
            System.out.println("product is avaliable >==" + p);
            if (p.getStatus().equals("Available")) {
                //this.sellerRepository.deleteById(product.getBuyerProductId());
                int updateRemainingProduct = p.getNoOfProduct() - buyProduct;

                if (p.getNoOfProduct() >= buyProduct && p.getNoOfProduct() != 0) {
                    String requestBody = "{\"noOfProduct\":\""+updateRemainingProduct+"\"}";
                    System.out.println("request body >=="+requestBody);
                    HttpHeaders headers1 = new HttpHeaders();
                    headers1.add("user-agent", "Application");
                    headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    headers1.setContentType(MediaType.APPLICATION_JSON);
                    headers1.setBearerAuth(accessToken);
                    HttpEntity<String> entity1 = new HttpEntity<String>(requestBody,headers1);
                   // http://localhost:9091/update/"+product.getProductId()
                    String temp= String.valueOf(restTemplate.exchange("http://localhost:9091/update/"+product.getProductId(), HttpMethod.PUT, entity1, String.class));
                    System.out.println("Answer>==="+temp);

                    // Send the POST request
                    //ResponseEntity<Object> response = restTemplate.exchange("http://localhost:9091/update/"+product.getProductId(), HttpMethod.PUT, requestEntity, Object.class);

                    product.setProductName(p.getName());
                    product.setPrice(p.getPrice());
                    product.setStatus(p.getStatus());
                    product.setSellerName(p.getSellerName());
                    this.buyerService.addBuyProduct(product);
                    return new ResponseEntity<>("Product purchase Successfully", statusOk);
                } else {
                    return new ResponseEntity<>("Product Not Available", statusNotOk);
                }
            } else {
                return new ResponseEntity<>("Product Currently Not Available", statusNotOk);
            }
        } else {
            return new ResponseEntity<>("Product Not Found", statusNotOk);
        }

    }

    @GetMapping("/allPurchase")
    public ResponseEntity<?> allPurchase() {
        List<BuyerProducts> list = buyerRepository.findAll();
        if(list.size()<=0)
        {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }

        return ResponseEntity.of(Optional.of(list));
    }


    @GetMapping("/showPurches")
    public ResponseEntity<?> showPurchesedProduct() {
        FinalPurchase list = this.buyerService.showPurches();

        System.out.println("list of purchase product >== " + list);
        if(list==null)
        {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }
        return ResponseEntity.of(Optional.of(list));
    }

    @PutMapping("/rating/{id}")
    public ResponseEntity<?> rating(@Valid @RequestBody BuyerProducts product, @PathVariable int id) {
        String MsgTrue = "Product Id " + id + " Found Product Update Successfully";
        String MsgFalse = "Product Id " + id + " Not Found please enter correct Product Id";
        String details = userLoginDetails.details();
        List<BuyerProducts> list = this.buyerRepository.findAll();
        list = list.stream().filter(e -> e.getBuyerName().equals(details)).collect(Collectors.toList());
        boolean isPresent = this.buyerService.isContainsProduct(list, id);
        System.out.println("inside rating");

        if (isPresent == true) {
            Query query = new Query(Criteria.where("id").is(id));
            Update update = new Update();
            update.set("productRating", product.getProductRating());
            update.set("sellerRating", product.getSellerRating());
            mongoTemplate.findAndModify(query, update, BuyerProducts.class);
            return new ResponseEntity<>(MsgTrue, statusOk);

        } else {
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }
    }

    @GetMapping("/showProducts")
    public ResponseEntity<?> showAllProduct()
    {
        try {
            HttpHeaders headers = new HttpHeaders();
            String accessToken=jwtFilter.getToken();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            //http://localhost:9091/getall
            JsonNode list = restTemplate.exchange("http://localhost:9091/getall", HttpMethod.GET, entity, JsonNode.class).getBody();
            return ResponseEntity.of(Optional.of(list));
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }
    }


}
