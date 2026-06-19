package org.example.dao;

import org.example.domain.Product;
import org.example.util.DatabaseConnection;
import org.example.util.FileLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 1. Fetch All Records for the UI Grid Table
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("category_id")
                ));
            }
        } catch (SQLException e) {
            FileLogger.logError("Failed to fetch all products for UI grid", e);
        }
        return list;
    }

    // 2. Add New Product Item
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (product_name, sku, price, stock_quantity, category_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getProductName());
            ps.setString(2, p.getSku());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQuantity());
            ps.setInt(5, p.getCategoryId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            FileLogger.logError("Failed to add new product record", e);
            return false;
        }
    }

    // 3. Update Existing Product Record (Shared Form Logic)
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET product_name = ?, sku = ?, price = ?, stock_quantity = ?, category_id = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getProductName());
            ps.setString(2, p.getSku());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQuantity());
            ps.setInt(5, p.getCategoryId());
            ps.setInt(6, p.getProductId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            FileLogger.logError("Failed to update product details", e);
            return false;
        }
    }

    // 4. Delete Product Item Catalog Entry
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            FileLogger.logError("Failed to delete product record", e);
            return false;
        }
    }

    // 5. REQUIREMENT 9: Multi-table Transaction Example (Sales Order Processing)
    public boolean placeOrderTransaction(int customerId, int productId, int quantity, double price) {
        Connection conn = null;
        PreparedStatement insertOrder = null;
        PreparedStatement insertDetail = null;
        PreparedStatement updateStock = null;

        String orderSQL = "INSERT INTO sales_orders (customer_id, order_date, total_amount) VALUES (?, NOW(), ?)";
        String detailSQL = "INSERT INTO sales_order_details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        String stockSQL = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction scope

            insertOrder = conn.prepareStatement(orderSQL, Statement.RETURN_GENERATED_KEYS);
            insertOrder.setInt(1, customerId);
            insertOrder.setDouble(2, (price * quantity));
            insertOrder.executeUpdate();

            ResultSet rs = insertOrder.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            insertDetail = conn.prepareStatement(detailSQL);
            insertDetail.setInt(1, orderId);
            insertDetail.setInt(2, productId);
            insertDetail.setInt(3, quantity);
            insertDetail.setDouble(4, price);
            insertDetail.executeUpdate();

            updateStock = conn.prepareStatement(stockSQL);
            updateStock.setInt(1, quantity);
            updateStock.setInt(2, productId);
            updateStock.setInt(3, quantity);
            int rowsUpdated = updateStock.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Insolvent stock inventory level limits reached!");
            }

            conn.commit(); // Lock in all changes cleanly
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException e) { FileLogger.logError("Transaction Rollback failed", e); }
            }
            FileLogger.logError("Failed executing multi-table Sales Order transaction block", ex);
            return false;
        } finally {
            try {
                if (insertOrder != null) insertOrder.close();
                if (insertDetail != null) insertDetail.close();
                if (updateStock != null) updateStock.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                FileLogger.logError("Closing connections failed during transaction cleanup", e);
            }
        }
    }

    // 6. REQUIREMENT 11: Stored Procedure execution block - View Low Stock Alerts
    public List<Product> getLowStockInventory(int threshold) {
        List<Product> criticalZone = new ArrayList<>();
        String query = "{call GetLowStockProducts(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    criticalZone.add(new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("sku"),
                            0.0,
                            rs.getInt("stock_quantity"),
                            0
                    ));
                }
            }
        } catch (SQLException e) {
            FileLogger.logError("Stored procedure GetLowStockProducts execution dropped", e);
        }
        return criticalZone;
    }

    // 7. REQUIREMENT 11: Stored Procedure execution block - Restock item
    public boolean restockProduct(int productId, int amount) {
        String query = "{call ProcessRestock(?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, amount);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            FileLogger.logError("Stored procedure ProcessRestock execution dropped", e);
            return false;
        }
    }
}