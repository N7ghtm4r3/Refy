package com.tecknobit.refy.services.users.repositories;

import com.tecknobit.equinoxcore.annotations.RequiresDocumentation;
import com.tecknobit.refy.services.users.entities.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@RequiresDocumentation(additionalNotes = "TO INSERT SINCE")
@Repository
public interface RefyUserSettingsRepository extends JpaRepository<String, UserSettings> {
}
