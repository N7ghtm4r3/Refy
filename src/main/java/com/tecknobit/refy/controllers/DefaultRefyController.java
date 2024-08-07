package com.tecknobit.refy.controllers;

import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.refy.helpers.services.LinksCollectionsHelper;
import com.tecknobit.refy.helpers.services.TeamsHelper;
import com.tecknobit.refy.helpers.services.links.LinksHelper;
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
public abstract class DefaultRefyController extends EquinoxController<RefyUser> {

    @Autowired
    protected LinksHelper linksHelper;

    @Autowired
    protected LinksCollectionsHelper collectionsHelper;

    @Autowired
    protected TeamsHelper teamsHelper;

    public abstract <T> T list(
            String token,
            String userId
    );

    public abstract String create(
            String token,
            String userId,
            Map<String, String> payload
    );

    public abstract String edit(
            String token,
            String userId,
            String itemId,
            Map<String, String> payload
    );

    public abstract <T> T getItem(
            String token,
            String userId,
            String itemId
    );

    public abstract String delete(
            String token,
            String userId,
            String itemId
    );

    public String manageItemAttachmentsList(Map<String, Object> payload, String attachmentsKey,
                                            PerformAttachmentsManagement management) {
        loadJsonHelper(payload);
        ArrayList<String> attachments = jsonHelper.fetchList(attachmentsKey, new ArrayList<>());
        HashSet<String> userAttachments = management.getUserAttachments();
        if(!userAttachments.containsAll(attachments))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        List<String> itemAttachments = management.getAttachmentsIds();
        if(itemAttachments.isEmpty())
            itemAttachments.addAll(attachments);
        else {
            attachments.forEach(userAttachments::remove);
            itemAttachments.removeAll(userAttachments);
        }
        management.execute(itemAttachments);
        return successResponse();

    }

    public interface PerformAttachmentsManagement {

        HashSet<String> getUserAttachments();

        List<String> getAttachmentsIds();

        void execute(List<String> attachments);

    }

    /*
    @Override
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId
    ) {

    }

    @Override
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, String> payload
    ) {

    }

    @Override
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId,
            @RequestBody Map<String, String> payload
    ) {

    }

    @Override
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {

    }

    @Override
    public <T> T delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {

    }
     */

}
