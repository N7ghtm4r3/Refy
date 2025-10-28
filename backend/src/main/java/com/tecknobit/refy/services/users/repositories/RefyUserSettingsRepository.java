package com.tecknobit.refy.services.users.repositories;

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation;
import com.tecknobit.refy.services.users.entities.UserSettings;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import static com.tecknobit.equinoxbackend.apis.database.SQLConstants._VALUES_;
import static com.tecknobit.equinoxbackend.apis.database.SQLConstants._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.refycore.ConstantsKt.CLOSE_APPLICATION_ON_LINK_OPEN_KEY;
import static com.tecknobit.refycore.ConstantsKt.SETTINGS_KEY;

@RequiresDocumentation(additionalNotes = "TO INSERT SINCE")
@Repository
public interface RefyUserSettingsRepository extends JpaRepository<UserSettings, String> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + SETTINGS_KEY +
                    "(" + IDENTIFIER_KEY + ")" + _VALUES_
                    + "(:" + IDENTIFIER_KEY + ")",
            nativeQuery = true
    )
    void createSettingsRecord(
            @Param(IDENTIFIER_KEY) String userId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + SETTINGS_KEY + " SET " +
                    CLOSE_APPLICATION_ON_LINK_OPEN_KEY + "=:" + CLOSE_APPLICATION_ON_LINK_OPEN_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeCloseApplicationOnOpenLink(
            @Param(IDENTIFIER_KEY) String userId,
            @Param(CLOSE_APPLICATION_ON_LINK_OPEN_KEY) boolean closeApplicationOnOpenLink
    );

}
