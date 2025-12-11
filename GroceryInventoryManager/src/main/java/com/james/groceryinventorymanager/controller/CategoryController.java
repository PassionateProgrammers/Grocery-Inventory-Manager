/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.controller;

import com.james.groceryinventorymanager.dao.DatabaseAdapter;
import com.james.groceryinventorymanager.model.Category;
import com.james.groceryinventorymanager.model.Inventory;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author james
 */

public class CategoryController {

    private final DatabaseAdapter db;
    private final Inventory inventory; // not used now, but available if needed later

    public CategoryController(DatabaseAdapter db, Inventory inventory) {
        this.db = db;
        this.inventory = inventory;
    }

    public List<Category> getAllCategories() {
        return db.loadCategories();
    }

    public void addCategory(String name) throws SQLException {
        Category c = new Category(0, name);
        db.insertCategory(c);
    }

    public void updateCategory(Category category, String newName) throws SQLException {
        category.setName(newName);
        db.updateCategory(category);
    }

    public void deleteCategory(Category category) throws SQLException {
        db.deleteCategory(category.getId());
    }
}

