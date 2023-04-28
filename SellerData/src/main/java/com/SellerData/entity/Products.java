package com.SellerData.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Document(collection = "Seller")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
@ToString
public class Products {

   /* @Transient
    public static final String SEQUENCE_NAME="user_sequence";
*/

    @Id
    private int id;
    @NotEmpty(message = "Name may not be empty")
    private String name;
    @NotEmpty(message = "Price may not be empty")
    private String price;
    @NotEmpty
    @Pattern(regexp = "^(Available|NotAvailable)$", message = "Value must be either Available or NotAvailable")
    private String status;

    @Min(value = 1,message = "No of product is greater than 1")
    private int noOfProduct;

    private String sellerName;

}
