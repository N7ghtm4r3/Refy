package com.tecknobit.refy.controllers;

import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.refy.helpers.services.LinksCollectionsHelper;
import com.tecknobit.refy.helpers.services.TeamsHelper;
import com.tecknobit.refy.helpers.services.links.LinksHelper;
import com.tecknobit.refycore.records.RefyItem;
import com.tecknobit.refycore.records.RefyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}")
public abstract class DefaultRefyController<I extends RefyItem> extends EquinoxController<RefyUser> {

    public interface AttachmentsManagement {

        HashSet<String> getUserAttachments();

        List<String> getAttachmentsIds();

        void execute(List<String> attachments);

    }

    protected I userItem;

    @Autowired
    protected LinksHelper linksHelper;

    @Autowired
    protected LinksCollectionsHelper collectionsHelper;

    @Autowired
    protected TeamsHelper teamsHelper;

    public abstract <T> T list(
            String token,
            String userId,
            boolean ownedOnly
    );

    public abstract String create(
            String token,
            String userId,
            Map<String, Object> payload
    );

    public abstract String edit(
            String token,
            String userId,
            String itemId,
            Map<String, Object> payload
    );

    public <T> T getItem(
            String token,
            String userId,
            String itemId
    ) {
        if(isUserNotAuthorized(userId, token, itemId))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(userItem);
    }

    public abstract String delete(
            String token,
            String userId,
            String itemId
    );

    public String editAttachmentsList(Map<String, Object> payload, String attachmentsKey, AttachmentsManagement management) {
        return editAttachmentsList(payload, true, attachmentsKey, management);
    }

    public String editAttachmentsList(Map<String, Object> payload, boolean itemsListCanBeEmpty, String attachmentsKey,
                                      AttachmentsManagement management) {
        loadJsonHelper(payload);
        ArrayList<String> attachments = jsonHelper.fetchList(attachmentsKey, new ArrayList<>());
        if(!itemsListCanBeEmpty && attachments.isEmpty())
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        HashSet<String> userAttachments = management.getUserAttachments();
        if(!userAttachments.containsAll(attachments))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        List<String> itemAttachments = management.getAttachmentsIds();
        if(!itemAttachments.isEmpty()) {
            attachments.forEach(userAttachments::remove);
            itemAttachments.removeAll(userAttachments);
        }
        itemAttachments.addAll(attachments);
        management.execute(itemAttachments);
        return successResponse();
    }

    protected abstract boolean isUserNotAuthorized(String userId, String token, String itemId);

}
