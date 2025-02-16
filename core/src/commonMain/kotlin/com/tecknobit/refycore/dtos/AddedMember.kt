package com.tecknobit.refycore.dtos

import com.tecknobit.equinoxcore.annotations.DTO
import com.tecknobit.refycore.enums.TeamRole

/**
 * Custom DTO used to manage the added member details during the team creation
 *
 * @property memberId The identifier of the member
 * @property role The role of the member
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@DTO
data class AddedMember(
    val memberId: String,
    val role: TeamRole,
)
