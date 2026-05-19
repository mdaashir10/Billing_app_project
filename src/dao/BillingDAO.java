// ====================================================================================
// FILE 4: src/dao/BillingDAO.java
// ====================================================================================

package dao;

import database.DatabaseConnection;
import models.Bill;
import models.BillItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for billing operations
 */
public class BillingDAO {
    
    /**
     * Save a new bill to database
     */
    public boolean saveBill(Bill bill) {
        Connection conn = null;
        PreparedStatement billStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert bill
            String billSQL = "INSERT INTO bills (customer_name, customer_phone, total_amount, " +
                           "discount_percent, final_amount) VALUES (?, ?, ?, ?, ?)";
            billStmt = conn.prepareStatement(billSQL, Statement.RETURN_GENERATED_KEYS);
            billStmt.setString(1, bill.getCustomerName());
            billStmt.setString(2, bill.getCustomerPhone());
            billStmt.setDouble(3, bill.getTotalAmount());
            billStmt.setDouble(4, bill.getDiscountPercent());
            billStmt.setDouble(5, bill.getFinalAmount());
            
            billStmt.executeUpdate();
            
            // Get generated bill ID
            rs = billStmt.getGeneratedKeys();
            if (rs.next()) {
                int billId = rs.getInt(1);
                bill.setBillId(billId);
                
                // Insert bill items
                String itemSQL = "INSERT INTO bill_items (bill_id, item_name, quantity, " +
                               "price, item_total) VALUES (?, ?, ?, ?, ?)";
                itemStmt = conn.prepareStatement(itemSQL);
                
                for (BillItem item : bill.getItems()) {
                    itemStmt.setInt(1, billId);
                    itemStmt.setString(2, item.getItemName());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.setDouble(5, item.getItemTotal());
                    itemStmt.addBatch();
                }
                
                itemStmt.executeBatch();
            }
            
            conn.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (billStmt != null) billStmt.close();
                if (itemStmt != null) itemStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get all bills from database
     */
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setCustomerName(rs.getString("customer_name"));
                bill.setCustomerPhone(rs.getString("customer_phone"));
                bill.setBillDate(rs.getTimestamp("bill_date"));
                bill.setTotalAmount(rs.getDouble("total_amount"));
                bill.setDiscountPercent(rs.getDouble("discount_percent"));
                bill.setFinalAmount(rs.getDouble("final_amount"));
                bills.add(bill);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bills;
    }
    
    /**
     * Get bill by ID with all items
     */
    public Bill getBillById(int billId) {
        Bill bill = null;
        String billSQL = "SELECT * FROM bills WHERE bill_id = ?";
        String itemsSQL = "SELECT * FROM bill_items WHERE bill_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement billStmt = conn.prepareStatement(billSQL);
             PreparedStatement itemsStmt = conn.prepareStatement(itemsSQL)) {
            
            // Get bill details
            billStmt.setInt(1, billId);
            ResultSet billRS = billStmt.executeQuery();
            
            if (billRS.next()) {
                bill = new Bill();
                bill.setBillId(billRS.getInt("bill_id"));
                bill.setCustomerName(billRS.getString("customer_name"));
                bill.setCustomerPhone(billRS.getString("customer_phone"));
                bill.setBillDate(billRS.getTimestamp("bill_date"));
                bill.setTotalAmount(billRS.getDouble("total_amount"));
                bill.setDiscountPercent(billRS.getDouble("discount_percent"));
                bill.setFinalAmount(billRS.getDouble("final_amount"));
                
                // Get bill items
                itemsStmt.setInt(1, billId);
                ResultSet itemsRS = itemsStmt.executeQuery();
                
                while (itemsRS.next()) {
                    BillItem item = new BillItem();
                    item.setItemId(itemsRS.getInt("item_id"));
                    item.setBillId(itemsRS.getInt("bill_id"));
                    item.setItemName(itemsRS.getString("item_name"));
                    item.setQuantity(itemsRS.getInt("quantity"));
                    item.setPrice(itemsRS.getDouble("price"));
                    item.setItemTotal(itemsRS.getDouble("item_total"));
                    bill.addItem(item);
                }
                
                itemsRS.close();
            }
            
            billRS.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bill;
    }
    
    /**
     * Delete a bill by ID
     */
    public boolean deleteBill(int billId) {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, billId);
            int rows = stmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}