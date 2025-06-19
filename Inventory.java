package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventoryid")
    private Long inventoryid;

    @ManyToOne
    @JoinColumn(name = "bookid", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int quantity;

    public Inventory() {}

    public Inventory(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Long getInventoryid() {
        return inventoryid;
    }

    public void setInventoryid(Long inventoryid) {
        this.inventoryid = inventoryid;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative!");
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Inventory [inventoryid=" + inventoryid + ", bookid=" + book.getBookid() + ", quantity=" + quantity + "]";
    }
}
