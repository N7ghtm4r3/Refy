package com.tecknobit.refy.helpers.services;

import com.tecknobit.refycore.records.RefyItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.List;

import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_LINKS_TABLE;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTION_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.Team.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

@Transactional
public abstract class RefyItemsHelper<T extends RefyItem> {

    protected static final String MANAGE_LINK_COLLECTION_RELATIONSHIP_QUERY =
            "REPLACE INTO " + COLLECTIONS_LINKS_TABLE +
                    "(" +
                    COLLECTION_IDENTIFIER_KEY + "," +
                    LINK_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    protected static final String MANAGE_LINK_TEAM_RELATIONSHIP_QUERY =
            "REPLACE INTO " + TEAMS_LINKS_TABLE +
                    "(" +
                    TEAM_IDENTIFIER_KEY + "," +
                    LINK_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    protected static final String MANAGE_COLLECTION_TEAM_RELATIONSHIP_QUERY =
            "REPLACE INTO " + COLLECTIONS_TEAMS_TABLE +
                    "(" +
                    COLLECTION_IDENTIFIER_KEY + "," +
                    TEAM_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    protected static final String RELATIONSHIP_VALUES_SLICE = "(?, ?)";

    private static final String SINGLE_QUOTE = "'";

    private static final String ROUND_BRACKET = ")";

    public interface BatchQuery {

        void prepareQuery(Query query);

    }

    public interface AttachmentsManagementWorkflow {

        List<String> getIds();

        String insertQuery();

        String deleteQuery();

    }

    private static final String COMMA = ",";

    @PersistenceContext
    protected EntityManager entityManager;

    public abstract T getItemIfAllowed(String userId, String itemId);

    protected void manageAttachments(AttachmentsManagementWorkflow workflow, String itemId, List<String> ids) {
        List<String> currentAttachmentsIds = workflow.getIds();
        executeInsertBatch(workflow.insertQuery(), RELATIONSHIP_VALUES_SLICE, ids,
                query -> {
                    int index = 1;
                    for (String id : ids) {
                        query.setParameter(index++, id);
                        query.setParameter(index++, itemId);
                    }
                }
        );
        currentAttachmentsIds.removeAll(ids);
        executeDeleteBatch(workflow.deleteQuery(), itemId, currentAttachmentsIds);
    }

    protected <I> void executeInsertBatch(String insertQuery, String valuesSlice, List<I> values, BatchQuery batchQuery) {
        if(values.isEmpty())
            return;
        Query query = assembleInsertBatchQuery(insertQuery, valuesSlice, values);
        batchQuery.prepareQuery(query);
        query.executeUpdate();
    }

    private <I> Query assembleInsertBatchQuery(String insertQuery, String valuesSlice, List<I> values) {
        StringBuilder queryAssembler = new StringBuilder(insertQuery);
        System.out.println(values);
        int size = values.size();
        for (int j = 0; j < size; j++) {
            queryAssembler.append(valuesSlice);
            if(j < size - 1)
                queryAssembler.append(COMMA);
        }
        return entityManager.createNativeQuery(queryAssembler.toString());
    }

    protected <I> void executeDeleteBatch(String deleteQuery, String itemToDeleteId, List<I> values) {
        if(values.isEmpty())
            return;
        Query query = assembleDeleteBatchQuery(deleteQuery, itemToDeleteId, values);
        query.executeUpdate();
    }

    private <I> Query assembleDeleteBatchQuery(String deleteQuery, String itemToDeleteId, List<I> values) {
        deleteQuery = String.format(deleteQuery, itemToDeleteId);
        StringBuilder queryAssembler = new StringBuilder(deleteQuery);
        int size = values.size();
        for (int j = 0; j < size; j++) {
            queryAssembler.append(SINGLE_QUOTE).append(values.get(j)).append(SINGLE_QUOTE);
            if(j < size - 1)
                queryAssembler.append(COMMA);
        }
        queryAssembler.append(ROUND_BRACKET);
        System.out.println(queryAssembler);
        return entityManager.createNativeQuery(queryAssembler.toString());
    }

}
