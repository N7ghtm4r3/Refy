package com.tecknobit.refy.services.users.repositories;

import com.tecknobit.equinoxbackend.environment.services.users.repository.EquinoxUsersRepository;
import com.tecknobit.refy.services.users.entities.RefyUser;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.equinoxbackend.apis.database.SQLConstants._WHERE_;
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
     * Method to execute the query to get the potential members for a team
     *
     * @param userId The identifier of the user to not fetch
     *
     * @return list of potential members as {@link List} of {@link List} of {@link String}
     */
    @Query(
            value = "SELECT " + IDENTIFIER_KEY + "," + PROFILE_PIC_KEY + "," + NAME_KEY + "," + SURNAME_KEY + ","
                    + EMAIL_KEY + "," + TAG_NAME_KEY + " FROM " + USERS_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "!=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<List<String>> getPotentialMembers(
            @Param(IDENTIFIER_KEY) String userId,
            Pageable pageable
    );

    /**
     * Method to execute the query to change the tag name of the {@link RefyUser}
     *
     * @param newTagName The new tag name of the user
     * @param id The identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET "
                    + TAG_NAME_KEY + "=:" + TAG_NAME_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeTagName(
            @Param(TAG_NAME_KEY) String newTagName,
            @Param(IDENTIFIER_KEY) String id
    );

}