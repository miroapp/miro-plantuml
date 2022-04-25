package com.miro.miroappoauth.client.v2

import java.util.stream.Stream

enum class ShapeType {
    rectangle,
    triangle,
    circle;

    companion object {
        fun fromString(value: String?): ShapeType {
            return Stream.of(*values())
                .filter { cnst: ShapeType -> cnst.name.equals(value, ignoreCase = true) }
                .findFirst()
                .orElse(rectangle)
        }
    }
}
