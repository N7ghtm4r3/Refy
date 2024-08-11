package com.tecknobit.refy.helpers.services.repositories.links;

import com.tecknobit.refycore.records.links.CustomRefyLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.RefyUser.DISCRIMINATOR_VALUE_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.Team.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.Team.TITLE_KEY;
import static com.tecknobit.refycore.records.links.CustomRefyLink.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.REFERENCE_LINK_KEY;

@Service
@Repository
public interface CustomLinksRepository extends LinksBaseRepository<CustomRefyLink> {

    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY
                    + " AND dtype='" + CUSTOM_LINK_KEY + "' ORDER BY " + CREATION_DATE_KEY,
            nativeQuery = true
    )
    List<CustomRefyLink> getUserCustomLinks(
            @Param(USER_IDENTIFIER_KEY) String userId
    );

    @Query(
            value = "SELECT l.* FROM " + LINKS_KEY + " AS l WHERE l." + OWNER_KEY + "=:" + USER_IDENTIFIER_KEY
                    + " AND " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY,
            nativeQuery = true
    )
    CustomRefyLink getLinkIfAllowed(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + LINKS_KEY + "(" +
                    DISCRIMINATOR_VALUE_KEY + "," +
                    LINK_IDENTIFIER_KEY + "," +
                    TITLE_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    REFERENCE_LINK_KEY + "," +
                    CREATION_DATE_KEY + "," +
                    EXPIRED_TIME_KEY + "," +
                    UNIQUE_ACCESS_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + DISCRIMINATOR_VALUE_KEY + "," +
                    ":" + LINK_IDENTIFIER_KEY + "," +
                    ":" + TITLE_KEY + "," +
                    ":" + DESCRIPTION_KEY + "," +
                    ":" + REFERENCE_LINK_KEY + "," +
                    ":" + CREATION_DATE_KEY + "," +
                    ":#{#" + EXPIRED_TIME_KEY + ".name()}," +
                    ":" + UNIQUE_ACCESS_KEY + "," +
                    ":" + OWNER_KEY +
                    ")",
            nativeQuery = true
    )
    void saveLink(
            @Param(DISCRIMINATOR_VALUE_KEY) String discriminatorValue,
            @Param(LINK_IDENTIFIER_KEY) String linkId,
            @Param(TITLE_KEY) String title,
            @Param(DESCRIPTION_KEY) String description,
            @Param(REFERENCE_LINK_KEY) String referenceLink,
            @Param(CREATION_DATE_KEY) long creationDate,
            @Param(EXPIRED_TIME_KEY) ExpiredTime expiredTime,
            @Param(UNIQUE_ACCESS_KEY) boolean hasUniqueAccess,
            @Param(OWNER_KEY) String owner
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + LINKS_KEY + " SET " +
                    TITLE_KEY + "=:" + TITLE_KEY + "," +
                    DESCRIPTION_KEY + "=:" + DESCRIPTION_KEY + "," +
                    EXPIRED_TIME_KEY + "=" + ":#{#" + EXPIRED_TIME_KEY + ".name()}," +
                    UNIQUE_ACCESS_KEY + "=:" + UNIQUE_ACCESS_KEY +
                    " WHERE " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY + " AND " + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    void updateLink(
            @Param(LINK_IDENTIFIER_KEY) String linkId,
            @Param(TITLE_KEY) String title,
            @Param(DESCRIPTION_KEY) String description,
            @Param(EXPIRED_TIME_KEY) ExpiredTime expiredTime,
            @Param(UNIQUE_ACCESS_KEY) boolean hasUniqueAccess,
            @Param(OWNER_KEY) String owner
    );

}
