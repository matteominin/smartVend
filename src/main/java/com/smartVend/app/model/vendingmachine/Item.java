package com.smartvend.app.model.vendingmachine;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

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

    @Column(nullable = false)
    public Instant createdAt;

    @Column(nullable = false)
    public int position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ItemType type;

    public Item(long id, String name, String description, int volume, int quantity, double price,
            int position, ItemType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.volume = volume;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = Instant.now();
        this.position = position;
        this.type = type;
    }

    public long getId() {
        return id;
    }
}
