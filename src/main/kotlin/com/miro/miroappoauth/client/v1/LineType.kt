package com.miro.miroappoauth.client.v1

import java.util.stream.Stream

enum class LineType {
    straight,
    orthogonal,
    bezier,
    sketch;

    companion object {
        fun fromString(value: String?): LineType {
            return Stream.of(*values())
                .filter { cnst: LineType -> cnst.name.equals(value, ignoreCase = true) }
                .findFirst()
                .orElse(straight)
        }
    }
}
