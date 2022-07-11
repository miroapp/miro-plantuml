package com.miro.miroappoauth.client.v2

import com.miro.miroappoauth.services.LineType

enum class LineShape {
    straight,
    elbowed,
    curved;

    companion object {
        fun adapt(value: LineType): LineShape =
            when (value) {
                LineType.straight -> straight
                LineType.bezier -> curved
                LineType.orthogonal -> elbowed
                LineType.sketch -> straight
            }
    }
}
