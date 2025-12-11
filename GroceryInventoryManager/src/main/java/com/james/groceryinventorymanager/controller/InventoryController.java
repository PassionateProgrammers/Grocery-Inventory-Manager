/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.controller;

import com.james.groceryinventorymanager.dao.DatabaseAdapter;
import com.james.groceryinventorymanager.model.Category;
import com.james.groceryinventorymanager.model.FoodItem;
import com.james.groceryinventorymanager.model.Inventory;
import com.james.groceryinventorymanager.model.Location;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author james
 */

public class InventoryController {

    private final Inventory inventory;
    private final DatabaseAdapter dbAdapter;

    private final LocationController locationController;
    private final CategoryController categoryController;

    private List<Location> locationsCache = new ArrayList<>();
    private List<Category> categoriesCache = new ArrayList<>();

    public InventoryController(Inventory inventory, DatabaseAdapter dbAdapter) {
        this.inventory = inventory;
        this.dbAdapter = dbAdapter;

        this.locationController = new LocationController(dbAdapter, inventory);
        this.categoryController = new CategoryController(dbAdapter, inventory);

        reloadAll();
    }

    public LocationController getLocationController() {
        return locationController;
    }

    public CategoryController getCategoryController() {
        return categoryController;
    }

    public void reloadAll() {
        locationsCache = dbAdapter.loadLocations();
        categoriesCache = dbAdapter.loadCategories();
        dbAdapter.loadAllIntoInventory(inventory);
    }

    public List<Location> getAllLocations() {
        return new ArrayList<>(locationsCache);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categoriesCache);
    }

    public List<FoodItem> getItemsForLocation(Location location) {
        return inventory.getItemsByLocation(location);
    }

    public List<FoodItem> searchItemsByName(Location location, String query) {
        List<FoodItem> itemsInLoc = inventory.getItemsByLocation(location);
        String lower = query.toLowerCase();
        List<FoodItem> result = new ArrayList<>();
        for (FoodItem item : itemsInLoc) {
            if (item.getName() != null && item.getName().toLowerCase().contains(lower)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<FoodItem> filterByFreshness(Location location,
                                            boolean includeFresh,
                                            boolean includeExpiringSoon,
                                            boolean includeExpired) {
        List<FoodItem> allFiltered =
                inventory.filterByFreshness(includeFresh, includeExpiringSoon, includeExpired);

        List<FoodItem> result = new ArrayList<>();
        for (FoodItem item : allFiltered) {
            if (item.getLocation() != null && item.getLocation().equals(location)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<FoodItem> sortByName(List<FoodItem> source, boolean ascending) {
        return inventory.sortByName(source, ascending);
    }

    public List<FoodItem> sortByCategory(List<FoodItem> source, boolean ascending) {
        return inventory.sortByCategory(source, ascending);
    }

    public List<FoodItem> sortByQuantity(List<FoodItem> source, boolean ascending) {
        return inventory.sortByQuantity(source, ascending);
    }

    public FoodItem addItem(String name,
                            int quantity,
                            LocalDate expiration,
                            Category category,
                            Location location) throws SQLException {
        FoodItem item = new FoodItem(name, quantity, expiration, category, location);
        dbAdapter.insertItem(item);
        inventory.addItem(item);
        return item;
    }

    public void updateItem(FoodItem item,
                           String name,
                           int quantity,
                           LocalDate expiration,
                           Category category,
                           Location location) throws SQLException {

        item.setName(name);
        item.setQuantity(quantity);
        item.setExpirationDate(expiration);
        item.setCategory(category);
        item.setLocation(location);

        dbAdapter.updateItem(item);
    }

    public void deleteItem(FoodItem item) throws SQLException {
        dbAdapter.deleteItem(item.getId());
        inventory.removeItem(item);
    }
}


