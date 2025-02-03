package com.tecknobit.refycore.enums

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Constructor to init the [ExpiredTime] class
 *
 * @param timeValue Temporal value of the expiration
 * @param gap The temporal gap value used to calculated if the link has been expired
 */
enum class ExpiredTime(
    val timeValue: Int,
    val gap: Long
) {

    /**
     * `NO_EXPIRATION` the link not expires
     */
    NO_EXPIRATION(
        timeValue = 0,
        gap = -1
    ),

    /**
     * `ONE_MINUTE` the link expires after one minute after creation
     */
    ONE_MINUTE(
        timeValue = 1,
        gap = 1.minutes.inWholeMilliseconds
    ),

    /**
     * `FIFTEEN_MINUTES` the link expires after fifteen minutes after creation
     */
    FIFTEEN_MINUTES(
        timeValue = 15,
        gap = 15.minutes.inWholeMilliseconds
    ),

    /**
     * `THIRTY_MINUTES` the link expires after thirty minutes after creation
     */
    THIRTY_MINUTES(
        timeValue = 30,
        gap = 30.minutes.inWholeMilliseconds
    ),

    /**
     * `ONE_HOUR` the link expires after one hour after creation
     */
    ONE_HOUR(
        1,
        1.hours.inWholeMilliseconds
    ),

    /**
     * `ONE_DAY` the link expires after one day after creation
     */
    ONE_DAY(
        timeValue = 1,
        gap = 1.days.inWholeMilliseconds
    ),

    /**
     * `ONE_WEEK` the link expires after one week after creation
     */
    ONE_WEEK(
        timeValue = 1,
        gap = 7.days.inWholeMilliseconds
    )

}
