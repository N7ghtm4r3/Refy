package com.tecknobit.refy.services.shared.batch.procedures;

import com.tecknobit.equinoxbackend.annotations.BatchSyncProcedureImpl;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.apis.batch.BatchSynchronizationProcedure;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.shared.batch.items.TeamLinkBatchItem;
import com.tecknobit.refy.services.teams.entities.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Collection;

import static com.tecknobit.refy.services.shared.batch.items.TeamLinkBatchItem.TEAMS_LINKS_JOIN_TABLE_COLUMNS;
import static com.tecknobit.refycore.ConstantsKt.*;

/**
 * The {@code TeamLinkBatchSyncProcedure} class is designed to compact and to clean implement a batch synchronization
 * procedure between {@link Team} and {@link RefyLink} elements
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see BatchSynchronizationProcedure
 * @see TeamLinkBatchItem
 */
@BatchSyncProcedureImpl
public class TeamLinkBatchSyncProcedure extends BatchSynchronizationProcedure<String, String, TeamLinkBatchItem> {

    /**
     * Constructor used to init the sync procedure
     *
     * @param owner         The owner entity in the relationship
     * @param ownedItems    The collection of the entities owned by the {@link #owner} entity
     * @param entityManager The entity manager helper
     */
    public TeamLinkBatchSyncProcedure(String owner, Collection<String> ownedItems, EntityManager entityManager) {
        super(owner, ownedItems, TEAMS_LINKS_TABLE, entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SyncBatchModel createModel() {
        return new SyncBatchModel() {
            @Override
            public Collection<TeamLinkBatchItem> getCurrentData() {
                return converter.convert(currentDataCallback.retrieveCurrentData());
            }

            @Override
            public String[] getDeletingColumns() {
                return TEAMS_LINKS_JOIN_TABLE_COLUMNS;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BatchQuery<TeamLinkBatchItem> createBatchQuery() {
        return new BatchQuery<>() {
            @Override
            public Collection<TeamLinkBatchItem> getData() {
                return converter.convert(ownedItems);
            }

            @Override
            @TableColumns(columns = {TEAM_IDENTIFIER_KEY, LINK_IDENTIFIER_KEY})
            public void prepareQuery(Query query, int index, Collection<TeamLinkBatchItem> items) {
                for (TeamLinkBatchItem element : items) {
                    query.setParameter(index++, element.getOwner());
                    query.setParameter(index++, element.getOwned());
                }
            }

            @Override
            public String[] getColumns() {
                return TEAMS_LINKS_JOIN_TABLE_COLUMNS;
            }
        };
    }

}
