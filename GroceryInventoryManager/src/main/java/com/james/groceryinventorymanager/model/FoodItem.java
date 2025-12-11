/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.model;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author james
 */

public class FoodItem {

    private int id; // database id, 0 if not yet persisted
    private String name;
    private int quantity;
    private LocalDate expirationDate; // can be null
    private Category category;        // can be null for "None"
    private Location location;        // should not be null

    public FoodItem(int id,
                    String name,
                    int quantity,
                    LocalDate expirationDate,
                    Category category,
                    Location location) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.category = category;
        this.location = location;
    }

    public FoodItem(String name,
                    int quantity,
                    LocalDate expirationDate,
                    Category category,
                    Location location) {
        this(0, name, quantity, expirationDate, category, location);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodItem)) return false;
        FoodItem foodItem = (FoodItem) o;
        // if id is set, use id for equality
        if (this.id != 0 && foodItem.id != 0) {
            return this.id == foodItem.id;
        }
        // fallback for non persisted items
        return Objects.equals(name, foodItem.name)
                && quantity == foodItem.quantity
                && Objects.equals(expirationDate, foodItem.expirationDate)
                && Objects.equals(category, foodItem.category)
                && Objects.equals(location, foodItem.location);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(name, quantity, expirationDate, category, location);
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", expirationDate=" + expirationDate +
                ", category=" + (category != null ? category.getName() : "None") +
                ", location=" + (location != null ? location.getName() : "None") +
                '}';
    }
}

