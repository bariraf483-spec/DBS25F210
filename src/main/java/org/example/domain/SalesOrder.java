package org.example.domain;

import java.sql.Timestamp;

public class SalesOrder {
    private int salesId;
    private int customerId;
    private Timestamp orderDate;
    private double totalAmount;

    // Default Constructor
    public SalesOrder() {}

    // Parameterized Constructor
    public SalesOrder(int salesId, int customerId, Timestamp orderDate, double totalAmount) {
        this.salesId = salesId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getSalesId() { return salesId; }
    public void setSalesId(int salesId) { this.salesId = salesId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}