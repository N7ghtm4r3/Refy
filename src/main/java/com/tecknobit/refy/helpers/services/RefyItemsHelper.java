package com.tecknobit.refy.helpers.services;

import java.util.List;

public abstract class RefyItemsHelper {

    protected void manageItemAttachments(ManageItemAttachmentsWorkflow workflow, String itemId, List<String> ids) {
        List<String> currentContainers = workflow.getIds();
        for (String attachmentId : ids)
            if(!currentContainers.contains(attachmentId))
                workflow.add(attachmentId);
        currentContainers.removeAll(ids);
        for (String attachmentId : currentContainers)
            workflow.remove(attachmentId);
    }

    public interface ManageItemAttachmentsWorkflow {

        List<String> getIds();

        void add(String attachmentId);

        void remove(String attachmentId);

    }

}
