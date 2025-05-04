package com.tecknobit.refy.services.shared.batch.procedures;

import com.tecknobit.equinoxbackend.annotations.BatchSyncProcedureImpl;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.batch.BatchSynchronizationProcedure;
import com.tecknobit.equinoxcore.annotations.FutureEquinoxApi;
import com.tecknobit.refy.services.shared.batch.items.CollectionLinkBatchItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Collection;
import java.util.List;

import static com.tecknobit.refy.services.shared.batch.items.CollectionLinkBatchItem.COLLECTION_LINK_JOIN_TABLE_COLUMNS;
import static com.tecknobit.refycore.ConstantsKt.*;

@BatchSyncProcedureImpl
public class LinkCollectionBatchSyncProcedure extends BatchSynchronizationProcedure<String, String , CollectionLinkBatchItem> {

    @FutureEquinoxApi(
            releaseVersion = "1.1.2",
            additionalNotes = """
                - At the moment is a raw behavior will be improved, check for example for a better name
                - Document its usage properly
                - Will replace the BatchSynchronizationProcedure.loadDataList method
                - Must be not mandatory with a dedicated constructor
                """
    )
    private RawCollectionConverter<String, CollectionLinkBatchItem> converter;

    /**
     * Constructor to init the sync procedure
     *
     * @param owner         The owner entity in the relationship
     * @param ownedItems    The collection of the entities owned by the {@link #owner} entity
     * @param entityManager The entity manager helper
     */
    public LinkCollectionBatchSyncProcedure(String owner, Collection<String> ownedItems, EntityManager entityManager) {
        super(owner, ownedItems, COLLECTIONS_LINKS_TABLE, entityManager);
    }

    @Override
    protected SyncBatchModel createModel() {
        return new SyncBatchModel() {
            @Override
            public Collection<CollectionLinkBatchItem> getCurrentData() {
                return converter.convert(currentDataCallback.retrieveCurrentData());
            }

            @Override
            @TableColumns(columns = {COLLECTION_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY})
            public String[] getDeletingColumns() {
                return COLLECTION_LINK_JOIN_TABLE_COLUMNS;
            }
        };
    }

    @Override
    protected BatchQuery<CollectionLinkBatchItem> createBatchQuery() {
        return new BatchQuery<>() {
            @Override
            public Collection<CollectionLinkBatchItem> getData() {
                return converter.convert(ownedItems);
            }

            @Override
            public void prepareQuery(Query query, int index, Collection<CollectionLinkBatchItem> items) {
                for (CollectionLinkBatchItem element : items) {
                    query.setParameter(index++, element.getOwner());
                    query.setParameter(index++, element.getOwned());
                }
            }

            @Override
            @TableColumns(columns = {COLLECTION_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY})
            public String[] getColumns() {
                return COLLECTION_LINK_JOIN_TABLE_COLUMNS;
            }
        };
    }

    public void setConverter(RawCollectionConverter<String, CollectionLinkBatchItem> converter) {
        this.converter = converter;
    }

    @Override
    @Deprecated
    protected Collection<CollectionLinkBatchItem> loadDataList(Collection<String> rawData) {
        return List.of();
    }

}
