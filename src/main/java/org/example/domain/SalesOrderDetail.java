package org.example.domain;

public class SalesOrderDetail {
    private int salesDetailId;
    private int salesId;
    private int productId;
    private int quantity;
    private double unitPrice;

    // Default Constructor
    public SalesOrderDetail() {}

    // Parameterized Constructor
    public SalesOrderDetail(int salesDetailId, int salesId, int productId, int quantity, double unitPrice) {
        this.salesDetailId = salesDetailId;
        this.salesId = salesId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
    public int getSalesDetailId() { return salesDetailId; }
    public void setSalesDetailId(int salesDetailId) { this.salesDetailId = salesDetailId; }

    public int getSalesId() { return salesId; }
    public void setSalesId(int salesId) { this.salesId = salesId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}