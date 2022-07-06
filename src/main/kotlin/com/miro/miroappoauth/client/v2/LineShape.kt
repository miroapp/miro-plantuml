package com.miro.miroappoauth.client.v2

import com.miro.miroappoauth.client.v1.LineType

enum class LineShape {
    straight,
    elbowed,
    curved;

    companion object {
        fun valueOfOrNull(raw: String): LineShape? =
            values().find { it.name.equals(raw, ignoreCase = true) }

        fun adapt(value: LineType): LineShape =
            when (value) {
                LineType.straight -> straight
                LineType.bezier -> curved
                LineType.orthogonal -> elbowed
                LineType.sketch -> straight
            }
    }
}