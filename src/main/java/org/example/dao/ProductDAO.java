//package org.example.dao;
//
//import org.example.domain.Product;
//import org.example.util.DatabaseConnection;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductDAO {
//
//    // Method to fetch all products from the database
//    public List<Product> getAllProducts() {
//        List<Product> products = new ArrayList<>();
//        String query = "SELECT * FROM products";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                Product product = new Product(
//                        rs.getInt("product_id"),
//                        rs.getString("product_name"),
//                        rs.getString("sku"),
//                        rs.getDouble("price"),
//                        rs.getInt("stock_quantity"),
//                        rs.getInt("category_id")
//                );
//                products.add(product);
//            }
//        } catch (SQLException e) {
//            System.err.println("❌ Error fetching products from the database!");
//            e.printStackTrace();
//        }
//        return products;
//    }
//}

package org.example.dao;

import org.example.domain.Product;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 1. READ: Fetch all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("category_id")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching products from the database!");
            e.printStackTrace();
        }
        return products;
    }

    // 2. CREATE: Insert a brand new product into the inventory
    public boolean addProduct(Product product) {
        String query = "INSERT INTO products (product_name, sku, price, stock_quantity, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getSku());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setInt(5, product.getCategoryId());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error inserting new product!");
            e.printStackTrace();
            return false;
        }
    }

    // 3. UPDATE: Modify the details/stock levels of an existing item
    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET product_name = ?, sku = ?, price = ?, stock_quantity = ?, category_id = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getSku());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setInt(5, product.getCategoryId());
            stmt.setInt(6, product.getProductId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating product ID: " + product.getProductId());
            e.printStackTrace();
            return false;
        }
    }

    // 4. DELETE: Remove an item completely from inventory records
    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting product ID: " + productId);
            e.printStackTrace();
            return false;
        }
    }
}