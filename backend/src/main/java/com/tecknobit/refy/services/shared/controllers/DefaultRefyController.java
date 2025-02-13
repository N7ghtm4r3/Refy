package com.tecknobit.refy.services.shared.controllers;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.collections.service.LinksCollectionsService;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.links.service.LinksService;
import com.tecknobit.refy.services.shared.entities.RefyItem;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.service.TeamsService;
import com.tecknobit.refy.services.users.entity.RefyUser;
import com.tecknobit.refy.services.users.repository.RefyUsersRepository;
import com.tecknobit.refy.services.users.service.RefyUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.refycore.ConstantsKt.USER_IDENTIFIER_KEY;

/**
 * The {@code DefaultRefyController} class is useful to give the base behavior of the <b>Refy's controllers</b>
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EquinoxController
 */
@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + USER_IDENTIFIER_KEY + "}")
public abstract class DefaultRefyController<I extends RefyItem> extends EquinoxController<RefyUser,
        RefyUsersRepository, RefyUsersService> {

    /**
     * The {@code AttachmentsManagement} interface is useful to attach items to the containers such
     * {@link LinksCollection} and {@link Team}
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public interface AttachmentsManagement {

        /**
         * Method to get the current user attachments, so the items who the author is the owner <br>
         * No any params required
         *
         * @return the identifiers of the owned user items as {@link HashSet} of {@link String}
         */
        HashSet<String> getUserAttachments();

        /**
         * Method to get the remaining items available to be attached to a container <br>
         * No any params required
         *
         * @return the identifiers of the remaining items available to be attached as {@link List} of {@link String}
         */
        List<String> getAttachmentsIds();

        /**
         * Method to execute the query of the attachments management
         *
         * @param attachments The attachments list to  manage
         */
        void execute(List<String> attachments);

    }

    /**
     * {@code userItem} the current item requested by the user
     */
    protected I userItem;

    /**
     * {@code linksService} helper to manage the {@link RefyLink} database operations
     */
    @Autowired
    protected LinksService linksService;

    /**
     * {@code linksCollectionsService} helper to manage the {@link LinksCollection} database operations
     */
    @Autowired
    protected LinksCollectionsService linksCollectionsService;

    /**
     * {@code teamsService} helper to manage the {@link Team} database operations
     */
    @Autowired
    protected TeamsService teamsService;

    /**
     * Method to get a list of items
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param ownedOnly Whether to get only the items where the user is the owner
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param keywords The keywords used to filter the query to retrieve the items
     *
     * @return the items list, if authorized, else failed message as {@link T}
     *
     * @param <T> the {@link RefyItem} type
     */
    public abstract <T> T list(
            String token,
            String userId,
            boolean ownedOnly,
            int page,
            int pageSize,
            Set<String> keywords
    );

    /**
     * Method to create a new item
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param payload The payload to create the item
     *
     * @return the response of the request as {@link String}
     *
     */
    public abstract String create(
            String token,
            String userId,
            Map<String, Object> payload
    );

    /**
     * Method to edit an existing item
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param itemId The identifier of the item to edit
     * @param payload The payload to edit the item
     *
     * @return the response of the request as {@link String}
     *
     */
    public abstract String edit(
            String token,
            String userId,
            String itemId,
            Map<String, Object> payload
    );

    /**
     * Method to get an existing item
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param itemId The identifier of the item to get
     *
     * @return the item requested, if authorized, or the failed response message as {@link T}
     *
     */
    public <T> T getItem(
            String token,
            String userId,
            String itemId
    ) {
        if(isUserNotAuthorized(userId, token, itemId))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(userItem);
    }

    /**
     * Method to delete an item
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param itemId The identifier of the item to delete
     *
     * @return the response message as {@link String}
     *
     */
    public abstract String delete(
            String token,
            String userId,
            String itemId
    );

    /**
     * Method to edit the attachments list of an item
     *
     * @param payload The payload of the request
     * @param attachmentsKey The key of the attachments list to fetch from the payload sent with payload
     * @param management The management to execute
     * @return the response message as {@link String}
     */
    @Wrapper
    public String editAttachmentsList(Map<String, Object> payload, String attachmentsKey, AttachmentsManagement management) {
        return editAttachmentsList(payload, true, attachmentsKey, management);
    }

    /**
     * Method to edit the attachments list of an item
     *
     * @param payload The payload of the request
     * @param itemsListCanBeEmpty: whether the attachments list can be empty
     * @param attachmentsKey The key of the list to fetch from the payload sent with payload
     * @param management The management to execute
     * @return the response message as {@link String}
     */
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

    /**
     * Method to get whether the user is or not the owner of the item requested
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param itemId The identifier of the item requested
     * @return whether the user is or not the owner of the item requested as boolean
     */
    protected boolean userIsNotTheItemOwner(String userId, String token, String itemId) {
        boolean isNotAuthorized = isUserNotAuthorized(userId, token, itemId);
        if(isNotAuthorized)
            return true;
        return !userItem.getOwner().getId().equals(userId);
    }

    /**
     * Method to get whether the user is or not authorized to operate with the item requested
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param itemId The identifier of the item requested
     * @return whether the user is or not authorized to operate with the item requested
     */
    protected abstract boolean isUserNotAuthorized(String userId, String token, String itemId);

}
