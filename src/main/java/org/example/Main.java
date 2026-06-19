package org.example;

import org.example.dao.ProductDAO;
import org.example.domain.Product;
import org.example.report.PDFReportEngine;
import org.example.util.DataValidator;
import org.example.util.FileLogger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Main extends JFrame {
    private final ProductDAO productDAO = new ProductDAO();
    private JTable productTable;
    private DefaultTableModel tableModel;

    // UI Input Components
    private JTextField txtId, txtName, txtSku, txtPrice, txtStock;
    private JPasswordField txtFormPassCode;
    private JComboBox<String> dropdownCategory;
    private JRadioButton radioActiveStatus, radioInactiveStatus;
    private JCheckBox chkTermsAgreed;
    private JTextArea txtNotesArea;

    public Main() {
        setTitle("Wholesale System - Registration ID Configuration: 2025cys210");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. File Menu Bar Setup
        JMenuBar menuBar = new JMenuBar();
        JMenu systemMenu = new JMenu("System Options");
        JMenuItem printPdfReport = new JMenuItem("Export Inventory Report via Print Canvas");
        JMenuItem appClose = new JMenuItem("Exit Application Link");

        printPdfReport.addActionListener(e -> triggerNativePrintReport());
        appClose.addActionListener(e -> System.exit(0));
        systemMenu.add(printPdfReport);
        systemMenu.addSeparator();
        systemMenu.add(appClose);
        menuBar.add(systemMenu);
        setJMenuBar(menuBar);

        // Responsive Parent Layout
        setLayout(new BorderLayout(15, 15));

        // Center Panel: Data Table Grid View
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Warehouse Active Stock Registry View"));
        String[] columns = {"Product ID", "Product Name", "SKU Barcode", "Unit Price", "Warehouse Stock"};
        tableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollTablePane = new JScrollPane(productTable);
        centerPanel.add(scrollTablePane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // East Panel: Input Form Workspace
        JPanel eastFormPanel = new JPanel();
        eastFormPanel.setLayout(new BoxLayout(eastFormPanel, BoxLayout.Y_AXIS));
        eastFormPanel.setBorder(BorderFactory.createTitledBorder("Dynamic Transaction Processing Form"));
        eastFormPanel.setPreferredSize(new Dimension(380, 600));

        // Form Fields Grid Grid Layout
        JPanel gridFields = new JPanel(new GridLayout(9, 2, 5, 8));

        gridFields.add(new JLabel("Product ID (Read-Only):"));
        txtId = new JTextField(); txtId.setEditable(false); gridFields.add(txtId);

        gridFields.add(new JLabel("Product Name:"));
        txtName = new JTextField(); gridFields.add(txtName);

        gridFields.add(new JLabel("Unique SKU String:"));
        txtSku = new JTextField(); gridFields.add(txtSku);

        gridFields.add(new JLabel("Unit Price (Rs.):"));
        txtPrice = new JTextField(); gridFields.add(txtPrice);

        gridFields.add(new JLabel("Stock Quantity Count:"));
        txtStock = new JTextField(); gridFields.add(txtStock);

        gridFields.add(new JLabel("System Security Token:"));
        txtFormPassCode = new JPasswordField(); gridFields.add(txtFormPassCode);

        gridFields.add(new JLabel("Category Type Selection:"));
        dropdownCategory = new JComboBox<>(new String[]{"1 - Electronics", "2 - Mechanical components", "3 - Network Equipment"});
        gridFields.add(dropdownCategory);

        gridFields.add(new JLabel("Warehouse Status:"));
        JPanel radioGroupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioActiveStatus = new JRadioButton("In-Stock", true);
        radioInactiveStatus = new JRadioButton("Discontinued", false);
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(radioActiveStatus); statusGroup.add(radioInactiveStatus);
        radioGroupPanel.add(radioActiveStatus); radioGroupPanel.add(radioInactiveStatus);
        gridFields.add(radioGroupPanel);

        gridFields.add(new JLabel("Audit Verification:"));
        chkTermsAgreed = new JCheckBox("Verify Entry Metrics", false);
        gridFields.add(chkTermsAgreed);

        eastFormPanel.add(gridFields);

        // Notes Area Layout
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("Supplier Shipping Manifest Notes"));
        txtNotesArea = new JTextArea(4, 20);
        txtNotesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(txtNotesArea);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        eastFormPanel.add(notesPanel);

        // Action Buttons Setup (Save, Delete, Clear)
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("💾 Save Entry");
        JButton btnDelete = new JButton("❌ Delete Item");
        JButton btnClear = new JButton("🧹 Clear");

        // Color code delete button for safe visual warning
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);

        actionButtonPanel.add(btnSave);
        actionButtonPanel.add(btnDelete);
        actionButtonPanel.add(btnClear);
        eastFormPanel.add(actionButtonPanel);

        add(eastFormPanel, BorderLayout.EAST);

        // Event Listeners
        productTable.getSelectionModel().addListSelectionListener(e -> fillFormFieldsFromSelectedGridRow());
        btnSave.addActionListener(e -> processDatabaseWriteAction());
        btnClear.addActionListener(e -> resetInputFieldsToBlank());

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a product row from the table first to delete.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you absolutely sure you want to delete this product catalog item permanently?",
                    "Confirm Deletion Scope",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int targetId = Integer.parseInt(txtId.getText());
                    boolean successfulDeleted = productDAO.deleteProduct(targetId);

                    if (successfulDeleted) {
                        JOptionPane.showMessageDialog(this, "🎉 Item successfully dropped from database inventory maps.");
                        resetInputFieldsToBlank();
                        refreshTableDataGrid();
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ SQL Operation dropped. Verify database constraint connections.", "Execution Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    FileLogger.logError("Error executing delete callback context logic", ex);
                }
            }
        });

        refreshTableDataGrid();
    }

    private void refreshTableDataGrid() {
        tableModel.setRowCount(0);
        try {
            List<Product> list = productDAO.getAllProducts();
            for (Product p : list) {
                tableModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getSku(), p.getPrice(), p.getStockQuantity()});
            }
        } catch (Exception ex) {
            FileLogger.logError("Failed rendering matching grid values", ex);
        }
    }

    private void fillFormFieldsFromSelectedGridRow() {
        int activeRow = productTable.getSelectedRow();
        if (activeRow >= 0) {
            txtId.setText(tableModel.getValueAt(activeRow, 0).toString());
            txtName.setText(tableModel.getValueAt(activeRow, 1).toString());
            txtSku.setText(tableModel.getValueAt(activeRow, 2).toString());
            txtPrice.setText(tableModel.getValueAt(activeRow, 3).toString());
            txtStock.setText(tableModel.getValueAt(activeRow, 4).toString());
            chkTermsAgreed.setSelected(true);
        }
    }

    private void processDatabaseWriteAction() {
        if (!chkTermsAgreed.isSelected()) {
            JOptionPane.showMessageDialog(this, "⚠️ Validation Error: You must check the Audit Verification box before saving entries.", "Compliance Guard Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!DataValidator.isNotEmpty(txtName.getText()) || !DataValidator.isNotEmpty(txtSku.getText())) {
            JOptionPane.showMessageDialog(this, "❌ Core input fields cannot be left empty.", "Formatting Warning", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String name = txtName.getText();
            String sku = txtSku.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());
            int categoryId = dropdownCategory.getSelectedIndex() + 1;

            if (!DataValidator.isPositive(price) || !DataValidator.isPositive(stock)) {
                JOptionPane.showMessageDialog(this, "❌ Value Error: Unit Prices and Stock values cannot be negative.", "Bound Constraint Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (txtId.getText().isEmpty()) {
                productDAO.addProduct(new Product(0, name, sku, price, stock, categoryId));
                JOptionPane.showMessageDialog(this, "🎉 Success: New item registered smoothly inside data catalog.");
            } else {
                int existingId = Integer.parseInt(txtId.getText());
                productDAO.updateProduct(new Product(existingId, name, sku, price, stock, categoryId));
                JOptionPane.showMessageDialog(this, "🎉 Success: Adjustments overwritten and saved into target entry row.");
            }

            resetInputFieldsToBlank();
            refreshTableDataGrid();

        } catch (NumberFormatException ex) {
            FileLogger.logError("Failed to parse field variables to valid numeric integers", ex);
            JOptionPane.showMessageDialog(this, "❌ Form Field parsing mismatch exception. Please check numeric data entries.", "Parsing Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void triggerNativePrintReport() {
        try {
            List<Product> currentDataList = productDAO.getAllProducts();
            PDFReportEngine.generateInventoryReport(currentDataList, "Warehouse_Asset_Audit_Manifest.txt", "Master Stock Parameters");
            JOptionPane.showMessageDialog(this, "🎉 Complete Report generated successfully. Select 'Print to PDF' options to output report formats natively.");
        } catch (Exception ex) {
            FileLogger.logError("System printed output canvas initialization error context failed", ex);
        }
    }

    private void resetInputFieldsToBlank() {
        txtId.setText(""); txtName.setText(""); txtSku.setText(""); txtPrice.setText(""); txtStock.setText("");
        txtFormPassCode.setText(""); txtNotesArea.setText(""); chkTermsAgreed.setSelected(false);
        productTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}