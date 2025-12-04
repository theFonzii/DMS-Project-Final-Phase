package com.example.dms_project_final_phase;

import java.util.List;

/**
 * David Alfonsi
 * CEN 3024C - Software Development 1
 * December 3rd, 2025
 * Performs backpack calculations using data from the database.
 */
public class BackpackService {

    private final ItemDao dao;
    private final double weightLimitKg;

    public BackpackService(ItemDao dao, double weightLimitKg) {
        this.dao = dao;
        this.weightLimitKg = weightLimitKg;
    }

    /**
     * @return total weight of all items in kilograms.
     */
    public double calculateTotalWeight() {
        List<RunescapeItem> items = dao.findAll();
        double sum = 0.0;
        for (RunescapeItem item : items) {
            sum += item.getWeightKg();
        }
        return sum;
    }

    /**
     * @return total GE value of all items.
     */
    public int calculateTotalValue() {
        List<RunescapeItem> items = dao.findAll();
        int sum = 0;
        for (RunescapeItem item : items) {
            sum += item.getGePrice();
        }
        return sum;
    }

    public boolean isOverWeight() {
        return calculateTotalWeight() > weightLimitKg;
    }

    public double getWeightLimitKg() {
        return weightLimitKg;
    }

    public double getRemainingCapacity() {
        return Math.max(0.0, weightLimitKg - calculateTotalWeight());
    }
}
