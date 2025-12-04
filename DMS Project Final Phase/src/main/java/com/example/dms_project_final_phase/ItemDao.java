package com.example.dms_project_final_phase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * December 3rd, 2025
 * Data Access Object for RunescapeItem, using SQLite via JDBC.
 * All CRUD operations are implemented here.
 */
public class ItemDao {

    private final DatabaseManager db;

    public ItemDao(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Loads all items from the database.
     */
    public List<RunescapeItem> findAll() {
        String sql = "SELECT id, name, type, required_level, weight_kg, ge_price FROM runescape_items";
        List<RunescapeItem> result = new ArrayList<>();

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RunescapeItem item = mapRow(rs);
                result.add(item);
            }
            return result;
        } catch (SQLException e) {
            throw new DbException("Failed to load items.", e);
        }
    }

    /**
     * Finds a single item by ID.
     */
    public Optional<RunescapeItem> findById(int id) {
        String sql = "SELECT id, name, type, required_level, weight_kg, ge_price " +
                "FROM runescape_items WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DbException("Failed to find item.", e);
        }
    }

    /**
     * Inserts a new item into the database.
     */
    public RunescapeItem insert(RunescapeItem item) throws ValidationException {
        // enforce unique ID
        if (findById(item.getId()).isPresent()) {
            throw new ValidationException("An item with ID " + item.getId() + " already exists.");
        }

        String sql = "INSERT INTO runescape_items (id, name, type, required_level, weight_kg, ge_price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getType().name());
            ps.setInt(4, item.getRequiredLevel());
            ps.setDouble(5, item.getWeightKg());
            ps.setInt(6, item.getGePrice());
            ps.executeUpdate();
            return item;
        } catch (SQLException e) {
            throw new DbException("Failed to insert item.", e);
        }
    }

    /**
     * Updates an existing item.
     */
    public void update(RunescapeItem item) throws ValidationException {
        if (findById(item.getId()).isEmpty()) {
            throw new ValidationException("No item exists with ID " + item.getId() + " to update.");
        }

        String sql = "UPDATE runescape_items " +
                "SET name = ?, type = ?, required_level = ?, weight_kg = ?, ge_price = ? " +
                "WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getType().name());
            ps.setInt(3, item.getRequiredLevel());
            ps.setDouble(4, item.getWeightKg());
            ps.setInt(5, item.getGePrice());
            ps.setInt(6, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Failed to update item.", e);
        }
    }

    /**
     * Deletes an item by ID.
     */
    public void deleteById(int id) throws ValidationException {
        if (findById(id).isEmpty()) {
            throw new ValidationException("No item exists with ID " + id + " to delete.");
        }

        String sql = "DELETE FROM runescape_items WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Failed to delete item.", e);
        }
    }

    /**
     * Helper to map a ResultSet row to a RunescapeItem object.
     */
    private RunescapeItem mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String typeStr = rs.getString("type");
        ItemType type = ItemType.valueOf(typeStr);
        int reqLevel = rs.getInt("required_level");
        double weight = rs.getDouble("weight_kg");
        int price = rs.getInt("ge_price");

        return new RunescapeItem(id, name, type, reqLevel, weight, price);
    }
}
