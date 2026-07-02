package com.warren.clob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeductCash {
    private Long buyerId;
    private Long sellerId;
    private Integer quantity;
    private Integer price;
}
