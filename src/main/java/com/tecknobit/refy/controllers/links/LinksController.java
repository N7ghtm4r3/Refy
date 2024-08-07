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
import static com.tecknobit.refycore.records.RefyItem.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.RefyUser.LINKS_KEY;
import static com.tecknobit.refycore.records.RefyUser.USER_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.REFERENCE_LINK_KEY;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + LINKS_KEY)
public class LinksController extends DefaultRefyController {

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
            @PathVariable(LINK_IDENTIFIER_KEY) String itemId,
            @RequestBody Map<String, String> payload
    ) {
        RefyLink link = linksHelper.getUserLinkIfOwner(userId, itemId);
        if(!isMe(userId, token) || link == null)
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        String referenceLink = jsonHelper.getString(REFERENCE_LINK_KEY);
        if(!isDescriptionValid(description) || !isLinkResourceValid(referenceLink))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            String title = link.getTitle();
            if(!link.getReferenceLink().equals(referenceLink))
                title = getLinkTitle(referenceLink);
            linksHelper.editLink(userId, itemId, title, description, referenceLink);
            return successResponse();
        } catch (IOException e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

    @Override
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {
        return null;
    }

    @Override
    public <T> T delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(IDENTIFIER_KEY) String itemId
    ) {
        return (T) "";
    }

}
