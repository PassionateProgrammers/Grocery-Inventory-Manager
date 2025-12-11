# Grocery-Inventory-Manager
Project for COP4331 at FAU

Video Link: https://www.youtube.com/watch?v=V4pbvlbas44

A lightweight Java Swing desktop application for tracking household food inventory. Items can be organized by custom locations, assigned categories, filtered by freshness, searched, sorted, and managed through an intuitive UI. A built-in grocery list tool allows quick note-taking with PDF export and printing.

## Technologies Used
- Java 24
- Swing (GUI)
- SQLite (via JDBC)
- MVC architecture
- Maven (build management)
- JUnit 5 (basic automated tests)

## Core Features
- Add, edit, and delete food items
- Manage categories and storage locations
- Search items by name
- Sort by name, category, quantity, or expiration date
- Filter by freshness (Fresh, Expiring Soon, Expired)
- Grocery list window with Save to PDF and Print options

## Project Structure
- model: Data classes (FoodItem, Category, Location, Inventory)
- controller: Application logic (InventoryController, CategoryController, LocationController)
- view: Java Swing UI components and dialogs
- dao: DatabaseAdapter for SQLite persistence

## Running the Application
1. Clone the repository  
   `git clone https://github.com/yourusername/GroceryInventoryManager.git`
2. Build and run  
   `mvn clean compile exec:java`

## Tests
Includes simple JUnit tests for:
- Database initialization
- Sorting logic
- Freshness filtering

## License
MIT License
