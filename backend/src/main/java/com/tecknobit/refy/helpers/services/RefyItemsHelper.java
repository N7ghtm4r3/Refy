package com.tecknobit.refy.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.refycore.records.LinksCollection;
import com.tecknobit.refycore.records.RefyItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * The {@code RefyItemsHelper} class is useful to manage all the {@link RefyItem} database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Transactional
public abstract class RefyItemsHelper<T extends RefyItem> {

    /**
     * The {@code BatchQuery} interface to manage the batch queries to insert or delete items in batch
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public interface BatchQuery {

        /**
         * Method to prepare the batch query
         *
         * @param query: query instance used to execute the SQL command
         */
        void prepareQuery(Query query);

    }

    /**
     * The {@code AttachmentsManagementWorkflow} interface useful to manage the workflow to attach or detach items from
     * containers such {@link LinksCollection} or {@link com.tecknobit.refycore.records.Team}
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public interface AttachmentsManagementWorkflow {

        /**
         * Method to get a list of identifiers <br>
         *
         * No-any params required
         *
         * @return list of identifiers as {@link List} of {@link String}
         */
        List<String> getIds();

        /**
         * Method to get a query used to insert a new attachment <br>
         *
         * No-any params required
         *
         * @return the insert query as {@link String}
         */
        String insertQuery();

        /**
         * Method to get a query used to delete an attachment <br>
         *
         * No-any params required
         *
         * @return the delete query as {@link String}
         */
        String deleteQuery();

    }

    /**
     * {@code RELATIONSHIP_VALUES_SLICE} query part to insert in the join table new row
     */
    protected static final String RELATIONSHIP_VALUES_SLICE = "(?, ?)";

    /**
     * {@code TUPLE_VALUES_SLICE} query part to insert in the join table new row
     */
    protected static final String TUPLE_VALUES_SLICE = "(?, ?, ?)";

    /**
     * {@code SINGLE_QUOTE} single quote character
     */
    private static final String SINGLE_QUOTE = "'";

    /**
     * {@code ROUND_BRACKET} round bracket character
     */
    private static final String ROUND_BRACKET = ")";

    /**
     * {@code COMMA} comma character
     */
    private static final String COMMA = ",";

    /**
     * {@code entityManager} entity manager helper
     */
    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * Method used to get an item if the user is authorized
     *
     * @param userId: the user identifier
     * @param itemId: the identifier of the target item
     *
     * @return the target item as {@link T}
     */
    public abstract T getItemIfAllowed(String userId, String itemId);

    /**
     * Method to manage the attachments of an item to a container
     *
     * @param workflow: the workflow to execute
     * @param itemId: the identifier of the target item
     * @param ids: the list of identifiers of the attachments
     */
    @Wrapper
    protected void manageAttachments(AttachmentsManagementWorkflow workflow, String itemId, List<String> ids) {
        manageAttachments(workflow, RELATIONSHIP_VALUES_SLICE, itemId, ids,
                query -> {
                    int index = 1;
                    for (String id : ids) {
                        query.setParameter(index++, id);
                        query.setParameter(index++, itemId);
                    }
                }
        );
    }

    /**
     * Method to manage the attachments of an item to a container
     *
     * @param workflow: the workflow to execute
     * @param valuesSlice: the query slice to use for the query
     * @param itemId: the identifier of the target item
     * @param ids: the list of identifiers of the attachments
     * @param batchQuery: the batch query to execute
     */
    protected void manageAttachments(AttachmentsManagementWorkflow workflow, String valuesSlice, String itemId,
                                     List<String> ids, BatchQuery batchQuery) {
        List<String> currentAttachmentsIds = workflow.getIds();
        executeInsertBatch(workflow.insertQuery(), valuesSlice, ids, batchQuery);
        currentAttachmentsIds.removeAll(ids);
        executeDeleteBatch(workflow.deleteQuery(), itemId, currentAttachmentsIds);
    }

    /**
     * Method to execute a batch query to insert attachments
     *
     * @param insertQuery: the query used to insert new attachments
     * @param valuesSlice: the query slice to use for the query
     * @param values: the values of the attachments to insert
     * @param batchQuery: the batch query to use to insert in batch the new attachments
     * @param <I> type of the items to insert
     */
    protected <I> void executeInsertBatch(String insertQuery, String valuesSlice, Collection<I> values,
                                          BatchQuery batchQuery) {
        if(values.isEmpty())
            return;
        Query query = assembleInsertBatchQuery(insertQuery, valuesSlice, values);
        batchQuery.prepareQuery(query);
        query.executeUpdate();
    }

    /**
     * Method to assemble the batch query to insert attachments
     *
     * @param insertQuery: the base query used to insert new attachments
     * @param valuesSlice: the query slice to use for the query
     * @param values: the values of the attachments to insert
     * @param <I> type of the items to insert
     */
    private <I> Query assembleInsertBatchQuery(String insertQuery, String valuesSlice, Collection<I> values) {
        StringBuilder queryAssembler = new StringBuilder(insertQuery);
        int size = values.size();
        for (int j = 0; j < size; j++) {
            queryAssembler.append(valuesSlice);
            if(j < size - 1)
                queryAssembler.append(COMMA);
        }
        return entityManager.createNativeQuery(queryAssembler.toString());
    }

    /**
     * Method to execute a batch query to delete attachments
     *
     * @param deleteQuery: the query used to delete an attachments
     * @param itemToDeleteId: the identifier of the item to delete
     * @param values: the values of the attachments to delete
     * @param <I> type of the items to delete
     */
    protected <I> void executeDeleteBatch(String deleteQuery, String itemToDeleteId, List<I> values) {
        if(values.isEmpty())
            return;
        Query query = assembleDeleteBatchQuery(deleteQuery, itemToDeleteId, values);
        query.executeUpdate();
    }

    /**
     * Method to assemble a batch query to delete attachments
     *
     * @param deleteQuery: the query used to delete an attachments
     * @param itemToDeleteId: the identifier of the item to delete
     * @param values: the values of the attachments to delete
     * @param <I> type of the items to delete
     */
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
        return entityManager.createNativeQuery(queryAssembler.toString());
    }

}
