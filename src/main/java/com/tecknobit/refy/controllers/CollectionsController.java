package com.tecknobit.refy.controllers;

import com.tecknobit.refycore.records.LinksCollection;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinox.environment.records.EquinoxUser.TOKEN_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.USERS_KEY;
import static com.tecknobit.refycore.helpers.RefyInputValidator.isCollectionPayloadValid;
import static com.tecknobit.refycore.records.LinksCollection.*;
import static com.tecknobit.refycore.records.RefyItem.DESCRIPTION_KEY;
import static com.tecknobit.refycore.records.RefyItem.TITLE_KEY;
import static com.tecknobit.refycore.records.RefyUser.*;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}/" + COLLECTIONS_KEY)
public class CollectionsController extends DefaultRefyController<LinksCollection> {

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
        return (T) collectionsHelper.getAllUserCollections(userId);
    }

    @PostMapping(
            headers = TOKEN_KEY
    )
    @Override
    public String create(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @RequestBody Map<String, Object> payload
    ) {
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String color = jsonHelper.getString(COLLECTION_COLOR_KEY);
        String title = jsonHelper.getString(TITLE_KEY);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        ArrayList<String> links = jsonHelper.fetchList(LINKS_KEY, new ArrayList<>());
        HashSet<String> userLinks = linksHelper.getUserLinks(userId);
        if(!userLinks.containsAll(links) || !isCollectionPayloadValid(color, title, description, links))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        collectionsHelper.createCollection(userId, generateIdentifier(), color, title, description, links);
        return successResponse();
    }

    @PatchMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}"
    )
    @Override
    public String edit(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String color = jsonHelper.getString(COLLECTION_COLOR_KEY);
        String title = jsonHelper.getString(TITLE_KEY);
        String description = jsonHelper.getString(DESCRIPTION_KEY);
        ArrayList<String> links = jsonHelper.fetchList(LINKS_KEY, new ArrayList<>());
        if(!isCollectionPayloadValid(color, title, description, links))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        return editAttachmentsList(payload, LINKS_KEY, new AttachmentsManagement() {

            @Override
            public HashSet<String> getUserAttachments() {
                return linksHelper.getUserLinks(userId);
            }

            @Override
            public List<String> getAttachmentsIds() {
                return userItem.getLinkIds();
            }

            @Override
            public void execute(List<String> links) {
                collectionsHelper.editCollection(userId, collectionId, color, title, description, links);
            }

        });
    }

    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + LINKS_KEY
    )
    public String manageCollectionLinks(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return editAttachmentsList(payload, false, LINKS_KEY,
                new AttachmentsManagement() {

                    @Override
                    public HashSet<String> getUserAttachments() {
                        return linksHelper.getUserLinks(userId);
                    }

                    @Override
                    public List<String> getAttachmentsIds() {
                        return userItem.getLinkIds();
                    }

                    @Override
                    public void execute(List<String> links) {
                        collectionsHelper.manageCollectionLinks(collectionId, links);
                    }

                }
        );
    }

    @PutMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}/" + TEAMS_KEY
    )
    public String manageCollectionTeams(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId,
            @RequestBody Map<String, Object> payload
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return editAttachmentsList(payload, TEAMS_KEY, new AttachmentsManagement() {

            @Override
            public HashSet<String> getUserAttachments() {
                return teamsHelper.getUserTeams(userId);
            }

            @Override
            public List<String> getAttachmentsIds() {
                return userItem.getTeamIds();
            }

            @Override
            public void execute(List<String> teams) {
                collectionsHelper.manageCollectionTeams(collectionId, teams);
            }

        });
    }

    @GetMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}"
    )
    @Override
    public <T> T getItem(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId
    ) {
        return super.getItem(token, userId, collectionId);
    }

    @DeleteMapping(
            headers = TOKEN_KEY,
            path = "/{" + COLLECTION_IDENTIFIER_KEY + "}"
    )
    @Override
    public String delete(
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(USER_IDENTIFIER_KEY) String userId,
            @PathVariable(COLLECTION_IDENTIFIER_KEY) String collectionId
    ) {
        if(isUserNotAuthorized(userId, token, collectionId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        collectionsHelper.deleteCollection(collectionId);
        return successResponse();
    }

    @Override
    protected boolean isUserNotAuthorized(String userId, String token, String collectionId) {
        userItem = collectionsHelper.getItemIfAllowed(userId, collectionId);
        return !isMe(userId, token) || userItem == null;
    }

}
