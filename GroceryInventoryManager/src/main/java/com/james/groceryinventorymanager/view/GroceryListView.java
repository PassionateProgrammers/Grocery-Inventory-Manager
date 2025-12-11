/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author james
 */

public class GroceryListView extends JDialog {

    private JTextArea listArea;
    private JButton saveButton;
    private JButton printButton;
    private JButton closeButton;

    public GroceryListView(JFrame parent) {
        super(parent, "Grocery List", true);
        initUi();
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    private void initUi() {
        setLayout(new BorderLayout());

        // Text area
        listArea = new JTextArea();
        listArea.setLineWrap(true);
        listArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(listArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Grocery List Contents"));

        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        printButton = new JButton("Print");
        closeButton = new JButton("Close");

        buttonPanel.add(saveButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        initListeners();
    }

    private void initListeners() {
        saveButton.addActionListener((ActionEvent e) -> onSave());
        printButton.addActionListener((ActionEvent e) -> onPrint());
        closeButton.addActionListener((ActionEvent e) -> onClose());
    }

    /**
     * Save the current contents of the text area to a file on disk.
     * The app does not store this in the database; it is purely local export.
     */
    private void onSave() {
        String text = listArea.getText();
        if (text.isEmpty()) {
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "The grocery list is empty. Do you still want to save?",
                    "Empty List",
                    JOptionPane.YES_NO_OPTION
            );
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Grocery List");
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; // user cancelled
        }

        File file = chooser.getSelectedFile();
        // If user did not specify an extension, default to .txt
        if (!file.getName().contains(".")) {
            file = new File(file.getParentFile(), file.getName() + ".txt");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(text);
            writer.flush();
            JOptionPane.showMessageDialog(this,
                    "Grocery list saved to:\n" + file.getAbsolutePath(),
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving grocery list: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Send the grocery list contents to the system print dialog.
     * On macOS and many systems, users can choose "Save as PDF" in that dialog.
     */
    private void onPrint() {
        try {
            boolean done = listArea.print();
            if (!done) {
                JOptionPane.showMessageDialog(this,
                        "Print was cancelled or did not complete.",
                        "Print",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (PrinterException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error printing: " + ex.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Close the window. Ask for confirmation, since contents are not stored.
     */
    private void onClose() {
        String text = listArea.getText();
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Contents will be discarded. Do you wish to continue?",
                "Close Grocery List",
                JOptionPane.YES_NO_OPTION
        );
        if (answer == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
}

