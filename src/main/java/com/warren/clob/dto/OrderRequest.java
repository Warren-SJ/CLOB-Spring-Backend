package com.warren.clob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    public enum ActionType {
        ADD, EDIT, DELETE
    }
    private ActionType action;
    private Long orderId;
    private Long clientId;
    private Long ticker;
    private Character side;
    private Integer price;
    private Integer quantity;
}
