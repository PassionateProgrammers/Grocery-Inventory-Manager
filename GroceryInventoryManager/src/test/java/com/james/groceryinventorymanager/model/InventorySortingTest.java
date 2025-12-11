/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.model;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author james
 */

public class InventorySortingTest {

    @Test
    public void testSortByNameAscendingAndDescending() {
        Inventory inventory = new Inventory();
        Location pantry = new Location(1, "Pantry");

        FoodItem c = new FoodItem(0, "Carrots", 3, null, null, pantry);
        FoodItem a = new FoodItem(0, "Apples", 5, null, null, pantry);
        FoodItem b = new FoodItem(0, "Bananas", 2, null, null, pantry);

        inventory.addItem(c);
        inventory.addItem(a);
        inventory.addItem(b);

        List<FoodItem> source = Arrays.asList(c, a, b);

        List<FoodItem> ascending = inventory.sortByName(source, true);
        assertEquals(Arrays.asList(a, b, c), ascending, "Sort by name ascending should be A, B, C");

        List<FoodItem> descending = inventory.sortByName(source, false);
        assertEquals(Arrays.asList(c, b, a), descending, "Sort by name descending should be C, B, A");
    }

    @Test
    public void testSortByCategoryName() {
        Inventory inventory = new Inventory();
        Location pantry = new Location(1, "Pantry");

        Category dairy = new Category(1, "Dairy");
        Category canned = new Category(2, "Canned");
        Category snacks = new Category(3, "Snacks");

        FoodItem milk = new FoodItem(0, "Milk", 1, null, dairy, pantry);
        FoodItem beans = new FoodItem(0, "Beans", 4, null, canned, pantry);
        FoodItem chips = new FoodItem(0, "Chips", 2, null, snacks, pantry);

        inventory.addItem(milk);
        inventory.addItem(beans);
        inventory.addItem(chips);

        List<FoodItem> source = Arrays.asList(milk, beans, chips);

        List<FoodItem> ascending = inventory.sortByCategory(source, true);
        // Alphabetical by category name: "Canned", "Dairy", "Snacks"
        assertEquals(Arrays.asList(beans, milk, chips), ascending);

        List<FoodItem> descending = inventory.sortByCategory(source, false);
        assertEquals(Arrays.asList(chips, milk, beans), descending);
    }

    @Test
    public void testSortByQuantity() {
        Inventory inventory = new Inventory();
        Location pantry = new Location(1, "Pantry");

        FoodItem low = new FoodItem(0, "Low", 1, null, null, pantry);
        FoodItem mid = new FoodItem(0, "Mid", 5, null, null, pantry);
        FoodItem high = new FoodItem(0, "High", 10, null, null, pantry);

        inventory.addItem(low);
        inventory.addItem(mid);
        inventory.addItem(high);

        List<FoodItem> source = Arrays.asList(mid, high, low);

        List<FoodItem> ascending = inventory.sortByQuantity(source, true);
        assertEquals(Arrays.asList(low, mid, high), ascending, "Ascending should be 1, 5, 10");

        List<FoodItem> descending = inventory.sortByQuantity(source, false);
        assertEquals(Arrays.asList(high, mid, low), descending, "Descending should be 10, 5, 1");
    }
}
