// TODO TO MOVE 

//package com.tecknobit.refycore.helpers
//
//import com.tecknobit.apimanager.annotations.RequestPath
//import com.tecknobit.apimanager.annotations.WrappedRequest
//import com.tecknobit.apimanager.apis.APIRequest.Params
//import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*
//import com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY
//import com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.SIGN_UP_ENDPOINT
//import com.tecknobit.equinox.environment.helpers.EquinoxRequester
//import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
//import com.tecknobit.equinox.inputs.InputValidator.isLanguageValid
//import com.tecknobit.refycore.helpers.RefyEndpointsSet.*
//import com.tecknobit.refy.services.collections.entity.LinksCollection
//import com.tecknobit.refy.services.collections.entity.LinksCollection.COLLECTIONS_KEY
//import com.tecknobit.refy.services.collections.entity.LinksCollection.COLLECTION_COLOR_KEY
//import com.tecknobit.refy.services.shared.entities.RefyItem.TITLE_KEY
//import com.tecknobit.refy.services.users.entity.RefyUser.*
//import com.tecknobit.refy.services.teams.entities.Team
//import com.tecknobit.refy.services.teams.entities.Team.LOGO_PIC_KEY
//import com.tecknobit.refy.services.teams.entities.Team.MEMBERS_KEY
//import com.tecknobit.refy.services.teams.entities.Team.RefyTeamMember
//import com.tecknobit.refy.services.teams.entities.Team.RefyTeamMember.TEAM_ROLE_KEY
//import com.tecknobit.refy.services.teams.entities.Team.RefyTeamMember.TeamRole
//import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink
//import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink.*
//import com.tecknobit.refy.services.links.entity.RefyLink
//import com.tecknobit.refy.services.links.entity.RefyLink.DESCRIPTION_KEY
//import com.tecknobit.refy.services.links.entity.RefyLink.REFERENCE_LINK_KEY
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONArray
//import org.json.JSONObject
//import java.io.File
//
///**
// * The **RefyRequester** class is useful to communicate with Refy's backend
// *
// * @param host The host address where is running the backend
// * @param userId The user identifier
// * @param userToken The user token
// *
// * @author N7ghtm4r3 - Tecknobit
// *
// */
//class RefyRequester(
//    host: String,
//    userId: String? = null,
//    userToken: String? = null
//) : EquinoxRequester(
//    host = host,
//    userId = userId,
//    userToken = userToken,
//    connectionTimeout = 5000,
//    connectionErrorMessage = DEFAULT_CONNECTION_ERROR_MESSAGE,
//    enableCertificatesValidation = true
//) {
//
//    /**
//     * Function to execute the request to sign up in the Refy's system
//     *
//     * @param serverSecret The secret of the personal Refy's backend
//     * @param tagName The tag name of the user
//     * @param name The name of the user
//     * @param surname The surname of the user
//     * @param email The email of the user
//     * @param password The password of the user
//     * @param language The language of the user
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/signUp", method = POST)
//    fun signUp(
//        serverSecret: String,
//        tagName: String,
//        name: String,
//        surname: String,
//        email: String,
//        password: String,
//        language: String
//    ): JSONObject {
//        val payload = Params()
//        payload.addParam(SERVER_SECRET_KEY, serverSecret)
//        payload.addParam(TAG_NAME_KEY, tagName)
//        payload.addParam(NAME_KEY, name)
//        payload.addParam(SURNAME_KEY, surname)
//        payload.addParam(EMAIL_KEY, email)
//        payload.addParam(PASSWORD_KEY, password)
//        payload.addParam(
//            LANGUAGE_KEY,
//            if (!isLanguageValid(language))
//                DEFAULT_LANGUAGE
//            else
//                language
//        )
//        return execPost(
//            endpoint = SIGN_UP_ENDPOINT,
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to get the links of the user
//     *
//     * @param ownedOnly: whether to get only the links where the user is the owner
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/links", method = GET)
//    fun getLinks(
//        ownedOnly: Boolean = false
//    ) : JSONObject {
//        return execGet(
//            endpoint = assembleLinksEndpointPath(
//                query = createOwnedOnlyQuery(
//                    ownedOnly = ownedOnly
//                )
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to create a new link
//     *
//     * @param referenceLink The reference link value
//     * @param description The description of the link
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/links", method = POST)
//    fun createLink(
//        referenceLink: String,
//        description: String
//    ): JSONObject {
//        val payload = createLinkPayload(
//            referenceLink = referenceLink,
//            description = description
//        )
//        return execPost(
//            endpoint = assembleLinksEndpointPath(),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a link
//     *
//     * @param link The link to edit
//     * @param referenceLink The reference link value
//     * @param description The description of the link
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}", method = PATCH)
//    fun editLink(
//        link: com.tecknobit.refy.services.links.entity.RefyLink,
//        referenceLink: String,
//        description: String
//    ): JSONObject {
//        return editLink(
//            linkId = link.id,
//            referenceLink = referenceLink,
//            description = description
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a link
//     *
//     * @param linkId The link identifier to edit
//     * @param referenceLink The reference link value
//     * @param description The description of the link
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}", method = PATCH)
//    fun editLink(
//        linkId: String,
//        referenceLink: String,
//        description: String
//    ): JSONObject {
//        val payload = createLinkPayload(
//            referenceLink = referenceLink,
//            description = description
//        )
//        return execPatch(
//            endpoint = assembleLinksEndpointPath(
//                subEndpoint = linkId
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to create a payload for the link creation/edit actions
//     *
//     * @param referenceLink The reference link value
//     * @param description The description of the link
//     *
//     * @return the payload as [Params]
//     *
//     */
//    private fun createLinkPayload(
//        referenceLink: String,
//        description: String
//    ) : Params {
//        val payload = Params()
//        payload.addParam(REFERENCE_LINK_KEY, referenceLink)
//        payload.addParam(DESCRIPTION_KEY, description)
//        return payload
//    }
//
//    /**
//     * Function to execute the request to manage the collections where the link is shared
//     *
//     * @param link The link where manage its collections
//     * @param collections The list of collections where share the link
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}/collections", method = PUT)
//    fun manageLinkCollections(
//        link: com.tecknobit.refy.services.links.entity.RefyLink,
//        collections: List<String>
//    ) : JSONObject {
//        return manageLinkCollections(
//            linkId = link.id,
//            collections = collections
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the collections where the link is shared
//     *
//     * @param linkId The link identifier where manage its collections
//     * @param collections The list of collections where share the link
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}/collections", method = PUT)
//    fun manageLinkCollections(
//        linkId: String,
//        collections: List<String>
//    ) : JSONObject {
//        val payload = Params()
//        payload.addParam(COLLECTIONS_KEY, JSONArray(collections))
//        return execPut(
//            endpoint = assembleLinksEndpointPath(
//                subEndpoint = "$linkId/$COLLECTIONS_KEY"
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the teams where the link is shared
//     *
//     * @param link The link where manage its collections
//     * @param teams The list of teams where share the link
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}/teams", method = PUT)
//    fun manageLinkTeams(
//        link: com.tecknobit.refy.services.links.entity.RefyLink,
//        teams: List<String>
//    ) : JSONObject {
//        return manageLinkTeams(
//            linkId = link.id,
//            teams = teams
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the teams where the link is shared
//     *
//     * @param linkId The link identifier where manage its collections
//     * @param teams The list of teams where share the link
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}/teams", method = PUT)
//    fun manageLinkTeams(
//        linkId: String,
//        teams: List<String>
//    ) : JSONObject {
//        val payload = Params()
//        payload.addParam(TEAMS_KEY, JSONArray(teams))
//        return execPut(
//            endpoint = assembleLinksEndpointPath(
//                subEndpoint = "$linkId/$TEAMS_KEY"
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a link
//     *
//     * @param link The link to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}", method = DELETE)
//    fun deleteLink(
//        link: com.tecknobit.refy.services.links.entity.RefyLink
//    ): JSONObject {
//        return deleteLink(
//            linkId = link.id
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a link
//     *
//     * @param linkId The link identifier of the link to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/links/{link_id}", method = DELETE)
//    fun deleteLink(
//        linkId: String
//    ): JSONObject {
//        return execDelete(
//            endpoint = assembleLinksEndpointPath(
//                subEndpoint = linkId
//            )
//        )
//    }
//
//    /**
//     * Function to assemble the endpoint to make the request to the links controller
//     *
//     * @param subEndpoint The sub-endpoint path of the url
//     * @param query The query to attach to the request
//     *
//     * @return an endpoint to make the request as [String]
//     */
//    private fun assembleLinksEndpointPath(
//        subEndpoint: String = "",
//        query: String = ""
//    ): String {
//        return assembleCustomEndpointPath(
//            customEndpoint = LINKS_KEY,
//            subEndpoint = subEndpoint,
//            query = query
//        )
//    }
//
//    /**
//     * Function to execute the request to get the collections of the user
//     *
//     * @param ownedOnly: whether to get only the collections where the user is the owner
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/collections", method = GET)
//    fun getCollections(
//        ownedOnly: Boolean = false
//    ) : JSONObject {
//        return execGet(
//            endpoint = assembleCollectionsEndpointPath(
//                query = createOwnedOnlyQuery(
//                    ownedOnly = ownedOnly
//                )
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to create a collection
//     *
//     * @param color: color of the collection
//     * @param title Title of the collection
//     * @param description: description of the collection
//     * @param links: list of links shared in a collection
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/collections", method = POST)
//    fun createCollection(
//        color: String,
//        title: String,
//        description: String,
//        links: List<String>
//    ): JSONObject {
//        val payload = createCollectionPayload(
//            color = color,
//            title = title,
//            description = description,
//            links = links
//        )
//        return execPost(
//            endpoint = assembleCollectionsEndpointPath(),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a collection
//     *
//     * @param collection The collection to edit
//     * @param color: color of the collection
//     * @param title Title of the collection
//     * @param description: description of the collection
//     * @param links: list of links shared in a collection
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = PATCH)
//    fun editCollection(
//        collection: com.tecknobit.refy.services.collections.entity.LinksCollection,
//        color: String,
//        title: String,
//        description: String,
//        links: List<String>
//    ): JSONObject {
//        return editCollection(
//            collectionId = collection.id,
//            color = color,
//            title = title,
//            description = description,
//            links = links
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a collection
//     *
//     * @param collectionId The identifier of the collection to edit
//     * @param color: color of the collection
//     * @param title Title of the collection
//     * @param description: description of the collection
//     * @param links: list of links shared in a collection
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = PATCH)
//    fun editCollection(
//        collectionId: String,
//        color: String,
//        title: String,
//        description: String,
//        links: List<String>
//    ): JSONObject {
//        val payload = createCollectionPayload(
//            color = color,
//            title = title,
//            description = description,
//            links = links
//        )
//        return execPatch(
//            endpoint = assembleCollectionsEndpointPath(
//                subEndpoint = collectionId
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to create a payload for the collection creation/edit actions
//     *
//     * @param color: color of the collection
//     * @param title Title of the collection
//     * @param description: description of the collection
//     * @param links: list of links shared in a collection
//     *
//     * @return the payload as [Params]
//     *
//     */
//    private fun createCollectionPayload(
//        color: String,
//        title: String,
//        description: String,
//        links: List<String>
//    ) : Params {
//        val payload = Params()
//        payload.addParam(COLLECTION_COLOR_KEY, color)
//        payload.addParam(TITLE_KEY, title)
//        payload.addParam(DESCRIPTION_KEY, description)
//        payload.addParam(LINKS_KEY, JSONArray(links))
//        return payload
//    }
//
//    /**
//     * Function to execute the request to manage the links shared with the collection
//     *
//     * @param collection The collection where manage the shared link
//     * @param links The list of links shared with the collection
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/links", method = PUT)
//    fun manageCollectionLinks(
//        collection: com.tecknobit.refy.services.collections.entity.LinksCollection,
//        links: List<String>
//    ) : JSONObject {
//        return manageCollectionLinks(
//            collectionId = collection.id,
//            links = links
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the links shared with the collection
//     *
//     * @param collectionId The identifier of the collection where manage the shared link
//     * @param links The list of links shared with the collection
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/links", method = PUT)
//    fun manageCollectionLinks(
//        collectionId: String,
//        links: List<String>
//    ) : JSONObject {
//        val payload = Params()
//        payload.addParam(LINKS_KEY, JSONArray(links))
//        return execPut(
//            endpoint = assembleCollectionsEndpointPath(
//                subEndpoint = "$collectionId/$LINKS_KEY"
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the teams where the collection is shared
//     *
//     * @param collection The collection where manage the teams list
//     * @param teams The list of the teams where the collection is shared
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/teams", method = PUT)
//    fun manageCollectionTeams(
//        collection: com.tecknobit.refy.services.collections.entity.LinksCollection,
//        teams: List<String>
//    ) : JSONObject {
//        return manageCollectionTeams(
//            collectionId = collection.id,
//            teams = teams
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the teams where the collection is shared
//     *
//     * @param collectionId The identifier of the collection where manage the teams list
//     * @param teams The list of the teams where the collection is shared
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}/teams", method = PUT)
//    fun manageCollectionTeams(
//        collectionId: String,
//        teams: List<String>
//    ) : JSONObject {
//        val payload = Params()
//        payload.addParam(TEAMS_KEY, JSONArray(teams))
//        return execPut(
//            endpoint = assembleCollectionsEndpointPath(
//                subEndpoint = "$collectionId/$TEAMS_KEY"
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to get a collection
//     *
//     * @param collection The collection to get
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = GET)
//    fun getCollection(
//        collection: com.tecknobit.refy.services.collections.entity.LinksCollection
//    ): JSONObject {
//        return getCollection(
//            collectionId = collection.id
//        )
//    }
//
//    /**
//     * Function to execute the request to get a collection
//     *
//     * @param collectionId The identifier of the collection to get
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = GET)
//    fun getCollection(
//        collectionId: String
//    ): JSONObject {
//        return execGet(
//            endpoint = assembleCollectionsEndpointPath(
//                subEndpoint = collectionId
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a collection
//     *
//     * @param collection The collection to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = DELETE)
//    fun deleteCollection(
//        collection: com.tecknobit.refy.services.collections.entity.LinksCollection
//    ): JSONObject {
//        return deleteCollection(
//            collectionId = collection.id
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a collection
//     *
//     * @param collectionId The identifier of the  collection to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/collections/{collection_id}", method = DELETE)
//    fun deleteCollection(
//        collectionId: String
//    ): JSONObject {
//        return execDelete(
//            endpoint = assembleCollectionsEndpointPath(
//                subEndpoint = collectionId
//            )
//        )
//    }
//
//    /**
//     * Function to assemble the endpoint to make the request to the collections controller
//     *
//     * @param subEndpoint The sub-endpoint path of the url
//     * @param query The query to attach to the request
//     *
//     * @return an endpoint to make the request as [String]
//     */
//    private fun assembleCollectionsEndpointPath(
//        subEndpoint: String = "",
//        query: String = ""
//    ): String {
//        return assembleCustomEndpointPath(
//            customEndpoint = COLLECTIONS_KEY,
//            subEndpoint = subEndpoint,
//            query = query
//        )
//    }
//
//    /**
//     * Function to execute the request to get the teams where the user is a member
//     *
//     * @param ownedOnly: whether to get only the teams where the user is the owner
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams", method = GET)
//    fun getTeams(
//        ownedOnly: Boolean = false
//    ) : JSONObject {
//        return execGet(
//            endpoint = assembleTeamsEndpointPath(
//                query = createOwnedOnlyQuery(
//                    ownedOnly = ownedOnly
//                )
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to get the potential members to add in a team 
//     * 
//     * No-any params required
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/members", method = GET)
//    fun getPotentialMembers() : JSONObject {
//        return execGet(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = MEMBERS_KEY
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to create a team
//     *
//     * @param title Title of the team
//     * @param logoPic The logo of the team
//     * @param description: description of the team
//     * @param members: list of the members in the team
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams", method = POST)
//    fun createTeam(
//        title: String,
//        logoPic: String,
//        description: String,
//        members: List<String>
//    ) : JSONObject {
//        val body = createTeamPayload(
//            title = title,
//            logoPic = File(logoPic),
//            description = description,
//            members = members
//        )
//        return execMultipartRequest(
//            endpoint = assembleTeamsEndpointPath(),
//            body = body
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a team
//     *
//     * @param team The team to edit
//     * @param title Title of the team
//     * @param logoPic The logo of the team
//     * @param description: description of the team
//     * @param members: list of the members in the team
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = POST)
//    fun editTeam(
//        team: com.tecknobit.refy.services.teams.entities.Team,
//        title: String,
//        logoPic: String,
//        description: String,
//        members: List<String>
//    ) : JSONObject {
//        return editTeam(
//            teamId = team.id,
//            title = title,
//            logoPic = logoPic,
//            description = description,
//            members = members
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a team
//     *
//     * @param teamId The identifier of the team to edit
//     * @param title Title of the team
//     * @param logoPic The logo of the team
//     * @param description: description of the team
//     * @param members: list of the members in the team
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = POST)
//    fun editTeam(
//        teamId: String,
//        title: String,
//        logoPic: String,
//        description: String,
//        members: List<String>
//    ) : JSONObject {
//        val body = createTeamPayload(
//            title = title,
//            logoPic = if(logoPic.contains(teamId))
//                null
//            else
//                File(logoPic),
//            description = description,
//            members = members
//        )
//        return execMultipartRequest(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = teamId
//            ),
//            body = body
//        )
//    }
//
//    /**
//     * Function to create a payload for the team creation/edit actions
//     *
//     * @param title Title of the team
//     * @param logoPic The logo of the team
//     * @param description: description of the team
//     * @param members: list of the members in the team
//     *
//     * @return the payload as [Params]
//     *
//     */
//    private fun createTeamPayload(
//        title: String,
//        logoPic: File?,
//        description: String,
//        members: List<String>
//    ) : MultipartBody {
//        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart(
//                TITLE_KEY,
//                title,
//            )
//            .addFormDataPart(
//                DESCRIPTION_KEY,
//                description,
//            )
//            .addFormDataPart(
//                MEMBERS_KEY,
//                JSONArray(members).toString(),
//            )
//        logoPic?.let {
//            body.addFormDataPart(
//                LOGO_PIC_KEY,
//                logoPic.name,
//                logoPic.readBytes().toRequestBody("*/*".toMediaType())
//            )
//        }
//        return body.build()
//    }
//
//    /**
//     * Function to execute the request to manage the links shared with the team
//     *
//     * @param team The team where manage the shared link
//     * @param links The list of links shared with the team
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/links", method = PUT)
//    fun manageTeamLinks(
//        team: com.tecknobit.refy.services.teams.entities.Team,
//        links: List<String>
//    ) : JSONObject {
//        return manageTeamLinks(
//            teamId = team.id,
//            links = links
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the links shared with the team
//     *
//     * @param teamId The team identifier where manage the shared link
//     * @param links The list of links shared with the team
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/links", method = PUT)
//    fun manageTeamLinks(
//        teamId: String,
//        links: List<String>
//    ) : JSONObject {
//        val payload = Params()
//        payload.addParam(LINKS_KEY, JSONArray(links))
//        return execPut(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = "$teamId/$LINKS_KEY"
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the collections shared with the team
//     *
//     * @param team The team where manage the shared collections
//     * @param collections The list of collections shared with the team
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/collections", method = PUT)
//    fun manageTeamCollections(
//        team: com.tecknobit.refy.services.teams.entities.Team,
//        collections: List<String>
//    ) : JSONObject {
//        return manageTeamCollections(
//            teamId = team.id,
//            collections = collections
//        )
//    }
//
//    /**
//     * Function to execute the request to manage the collections shared with the team
//     *
//     * @param teamId The team identifier where manage the shared collections
//     * @param collections The list of collections shared with the team
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/collections", method = PUT)
//    fun manageTeamCollections(
//        teamId: String,
//        collections: List<String>
//    ) : JSONObject {
//        val payload = Params()
//        payload.addParam(COLLECTIONS_KEY, JSONArray(collections))
//        return execPut(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = "$teamId/$COLLECTIONS_KEY"
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to get a team
//     *
//     * @param team The team to get
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = GET)
//    fun getTeam(
//        team: com.tecknobit.refy.services.teams.entities.Team
//    ): JSONObject {
//        return getTeam(
//            teamId = team.id
//        )
//    }
//
//    /**
//     * Function to execute the request to get a team
//     *
//     * @param teamId The identifier of the team to get
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = GET)
//    fun getTeam(
//        teamId: String
//    ): JSONObject {
//        return execGet(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = teamId
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to change the role of a member
//     *
//     * @param team The team to where change the member role
//     * @param member The member to change its role
//     * @param role The role of the member
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/members/{member_id}/updateRole", method = PATCH)
//    fun changeMemberRole(
//        team: com.tecknobit.refy.services.teams.entities.Team,
//        member: RefyTeamMember,
//        role TeamRole
//    ): JSONObject {
//        return changeMemberRole(
//            teamId = team.id,
//            memberId = member.id,
//            role = role
//        )
//    }
//
//    /**
//     * Function to execute the request to change the role of a member
//     *
//     * @param teamId The identifier of the team to where change the member role
//     * @param memberId The identifier of the member to change its role
//     * @param role The role of the member
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/members/{member_id}/updateRole", method = PATCH)
//    fun changeMemberRole(
//        teamId: String,
//        memberId: String,
//        role TeamRole
//    ): JSONObject {
//        val payload = Params()
//        payload.addParam(TEAM_ROLE_KEY, role)
//        return execPatch(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = "$teamId/$MEMBERS_KEY/$memberId$UPDATE_MEMBER_ROLE_ENDPOINT"
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to remove a member from the team
//     *
//     * @param team The team to where remove the member
//     * @param member The member to remove
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/members/{member_id}", method = DELETE)
//    fun removeMember(
//        team: com.tecknobit.refy.services.teams.entities.Team,
//        member: RefyTeamMember
//    ): JSONObject {
//        return removeMember(
//            teamId = team.id,
//            memberId = member.id
//        )
//    }
//
//    /**
//     * Function to execute the request to remove a member from the team
//     *
//     * @param teamId The identifier of the team to where remove the member
//     * @param memberId The identifier of the member to remove
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/members/{member_id}", method = DELETE)
//    fun removeMember(
//        teamId: String,
//        memberId: String
//    ): JSONObject {
//        return execDelete(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = "$teamId/$MEMBERS_KEY/$memberId"
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to leave from a team
//     *
//     * @param team The team from leave
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/leave}", method = DELETE)
//    fun leave(
//        team: com.tecknobit.refy.services.teams.entities.Team
//    ): JSONObject {
//        return leave(
//            teamId = team.id
//        )
//    }
//
//    /**
//     * Function to execute the request to leave from a team
//     *
//     * @param teamId The identifier of the team from leave
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}/leave}", method = DELETE)
//    fun leave(
//        teamId: String
//    ): JSONObject {
//        return execDelete(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = "$teamId$LEAVE_ENDPOINT"
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a team
//     *
//     * @param team The team to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = DELETE)
//    fun deleteTeam(
//        team: com.tecknobit.refy.services.teams.entities.Team
//    ): JSONObject {
//        return deleteTeam(
//            teamId = team.id
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a team
//     *
//     * @param teamId The identifier of the team to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/teams/{team_id}", method = DELETE)
//    fun deleteTeam(
//        teamId: String
//    ): JSONObject {
//        return execDelete(
//            endpoint = assembleTeamsEndpointPath(
//                subEndpoint = teamId
//            )
//        )
//    }
//
//    /**
//     * Function to assemble the endpoint to make the request to the teams controller
//     *
//     * @param subEndpoint The sub-endpoint path of the url
//     * @param query The query to attach to the request
//     *
//     * @return an endpoint to make the request as [String]
//     */
//    private fun assembleTeamsEndpointPath(
//        subEndpoint: String = "",
//        query: String = ""
//    ): String {
//        return assembleCustomEndpointPath(
//            customEndpoint = TEAMS_KEY,
//            subEndpoint = subEndpoint,
//            query = query
//        )
//    }
//
//    /**
//     * Function to assemble the query to manage the owned only query
//     *
//     * @param ownedOnly: whether to get only the teams where the user is the owner
//     *
//     * @return an endpoint to make the request as [String]
//     */
//    private fun createOwnedOnlyQuery(
//        ownedOnly: Boolean
//    ) : String {
//        val query = Params()
//        query.addParam(OWNED_ONLY_KEY, ownedOnly.toString())
//        return query.createQueryString()
//    }
//
//    /**
//     * Function to execute the request to get the custom links of the user
//     *
//     * No-any params
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks", method = GET)
//    fun getCustomLinks() : JSONObject {
//        return execGet(
//            endpoint = assembleCustomLinksEndpointPath()
//        )
//    }
//
//    /**
//     * Function to execute the request to create a custom link
//     *
//     * @param title Title of the link
//     * @param description: description of the link
//     * @param resources The resources to share with the link
//     * @param fields The fields used to protect the [resources] with a validation form
//     * @param hasUniqueAccess: whether the link, when requested for the first time, must be deleted and no more accessible
//     * @param expiredTime: when the link expires and automatically deleted
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks", method = POST)
//    fun createCustomLink(
//        title: String,
//        description: String,
//        resources: Map<String, String>,
//        fields: Map<String, String>,
//        hasUniqueAccess: Boolean = false,
//        expiredTime: ExpiredTime = ExpiredTime.NO_EXPIRATION
//    ): JSONObject {
//        val payload = createCustomLinkPayload(
//            title = title,
//            description = description,
//            resources = resources,
//            fields = fields,
//            hasUniqueAccess = hasUniqueAccess,
//            expiredTime = expiredTime
//        )
//        return execPost(
//            endpoint = assembleCustomLinksEndpointPath(),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a custom link
//     *
//     * @param link The link to edit
//     * @param title Title of the link
//     * @param description: description of the link
//     * @param resources The resources to share with the link
//     * @param fields The fields used to protect the [resources] with a validation form
//     * @param hasUniqueAccess: whether the link, when requested for the first time, must be deleted and no more accessible
//     * @param expiredTime: when the link expires and automatically deleted
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = PATCH)
//    fun editCustomLink(
//        link: com.tecknobit.refy.services.customlinks.entity.CustomRefyLink,
//        title: String,
//        description: String,
//        resources: Map<String, String>,
//        fields: Map<String, String>,
//        hasUniqueAccess: Boolean = false,
//        expiredTime: ExpiredTime = ExpiredTime.NO_EXPIRATION
//    ): JSONObject {
//        return editCustomLink(
//            linkId = link.id,
//            title = title,
//            description = description,
//            resources = resources,
//            fields = fields,
//            hasUniqueAccess = hasUniqueAccess,
//            expiredTime = expiredTime
//        )
//    }
//
//    /**
//     * Function to execute the request to edit a custom link
//     *
//     * @param linkId The identifier of the link to edit
//     * @param title Title of the link
//     * @param description: description of the link
//     * @param resources The resources to share with the link
//     * @param fields The fields used to protect the [resources] with a validation form
//     * @param hasUniqueAccess: whether the link, when requested for the first time, must be deleted and no more accessible
//     * @param expiredTime: when the link expires and automatically deleted
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = PATCH)
//    fun editCustomLink(
//        linkId: String,
//        title: String,
//        description: String,
//        resources: Map<String, String>,
//        fields: Map<String, String>,
//        hasUniqueAccess: Boolean = false,
//        expiredTime: ExpiredTime = ExpiredTime.NO_EXPIRATION
//    ): JSONObject {
//        val payload = createCustomLinkPayload(
//            title = title,
//            description = description,
//            resources = resources,
//            fields = fields,
//            hasUniqueAccess = hasUniqueAccess,
//            expiredTime = expiredTime
//        )
//        return execPatch(
//            endpoint = assembleCustomLinksEndpointPath(
//                subEndpoint = linkId
//            ),
//            payload = payload
//        )
//    }
//
//    /**
//     * Function to create a payload for the custom link creation/edit actions
//     *
//     * @param title Title of the link
//     * @param description: description of the link
//     * @param resources The resources to share with the link
//     * @param fields The fields used to protect the [resources] with a validation form
//     * @param hasUniqueAccess: whether the link, when requested for the first time, must be deleted and no more accessible
//     * @param expiredTime: when the link expires and automatically deleted
//     *
//     * @return the payload as [Params]
//     *
//     */
//    private fun createCustomLinkPayload(
//        title: String,
//        description: String,
//        resources: Map<String, String>,
//        fields: Map<String, String>,
//        hasUniqueAccess: Boolean,
//        expiredTime: ExpiredTime
//    ) : Params {
//        val payload = Params()
//        payload.addParam(TITLE_KEY, title)
//        payload.addParam(DESCRIPTION_KEY, description)
//        payload.addParam(RESOURCES_KEY, JSONObject(resources))
//        payload.addParam(FIELDS_KEY, JSONObject(fields))
//        payload.addParam(UNIQUE_ACCESS_KEY, hasUniqueAccess)
//        payload.addParam(EXPIRED_TIME_KEY, expiredTime)
//        return payload
//    }
//
//    /**
//     * Function to execute the request to get a custom link
//     *
//     * @param link The link to get
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = GET)
//    fun getCustomLink(
//        link: com.tecknobit.refy.services.customlinks.entity.CustomRefyLink
//    ): JSONObject {
//        return getCustomLink(
//            linkId = link.id
//        )
//    }
//
//    /**
//     * Function to execute the request to get a custom link
//     *
//     * @param linkId The identifier of the link to get
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = GET)
//    fun getCustomLink(
//        linkId: String
//    ): JSONObject {
//        return execGet(
//            endpoint = assembleCustomLinksEndpointPath(
//                subEndpoint = linkId
//            )
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a custom link
//     *
//     * @param link The link to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @WrappedRequest
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = DELETE)
//    fun deleteCustomLink(
//        link: com.tecknobit.refy.services.customlinks.entity.CustomRefyLink
//    ): JSONObject {
//        return deleteCustomLink(
//            linkId = link.id
//        )
//    }
//
//    /**
//     * Function to execute the request to delete a custom link
//     *
//     * @param linkId The identifier of the link to delete
//     *
//     * @return the result of the request as [JSONObject]
//     *
//     */
//    @RequestPath(path = "/api/v1/users/{user_id}/customLinks/{link_id}", method = DELETE)
//    fun deleteCustomLink(
//        linkId: String
//    ): JSONObject {
//        return execDelete(
//            endpoint = assembleCustomLinksEndpointPath(
//                subEndpoint = linkId
//            )
//        )
//    }
//
//    /**
//     * Function to assemble the endpoint to make the request to the custom links controller
//     *
//     * @param subEndpoint The sub-endpoint path of the url
//     * @param query The query to attach to the request
//     *
//     * @return an endpoint to make the request as [String]
//     */
//    private fun assembleCustomLinksEndpointPath(
//        subEndpoint: String = "",
//        query: String = ""
//    ): String {
//        return assembleCustomEndpointPath(
//            customEndpoint = CUSTOM_LINKS_ENDPOINT,
//            subEndpoint = subEndpoint,
//            query = query
//        )
//    }
//
//    /**
//     * Function to assemble the endpoint to make the request to the custom controllers
//     *
//     * @param customEndpoint The custom endpoint of the request, the main part of the complete url
//     * @param subEndpoint The sub-endpoint path of the url
//     * @param query The query to attach to the request
//     *
//     * @return an endpoint to make the request as [String]
//     */
//    private fun assembleCustomEndpointPath(
//        customEndpoint: String,
//        subEndpoint: String = "",
//        query: String = ""
//    ): String {
//        val subPath = if(subEndpoint.isNotBlank())
//            "/$subEndpoint"
//        else
//            subEndpoint
//        val requestUrl = "$customEndpoint$subPath$query"
//        return assembleUsersEndpointPath(
//            endpoint = if(customEndpoint.startsWith("/"))
//                requestUrl
//            else
//                "/$requestUrl"
//        )
//    }
//
//}