package com.tecknobit.refy.services.shared.controllers;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.refy.services.collections.entity.LinksCollection;
import com.tecknobit.refy.services.collections.service.LinksCollectionsService;
import com.tecknobit.refy.services.links.entity.RefyLink;
import com.tecknobit.refy.services.links.service.LinksService;
import com.tecknobit.refy.services.shared.entities.RefyItem;
import com.tecknobit.refy.services.teams.entities.Team;
import com.tecknobit.refy.services.teams.service.TeamsService;
import com.tecknobit.refy.services.users.entities.RefyUser;
import com.tecknobit.refy.services.users.repositories.RefyUsersRepository;
import com.tecknobit.refy.services.users.services.RefyUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USER_IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;

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
     * {@code userItem} the current item requested by the user
     */
    protected I userItem;

    /**
     * {@code linksService} helper to manage the {@link RefyLink} database operations
     */
    protected final LinksService linksService;

    /**
     * {@code linksCollectionsService} helper to manage the {@link LinksCollection} database operations
     */
    protected final LinksCollectionsService linksCollectionsService;

    /**
     * {@code teamsService} helper to manage the {@link Team} database operations
     */
    protected final TeamsService teamsService;

    /**
     * Constructor used to init the controller
     *
     * @param linksService The helper to manage the {@link RefyLink} database operations
     * @param linksCollectionsService The helper to manage the {@link LinksCollection} database operations
     * @param teamsService The helper to manage the {@link Team} database operations
     */
    @Autowired
    protected DefaultRefyController(LinksService linksService, LinksCollectionsService linksCollectionsService,
                                    TeamsService teamsService) {
        this.linksService = linksService;
        this.linksCollectionsService = linksCollectionsService;
        this.teamsService = teamsService;
    }

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
