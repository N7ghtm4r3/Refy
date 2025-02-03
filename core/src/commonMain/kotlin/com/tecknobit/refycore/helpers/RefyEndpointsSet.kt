package com.tecknobit.refycore.helpers

import com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet

/**
 * The `RefyEndpointsSet` class is a container with all the Refy's system base endpoints
 *
 * @author N7ghtm4r3 - Tecknobit
 */
object RefyEndpointsSet : EquinoxBaseEndpointsSet() {

    /**
     * `UPDATE_MEMBER_ROLE_ENDPOINT` the endpoint to execute the update of a member role action
     */
    const val UPDATE_MEMBER_ROLE_ENDPOINT: String = "/updateRole"

    /**
     * `LEAVE_ENDPOINT` the endpoint to leave from a team
     */
    const val LEAVE_ENDPOINT: String = "/leave"

    /**
     * `CUSTOM_LINKS_ENDPOINT` the endpoint to fetch the custom links of a user
     */
    const val CUSTOM_LINKS_ENDPOINT: String = "/customLinks"

}
