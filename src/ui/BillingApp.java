// ====================================================================================
// FILE 5: src/ui/BillingApp.java
// ====================================================================================

package ui;

import dao.BillingDAO;
import database.DatabaseConnection;
import models.Bill;
import models.BillItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Java Swing GUI for Billing Application
 */
public class BillingApp extends JFrame {
    
    private BillingDAO dao;
    private JTabbedPane tabbedPane;
    
    // Create Bill Tab Components
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField itemNameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField discountField;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private JLabel totalLabel;
    private JLabel discountAmountLabel;
    private JLabel finalAmountLabel;
    private List<BillItem> currentItems;
    
    // View Bills Tab Components
    private JTable billsTable;
    private DefaultTableModel billsTableModel;
    
    public BillingApp() {
        dao = new BillingDAO();
        currentItems = new ArrayList<>();
        
        setTitle("Billing Application - Java Swing + MySQL");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Test database connection
        System.out.println("Testing database connection...");
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to database!\nPlease check database configuration.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        System.out.println("Database connected successfully!");
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("📝 Create Bill", createBillPanel());
        tabbedPane.addTab("📋 View Bills", createViewBillsPanel());
        tabbedPane.addTab("ℹ️ About", createAboutPanel());
        
        add(tabbedPane);
    }
    
