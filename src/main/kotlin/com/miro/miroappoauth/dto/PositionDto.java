package com.miro.miroappoauth.dto;

public class PositionDto {

    private final double x;
    private final double y;

    private String origin = "center";

    public PositionDto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PositionDto setOrigin(String origin) {
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
