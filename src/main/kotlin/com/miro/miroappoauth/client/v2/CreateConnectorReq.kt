package com.miro.miroappoauth.client.v2

data class CreateConnectorReq(
    val startItem: ItemConnection,
    val endItem: ItemConnection,
    val shape: LineShape? = LineShape.straight,
    val style: ConnectorStyle? = ConnectorStyle()
) {
    data class ItemConnection(
        val id: String,
        val snapTo: SnapTo? = SnapTo.auto
    )

    enum class SnapTo {
        auto, top, right, bottom, left
    }
}
