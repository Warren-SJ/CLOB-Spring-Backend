package com.warren.clob.models;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Character status;
    @Column(name="type")
    private Character side;
    @Column(name="timestamp")
    private LocalDateTime timestamp;
}
