package com.tecknobit.refy.services.customlinks.repository;

import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink;
import com.tecknobit.refy.services.shared.links.repository.LinksBaseRepository;
import com.tecknobit.refy.services.shared.repositories.RefyItemsRepository;
import com.tecknobit.refycore.enums.ExpiredTime;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.equinoxbackend.configuration.IndexesCreator._IN_BOOLEAN_MODE;
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.DISCRIMINATOR_VALUE_KEY;
import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper._WHERE_;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code CustomLinksRepository} interface is useful to manage the queries of the {@link CustomRefyLink}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see RefyItemsRepository
 * @see LinksBaseRepository
 *
 */
@Service
@Repository
public interface CustomLinksRepository extends LinksBaseRepository<CustomRefyLink> {

    /**
     * Method to count the user's custom links
     *
     * @param userId   The identifier of the user
     * @param keywords The keywords used to filter the query to retrieve the items
     * @return the count of custom links as {@link long}
     */
    @Query(
            value = "SELECT COUNT(*) FROM " + LINKS_KEY + _WHERE_ +
                    OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND dtype='" + CUSTOM_LINK_KEY +
                    "' AND ( " +
                    "    MATCH(" + TITLE_KEY + "," + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + "=''" +
                    " )",
            nativeQuery = true
    )
    long countUserCustomLinks(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(KEYWORDS_KEY) String keywords
    );

    /**
     * Method to execute the query to get all the user's custom links
     *
     * @param userId The identifier of the user
     * @param keywords     The keywords used to filter the query to retrieve the items
     * @param pageable     The parameters to paginate the query
     *
     * @return the user custom links as {@link List} of {@link CustomRefyLink}
     */
    @Query(
            value = "SELECT * FROM " + LINKS_KEY + _WHERE_ +
                    OWNER_KEY + "=:" + USER_IDENTIFIER_KEY +
                    " AND dtype='" + CUSTOM_LINK_KEY +
                    "' AND ( " +
                    "    MATCH(" + TITLE_KEY + "," + DESCRIPTION_KEY + ") AGAINST (:" + KEYWORDS_KEY + _IN_BOOLEAN_MODE + ") " +
                    "    OR :" + KEYWORDS_KEY + "=''" +
                    " ) " +
                    "ORDER BY " + DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<CustomRefyLink> getUserCustomLinks(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(KEYWORDS_KEY) String keywords,
            Pageable pageable
    );

    /**
     * Method to execute the query to get a custom link if the owner is authorized
     *
     * @param userId The identifier of the user
     * @param linkId The link identifier
     *
     * @return the custom link if the user is authorized as {@link CustomRefyLink}
     */
    @Query(
            value = "SELECT * FROM " + LINKS_KEY + _WHERE_ +
                    OWNER_KEY + "=:" + USER_IDENTIFIER_KEY + " AND "
                    + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY,
            nativeQuery = true
    )
    CustomRefyLink getLinkIfAllowed(
            @Param(USER_IDENTIFIER_KEY) String userId,
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

    /**
     * Method to execute the query to save a link
     *
     * @param discriminatorValue The discriminator value
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param description The description of the link
     * @param referenceLink The reference link value
     * @param creationDate: when the link has been created
     * @param expiredTime The expiration time set for the link
     * @param hasUniqueAccess: whether the link has the unique access
     * @param previewToken The token used to access to the custom link in preview mode
     * @param owner The owner of the link
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + LINKS_KEY + "(" +
                    DISCRIMINATOR_VALUE_KEY + "," +
                    LINK_IDENTIFIER_KEY + "," +
                    TITLE_KEY + "," +
                    DESCRIPTION_KEY + "," +
                    REFERENCE_LINK_KEY + "," +
                    DATE_KEY + "," +
                    EXPIRED_TIME_KEY + "," +
                    UNIQUE_ACCESS_KEY + "," +
                    PREVIEW_TOKEN_KEY + "," +
                    OWNER_KEY
                    + ") VALUES (" +
                    ":" + DISCRIMINATOR_VALUE_KEY + "," +
                    ":" + LINK_IDENTIFIER_KEY + "," +
                    ":" + TITLE_KEY + "," +
                    ":" + DESCRIPTION_KEY + "," +
                    ":" + REFERENCE_LINK_KEY + "," +
                    ":" + DATE_KEY + "," +
                    ":#{#" + EXPIRED_TIME_KEY + ".name()}," +
                    ":" + UNIQUE_ACCESS_KEY + "," +
                    ":" + PREVIEW_TOKEN_KEY + "," +
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
            @Param(DATE_KEY) long creationDate,
            @Param(EXPIRED_TIME_KEY) ExpiredTime expiredTime,
            @Param(UNIQUE_ACCESS_KEY) boolean hasUniqueAccess,
            @Param(PREVIEW_TOKEN_KEY) String previewToken,
            @Param(OWNER_KEY) String owner
    );

    /**
     * Method to execute the query to save a link
     *
     * @param linkId The identifier of the link
     * @param title The title of the link
     * @param description The description of the link
     * @param expiredTime The expiration time set for the link
     * @param hasUniqueAccess: whether the link has the unique access
     * @param owner The owner of the link
     */
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
