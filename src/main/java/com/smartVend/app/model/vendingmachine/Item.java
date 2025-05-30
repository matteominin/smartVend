package com.smartVend.app.model.vendingmachine;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Item implements Serializable {
    @Id
    public String id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    public int volume;

    @Column(nullable = false)
    public int quantity;

    @Column(nullable = false)
    public double price;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    public Date createdAt;

    @Column(nullable = false)
    public int position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ItemType type;

    public Item() {}

    public Item(String id, String name, String description, int volume, int quantity, double price, Date createdAt, int position, ItemType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.volume = volume;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.position = position;
        this.type = type;
    }
}
