/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.model;
import com.james.groceryinventorymanager.model.Location;
import com.james.groceryinventorymanager.model.Category;
import com.james.groceryinventorymanager.model.FoodItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author james
 */

public class Inventory {

    private final List<FoodItem> items = new ArrayList<>();
    
    public void clear() {
        items.clear();
    }

    public void addItem(FoodItem item) {
        items.add(item);
    }

    public void removeItem(FoodItem item) {
        items.remove(item);
    }

    public void removeItemById(int id) {
        items.removeIf(i -> i.getId() == id);
    }

    public List<FoodItem> getAllItems() {
        return new ArrayList<>(items);
    }

    public List<FoodItem> getItemsByLocation(Location location) {
        return items.stream()
                .filter(i -> i.getLocation() != null && i.getLocation().equals(location))
                .collect(Collectors.toList());
    }

    public List<FoodItem> getItemsByCategory(Category category) {
        return items.stream()
                .filter(i -> i.getCategory() != null && i.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<FoodItem> searchByName(String query) {
        String lower = query.toLowerCase();
        return items.stream()
                .filter(i -> i.getName() != null && i.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * Filter items by freshness rules.
     *
     * includeFresh: expiration_date is null or > today + 30 days
     * includeExpiringSoon: expiration_date > today and <= today + 30 days
     * includeExpired: expiration_date <= today
     */
    public List<FoodItem> filterByFreshness(boolean includeFresh,
                                            boolean includeExpiringSoon,
                                            boolean includeExpired) {
        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        return items.stream()
                .filter(item -> {
                    LocalDate exp = item.getExpirationDate();

                    // Treat null expiration as Fresh
                    if (exp == null) {
                        return includeFresh;
                    }

                    boolean isExpired = !exp.isAfter(today); // exp <= today
                    boolean isExpiringSoon = exp.isAfter(today) && !exp.isAfter(in30Days);
                    boolean isFreshItem = exp.isAfter(in30Days);

                    return (includeFresh && isFreshItem)
                            || (includeExpiringSoon && isExpiringSoon)
                            || (includeExpired && isExpired);
                })
                .collect(Collectors.toList());
    }

    public List<FoodItem> sortByName(List<FoodItem> source, boolean ascending) {
        List<FoodItem> copy = new ArrayList<>(source);
        Comparator<FoodItem> cmp = Comparator.comparing(FoodItem::getName,
                String.CASE_INSENSITIVE_ORDER);
        if (!ascending) {
            cmp = cmp.reversed();
        }
        copy.sort(cmp);
        return copy;
    }

    public List<FoodItem> sortByCategory(List<FoodItem> source, boolean ascending) {
        List<FoodItem> copy = new ArrayList<>(source);
        Comparator<FoodItem> cmp = Comparator.comparing(
                item -> item.getCategory() != null ? item.getCategory().getName() : "",
                String.CASE_INSENSITIVE_ORDER
        );
        if (!ascending) {
            cmp = cmp.reversed();
        }
        copy.sort(cmp);
        return copy;
    }

    public List<FoodItem> sortByQuantity(List<FoodItem> source, boolean ascending) {
        List<FoodItem> copy = new ArrayList<>(source);
        Comparator<FoodItem> cmp = Comparator.comparingInt(FoodItem::getQuantity);
        if (!ascending) {
            cmp = cmp.reversed();
        }
        copy.sort(cmp);
        return copy;
    }
}
