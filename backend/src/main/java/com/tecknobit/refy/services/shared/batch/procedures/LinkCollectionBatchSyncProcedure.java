package com.tecknobit.refy.services.shared.batch.procedures;

import com.tecknobit.equinoxbackend.annotations.BatchSyncProcedureImpl;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.apis.batch.BatchSynchronizationProcedure;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.batch.items.CollectionLinkBatchItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Collection;

import static com.tecknobit.refy.services.shared.batch.items.CollectionLinkBatchItem.COLLECTION_LINK_JOIN_TABLE_COLUMNS;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code LinkCollectionBatchSyncProcedure} class is designed to compact and to clean implement a batch synchronization
 * procedure between {@link RefyLink} and {@link LinksCollection} elements
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see BatchSynchronizationProcedure
 * @see CollectionLinkBatchItem
 */
@BatchSyncProcedureImpl
public class LinkCollectionBatchSyncProcedure extends BatchSynchronizationProcedure<String, String , CollectionLinkBatchItem> {

    /**
     * Constructor used to init the sync procedure
     *
     * @param owner         The owner entity in the relationship
     * @param ownedItems    The collection of the entities owned by the {@link #owner} entity
     * @param entityManager The entity manager helper
     */
    public LinkCollectionBatchSyncProcedure(String owner, Collection<String> ownedItems, EntityManager entityManager) {
        super(owner, ownedItems, COLLECTIONS_LINKS_TABLE, entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SyncBatchModel createModel() {
        return new SyncBatchModel() {
            @Override
            public Collection<CollectionLinkBatchItem> getCurrentData() {
                return converter.convert(currentDataCallback.retrieveCurrentData());
            }

            @Override
            public String[] getDeletingColumns() {
                return COLLECTION_LINK_JOIN_TABLE_COLUMNS;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BatchQuery<CollectionLinkBatchItem> createBatchQuery() {
        return new BatchQuery<>() {
            @Override
            public Collection<CollectionLinkBatchItem> getData() {
                return converter.convert(ownedItems);
            }

            @Override
            @TableColumns(columns = {COLLECTION_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY})
            public void prepareQuery(Query query, int index, Collection<CollectionLinkBatchItem> items) {
                for (CollectionLinkBatchItem element : items) {
                    query.setParameter(index++, element.getOwner());
                    query.setParameter(index++, element.getOwned());
                }
            }

            @Override
            public String[] getColumns() {
                return COLLECTION_LINK_JOIN_TABLE_COLUMNS;
            }
        };
    }

}
