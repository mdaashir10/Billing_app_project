// ====================================================================================
// FILE 5: src/ui/BillingApp.java
// ====================================================================================

package ui;

import dao.BillingDAO;
import database.DatabaseConnection;
import models.Bill;
import models.BillItem;

import java.util.List;
import java.util.Scanner;

/**
 * Main console-based UI for Billing Application
 */
public class BillingApp {
    
    private static Scanner scanner = new Scanner(System.in);
    private static BillingDAO dao = new BillingDAO();
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SIMPLE BILLING APPLICATION");
        System.out.println("========================================");
        
        // Test database connection
        System.out.print("Testing database connection... ");
        if (DatabaseConnection.testConnection()) {
            System.out.println("SUCCESS!");
        } else {
            System.out.println("FAILED!");
            System.out.println("Please check database configuration.");
            return;
        }
        
        System.out.println();
        
        // Main menu loop
        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    createNewBill();
                    break;
                case 2:
                    viewAllBills();
                    break;
                case 3:
                    viewBillDetails();
                    break;
                case 4:
                    deleteBill();
                    break;
                case 5:
                    System.out.println("\nThank you for using Billing Application!");
                    System.exit(0);
                default:
                    System.out.println("\nInvalid choice! Please try again.");
            }
        }
    }
    
    /**
     * Display main menu
     */
    private static void displayMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Create New Bill");
        System.out.println("2. View All Bills");
        System.out.println("3. View Bill Details");
        System.out.println("4. Delete Bill");
        System.out.println("5. Exit");
        System.out.println("================================");
    }
    
    /**
     * Create a new bill
     */
    private static void createNewBill() {
        System.out.println("\n========== CREATE NEW BILL ==========");
        
        // Get customer details
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter customer phone: ");
        String phone = scanner.nextLine();
        
        Bill bill = new Bill(name, phone);
        
        // Add items
        while (true) {
            System.out.println("\n--- Add Item ---");
            System.out.print("Item name: ");
            String itemName = scanner.nextLine();
            
            int quantity = getIntInput("Quantity: ");
            double price = getDoubleInput("Price per unit: ");
            
            BillItem item = new BillItem(itemName, quantity, price);
            bill.addItem(item);
            
            System.out.print("\nAdd more items? (y/n): ");
            String more = scanner.nextLine();
            if (!more.equalsIgnoreCase("y")) {
                break;
            }
        }
        
        // Apply discount
        double discount = getDoubleInput("\nEnter discount percentage (0 for none): ");
        bill.setDiscountPercent(discount);
        
        // Calculate totals
        bill.calculateTotals();
        
        // Display bill preview
        System.out.println("\n========== BILL PREVIEW ==========");
        displayBillDetails(bill);
        
        // Confirm and save
        System.out.print("\nSave this bill? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (dao.saveBill(bill)) {
                System.out.println("\n✓ Bill saved successfully! Bill ID: " + bill.getBillId());
            } else {
                System.out.println("\n✗ Failed to save bill. Please try again.");
            }
        } else {
            System.out.println("\nBill cancelled.");
        }
    }
    
    /**
     * View all bills
     */
    private static void viewAllBills() {
        System.out.println("\n========== ALL BILLS ==========");
        
        List<Bill> bills = dao.getAllBills();
        
        if (bills.isEmpty()) {
            System.out.println("No bills found.");
            return;
        }
        
        System.out.println("\n" + String.format("%-8s %-20s %-15s %-20s %-12s", 
                          "Bill ID", "Customer", "Phone", "Date", "Amount"));
        System.out.println("─".repeat(80));
        
        for (Bill bill : bills) {
            System.out.println(String.format("%-8d %-20s %-15s %-20s ₹%-12.2f",
                bill.getBillId(),
                bill.getCustomerName(),
                bill.getCustomerPhone(),
                bill.getBillDate().toString().substring(0, 16),
                bill.getFinalAmount()));
        }
    }
    
    /**
     * View detailed bill information
     */
    private static void viewBillDetails() {
        int billId = getIntInput("\nEnter Bill ID: ");
        
        Bill bill = dao.getBillById(billId);
        
        if (bill == null) {
            System.out.println("\n✗ Bill not found!");
            return;
        }
        
        System.out.println("\n========== BILL DETAILS ==========");
        displayBillDetails(bill);
    }
    
    /**
     * Delete a bill
     */
    private static void deleteBill() {
        int billId = getIntInput("\nEnter Bill ID to delete: ");
        
        System.out.print("Are you sure you want to delete this bill? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (dao.deleteBill(billId)) {
                System.out.println("\n✓ Bill deleted successfully!");
            } else {
                System.out.println("\n✗ Failed to delete bill. Please check the Bill ID.");
            }
        } else {
            System.out.println("\nDeletion cancelled.");
        }
    }
    
    /**
     * Display bill details (helper method)
     */
    private static void displayBillDetails(Bill bill) {
        System.out.println("Bill ID: " + bill.getBillId());
        System.out.println("Customer: " + bill.getCustomerName());
        System.out.println("Phone: " + bill.getCustomerPhone());
        if (bill.getBillDate() != null) {
            System.out.println("Date: " + bill.getBillDate());
        }
        
        System.out.println("\n--- Items ---");
        System.out.println(String.format("%-20s %-10s %-12s %-12s", 
                          "Item", "Quantity", "Price", "Total"));
        System.out.println("─".repeat(60));
        
        for (BillItem item : bill.getItems()) {
            System.out.println(String.format("%-20s %-10d ₹%-12.2f ₹%-12.2f",
                item.getItemName(),
                item.getQuantity(),
                item.getPrice(),
                item.getItemTotal()));
        }
        
        System.out.println("─".repeat(60));
        System.out.println(String.format("%-45s ₹%-12.2f", "Subtotal:", bill.getTotalAmount()));
        
        if (bill.getDiscountPercent() > 0) {
            double discountAmount = bill.getTotalAmount() * (bill.getDiscountPercent() / 100);
            System.out.println(String.format("%-45s ₹%-12.2f", 
                "Discount (" + bill.getDiscountPercent() + "%):", discountAmount));
        }
        
        System.out.println("─".repeat(60));
        System.out.println(String.format("%-45s ₹%-12.2f", "FINAL AMOUNT:", bill.getFinalAmount()));
    }
    
    /**
     * Get integer input with validation
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
    
    /**
     * Get double input with validation
     */
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }
}