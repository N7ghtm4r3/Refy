package com.tecknobit.refy.services.shared.batch.procedures;

import com.tecknobit.equinoxbackend.annotations.BatchSyncProcedureImpl;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.batch.BatchSynchronizationProcedure;
import com.tecknobit.refy.services.shared.batch.items.TeamCollectionBatchItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Collection;
import java.util.List;

import static com.tecknobit.refy.services.shared.batch.items.TeamCollectionBatchItem.TEAM_COLLECTION_JOIN_TABLE_COLUMNS;
import static com.tecknobit.refycore.ConstantsKt.*;

@BatchSyncProcedureImpl
public class TeamCollectionBatchSyncProcedure extends BatchSynchronizationProcedure<String, String, TeamCollectionBatchItem> {

    private RawCollectionConverter<String, TeamCollectionBatchItem> converter;

    public TeamCollectionBatchSyncProcedure(String owner, Collection<String> ownedItems, EntityManager entityManager) {
        super(owner, ownedItems, COLLECTIONS_TEAMS_TABLE, entityManager);
    }

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

    public void useConverter(RawCollectionConverter<String, TeamCollectionBatchItem> converter) {
        this.converter = converter;
    }

    @Override
    @Deprecated
    protected Collection<TeamCollectionBatchItem> loadDataList(Collection<String> rawData) {
        return List.of();
    }

}
