package com.tecknobit.refy.configuration.indexes;

import com.tecknobit.equinoxcore.annotations.Wrapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code RefyItemFullTextIndexCreator} is a custom component used to create the {@code fulltext indexes} to filter
 * the items query such links, collections and tables using the FullText-Search (FTS) approach
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see IndexesCreator
 * @since 1.0.1
 */
@Component
public class RefyItemFullTextIndexCreator extends IndexesCreator {

    /**
     * {@code FULL_TEXT_INDEX_FIELDS} the fields used to create the fulltext index
     */
    private final List<String> FULL_TEXT_INDEX_FIELDS = List.of(TITLE_KEY, DESCRIPTION_KEY);

    /**
     * Method invoked automatically to create the different fulltext indexes required
     */
    @PostConstruct
    public void createFullTextIndexes() {
        createLinksTableFullTextIndex();
        createCollectionsTableFullTextIndex();
        createTeamsTableFullTextIndex();
    }

    /**
     * Method used to create the fulltext index in the links table
     */
    @Wrapper
    private void createLinksTableFullTextIndex() {
        createFullTextIndex(LINKS_KEY, LINKS_KEY, FULL_TEXT_INDEX_FIELDS);
    }

    /**
     * Method used to create the fulltext index in the collections table
     */
    @Wrapper
    private void createCollectionsTableFullTextIndex() {
        createFullTextIndex(COLLECTIONS_KEY, COLLECTIONS_KEY, FULL_TEXT_INDEX_FIELDS);
    }

    /**
     * Method used to create the fulltext index in the teams table
     */
    @Wrapper
    private void createTeamsTableFullTextIndex() {
        createFullTextIndex(TEAMS_KEY, TEAMS_KEY, FULL_TEXT_INDEX_FIELDS);
    }

}
