package com.tecknobit.refycore.helpers

import com.tecknobit.apimanager.apis.APIRequest.Params
import com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY
import com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.SIGN_UP_ENDPOINT
import com.tecknobit.equinox.environment.helpers.EquinoxRequester
import com.tecknobit.equinox.environment.records.EquinoxUser.*
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.isLanguageValid
import com.tecknobit.refycore.records.LinksCollection
import com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY
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

    /**
     * Function to assemble the endpoint to make the request to the users controller
     *
     * @param subEndpoint: the endpoint path of the url
     *
     * @return an endpoint to make the request as [String]
     */
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

}