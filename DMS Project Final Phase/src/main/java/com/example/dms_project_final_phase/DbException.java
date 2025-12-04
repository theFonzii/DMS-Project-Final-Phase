package com.example.dms_project_final_phase;

/**
 * Wraps checked SQL exceptions with a simpler runtime exception that can be
 * surfaced to the user.
 */
public class DbException extends RuntimeException {
    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}
