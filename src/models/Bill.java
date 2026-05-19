// ====================================================================================
// FILE 2: src/models/Bill.java
// ====================================================================================

package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bill with customer details and items
 */
public class Bill {
    private int billId;
    private String customerName;
    private String customerPhone;
    private Timestamp billDate;
    private double totalAmount;
    private double discountPercent;
    private double finalAmount;
    private List<BillItem> items;
    
    public Bill() {
        this.items = new ArrayList<>();
    }
    
    public Bill(String customerName, String customerPhone) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.items = new ArrayList<>();
        this.discountPercent = 0;
    }
    
    // Add item to bill
    public void addItem(BillItem item) {
        items.add(item);
    }
    
    // Calculate total amount
    public void calculateTotals() {
        totalAmount = 0;
        for (BillItem item : items) {
            totalAmount += item.getItemTotal();
        }
        
        // Apply discount
        double discountAmount = totalAmount * (discountPercent / 100);
        finalAmount = totalAmount - discountAmount;
    }
    
    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public Timestamp getBillDate() { return billDate; }
    public void setBillDate(Timestamp billDate) { this.billDate = billDate; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    
    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
}