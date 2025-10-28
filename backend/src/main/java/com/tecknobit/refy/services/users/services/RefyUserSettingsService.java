package com.tecknobit.refy.services.users.services;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinoxcore.annotations.RequiresDocumentation;
import com.tecknobit.refy.services.users.entities.RefyUser;
import com.tecknobit.refy.services.users.repositories.RefyUserSettingsRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.tecknobit.refycore.ConstantsKt.CLOSE_APPLICATION_ON_LINK_OPEN_KEY;

@RequiresDocumentation(additionalNotes = "TO INSERT SINCE")
@Service
public class RefyUserSettingsService {

    private final RefyUserSettingsRepository settingsRepository;

    @Autowired
    public RefyUserSettingsService(RefyUserSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public void changeUserSettings(RefyUser user, JsonHelper jPayload) {
        JSONObject source = jPayload.getJSONObjectSource();
        // TODO: 28/10/2025 WHEN INTEGRATED USE THE JsonHelper HAS METHOD
        if (source.has(CLOSE_APPLICATION_ON_LINK_OPEN_KEY))
            changeCloseApplicationOnOpenLink(user, jPayload);
    }

    private void changeCloseApplicationOnOpenLink(RefyUser user, JsonHelper jPayload) {
        String userId = user.getId();
        boolean closeApplicationOnOpenLink = jPayload.getBoolean(CLOSE_APPLICATION_ON_LINK_OPEN_KEY, false);
        settingsRepository.changeCloseApplicationOnOpenLink(userId, closeApplicationOnOpenLink);
    }

}
