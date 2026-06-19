//package org.example;
//
////TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
//// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//public class Main {
//    static void main() {
//        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//        // to see how IntelliJ IDEA suggests fixing it.
//        IO.println(String.format("Hello and welcome!"));
//
//        for (int i = 1; i <= 5; i++) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            IO.println("i = " + i);
//        }
//    }
//}
//package org.example;
//
//import org.example.util.DatabaseConnection;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//public class Main {
//    public static void main(String[] args) {
//        System.out.println("🚀 Starting Wholesale Inventory System Integration Test...");
//
//        try {
//            // Attempt to establish a connection using our utility class
//            Connection conn = DatabaseConnection.getConnection();
//
//            if (conn != null && !conn.isClosed()) {
//                System.out.println("\n🎉 SUCCESS! Your Java application is now directly linked with WholesaleInventoryDB.");
//                System.out.println("📋 Connection details: " + conn.toString());
//            }
//
//        } catch (SQLException e) {
//            System.err.println("\n❌ CONNECTION FAILED!");
//            System.err.println("Please check if your MySQL Server is running and your root password is correct.");
//            e.printStackTrace();
//        } finally {
//            // Clean up and close connection
//            DatabaseConnection.closeConnection();
//        }
//    }
//}


//package org.example;
//
//import org.example.dao.ProductDAO;
//import org.example.domain.Product;
//import java.util.List;
//
//public class Main {
//    public static void main(String[] args) {
//        System.out.println("🚀 Querying Inventory Records via ProductDAO...\n");
//
//        ProductDAO productDAO = new ProductDAO();
//        List<Product> productList = productDAO.getAllProducts();
//
//        if (productList.isEmpty()) {
//            System.out.println("ℹ️ The database connection works, but your 'products' table is currently empty.");
//            System.out.println("   Go ahead and run some INSERT statements in MySQL Workbench if you want to see records here!");
//        } else {
//            System.out.println("📦 --- CURRENT PRODUCT INVENTORY ---");
//            System.out.printf("%-5s | %-25s | %-12s | %-10s | %-8s\n", "ID", "Product Name", "SKU", "Price", "Stock");
//            System.out.println("-------------------------------------------------------------------------");
//
//            for (Product p : productList) {
//                System.out.printf("%-5d | %-25s | %-12s | Rs.%-8.2f | %-8d\n",
//                        p.getProductId(),
//                        p.getProductName(),
//                        p.getSku(),
//                        p.getPrice(),
//                        p.getStockQuantity()
//                );
//            }
//            System.out.println("-------------------------------------------------------------------------");
//            System.out.println("🎉 Data extraction test complete!");
//        }
//    }
//}

package org.example;

import org.example.dao.ProductDAO;
import org.example.domain.Product;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final ProductDAO productDAO = new ProductDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   🏬 WHOLESALE INVENTORY MANAGEMENT SYSTEM 🏬   ");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View All Products");
            System.out.println("2. Add New Product");
            System.out.println("3. Update Product Stock/Price");
            System.out.println("4. Remove Product");
            System.out.println("5. Exit System");
            System.out.print("👉 Enter your choice (1-5): ");

            int choice = readIntegerInput();

            switch (choice) {
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    handleAddNewProduct();
                    break;
                case 3:
                    handleUpdateProduct();
                    break;
                case 4:
                    handleDeleteProduct();
                    break;
                case 5:
                    System.out.println("\n🔌 Shutting down system. Connection closed cleanly. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid option! Please select a choice between 1 and 5.");
            }
        }
        scanner.close();
    }

    // 1. VIEW ALL PRODUCTS
    private static void displayAllProducts() {
        System.out.println("\n📦 --- CURRENT PRODUCT INVENTORY ---");
        List<Product> products = productDAO.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("ℹ️ No inventory items found in the database.");
            return;
        }

        System.out.printf("%-5s | %-25s | %-12s | %-10s | %-8s\n", "ID", "Product Name", "SKU", "Price", "Stock");
        System.out.println("-------------------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d | %-25s | %-12s | Rs.%-8.2f | %-8d\n",
                    p.getProductId(), p.getProductName(), p.getSku(), p.getPrice(), p.getStockQuantity());
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    // 2. ADD NEW PRODUCT
    private static void handleAddNewProduct() {
        System.out.println("\n➕ --- REGISTER NEW PRODUCT ---");

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Unique SKU: ");
        String sku = scanner.nextLine();

        System.out.print("Enter Unit Price (Rs.): ");
        double price = readDoubleInput();

        System.out.print("Enter Initial Stock Quantity: ");
        int stock = readIntegerInput();

        System.out.print("Enter Category ID (e.g., 1 for Electronics, 2 for Groceries): ");
        int categoryId = readIntegerInput();

        Product newProduct = new Product(0, name, sku, price, stock, categoryId);
        if (productDAO.addProduct(newProduct)) {
            System.out.println("🎉 SUCCESS: Product successfully added to active inventory!");
        } else {
            System.out.println("❌ FAILURE: Unable to add product. Verify SKU uniqueness.");
        }
    }

    // 3. UPDATE PRODUCT RECORD
    private static void handleUpdateProduct() {
        System.out.println("\n🔄 --- UPDATE PRODUCT DETAILS ---");
        System.out.print("Enter the Product ID you wish to modify: ");
        int id = readIntegerInput();

        System.out.print("Enter New Product Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter New SKU: ");
        String sku = scanner.nextLine();

        System.out.print("Enter New Unit Price (Rs.): ");
        double price = readDoubleInput();

        System.out.print("Enter New Stock Quantity: ");
        int stock = readIntegerInput();

        System.out.print("Enter New Category ID: ");
        int categoryId = readIntegerInput();

        Product updatedProduct = new Product(id, name, sku, price, stock, categoryId);
        if (productDAO.updateProduct(updatedProduct)) {
            System.out.println("🎉 SUCCESS: Inventory changes saved successfully!");
        } else {
            System.out.println("❌ FAILURE: Could not update product. Check if the ID exists.");
        }
    }

    // 4. REMOVE PRODUCT
    private static void handleDeleteProduct() {
        System.out.println("\n❌ --- REMOVE ITEM FROM RECORDS ---");
        System.out.print("Enter the Product ID to delete: ");
        int id = readIntegerInput();

        System.out.print("⚠️ Are you absolutely sure? This action cannot be undone. (Y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("y")) {
            if (productDAO.deleteProduct(id)) {
                System.out.println("🎉 SUCCESS: Item wiped completely from the inventory database.");
            } else {
                System.out.println("❌ FAILURE: Item could not be deleted. Check if ID exists or is locked by foreign keys.");
            }
        } else {
            System.out.println("ℹ️ Operation canceled by user.");
        }
    }

    // --- HELPER UTILITIES TO PREVENT SCANNER CRASHES ---
    private static int readIntegerInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("❌ Invalid type! Please enter a valid integer number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        return value;
    }

    private static double readDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.print("❌ Invalid type! Please enter a numerical price: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // Clear buffer
        return value;
    }
}