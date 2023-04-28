package com.SellerData.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Document(collection = "Buyer")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
@ToString
public class BuyerProducts {
    @Id
    private int id;
    private String buyerName;
    private int productId;
    private  String productName;
    private String price;
    private String status;
    private int purchaseProduct;
    private String sellerName;

    @Max(value = 5,message = "Rating must be 5 or less than 5")
    @Min(value = 0,message = "Rating must be 0 or greater than 0")
    private int productRating;

    @Max(value = 5,message = "Rating must be 5 or less than 5")
    @Min(value = 0,message = "Rating must be 0 or greater than 0")
    private int sellerRating;
}
