// ====================================================================================
// FILE 3: src/models/BillItem.java
// ====================================================================================

package models;

/**
 * Represents a single item in a bill
 */
public class BillItem {
    private int itemId;
    private int billId;
    private String itemName;
    private int quantity;
    private double price;
    private double itemTotal;
    
    public BillItem() {}
    
    public BillItem(String itemName, int quantity, double price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.itemTotal = quantity * price;
    }
    
    // Calculate item total
    public void calculateTotal() {
        this.itemTotal = quantity * price;
    }
    
    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        calculateTotal();
    }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { 
        this.price = price;
        calculateTotal();
    }
    
    public double getItemTotal() { return itemTotal; }
    public void setItemTotal(double itemTotal) { this.itemTotal = itemTotal; }
}