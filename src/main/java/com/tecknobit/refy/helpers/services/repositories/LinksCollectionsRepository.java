package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.refycore.records.LinksCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;

import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;

@Service
@Repository
public interface LinksCollectionsRepository extends JpaRepository<LinksCollection, String> {

    @Query(
            value = "SELECT " + IDENTIFIER_KEY + " FROM " + COLLECTIONS_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    HashSet<String> getUserCollections(
            @Param(OWNER_KEY) String owner
    );

}
