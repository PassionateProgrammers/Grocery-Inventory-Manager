/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.view;
import com.james.groceryinventorymanager.model.FoodItem;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author james
 */

public class FoodItemTableModel extends AbstractTableModel {

    private final String[] columns = {"Name ↕", "Category ↕", "Quantity ↕", "Expiration ↕"};
    private final List<FoodItem> items = new ArrayList<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setItems(List<FoodItem> foodItems) {
        items.clear();
        if (foodItems != null) {
            items.addAll(foodItems);
        }
        fireTableDataChanged();
    }

    public FoodItem getItemAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= items.size()) {
            return null;
        }
        return items.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FoodItem item = items.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return item.getName();
            case 1:
                return item.getCategory() != null ? item.getCategory().getName() : "";
            case 2:
                return item.getQuantity();
            case 3:
                return item.getExpirationDate() != null
                        ? item.getExpirationDate().format(dateFormatter)
                        : "";
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 2 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // read only table for now
    }
}
