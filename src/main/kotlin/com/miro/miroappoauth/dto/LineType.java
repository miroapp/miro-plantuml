package com.miro.miroappoauth.dto;

import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public enum LineType {
    straight,
    orthogonal,
    bezier,
    sketch;

    public static LineType fromString(@Nullable String value) {
        return Stream.of(values())
                .filter(cnst -> cnst.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(straight);
    }
}
