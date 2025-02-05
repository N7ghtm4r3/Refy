package com.tecknobit.refy.services.users.repository;

import com.tecknobit.equinoxbackend.environment.services.users.repository.EquinoxUsersRepository;
import com.tecknobit.refy.services.users.entity.RefyUser;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.DISCRIMINATOR_VALUE_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.refycore.ConstantsKt.TAG_NAME_KEY;

/**
 * The {@code RefyUsersRepository} interface is useful to manage the queries for the users operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see EquinoxUsersRepository
 *
 */
@Primary
@Service
@Repository
public interface RefyUsersRepository extends EquinoxUsersRepository<RefyUser> {

    /**
     * Method to execute the query to save a new user in the system
     *
     * @param discriminatorValue The discriminator value
     * @param id The identifier of the user
     * @param token The token of the user
     * @param tagName The tag name of the user
     * @param name The name of the user
     * @param surname The surname of the user
     * @param email The email of the user
     * @param password The password of the user
     * @param language The language of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + USERS_KEY + "(" +
                    DISCRIMINATOR_VALUE_KEY + "," +
                    IDENTIFIER_KEY + "," +
                    TOKEN_KEY + "," +
                    TAG_NAME_KEY + "," +
                    NAME_KEY + "," +
                    SURNAME_KEY + "," +
                    EMAIL_KEY + "," +
                    PASSWORD_KEY + "," +
                    LANGUAGE_KEY
                    + ") VALUES (" +
                    ":" + DISCRIMINATOR_VALUE_KEY + "," +
                    ":" + IDENTIFIER_KEY + "," +
                    ":" + TOKEN_KEY + "," +
                    ":" + TAG_NAME_KEY + "," +
                    ":" + NAME_KEY + "," +
                    ":" + SURNAME_KEY + "," +
                    ":" + EMAIL_KEY + "," +
                    ":" + PASSWORD_KEY + "," +
                    ":" + LANGUAGE_KEY +
                    ")",
            nativeQuery = true
    )
    void saveUser(
            @Param(DISCRIMINATOR_VALUE_KEY) String discriminatorValue,
            @Param(IDENTIFIER_KEY) String id,
            @Param(TOKEN_KEY) String token,
            @Param(TAG_NAME_KEY) String tagName,
            @Param(NAME_KEY) String name,
            @Param(SURNAME_KEY) String surname,
            @Param(EMAIL_KEY) String email,
            @Param(PASSWORD_KEY) String password,
            @Param(LANGUAGE_KEY) String language
    );

    /**
     * Method to execute the query to get the potential members for a team
     *
     * @param userId The identifier of the user to not fetch
     *
     * @return list of potential members as {@link List} of {@link List} of {@link String}
     */
    @Query(
            value = "SELECT " + IDENTIFIER_KEY + "," + PROFILE_PIC_KEY + "," + NAME_KEY + "," + SURNAME_KEY + ","
                    + TAG_NAME_KEY + " FROM " + USERS_KEY + " WHERE " + IDENTIFIER_KEY + "!=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<List<String>> getPotentialMembers(
            @Param(IDENTIFIER_KEY) String userId
    );

    /**
     * Method to execute the query to delete a user from the system
     *
     * @param userId The user identifier of the user to delete
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + USERS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteUser(
            @Param(IDENTIFIER_KEY) String userId
    );

}