/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.controller;
import com.james.groceryinventorymanager.dao.DatabaseAdapter;
import com.james.groceryinventorymanager.model.Inventory;
import com.james.groceryinventorymanager.model.Location;

import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author james
 */

public class LocationController {

    private final DatabaseAdapter db;
    private final Inventory inventory;

    public LocationController(DatabaseAdapter db, Inventory inventory) {
        this.db = db;
        this.inventory = inventory;
    }

    public List<Location> getAllLocations() {
        return db.loadLocations();
    }

    public void addLocation(String name) {
        try {
            Location loc = new Location(0, name);
            db.insertLocation(loc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLocation(Location location) {
        try {
            db.deleteLocation(location.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

