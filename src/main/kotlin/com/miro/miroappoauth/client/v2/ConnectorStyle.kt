package com.miro.miroappoauth.client.v2

data class ConnectorStyle(
    val startStrokeCap: StrokeCap? = StrokeCap.none,
    // todo StrokeCap.arrow
    val endStrokeCap: StrokeCap? = StrokeCap.none,
    val strokeWidth: Int? = 1,
    val strokeStyle: StrokeStyle? = StrokeStyle.normal,
    val strokeColor: String? = "#da0063"
) {
    enum class StrokeCap {
        filled_triangle,
        arrow,
        none
    }

    enum class StrokeStyle {
        normal,
        dashed,
        dotted;

        companion object {
            fun valueOfOrNull(raw: String?): StrokeStyle? =
                values().find { it.name.equals(raw, ignoreCase = true) }
        }
    }
}
