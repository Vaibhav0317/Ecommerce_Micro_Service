package com.SellerData.controller;


import com.SellerData.entity.BuyerProducts;
import com.SellerData.entity.Products;
import com.SellerData.filter.JwtFilter;
import com.SellerData.model.JWTRequest;
import com.SellerData.model.JWTResponse;
import com.SellerData.service.SellerRepository;
import com.SellerData.service.SellerServiceImpl;
import com.SellerData.service.UserLoginDetails;
import com.SellerData.service.UserService;
import com.SellerData.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class SellerController {

    @Autowired
    private SellerServiceImpl sellerServiceImpl;
    @Autowired
    private UserLoginDetails userLoginDetails;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private RestTemplate restTemplate;


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

    @GetMapping("/demo")
    public String demo()
    {
        System.out.println("inside demo");
        return "return string";
    }

    @PostMapping("/authenticate")
    public JWTResponse authenticate(@RequestBody JWTRequest jwtRequest) throws Exception
    {
        System.out.println("user name >==="+jwtRequest.getUsername());
        System.out.println("password >==="+jwtRequest.getPassword());
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




    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody Products product)
    {
        System.out.println("inside add controller");
        String MsgTrue1 = "Product Added Successfully";
        String MsgFalse1 = "Product Id already present please use different Product id";
        List<Products> list=this.sellerServiceImpl.getAll();
        boolean isPresent=false;
        isPresent=this.sellerServiceImpl.isContains(list,product.getId());


        if(isPresent==true)
        {
            return new ResponseEntity<>(MsgFalse1, statusNotOk);
        }
        else
        {
            Products p=this.sellerServiceImpl.addProduct(product);
            return new ResponseEntity<>(MsgTrue1, statusOk);
        }
    }




    @GetMapping("/getall")
    public ResponseEntity<?> get()
    {
        System.out.println("inside get all");
        List<Products> list= sellerServiceImpl.getAll();
        System.out.println("list in get api>==="+list);
        if(list.size()<=0)
        {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }
        return  ResponseEntity.of(Optional.of(list));

    }

    @GetMapping("/show")
    public ResponseEntity<?> userProduct()
    {

        String details=userLoginDetails.details();
        List<Products> list=sellerServiceImpl.getAll();

        System.out.println("before list Seller product >=="+list);
        list=list.stream().filter(e->e.getSellerName().equals(details)).collect(Collectors.toList());
        System.out.println("after list Seller product >=="+list);
        if(list.size()<=0)
        {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }
        return  ResponseEntity.of(Optional.of(list));
        //return list;
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id)
    {
        String MsgTrue = "Product Id "+id+" Found Product Deleted Successfully";
        String MsgFalse1 = "Product Id "+id+" Not Found please enter correct Product Id";
        String details=this.userLoginDetails.details();
        List<Products> list=this.sellerServiceImpl.getAll();
        list=list.stream().filter(e->e.getSellerName().equals(details)).collect(Collectors.toList());
        boolean isPresent=this.sellerServiceImpl.isContains(list,id);
        if(isPresent==true)
        {
            Products product=this.sellerServiceImpl.deleteProductById(id);
            return new ResponseEntity<>(MsgTrue, statusOk);
        }
        else
        {
            return new ResponseEntity<>(MsgFalse1, statusNotOk);
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllProducts()
    {
        this.sellerServiceImpl.deleteAllProduct();
        return new ResponseEntity<>("delete all", statusOk);
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody Products product, @PathVariable int id)
    {
        String MsgTrue = "Product Id "+id+" Found Product Update Successfully";
        String MsgFalse = "Product Id "+id+" Not Found please enter correct Product Id";
        String details=this.userLoginDetails.details();
        List<Products> list=this.sellerServiceImpl.getAll();
        list=list.stream().filter(e->e.getSellerName().equals(details)).collect(Collectors.toList());
        boolean isPresent=this.sellerServiceImpl.isContains(list,id);
        if(isPresent==true)
        {
            Products p=this.sellerServiceImpl.modify(product,id);
            this.sellerRepository.save(p);
            return new ResponseEntity<>(MsgTrue, statusOk);

        }
        else
        {
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }
    }
    @GetMapping("/allPurchaseProduct")
    public ResponseEntity<?> allBuyProduct()
    {

        HttpHeaders headers = new HttpHeaders();
        String accessToken=jwtFilter.getToken();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        //http://localhost:9092/allPurchase

        try {
            List list = restTemplate.exchange("http://localhost:9092/allPurchase", HttpMethod.GET, entity, List.class).getBody();
            if(list.size()<=0)
            {
                //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                return new ResponseEntity<>(MsgFalse, statusNotOk);
            }
            return  ResponseEntity.of(Optional.of(list));
        }catch (Exception e) {
            return new ResponseEntity<>(MsgFalse, statusNotOk);
        }
    }

}
