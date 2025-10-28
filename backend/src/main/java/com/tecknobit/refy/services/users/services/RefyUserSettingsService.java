package com.tecknobit.refy.services.users.services;

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation;
import com.tecknobit.refy.services.users.repositories.RefyUserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiresDocumentation(additionalNotes = "TO INSERT SINCE")
@Service
public class RefyUserSettingsService {

    private final RefyUserSettingsRepository settingsRepository;

    @Autowired
    public RefyUserSettingsService(RefyUserSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }



}
