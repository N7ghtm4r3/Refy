package com.tecknobit.refy.helpers.services;

import java.util.List;

public abstract class RefyItemsHelper {

    protected void manageAttachments(AttachmentsManagementWorkflow workflow, List<String> ids) {
        List<String> currentAttachmentsIds = workflow.getIds();
        for (String attachmentId : ids)
            if(!currentAttachmentsIds.contains(attachmentId))
                workflow.add(attachmentId);
        currentAttachmentsIds.removeAll(ids);
        for (String attachmentId : currentAttachmentsIds)
            workflow.remove(attachmentId);
    }

    public interface AttachmentsManagementWorkflow {

        List<String> getIds();

        void add(String attachmentId);

        void remove(String attachmentId);

    }

}
