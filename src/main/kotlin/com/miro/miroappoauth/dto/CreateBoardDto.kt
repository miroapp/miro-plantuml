package com.miro.miroappoauth.dto

/**
 * https://miro-ea.readme.io/reference/create-a-board
 */
data class CreateBoardDto(
    val name: String,
    val sharingPolicy: SharingPolicyDto
) {

    data class SharingPolicyDto(
        val access: AccessType = AccessType.PRIVATE,
        val teamAccess: TeamAccessType = TeamAccessType.PRIVATE
    )
}

enum class AccessType {
    PRIVATE,
    VIEW,
    COMMENT
}

enum class TeamAccessType {
    PRIVATE,
    VIEW,
    COMMENT,
    EDIT
}
