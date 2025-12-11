/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.view;
import com.james.groceryinventorymanager.controller.LocationController;
import com.james.groceryinventorymanager.model.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
/**
 *
 * @author james
 */

public class LocationManagementView extends JDialog {

    private final LocationController controller;
    private DefaultListModel<Location> locationModel;
    private JList<Location> locationList;

    private JButton addButton;
    private JButton deleteButton;
    private JButton closeButton;

    public LocationManagementView(JFrame parent, LocationController controller) {
        super(parent, "Manage Locations", true);
        this.controller = controller;

        initUi();
        loadLocations();
        setSize(400, 350);
        setLocationRelativeTo(parent);
    }

    private void initUi() {
        setLayout(new BorderLayout());

        locationModel = new DefaultListModel<>();
        locationList = new JList<>(locationModel);
        locationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(locationList);
        scroll.setBorder(BorderFactory.createTitledBorder("Locations"));

        add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        closeButton = new JButton("Close");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        initListeners();
    }

    private void initListeners() {

        addButton.addActionListener((ActionEvent e) -> {
            String name = JOptionPane.showInputDialog(this,
                    "Enter new location name:");

            if (name == null || name.trim().isEmpty()) {
                return;
            }

            controller.addLocation(name);
            loadLocations();
        });

        deleteButton.addActionListener((ActionEvent e) -> {
            Location selected = locationList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "No location selected.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this location and all its items?",
                    "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteLocation(selected);
                loadLocations();
            }
        });

        closeButton.addActionListener((ActionEvent e) -> dispose());
    }

    private void loadLocations() {
        locationModel.clear();
        List<Location> locations = controller.getAllLocations();
        for (Location loc : locations) {
            locationModel.addElement(loc);
        }
    }
}
