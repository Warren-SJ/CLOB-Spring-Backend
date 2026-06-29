package com.warren.clob.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(name="id")
    private Long id;
    @Column(name="client")
    private Long clientId;
    @Column(name="price")
    private Integer price;
    @Column(name="original_quantity")
    private Integer originalQuantity;
    @Column(name="ticker")
    private Long ticker;
    @Column(name="remaining_quantity")
    private Long remainingQuantity;
    @Column(name="status")
    private char status;
    @Column(name="type")
    private char type;
    @Column(name="timestamp")
    private LocalDateTime timestamp;
}
