package com.tecknobit.refycore.helpers

import com.tecknobit.apimanager.apis.APIRequest.Params
import com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY
import com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.SIGN_UP_ENDPOINT
import com.tecknobit.equinox.environment.helpers.EquinoxRequester
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.isLanguageValid
import com.tecknobit.refycore.records.LinksCollection
import com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY
import com.tecknobit.refycore.records.LinksCollection.COLLECTION_COLOR_KEY
import com.tecknobit.refycore.records.RefyItem.TITLE_KEY
import com.tecknobit.refycore.records.RefyUser.*
import com.tecknobit.refycore.records.links.RefyLink
import com.tecknobit.refycore.records.links.RefyLink.DESCRIPTION_KEY
import com.tecknobit.refycore.records.links.RefyLink.REFERENCE_LINK_KEY
import org.json.JSONArray
import org.json.JSONObject

class RefyRequester(
    host: String,
    userId: String? = null,
    userToken: String? = null
) : EquinoxRequester(
    host = host,
    userId = userId,
    userToken = userToken,
    debugMode = true,
    connectionTimeout = 5000,
    connectionErrorMessage = DEFAULT_CONNECTION_ERROR_MESSAGE
) {

    fun signUp(
        serverSecret: String,
        tagName: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        language: String
    ): JSONObject {
        val payload = Params()
        payload.addParam(SERVER_SECRET_KEY, serverSecret)
        payload.addParam(TAG_NAME_KEY, tagName)
        payload.addParam(NAME_KEY, name)
        payload.addParam(SURNAME_KEY, surname)
        payload.addParam(EMAIL_KEY, email)
        payload.addParam(PASSWORD_KEY, password)
        payload.addParam(
            LANGUAGE_KEY,
            if (!isLanguageValid(language))
                DEFAULT_LANGUAGE
            else
                language
        )
        return execPost(
            endpoint = SIGN_UP_ENDPOINT,
            payload = payload
        )
    }

    fun getLinks() : JSONObject {
        return execGet(
            endpoint = assembleLinksEndpointPath()
        )
    }

    fun createLink(
        referenceLink: String,
        description: String
    ): JSONObject {
        val payload = createLinkPayload(
            referenceLink = referenceLink,
            description = description
        )
        return execPost(
            endpoint = assembleLinksEndpointPath(),
            payload = payload
        )
    }

    fun editLink(
        link: RefyLink,
        referenceLink: String,
        description: String
    ): JSONObject {
        return editLink(
            linkId = link.id,
            referenceLink = referenceLink,
            description = description
        )
    }

    fun editLink(
        linkId: String,
        referenceLink: String,
        description: String
    ): JSONObject {
        val payload = createLinkPayload(
            referenceLink = referenceLink,
            description = description
        )
        return execPatch(
            endpoint = assembleLinksEndpointPath(
                subEndpoint = linkId
            ),
            payload = payload
        )
    }

    private fun createLinkPayload(
        referenceLink: String,
        description: String
    ) : Params {
        val payload = Params()
        payload.addParam(REFERENCE_LINK_KEY, referenceLink)
        payload.addParam(DESCRIPTION_KEY, description)
        return payload;
    }

    fun manageLinkCollections(
        link: RefyLink,
        collections: List<String>
    ) : JSONObject {
        return manageLinkCollections(
            linkId = link.id,
            collections = collections
        )
    }

    fun manageLinkCollections(
        linkId: String,
        collections: List<String>
    ) : JSONObject {
        val payload = Params()
        payload.addParam(COLLECTIONS_KEY, JSONArray(collections))
        return execPut(
            endpoint = assembleLinksEndpointPath(
                subEndpoint = "$linkId/$COLLECTIONS_KEY"
            ),
            payload = payload
        )
    }

    fun manageLinkTeams(
        link: RefyLink,
        teams: List<String>
    ) : JSONObject {
        return manageLinkTeams(
            linkId = link.id,
            teams = teams
        )
    }

    fun manageLinkTeams(
        linkId: String,
        teams: List<String>
    ) : JSONObject {
        val payload = Params()
        payload.addParam(TEAMS_KEY, JSONArray(teams))
        return execPut(
            endpoint = assembleLinksEndpointPath(
                subEndpoint = "$linkId/$TEAMS_KEY"
            ),
            payload = payload
        )
    }

    fun deleteLink(
        link: RefyLink
    ): JSONObject {
        return execDelete(
            endpoint = assembleLinksEndpointPath(
                subEndpoint = link.id
            )
        )
    }

    fun deleteLink(
        linkId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleLinksEndpointPath(
                subEndpoint = linkId
            )
        )
    }

    private fun assembleLinksEndpointPath(
        subEndpoint: String = ""
    ): String {
        val subPath = if(subEndpoint.isNotBlank())
            "/$subEndpoint"
        else
            subEndpoint
        return assembleUsersEndpointPath(
            endpoint = "/$LINKS_KEY$subPath"
        )
    }

    fun getCollections() : JSONObject {
        return execGet(
            endpoint = assembleCollectionsEndpointPath()
        )
    }

    fun createCollection(
        color: String,
        title: String,
        description: String,
        links: List<String>
    ): JSONObject {
        val payload = createCollectionPayload(
            color = color,
            title = title,
            description = description,
            links = links
        )
        return execPost(
            endpoint = assembleCollectionsEndpointPath(),
            payload = payload
        )
    }

    fun editCollection(
        collection: LinksCollection,
        color: String,
        title: String,
        description: String,
        links: List<String>
    ): JSONObject {
        return editCollection(
            collectionId = collection.id,
            color = color,
            title = title,
            description = description,
            links = links
        )
    }

    fun editCollection(
        collectionId: String,
        color: String,
        title: String,
        description: String,
        links: List<String>
    ): JSONObject {
        val payload = createCollectionPayload(
            color = color,
            title = title,
            description = description,
            links = links
        )
        return execPatch(
            endpoint = assembleCollectionsEndpointPath(
                subEndpoint = collectionId
            ),
            payload = payload
        )
    }

    private fun createCollectionPayload(
        color: String,
        title: String,
        description: String,
        links: List<String>
    ) : Params {
        val payload = Params()
        payload.addParam(COLLECTION_COLOR_KEY, color)
        payload.addParam(TITLE_KEY, title)
        payload.addParam(DESCRIPTION_KEY, description)
        payload.addParam(LINKS_KEY, JSONArray(links))
        return payload;
    }

    fun manageCollectionLinks(
        collection: LinksCollection,
        links: List<String>
    ) : JSONObject {
        return manageCollectionLinks(
            collectionId = collection.id,
            links = links
        )
    }

    fun manageCollectionLinks(
        collectionId: String,
        links: List<String>
    ) : JSONObject {
        val payload = Params()
        payload.addParam(LINKS_KEY, JSONArray(links))
        return execPut(
            endpoint = assembleCollectionsEndpointPath(
                subEndpoint = "$collectionId/$LINKS_KEY"
            ),
            payload = payload
        )
    }

    fun manageCollectionTeams(
        collection: LinksCollection,
        teams: List<String>
    ) : JSONObject {
        return manageCollectionTeams(
            collectionId = collection.id,
            teams = teams
        )
    }

    fun manageCollectionTeams(
        collectionId: String,
        teams: List<String>
    ) : JSONObject {
        val payload = Params()
        payload.addParam(TEAMS_KEY, JSONArray(teams))
        return execPut(
            endpoint = assembleCollectionsEndpointPath(
                subEndpoint = "$collectionId/$TEAMS_KEY"
            ),
            payload = payload
        )
    }

    fun getCollection(
        collection: LinksCollection
    ): JSONObject {
        return getCollection(
            collectionId = collection.id
        )
    }

    fun getCollection(
        collectionId: String
    ): JSONObject {
        return execGet(
            endpoint = assembleCollectionsEndpointPath(
                subEndpoint = collectionId
            )
        )
    }

    fun deleteCollection(
        collection: LinksCollection
    ): JSONObject {
        return deleteCollection(
            collectionId = collection.id
        )
    }

    fun deleteCollection(
        collectionId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleCollectionsEndpointPath(
                subEndpoint = collectionId
            )
        )
    }

    private fun assembleCollectionsEndpointPath(
        subEndpoint: String = ""
    ): String {
        val subPath = if(subEndpoint.isNotBlank())
            "/$subEndpoint"
        else
            subEndpoint
        return assembleUsersEndpointPath(
            endpoint = "/$COLLECTIONS_KEY$subPath"
        )
    }

}