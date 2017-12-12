package com.gleb.monitoring.model;

public enum Status {
    OK("OK"),
    FUEL_DISCHARGE("FUEL DISCHARGE"),
    FUELING("FUELING");

    private final String message;

    Status(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
