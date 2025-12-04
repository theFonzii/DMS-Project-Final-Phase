package com.example.dms_project_final_phase;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * December 3rd, 2025
 * RunescapeItem.java
 * This class contains the constructors and mutators for
 * the RunescapeItem object, which will be the focal point
 * of the database. It includes all 6 attributes.
 */

public class RunescapeItem {

    private int id;
    private String name;
    private ItemType type;
    private int requiredLevel;
    private double weightKg;
    private int gePrice;

    public RunescapeItem(int id,
                         String name,
                         ItemType type,
                         int requiredLevel,
                         double weightKg,
                         int gePrice) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.requiredLevel = requiredLevel;
        this.weightKg = weightKg;
        this.gePrice = gePrice;
    }

    // No-arg constructor for convenience (e.g., FXML usage)
    public RunescapeItem() { }

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

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public int getGePrice() {
        return gePrice;
    }

    public void setGePrice(int gePrice) {
        this.gePrice = gePrice;
    }

    @Override
    public String toString() {
        return "RunescapeItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", requiredLevel=" + requiredLevel +
                ", weightKg=" + weightKg +
                ", gePrice=" + gePrice +
                '}';
    }
}
