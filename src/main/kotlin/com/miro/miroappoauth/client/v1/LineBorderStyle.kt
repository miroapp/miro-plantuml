package com.miro.miroappoauth.client.v1

import java.util.stream.Stream

enum class LineBorderStyle {
    normal, dashed, dotted;

    companion object {
        fun fromString(value: String?): LineBorderStyle {
            return Stream.of(*values())
                .filter { cnst: LineBorderStyle -> cnst.name.equals(value, ignoreCase = true) }
                .findFirst()
                .orElse(normal)
        }
    }
}
