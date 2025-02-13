package com.tecknobit.refy.services.links.batchitems;

import com.tecknobit.refy.helpers.JoinTableSyncBatchItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LinkCollectionsBatchItem extends JoinTableSyncBatchItem<String> {

    public LinkCollectionsBatchItem(String collectionsId, String linkId) {
        super(collectionsId, linkId);
    }

    @NotNull
    @Override
    public List<?> mappedValues() {
        ArrayList<String> mappedValues = new ArrayList<>();
        mappedValues.add(owner);
        mappedValues.add(owned);
        return mappedValues;
    }

}
