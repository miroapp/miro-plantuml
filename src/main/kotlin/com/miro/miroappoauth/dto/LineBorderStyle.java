package com.miro.miroappoauth.dto;

import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public enum LineBorderStyle {
    normal,
    dashed,
    dotted;

    public static LineBorderStyle fromString(@Nullable String value) {
        return Stream.of(values())
                .filter(cnst -> cnst.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(normal);
    }
}
