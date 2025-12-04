package com.example.dms_project_final_phase;

/**
 * Checked exception used when user input is invalid.
 * Keeps GUI validation separate from low-level code.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
