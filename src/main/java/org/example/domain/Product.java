package org.example.domain;

public class Product {
    private int productId;
    private String productName;
    private String sku;
    private double price;
    private int stockQuantity;
    private int categoryId;

    // Default Constructor
    public Product() {}

    // Parameterized Constructor
    public Product(int productId, String productName, String sku, double price, int stockQuantity, int categoryId) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
}