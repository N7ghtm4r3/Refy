package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.equinox.environment.helpers.services.repositories.EquinoxUsersRepository;
import com.tecknobit.refycore.records.RefyUser;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.equinox.environment.records.EquinoxItem.DISCRIMINATOR_VALUE_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.*;
import static com.tecknobit.refycore.records.RefyUser.TAG_NAME_KEY;

@Service
@Repository
@Primary
public interface RefyUsersRepository extends EquinoxUsersRepository<RefyUser> {


    /**
     * Method to execute the query to save a new user in the system
     *
     * @param discriminatorValue: the discriminator value
     * @param id:                 the identifier of the user
     * @param token:              the token of the user
     * @param name:               the name of the user
     * @param surname:            the surname of the user
     * @param email:              the email of the user
     * @param password:           the password of the user
     * @param language:           the language of the user
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

}