    // ==================================================================================
    // CREATE BILL PANEL
    // ==================================================================================
    private JPanel createBillPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Customer Details Panel
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        gbc.gridx = 0; gbc.gridy = 0;
        customerPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        customerNameField = new JTextField(25);
        customerNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        customerPanel.add(customerNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        customerPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        customerPhoneField = new JTextField(25);
        customerPhoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        customerPanel.add(customerPhoneField, gbc);
        
        // Add Item Panel
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        addItemPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Add Items to Bill"));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        gbc.gridx = 0; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        itemNameField = new JTextField(20);
        itemNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        addItemPanel.add(itemNameField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        addItemPanel.add(new JLabel("Qty:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.3;
        quantityField = new JTextField(8);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        addItemPanel.add(quantityField, gbc);
        
        gbc.gridx = 4; gbc.weightx = 0;
        addItemPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.3;
        priceField = new JTextField(10);
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        addItemPanel.add(priceField, gbc);
        
        gbc.gridx = 6; gbc.weightx = 0;
        JButton addButton = new JButton("➕ Add Item");
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addItem());
        addItemPanel.add(addButton, gbc);
        
        // Items Table
        String[] columns = {"Item Name", "Quantity", "Price (₹)", "Total (₹)"};
        itemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        itemsTable.setRowHeight(25);
        itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        JScrollPane tableScrollPane = new JScrollPane(itemsTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 220));
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Items in Current Bill"));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        JButton removeButton = new JButton("🗑️ Remove Selected");
        removeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        removeButton.setBackground(new Color(220, 53, 69));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(e -> removeSelectedItem());
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonPanel.add(removeButton);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        // Total Panel
        JPanel totalPanel = new JPanel(new GridBagLayout());
        totalPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Bill Summary"));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel discLabel = new JLabel("Discount %:");
        discLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        totalPanel.add(discLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.3;
        discountField = new JTextField("0", 8);
        discountField.setFont(new Font("Arial", Font.PLAIN, 14));
        discountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotals();
            }
        });
        totalPanel.add(discountField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        JLabel totLabel = new JLabel("Subtotal:");
        totLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.5;
        totalLabel = new JLabel("₹0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 102, 204));
        totalPanel.add(totalLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel discAmtLabel = new JLabel("Discount Amount:");
        discAmtLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        totalPanel.add(discAmtLabel, gbc);
        
        gbc.gridx = 1;
        discountAmountLabel = new JLabel("₹0.00");
        discountAmountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        discountAmountLabel.setForeground(new Color(220, 53, 69));
        totalPanel.add(discountAmountLabel, gbc);
        
        gbc.gridx = 2;
        JLabel finalLabel = new JLabel("FINAL AMOUNT:");
        finalLabel.setFont(new Font("Arial", Font.BOLD, 15));
        totalPanel.add(finalLabel, gbc);
        
        gbc.gridx = 3;
        finalAmountLabel = new JLabel("₹0.00");
        finalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        finalAmountLabel.setForeground(new Color(0, 128, 0));
        totalPanel.add(finalAmountLabel, gbc);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        
        JButton clearButton = new JButton("🔄 Clear All");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.setBackground(new Color(108, 117, 125));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearForm());
        
        JButton saveBillButton = new JButton("💾 Save Bill");
        saveBillButton.setFont(new Font("Arial", Font.BOLD, 15));
        saveBillButton.setBackground(new Color(40, 167, 69));
        saveBillButton.setForeground(Color.WHITE);
        saveBillButton.setFocusPainted(false);
        saveBillButton.setPreferredSize(new Dimension(140, 40));
        saveBillButton.addActionListener(e -> saveBill());
        
        buttonsPanel.add(clearButton);
        buttonsPanel.add(saveBillButton);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(customerPanel, BorderLayout.NORTH);
        topPanel.add(addItemPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private void addItem() {
        String itemName = itemNameField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String priceStr = priceField.getText().trim();
        
        if (itemName.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all item fields!", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            
            if (quantity <= 0 || price <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Quantity and price must be positive!", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BillItem item = new BillItem(itemName, quantity, price);
            currentItems.add(item);
            
            itemsTableModel.addRow(new Object[]{
                item.getItemName(),
                item.getQuantity(),
                String.format("%.2f", item.getPrice()),
                String.format("%.2f", item.getItemTotal())
            });
            
            // Clear fields
            itemNameField.setText("");
            quantityField.setText("");
            priceField.setText("");
            itemNameField.requestFocus();
            
            calculateTotals();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid quantity or price format!", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            currentItems.remove(selectedRow);
            itemsTableModel.removeRow(selectedRow);
            calculateTotals();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to remove!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void calculateTotals() {
        double total = 0;
        for (BillItem item : currentItems) {
            total += item.getItemTotal();
        }
        
        double discountPercent = 0;
        try {
            discountPercent = Double.parseDouble(discountField.getText());
            if (discountPercent < 0 || discountPercent > 100) {
                discountPercent = 0;
                discountField.setText("0");
            }
        } catch (NumberFormatException ex) {
            discountPercent = 0;
            discountField.setText("0");
        }
        
        double discountAmount = total * (discountPercent / 100);
        double finalAmount = total - discountAmount;
        
        totalLabel.setText(String.format("₹%.2f", total));
        discountAmountLabel.setText(String.format("₹%.2f", discountAmount));
        finalAmountLabel.setText(String.format("₹%.2f", finalAmount));
    }
    
    private void saveBill() {
        String customerName = customerNameField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();
        
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter customer name!", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            customerNameField.requestFocus();
            return;
        }
        
        if (currentItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please add at least one item!", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Bill bill = new Bill(customerName, customerPhone);
        for (BillItem item : currentItems) {
            bill.addItem(item);
        }
        
        try {
            double discountPercent = Double.parseDouble(discountField.getText());
            bill.setDiscountPercent(discountPercent);
        } catch (NumberFormatException ex) {
            bill.setDiscountPercent(0);
        }
        
        bill.calculateTotals();
        
        if (dao.saveBill(bill)) {
            JOptionPane.showMessageDialog(this, 
                "✅ Bill saved successfully!\n\nBill ID: " + bill.getBillId() + 
                "\nCustomer: " + bill.getCustomerName() +
                "\nFinal Amount: ₹" + String.format("%.2f", bill.getFinalAmount()), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshBillsTable();
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ Failed to save bill!\nPlease check database connection.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        customerNameField.setText("");
        customerPhoneField.setText("");
        itemNameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        discountField.setText("0");
        currentItems.clear();
        itemsTableModel.setRowCount(0);
        calculateTotals();
        customerNameField.requestFocus();
    }
    
    // ==================================================================================
    // VIEW BILLS PANEL
    // ==================================================================================
    private JPanel createViewBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Bills Table
        String[] columns = {"Bill ID", "Customer Name", "Phone", "Date & Time", "Total", "Discount", "Final Amount"};
        billsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(billsTableModel);
        billsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        billsTable.setRowHeight(28);
        billsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(billsTable);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 13));
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshBillsTable());
        
        JButton viewDetailsButton = new JButton("👁️ View Details");
        viewDetailsButton.setFont(new Font("Arial", Font.PLAIN, 13));
        viewDetailsButton.setBackground(new Color(23, 162, 184));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(e -> viewBillDetails());
        
        JButton deleteButton = new JButton("🗑️ Delete Bill");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 13));
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteBill());
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(viewDetailsButton);
        buttonsPanel.add(deleteButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Load initial data
        refreshBillsTable();
        
        return panel;
    }
    
    private void refreshBillsTable() {
        billsTableModel.setRowCount(0);
        List<Bill> bills = dao.getAllBills();
        
        for (Bill bill : bills) {
            billsTableModel.addRow(new Object[]{
                bill.getBillId(),
                bill.getCustomerName(),
                bill.getCustomerPhone(),
                bill.getBillDate().toString().substring(0, 19),
                String.format("₹%.2f", bill.getTotalAmount()),
                String.format("%.1f%%", bill.getDiscountPercent()),
                String.format("₹%.2f", bill.getFinalAmount())
            });
        }
        
        if (bills.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No bills found in database.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void viewBillDetails() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a bill to view!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int billId = (int) billsTableModel.getValueAt(selectedRow, 0);
        Bill bill = dao.getBillById(billId);
        
        if (bill == null) {
            JOptionPane.showMessageDialog(this, 
                "Bill not found in database!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create details dialog
        JDialog detailsDialog = new JDialog(this, "Bill Details - ID: " + billId, true);
        detailsDialog.setSize(700, 550);
        detailsDialog.setLocationRelativeTo(this);
        
        JPanel detailsPanel = new JPanel(new BorderLayout(15, 15));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Customer info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 15, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Bill Information"));
        
        infoPanel.add(new JLabel("Bill ID:"));
        JLabel billIdLabel = new JLabel(String.valueOf(bill.getBillId()));
        billIdLabel.setFont(new Font("Arial", Font.BOLD, 13));
        infoPanel.add(billIdLabel);
        
        infoPanel.add(new JLabel("Customer Name:"));
        JLabel nameLabel = new JLabel(bill.getCustomerName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        infoPanel.add(nameLabel);
        
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(bill.getCustomerPhone()));
        
        infoPanel.add(new JLabel("Date & Time:"));
        infoPanel.add(new JLabel(bill.getBillDate().toString()));
        
        // Items table
        String[] itemColumns = {"Item Name", "Quantity", "Price (₹)", "Total (₹)"};
        DefaultTableModel itemsModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable itemsTable = new JTable(itemsModel);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        itemsTable.setRowHeight(25);
        itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        
        for (BillItem item : bill.getItems()) {
            itemsModel.addRow(new Object[]{
                item.getItemName(),
                item.getQuantity(),
                String.format("%.2f", item.getPrice()),
                String.format("%.2f", item.getItemTotal())
            });
        }
        
        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
        itemsScrollPane.setBorder(BorderFactory.createTitledBorder("Items"));
        
        // Totals panel
        JPanel totalsPanel = new JPanel(new GridLayout(3, 2, 15, 8));
        totalsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        totalsPanel.add(new JLabel("Subtotal:"));
        JLabel subLabel = new JLabel(String.format("₹%.2f", bill.getTotalAmount()));
        subLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(subLabel);
        
        totalsPanel.add(new JLabel("Discount:"));
        double discAmt = bill.getTotalAmount() * (bill.getDiscountPercent() / 100);
        JLabel discLabel = new JLabel(String.format("%.1f%% (₹%.2f)", 
            bill.getDiscountPercent(), discAmt));
        discLabel.setForeground(new Color(220, 53, 69));
        totalsPanel.add(discLabel);
        
        totalsPanel.add(new JLabel("FINAL AMOUNT:"));
        JLabel finalLabel = new JLabel(String.format("₹%.2f", bill.getFinalAmount()));
        finalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        finalLabel.setForeground(new Color(0, 128, 0));
        totalsPanel.add(finalLabel);
        
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        detailsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        detailsPanel.add(totalsPanel, BorderLayout.SOUTH);
        
        detailsDialog.add(detailsPanel);
        detailsDialog.setVisible(true);
    }
    
    private void deleteBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a bill to delete!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int billId = (int) billsTableModel.getValueAt(selectedRow, 0);
        String customerName = (String) billsTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this bill?\n\n" +
            "Bill ID: " + billId + "\n" +
            "Customer: " + customerName, 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteBill(billId)) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Bill deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshBillsTable();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Failed to delete bill!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ==================================================================================
    // ABOUT PANEL
    // ==================================================================================
    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JLabel titleLabel = new JLabel("💼 Billing Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 15, 20, 15);
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        versionLabel.setForeground(Color.GRAY);
        panel.add(versionLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 15, 5, 15);
        JLabel techLabel = new JLabel("🔧 Technology Stack");
        techLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(techLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 15, 5, 15);
        JLabel tech1 = new JLabel("• Java Swing GUI");
        tech1.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(tech1, gbc);
        
        gbc.gridy++;
        JLabel tech2 = new JLabel("• MySQL Database");
        tech2.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(tech2, gbc);
        
        gbc.gridy++;
        JLabel tech3 = new JLabel("• JDBC for Database Connectivity");
        tech3.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(tech3, gbc);
        
        gbc.gridy++;
        JLabel tech4 = new JLabel("• Docker + Docker Compose");
        tech4.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(tech4, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(25, 15, 5, 15);
        JLabel featuresLabel = new JLabel("✨ Features");
        featuresLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(featuresLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 15, 5, 15);
        JLabel feat1 = new JLabel("• Create bills with multiple items");
        feat1.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(feat1, gbc);
        
        gbc.gridy++;
        JLabel feat2 = new JLabel("• Apply discounts to bills");
        feat2.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(feat2, gbc);
        
        gbc.gridy++;
        JLabel feat3 = new JLabel("• View all bills in database");
        feat3.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(feat3, gbc);
        
        gbc.gridy++;
        JLabel feat4 = new JLabel("• View detailed bill information");
        feat4.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(feat4, gbc);
        
        gbc.gridy++;
        JLabel feat5 = new JLabel("• Delete bills from database");
        feat5.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(feat5, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(35, 15, 10, 15);
        JLabel infoLabel = new JLabel("<html><center>Simple and efficient billing system<br>for small businesses</center></html>");
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        panel.add(infoLabel, gbc);
        
        return panel;
    }
    
    // ==================================================================================
    // MAIN METHOD
    // ==================================================================================
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new BillingApp();
        });
    }
}