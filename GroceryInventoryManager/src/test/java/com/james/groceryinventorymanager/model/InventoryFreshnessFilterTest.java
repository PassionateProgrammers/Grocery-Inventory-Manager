/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author james
 */

public class InventoryFreshnessFilterTest {

    @Test
    public void testFreshIncludesNullExpiration() {
        Inventory inventory = new Inventory();
        Location pantry = new Location(1, "Pantry");
        Category canned = new Category(1, "Canned");

        // Null expiration - should be treated as "fresh"
        FoodItem noDateItem = new FoodItem(0, "Beans (no date)", 2, null, canned, pantry);

        // Far future date - more than 30 days from now
        LocalDate future = LocalDate.now().plusDays(90);
        FoodItem futureItem = new FoodItem(0, "Pasta 90d", 1, future, canned, pantry);

        // Expiring soon - within 30 days
        LocalDate soon = LocalDate.now().plusDays(10);
        FoodItem soonItem = new FoodItem(0, "Milk 10d", 1, soon, canned, pantry);

        // Expired - before today
        LocalDate past = LocalDate.now().minusDays(1);
        FoodItem expiredItem = new FoodItem(0, "Old bread", 1, past, canned, pantry);

        inventory.addItem(noDateItem);
        inventory.addItem(futureItem);
        inventory.addItem(soonItem);
        inventory.addItem(expiredItem);

        // Fresh only - should include null expiration and > 30 days
        List<FoodItem> freshOnly = inventory.filterByFreshness(true, false, false);
        assertTrue(freshOnly.contains(noDateItem), "Null expiration should be counted as fresh");
        assertTrue(freshOnly.contains(futureItem), "Future item > 30 days should be fresh");
        assertFalse(freshOnly.contains(soonItem), "Soon item should not be in fresh only");
        assertFalse(freshOnly.contains(expiredItem), "Expired item should not be in fresh only");

        // Expiring soon only
        List<FoodItem> soonOnly = inventory.filterByFreshness(false, true, false);
        assertTrue(soonOnly.contains(soonItem), "Item within 30 days should be expiring soon");
        assertFalse(soonOnly.contains(futureItem));
        assertFalse(soonOnly.contains(expiredItem));
        assertFalse(soonOnly.contains(noDateItem));

        // Expired only
        List<FoodItem> expiredOnly = inventory.filterByFreshness(false, false, true);
        assertTrue(expiredOnly.contains(expiredItem), "Past date should be expired");
        assertFalse(expiredOnly.contains(soonItem));
        assertFalse(expiredOnly.contains(futureItem));
        assertFalse(expiredOnly.contains(noDateItem));

        // All filters on - everything should appear
        List<FoodItem> all = inventory.filterByFreshness(true, true, true);
        assertEquals(4, all.size(), "All four items should be included when all filters are on");
    }
}

