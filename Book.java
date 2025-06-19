package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne; // Import for ManyToOne
import jakarta.persistence.JoinColumn; // Import for JoinColumn
import jakarta.persistence.Version;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookid;
    
    private String title;
    
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false) 
    private Author author;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false) 
    private Category category;

    private double price;
    private int stockquantity;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @Column(length = 1000)
    private String description;

    @Version
    private int version;

    
    public Book() {}

    
    public Book(Long bookid, String title, Author author, Category category, double price, int stockquantity, byte[] imageData, String description) {
        this.bookid = bookid;
        this.title = title;
        this.author = author;       
        this.category = category;  
        this.price = price;
        this.stockquantity = stockquantity;
        this.imageData = imageData;
        this.description = description;
    }

    public Long getBookid() {
        return bookid;
    }

    public void setBookid(Long bookid) {
        this.bookid = bookid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockquantity() {
        return stockquantity;
    }

    public void setStockquantity(int stockquantity) {
        this.stockquantity = stockquantity;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Book [bookid=" + bookid + ", title=" + title +
               ", author=" + (author != null ? author.getName() : "null") +
               ", category=" + (category != null ? category.getName() : "null") +
               ", price=" + price + ", stockquantity=" + stockquantity +
               ", imageData=" + (imageData != null ? "present" : "null") +
               ", description='" + description + '\'' +
               ", version=" + version + "]";
    }
}
