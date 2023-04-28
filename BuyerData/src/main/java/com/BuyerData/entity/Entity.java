package com.BuyerData.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Entity {

    private int id;
    private  String name;
    private String price;
    private String status;
    private int purchaseProduct;
    private String sellerName;
    private int productRating;
    private int sellerRating;

}
