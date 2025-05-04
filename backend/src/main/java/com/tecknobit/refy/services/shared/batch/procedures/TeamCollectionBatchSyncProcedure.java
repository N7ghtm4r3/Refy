package com.tecknobit.refy.services.shared.batch.procedures;

import com.tecknobit.equinoxbackend.annotations.BatchSyncProcedureImpl;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.batch.BatchSynchronizationProcedure;
import com.tecknobit.equinoxcore.annotations.FutureEquinoxApi;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.shared.batch.items.TeamCollectionBatchItem;
import com.tecknobit.refy.services.teams.entities.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Collection;
import java.util.List;

import static com.tecknobit.refy.services.shared.batch.items.TeamCollectionBatchItem.TEAM_COLLECTION_JOIN_TABLE_COLUMNS;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code TeamCollectionBatchSyncProcedure} class is designed to compact and to clean implement a batch synchronization
 * procedure between {@link Team} and {@link LinksCollection} elements
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see BatchSynchronizationProcedure
 * @see TeamCollectionBatchItem
 */
@BatchSyncProcedureImpl
public class TeamCollectionBatchSyncProcedure extends BatchSynchronizationProcedure<String, String, TeamCollectionBatchItem> {

    @FutureEquinoxApi(
            releaseVersion = "1.1.2",
            additionalNotes = """
                - At the moment is a raw behavior will be improved, check for example for a better name
                - Document its usage properly
                - Will replace the BatchSynchronizationProcedure.loadDataList method
                - Must be not mandatory with a dedicated constructor
                """
    )
    private RawCollectionConverter<String, TeamCollectionBatchItem> converter;

    /**
     * Constructor to init the sync procedure
     *
     * @param owner         The owner entity in the relationship
     * @param ownedItems    The collection of the entities owned by the {@link #owner} entity
     * @param entityManager The entity manager helper
     */
    public TeamCollectionBatchSyncProcedure(String owner, Collection<String> ownedItems, EntityManager entityManager) {
        super(owner, ownedItems, COLLECTIONS_TEAMS_TABLE, entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SyncBatchModel createModel() {
        return new SyncBatchModel() {
            @Override
            public Collection<TeamCollectionBatchItem> getCurrentData() {
                return converter.convert(currentDataCallback.retrieveCurrentData());
            }

            @Override
            public String[] getDeletingColumns() {
                return TEAM_COLLECTION_JOIN_TABLE_COLUMNS;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BatchQuery<TeamCollectionBatchItem> createBatchQuery() {
        return new BatchQuery<>() {
            @Override
            public Collection<TeamCollectionBatchItem> getData() {
                return converter.convert(ownedItems);
            }

            @Override
            @TableColumns(columns = {TEAM_IDENTIFIER_KEY, COLLECTION_IDENTIFIER_KEY})
            public void prepareQuery(Query query, int index, Collection<TeamCollectionBatchItem> items) {
                for (TeamCollectionBatchItem item : items) {
                    query.setParameter(index++, item.getOwner());
                    query.setParameter(index++, item.getOwned());
                }
            }

            @Override
            public String[] getColumns() {
                return TEAM_COLLECTION_JOIN_TABLE_COLUMNS;
            }
        };
    }

    @FutureEquinoxApi(
            releaseVersion = "1.1.2",
            additionalNotes = """
                - At the moment is a raw behavior will be improved, check for example for a better name
                - Document its usage properly
                - Will replace the BatchSynchronizationProcedure.loadDataList method
                - Must be not mandatory with a dedicated constructor
                """
    )
    public void useConverter(RawCollectionConverter<String, TeamCollectionBatchItem> converter) {
        this.converter = converter;
    }

    @Override
    @Deprecated
    protected Collection<TeamCollectionBatchItem> loadDataList(Collection<String> rawData) {
        return List.of();
    }

}
