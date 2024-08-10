package com.tecknobit.refy.helpers.services.repositories.links;

import com.tecknobit.refy.helpers.services.repositories.RefyItemsRepository;
import com.tecknobit.refycore.records.links.RefyLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.refycore.records.RefyUser.LINKS_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

@Service
@Repository
public interface LinksBaseRepository<T extends RefyLink> extends RefyItemsRepository<T> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + LINKS_KEY + " WHERE " + LINK_IDENTIFIER_KEY + "=:" + LINK_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteLink(
            @Param(LINK_IDENTIFIER_KEY) String linkId
    );

}
