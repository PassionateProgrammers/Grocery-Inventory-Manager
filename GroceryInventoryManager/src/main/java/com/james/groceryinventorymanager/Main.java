/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.james.groceryinventorymanager;
import com.james.groceryinventorymanager.dao.DatabaseAdapter;
import com.james.groceryinventorymanager.model.Inventory;
import com.james.groceryinventorymanager.controller.InventoryController;
import com.james.groceryinventorymanager.view.MainView;

import javax.swing.SwingUtilities;

/**
 *
 * @author james
 */

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String dbFilePath = "grocery.db";

            DatabaseAdapter dbAdapter = new DatabaseAdapter(dbFilePath);
            dbAdapter.initializeDatabase();

            Inventory inventory = new Inventory();
            // controller will load everything
            InventoryController inventoryController = new InventoryController(inventory, dbAdapter);

            MainView mainView = new MainView(inventoryController);
            mainView.setVisible(true);
        });
    }
}
