package com.tecknobit.refycore.helpers

import com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet

/**
 * The `RefyEndpointsSet` class is a container with all the Refy's system base endpoints
 *
 * @author N7ghtm4r3 - Tecknobit
 */
object RefyEndpointsSet : EquinoxBaseEndpointsSet() {

    /**
     * `CHANGE_TAG_NAME_ENDPOINT` the endpoint to change the tag name of the user
     */
    const val CHANGE_TAG_NAME_ENDPOINT: String = "/changeTagName"

    /**
     * `CHANGE_MEMBER_ROLE_ENDPOINT` the endpoint to change the role of a member
     */
    const val CHANGE_MEMBER_ROLE_ENDPOINT: String = "/changeRole"

    /**
     * `LEAVE_ENDPOINT` the endpoint to leave from a team
     */
    const val LEAVE_ENDPOINT: String = "/leave"

    /**
     * `CUSTOM_LINKS_ENDPOINT` the endpoint to fetch the custom links of a user
     */
    const val CUSTOM_LINKS_ENDPOINT: String = "/customLinks"

}
