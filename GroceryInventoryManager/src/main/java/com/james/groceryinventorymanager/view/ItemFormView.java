/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.view;

import com.james.groceryinventorymanager.controller.InventoryController;
import com.james.groceryinventorymanager.model.Category;
import com.james.groceryinventorymanager.model.FoodItem;
import com.james.groceryinventorymanager.model.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 *
 * @author james
 */

public class ItemFormView extends JDialog {

    private final InventoryController controller;

    private JTextField nameField;
    private JSpinner quantitySpinner;
    private JComboBox<Location> locationCombo;
    private JComboBox<Category> categoryCombo;
    private JTextField expirationField; // yyyy-MM-dd or empty

    private JButton saveButton;
    private JButton cancelButton;

    private boolean saved = false;

    // edit support
    private final boolean editMode;
    private final FoodItem itemToEdit;

    // Add mode constructor (used for Add Item)
    public ItemFormView(JFrame parent,
                        InventoryController controller,
                        Location preselectedLocation) {
        this(parent, controller, preselectedLocation, null);
    }

    // Edit mode convenience constructor (used for Edit Item)
    public ItemFormView(JFrame parent,
                        InventoryController controller,
                        FoodItem itemToEdit) {
        this(parent, controller,
                itemToEdit != null ? itemToEdit.getLocation() : null,
                itemToEdit);
    }

    // Internal constructor used by both add and edit
    private ItemFormView(JFrame parent,
                         InventoryController controller,
                         Location preselectedLocation,
                         FoodItem itemToEdit) {
        super(parent,
                itemToEdit == null ? "Add Food Item" : "Edit Food Item",
                true);
        this.controller = controller;
        this.itemToEdit = itemToEdit;
        this.editMode = (itemToEdit != null);

        initUi();
        loadLocationsAndCategories(preselectedLocation);
        if (editMode) {
            populateFieldsFromItem();
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUi() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Name label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Name:"), gbc);

        // Name field
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        row++;

        // Quantity label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Quantity:"), gbc);

        // Quantity spinner
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        formPanel.add(quantitySpinner, gbc);
        row++;

        // Location label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Location:"), gbc);

        // Location combo
        locationCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        formPanel.add(locationCombo, gbc);
        row++;

        // Category label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Category (optional):"), gbc);

        // Category combo
        categoryCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        formPanel.add(categoryCombo, gbc);
        row++;

        // Expiration label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Expiration (YYYY-MM-DD, optional):"), gbc);

        // Expiration field
        expirationField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        formPanel.add(expirationField, gbc);
        row++;

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton(editMode ? "Save Changes" : "Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        initListeners();
    }

    private void loadLocationsAndCategories(Location preselectedLocation) {
        // Locations
        List<Location> locations = controller.getAllLocations();
        DefaultComboBoxModel<Location> locModel = new DefaultComboBoxModel<>();
        for (Location loc : locations) {
            locModel.addElement(loc);
        }
        locationCombo.setModel(locModel);

        if (preselectedLocation != null) {
            locationCombo.setSelectedItem(preselectedLocation);
        } else if (locations.size() > 0) {
            locationCombo.setSelectedIndex(0);
        }

        // Categories (null/first entry means "no category")
        List<Category> categories = controller.getAllCategories();
        DefaultComboBoxModel<Category> catModel = new DefaultComboBoxModel<>();
        catModel.addElement(null); // represents "None"
        for (Category cat : categories) {
            catModel.addElement(cat);
        }
        categoryCombo.setModel(catModel);

        // If editing and there is a category, select it
        if (editMode && itemToEdit != null && itemToEdit.getCategory() != null) {
            categoryCombo.setSelectedItem(itemToEdit.getCategory());
        } else {
            categoryCombo.setSelectedIndex(0);
        }
    }

    private void populateFieldsFromItem() {
        if (itemToEdit == null) {
            return;
        }

        nameField.setText(itemToEdit.getName());
        quantitySpinner.setValue(itemToEdit.getQuantity());

        if (itemToEdit.getExpirationDate() != null) {
            expirationField.setText(itemToEdit.getExpirationDate().toString());
        } else {
            expirationField.setText("");
        }
    }

    private void initListeners() {
        saveButton.addActionListener((ActionEvent e) -> onSave());
        cancelButton.addActionListener((ActionEvent e) -> onCancel());
    }

    private void onSave() {
        String name = nameField.getText().trim();
        int quantity = (Integer) quantitySpinner.getValue();
        Location location = (Location) locationCombo.getSelectedItem();
        Category category = (Category) categoryCombo.getSelectedItem();

        if (name.isEmpty() || location == null || quantity <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Name, location, and quantity are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String expText = expirationField.getText().trim();
        LocalDate expiration = null;
        if (!expText.isEmpty()) {
            try {
                expiration = LocalDate.parse(expText);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Expiration date must be in format YYYY-MM-DD.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            if (editMode && itemToEdit != null) {
                controller.updateItem(itemToEdit, name, quantity, expiration, category, location);
            } else {
                controller.addItem(name, quantity, expiration, category, location);
            }
            saved = true;
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving item: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        saved = false;
        dispose();
    }

    public boolean wasSaved() {
        return saved;
    }
}
