package com.miro.miroappoauth.dto;

import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public enum ShapeType {
    rectangle,
    circle;

    public static ShapeType fromString(@Nullable String value) {
        return Stream.of(values())
                .filter(cnst -> cnst.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(rectangle);
    }
}
