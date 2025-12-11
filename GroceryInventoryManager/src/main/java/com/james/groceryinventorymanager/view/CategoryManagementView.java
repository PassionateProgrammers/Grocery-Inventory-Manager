/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.view;

import com.james.groceryinventorymanager.controller.CategoryController;
import com.james.groceryinventorymanager.model.Category;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author james
 */

public class CategoryManagementView extends JDialog {

    private final CategoryController controller;

    private DefaultListModel<Category> categoryModel;
    private JList<Category> categoryList;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton closeButton;

    public CategoryManagementView(JFrame parent, CategoryController controller) {
        super(parent, "Manage Categories", true);
        this.controller = controller;

        initUi();
        loadCategories();
        setSize(400, 350);
        setLocationRelativeTo(parent);
    }

    private void initUi() {
        setLayout(new BorderLayout());

        categoryModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryModel);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(categoryList);
        scroll.setBorder(BorderFactory.createTitledBorder("Categories"));
        add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        closeButton = new JButton("Close");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        initListeners();
    }

    private void initListeners() {
        addButton.addActionListener((ActionEvent e) -> onAdd());
        editButton.addActionListener((ActionEvent e) -> onEdit());
        deleteButton.addActionListener((ActionEvent e) -> onDelete());
        closeButton.addActionListener((ActionEvent e) -> dispose());
    }

    private void loadCategories() {
        categoryModel.clear();
        List<Category> cats = controller.getAllCategories();
        for (Category c : cats) {
            categoryModel.addElement(c);
        }
    }

    private void onAdd() {
        String name = JOptionPane.showInputDialog(this,
                "Enter new category name:");
        if (name == null) {
            return; // user cancelled
        }
        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Category name cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check duplicate
        if (existsCategoryName(name)) {
            JOptionPane.showMessageDialog(this,
                    "Category name already exists.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controller.addCategory(name);
            loadCategories();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding category: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        Category selected = categoryList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "No category selected.",
                    "Edit Category",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newName = JOptionPane.showInputDialog(this,
                "Edit category name:", selected.getName());
        if (newName == null) {
            return; // cancelled
        }
        newName = newName.trim();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Category name cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newName.equals(selected.getName())) {
            return; // no change
        }

        if (existsCategoryName(newName)) {
            JOptionPane.showMessageDialog(this,
                    "Category name already exists.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controller.updateCategory(selected, newName);
            loadCategories();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating category: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Category selected = categoryList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "No category selected.",
                    "Delete Category",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Warning: All items using this category will be set to None.\nDo you wish to continue?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteCategory(selected);
            loadCategories();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting category: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean existsCategoryName(String name) {
        String lower = name.toLowerCase();
        for (int i = 0; i < categoryModel.size(); i++) {
            Category c = categoryModel.getElementAt(i);
            if (c.getName() != null && c.getName().toLowerCase().equals(lower)) {
                return true;
            }
        }
        return false;
    }
}

