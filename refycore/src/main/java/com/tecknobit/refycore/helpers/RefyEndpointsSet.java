package com.tecknobit.refycore.helpers;

import com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet;

/**
 * The {@code RefyEndpointsSet} class is a container with all the Refy's system base endpoints
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 */
public class RefyEndpointsSet extends EquinoxBaseEndpointsSet {

    /**
     * {@code UPDATE_MEMBER_ROLE_ENDPOINT} the endpoint to execute the update of a member role action
     */
    public static final String UPDATE_MEMBER_ROLE_ENDPOINT = "/updateRole";

    /**
     * {@code LEAVE_ENDPOINT} the endpoint to leave from a team
     */
    public static final String LEAVE_ENDPOINT = "/leave";

    /**
     * {@code CUSTOM_LINKS_ENDPOINT} the endpoint to fetch the custom links of a user
     */
    public static final String CUSTOM_LINKS_ENDPOINT = "/customLinks";

}
