package com.tecknobit.refy.controllers.links;

import com.tecknobit.refy.controllers.DefaultRefyController;
import com.tecknobit.refycore.records.links.RefyLink;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isDescriptionValid;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isLinkResourceValid;
import static com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY;
import static com.tecknobit.refycore.records.RefyItem.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.REFERENCE_LINK_KEY;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + LINKS_KEY)
public class LinksController extends DefaultRefyController {

    private RefyLink userLink;

    @GetMapping(
            headers = TOKEN_KEY
    )
    @Override
    public <T> T list(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) linksHelper.getAllUserLinks(userId);
    }

    @PostMapping(
            headers = TOKEN_KEY
    )
    @Override
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, String> payload
    ) {
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        String referenceLink = jsonHelper.getString(REFERENCE_LINK_KEY);
        if(!isDescriptionValid(description) || !isLinkResourceValid(referenceLink))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            linksHelper.createLink(userId, generateIdentifier(), getLinkTitle(referenceLink), description, referenceLink);
            return successResponse();
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    private String getLinkTitle(String referenceLink) throws IOException {
        return Jsoup.connect(referenceLink).get().title();
    }

    @PatchMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, String> payload
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        String referenceLink = jsonHelper.getString(REFERENCE_LINK_KEY);
        if(!isDescriptionValid(description) || !isLinkResourceValid(referenceLink))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            String title = userLink.getTitle();
            if(!userLink.getReferenceLink().equals(referenceLink))
                title = getLinkTitle(referenceLink);
            linksHelper.editLink(userId, linkId, title, description, referenceLink);
            return successResponse();
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY
    )
    public String addLinkToCollections(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestBody Map<String, String> payload
    ) {
        if(isUserNotAuthorized(userId, token, linkId)) {
            System.out.println(me);
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        }
        System.out.println(me);
        return "";
    }

    @Override
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {
        return null;
    }

    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @Override
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId
    ) {
        if(isUserNotAuthorized(userId, token, linkId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        linksHelper.deleteLink(linkId);
        return successResponse();
    }

    private boolean isUserNotAuthorized(String userId, String token, String linkId) {
        userLink = linksHelper.getUserLinkIfOwner(userId, linkId);
        return !isMe(userId, token) || userLink == null;
    }

}
