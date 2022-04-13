package com.miro.miroappoauth.dto;

public class ShapePosition {

    private final double x;
    private final double y;

    private String origin = "center";

    public ShapePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ShapePosition setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getOrigin() {
        return origin;
    }
}
