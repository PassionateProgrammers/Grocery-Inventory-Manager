/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.view;

import com.james.groceryinventorymanager.controller.InventoryController;
import com.james.groceryinventorymanager.model.FoodItem;
import com.james.groceryinventorymanager.model.Location;
import com.james.groceryinventorymanager.view.GroceryListView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author james
 */
public class MainView extends JFrame {

    private final InventoryController inventoryController;

    // UI components
    private JList<Location> locationList;
    private DefaultListModel<Location> locationListModel;

    private JTable itemsTable;
    private FoodItemTableModel itemsTableModel;
    private TableRowSorter<FoodItemTableModel> sorter;

    private JTextField searchField;
    private JButton searchButton;
    private JButton clearSearchButton;

    // Freshness filter checkboxes
    private JCheckBox freshCheckBox;
    private JCheckBox expiringSoonCheckBox;
    private JCheckBox expiredCheckBox;

    private JButton addItemButton;
    private JButton editItemButton;
    private JButton deleteItemButton;
    private JButton manageLocationsButton;
    private JButton manageCategoriesButton;
    private JButton groceryListButton;

    // current state
    private Location selectedLocation;

    public MainView(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        initUi();
        loadLocations();
    }

    private void initUi() {
        setTitle("Grocery Inventory Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout());

        // Left: locations list
        locationListModel = new DefaultListModel<>();
        locationList = new JList<>(locationListModel);
        locationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane locationScroll = new JScrollPane(locationList);
        locationScroll.setBorder(BorderFactory.createTitledBorder("Locations"));

        // Center: items table
        itemsTableModel = new FoodItemTableModel();
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(24);
        itemsTable.setFillsViewportHeight(true);

        // Sorting: click headers to sort
        sorter = new TableRowSorter<>(itemsTableModel);
        itemsTable.setRowSorter(sorter);

        styleTableColumns(); // column spacing and alignment

        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setBorder(BorderFactory.createTitledBorder("Items"));

        // Top: search bar + freshness filters
        JPanel topPanel = new JPanel(new BorderLayout());

        // Search row
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear");

        searchPanel.add(new JLabel("Search by name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);

        // Freshness filter row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        freshCheckBox = new JCheckBox("Fresh", true);
        expiringSoonCheckBox = new JCheckBox("Expiring Soon", true);
        expiredCheckBox = new JCheckBox("Expired", true);

        filterPanel.add(new JLabel("Freshness:"));
        filterPanel.add(freshCheckBox);
        filterPanel.add(expiringSoonCheckBox);
        filterPanel.add(expiredCheckBox);

        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        // Bottom: item actions and other tools
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addItemButton = new JButton("Add Item");
        editItemButton = new JButton("Edit Item");
        deleteItemButton = new JButton("Delete Item");
        manageLocationsButton = new JButton("Locations...");
        manageCategoriesButton = new JButton("Categories...");
        groceryListButton = new JButton("Grocery List");

        bottomPanel.add(addItemButton);
        bottomPanel.add(editItemButton);
        bottomPanel.add(deleteItemButton);
        bottomPanel.add(manageLocationsButton);
        bottomPanel.add(manageCategoriesButton);
        bottomPanel.add(groceryListButton);

        // Put left and center into a split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                locationScroll, itemsScroll);
        splitPane.setDividerLocation(200);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Wire up listeners
        initListeners();
    }

    /**
     * Adjust column alignment and padding so Quantity and Expiration
     * do not look jammed together.
     */
    private void styleTableColumns() {
        var columnModel = itemsTable.getColumnModel();

        // Base styling: left-aligned with padding
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            var col = columnModel.getColumn(i);

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.LEFT);
            renderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // padding
            col.setCellRenderer(renderer);

            col.setPreferredWidth(150);
        }

        // Quantity column -> centered with padding
        DefaultTableCellRenderer qtyRenderer = new DefaultTableCellRenderer();
        qtyRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        qtyRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        columnModel.getColumn(2).setCellRenderer(qtyRenderer);

        // Expiration column -> centered with padding
        DefaultTableCellRenderer expRenderer = new DefaultTableCellRenderer();
        expRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        expRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        columnModel.getColumn(3).setCellRenderer(expRenderer);
    }

    private void initListeners() {
        // When user selects a location, update items with filters and search
        locationList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectedLocation = locationList.getSelectedValue();
                    applyFiltersAndSearch();
                }
            }
        });

        // Search
        searchButton.addActionListener((ActionEvent e) -> applyFiltersAndSearch());

        clearSearchButton.addActionListener((ActionEvent e) -> {
            searchField.setText("");
            applyFiltersAndSearch();
        });

        // Freshness checkboxes: reapply filters any time one is toggled
        freshCheckBox.addActionListener((ActionEvent e) -> applyFiltersAndSearch());
        expiringSoonCheckBox.addActionListener((ActionEvent e) -> applyFiltersAndSearch());
        expiredCheckBox.addActionListener((ActionEvent e) -> applyFiltersAndSearch());

        addItemButton.addActionListener((ActionEvent e) -> {
            if (selectedLocation == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a location before adding an item.");
                return;
            }

            ItemFormView dlg = new ItemFormView(this, inventoryController, selectedLocation);
            dlg.setVisible(true);

            if (dlg.wasSaved()) {
                applyFiltersAndSearch();
            }
        });

        editItemButton.addActionListener((ActionEvent e) -> {
            FoodItem selected = getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "No item selected.",
                        "Edit Item",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ItemFormView dlg = new ItemFormView(this, inventoryController, selected);
            dlg.setVisible(true);

            if (dlg.wasSaved()) {
                applyFiltersAndSearch();
            }
        });

        deleteItemButton.addActionListener((ActionEvent e) -> {
            FoodItem selected = getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "No item selected.",
                        "Delete Item",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                inventoryController.deleteItem(selected);
                applyFiltersAndSearch();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting item: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        manageLocationsButton.addActionListener((ActionEvent e) -> {
            LocationManagementView dlg =
                    new LocationManagementView(this, inventoryController.getLocationController());
            dlg.setVisible(true);

            // reload caches from DB
            inventoryController.reloadAll();
            // then repopulate the left list
            loadLocations();
        });

        manageCategoriesButton.addActionListener((ActionEvent e) -> {
            CategoryManagementView dlg =
                    new CategoryManagementView(this, inventoryController.getCategoryController());
            dlg.setVisible(true);

            // reload caches so Add/Edit item sees latest categories
            inventoryController.reloadAll();
            // refresh table so category names update for existing items
            applyFiltersAndSearch();
        });

        groceryListButton.addActionListener((ActionEvent e) -> {
            GroceryListView dlg = new GroceryListView(this);
            dlg.setVisible(true);
        });

    }

    private void loadLocations() {
        List<Location> locations = inventoryController.getAllLocations();
        locationListModel.clear();
        for (Location loc : locations) {
            locationListModel.addElement(loc);
        }

        if (!locations.isEmpty()) {
            locationList.setSelectedIndex(0);
            selectedLocation = locations.get(0);
            applyFiltersAndSearch();
        } else {
            selectedLocation = null;
            itemsTableModel.setItems(List.of());
        }
    }

    /**
     * Because we are using a TableRowSorter, the selected row index
     * is in "view" coordinates. Convert to model row before asking
     * the table model for the FoodItem.
     */
    private FoodItem getSelectedItem() {
        int viewRow = itemsTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = itemsTable.convertRowIndexToModel(viewRow);
        return itemsTableModel.getItemAt(modelRow);
    }

    /**
     * Apply freshness checkboxes and search text together.
     * If all checkboxes are off, the list is empty.
     */
    private void applyFiltersAndSearch() {
        if (selectedLocation == null) {
            itemsTableModel.setItems(List.of());
            return;
        }

        boolean includeFresh = freshCheckBox.isSelected();
        boolean includeExpiringSoon = expiringSoonCheckBox.isSelected();
        boolean includeExpired = expiredCheckBox.isSelected();

        // All filters off → empty list
        if (!includeFresh && !includeExpiringSoon && !includeExpired) {
            itemsTableModel.setItems(List.of());
            return;
        }

        // Start with freshness filtering
        List<FoodItem> items = inventoryController.filterByFreshness(
                selectedLocation,
                includeFresh,
                includeExpiringSoon,
                includeExpired
        );

        // Optional search on top
        String query = searchField.getText().trim().toLowerCase();
        if (!query.isEmpty()) {
            List<FoodItem> filteredBySearch = new ArrayList<>();
            for (FoodItem item : items) {
                if (item.getName() != null &&
                        item.getName().toLowerCase().contains(query)) {
                    filteredBySearch.add(item);
                }
            }
            items = filteredBySearch;
        }

        itemsTableModel.setItems(items);
    }
}




