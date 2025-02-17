package com.tecknobit.refycore.enums

/**
 * `TeamRole` list of available team roles
 */
enum class TeamRole {

    /**
     * `ADMIN` role
     *
     * @apiNote this role allows to manage the members of the team, so add or remove them, and also manage
     * collections and links shared (only personal), so add or remove them
     */
    ADMIN,

    /**
     * `VIEWER` role
     *
     * @apiNote this role allows to read the content shared in the team
     */
    VIEWER

}