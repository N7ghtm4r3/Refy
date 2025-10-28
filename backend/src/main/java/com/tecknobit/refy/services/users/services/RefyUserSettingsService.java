package com.tecknobit.refy.services.users.services;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.refy.services.users.entities.RefyUser;
import com.tecknobit.refy.services.users.entities.UserSettings;
import com.tecknobit.refy.services.users.repositories.RefyUserSettingsRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.tecknobit.refycore.ConstantsKt.CLOSE_APPLICATION_ON_LINK_OPEN_KEY;

/**
 * The {@code RefyUserSettingsService} class is useful to manage the settings of the user operations
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @since 1.1.0
 */
@Service
public class RefyUserSettingsService {

    /**
     * {@code settingsRepository} The repository instance used to handle the database operations
     */
    private final RefyUserSettingsRepository settingsRepository;

    /**
     * Constructor used to init the {@link RefyUserSettingsService} service
     *
     * @param settingsRepository The repository instance used to handle the database operations
     */
    @Autowired
    public RefyUserSettingsService(RefyUserSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    /**
     * Method used to change a setting preference
     *
     * @param user The user owner of the settings
     * @param jPayload The payload of the request from retrieve the setting preference
     */
    public void changeUserSettings(RefyUser user, JsonHelper jPayload) {
        checkToCreateSettingsRecord(user);
        JSONObject source = jPayload.getJSONObjectSource();
        // TODO: 28/10/2025 WHEN INTEGRATED USE THE JsonHelper HAS METHOD
        if (source.has(CLOSE_APPLICATION_ON_LINK_OPEN_KEY))
            changeCloseApplicationOnOpenLink(user, jPayload);
    }

    /**
     * Method used to create the settings record of the user if not exists
     *
     * @param user The user owner of the settings
     */
    private void checkToCreateSettingsRecord(RefyUser user) {
        String userId = user.getId();
        UserSettings existingRecord = settingsRepository
                .findById(userId)
                .orElse(null);
        if(existingRecord != null)
           return;
        settingsRepository.createSettingsRecord(userId);
    }

    /**
     * Method used to change the {@code close application on open link} setting preference
     *
     * @param user The user owner of the settings
     * @param jPayload The payload of the request from retrieve the setting preference
     */
    private void changeCloseApplicationOnOpenLink(RefyUser user, JsonHelper jPayload) {
        String userId = user.getId();
        boolean closeApplicationOnOpenLink = jPayload.getBoolean(CLOSE_APPLICATION_ON_LINK_OPEN_KEY, false);
        settingsRepository.changeCloseApplicationOnOpenLink(userId, closeApplicationOnOpenLink);
    }

}
