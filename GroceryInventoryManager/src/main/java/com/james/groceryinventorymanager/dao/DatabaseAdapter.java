/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.dao;
import com.james.groceryinventorymanager.model.Category;
import com.james.groceryinventorymanager.model.FoodItem;
import com.james.groceryinventorymanager.model.Inventory;
import com.james.groceryinventorymanager.model.Location;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author james
 */

public class DatabaseAdapter {

    private final String url;

    public DatabaseAdapter(String dbFilePath) {
        // Example: "jdbc:sqlite:grocery.db"
        this.url = "jdbc:sqlite:" + dbFilePath;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Create tables if they do not exist.
     */
    public void initializeDatabase() {
        String createLocations = """
                CREATE TABLE IF NOT EXISTS locations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                );
                """;

        String createCategories = """
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                );
                """;

        String createItems = """
                CREATE TABLE IF NOT EXISTS items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    expiration_date TEXT NULL,
                    category_id INTEGER NULL,
                    location_id INTEGER NOT NULL,
                    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
                    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createLocations);
            stmt.execute(createCategories);
            stmt.execute(createItems);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // Locations
    // ==============================

    public List<Location> loadLocations() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT id, name FROM locations ORDER BY name ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                locations.add(new Location(id, name));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locations;
    }

    public Location insertLocation(Location location) throws SQLException {
        String sql = "INSERT INTO locations(name) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, location.getName());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    location.setId(id);
                }
            }
        }

        return location;
    }

    public void deleteLocation(int locationId) throws SQLException {
        String sql = "DELETE FROM locations WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, locationId);
            ps.executeUpdate();
        }
    }

    // ==============================
    // Categories
    // ==============================

    public List<Category> loadCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categories.add(new Category(id, name));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Category insertCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories(name) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    category.setId(id);
                }
            }
        }

        return category;
    }

    public void updateCategory(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getName());
            ps.setInt(2, category.getId());
            ps.executeUpdate();
        }
    }

    public void deleteCategory(int categoryId) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.executeUpdate();
        }
    }

    // ==============================
    // Items
    // ==============================

    /**
     * Load all items for a given location.
     */
    public List<FoodItem> loadItemsForLocation(Location location,
                                               List<Category> knownCategories,
                                               List<Location> knownLocations) {
        List<FoodItem> items = new ArrayList<>();

        String sql = """
                SELECT i.id, i.name, i.quantity, i.expiration_date,
                       i.category_id, i.location_id
                FROM items i
                WHERE i.location_id = ?
                ORDER BY i.name ASC
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, location.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int quantity = rs.getInt("quantity");
                    String expText = rs.getString("expiration_date");
                    Integer categoryId = rs.getObject("category_id") != null
                            ? rs.getInt("category_id")
                            : null;
                    int locationId = rs.getInt("location_id");

                    LocalDate expDate = null;
                    if (expText != null) {
                        expDate = LocalDate.parse(expText); // expects ISO yyyy-MM-dd
                    }

                    Category category = null;
                    if (categoryId != null) {
                        for (Category c : knownCategories) {
                            if (c.getId() == categoryId) {
                                category = c;
                                break;
                            }
                        }
                    }

                    Location loc = null;
                    for (Location l : knownLocations) {
                        if (l.getId() == locationId) {
                            loc = l;
                            break;
                        }
                    }

                    FoodItem item = new FoodItem(id, name, quantity, expDate, category, loc);
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public FoodItem insertItem(FoodItem item) throws SQLException {
        String sql = """
                INSERT INTO items(name, quantity, expiration_date, category_id, location_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());

            if (item.getExpirationDate() != null) {
                ps.setString(3, item.getExpirationDate().toString());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            if (item.getCategory() != null && item.getCategory().getId() != 0) {
                ps.setInt(4, item.getCategory().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(5, item.getLocation().getId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    item.setId(id);
                }
            }
        }

        return item;
    }

    public void updateItem(FoodItem item) throws SQLException {
        String sql = """
                UPDATE items
                SET name = ?, quantity = ?, expiration_date = ?, category_id = ?, location_id = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());

            if (item.getExpirationDate() != null) {
                ps.setString(3, item.getExpirationDate().toString());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            if (item.getCategory() != null && item.getCategory().getId() != 0) {
                ps.setInt(4, item.getCategory().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(5, item.getLocation().getId());
            ps.setInt(6, item.getId());

            ps.executeUpdate();
        }
    }

    public void deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ps.executeUpdate();
        }
    }
    // ==============================
    // Helper: load everything into Inventory
    // ==============================

    public void loadAllIntoInventory(Inventory inventory) {
        inventory.clear();  // this now calls Inventory.clear()

        List<Location> locations = loadLocations();
        List<Category> categories = loadCategories();

        for (Location loc : locations) {
            List<FoodItem> itemsForLoc = loadItemsForLocation(loc, categories, locations);
            for (FoodItem item : itemsForLoc) {
                inventory.addItem(item);
            }
        }
    }

}
