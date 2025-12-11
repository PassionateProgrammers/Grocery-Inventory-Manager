/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.james.groceryinventorymanager.dao;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DatabaseAdapterTest {

    @Test
    public void testInitializeDatabaseCreatesFile() {
        try {
            Path tempFile = Files.createTempFile("grocery-test-db", ".sqlite");
            String dbPath = tempFile.toAbsolutePath().toString();

            DatabaseAdapter adapter = new DatabaseAdapter(dbPath);
            adapter.initializeDatabase();

            assertTrue(Files.exists(tempFile), "Database file should exist after initialization");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Database initialization should not throw: " + e.getMessage());
        }
    }
}
