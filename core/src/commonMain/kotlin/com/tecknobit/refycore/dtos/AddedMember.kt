package com.tecknobit.refycore.dtos

import com.tecknobit.equinoxcore.annotations.DTO
import com.tecknobit.refycore.enums.TeamRole

// TODO: TO DOCUMENT
@DTO
data class AddedMember(
    val memberId: String,
    val role: TeamRole,
)
