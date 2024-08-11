package com.tecknobit.refycore.helpers

import com.tecknobit.apimanager.apis.APIRequest.Params
import com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY
import com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.SIGN_UP_ENDPOINT
import com.tecknobit.equinox.environment.helpers.EquinoxRequester
import com.tecknobit.equinox.inputs.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.equinox.inputs.InputValidator.isLanguageValid
import com.tecknobit.refycore.helpers.RefyEndpointsSet.*
import com.tecknobit.refycore.records.LinksCollection
import com.tecknobit.refycore.records.LinksCollection.COLLECTIONS_KEY
import com.tecknobit.refycore.records.LinksCollection.COLLECTION_COLOR_KEY
import com.tecknobit.refycore.records.RefyItem.TITLE_KEY
import com.tecknobit.refycore.records.RefyUser.*
import com.tecknobit.refycore.records.Team
import com.tecknobit.refycore.records.Team.LOGO_PIC_KEY
import com.tecknobit.refycore.records.Team.MEMBERS_KEY
import com.tecknobit.refycore.records.Team.RefyTeamMember
import com.tecknobit.refycore.records.Team.RefyTeamMember.TEAM_ROLE_KEY
import com.tecknobit.refycore.records.Team.RefyTeamMember.TeamRole
import com.tecknobit.refycore.records.links.CustomRefyLink
import com.tecknobit.refycore.records.links.CustomRefyLink.*
import com.tecknobit.refycore.records.links.RefyLink
import com.tecknobit.refycore.records.links.RefyLink.DESCRIPTION_KEY
import com.tecknobit.refycore.records.links.RefyLink.REFERENCE_LINK_KEY
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.aspectj.weaver.Member
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

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
        return payload
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
        return deleteLink(
            linkId = link.id
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
        return assembleCustomEndpointPath(
            customEndpoint = LINKS_KEY,
            subEndpoint = subEndpoint
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
        return payload
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
        return assembleCustomEndpointPath(
            customEndpoint = COLLECTIONS_KEY,
            subEndpoint = subEndpoint
        )
    }

    fun getTeams() : JSONObject {
        return execGet(
            endpoint = assembleTeamsEndpointPath()
        )
    }

    fun createTeam(
        title: String,
        logoPic: File,
        description: String,
        members: List<String>
    ) : JSONObject {
        val body = createTeamPayload(
            title = title,
            logoPic = logoPic,
            description = description,
            members = members
        )
        return execMultipartRequest(
            endpoint = assembleTeamsEndpointPath(),
            body = body
        )
    }

    fun editTeam(
        team: Team,
        title: String,
        logoPic: File,
        description: String,
        members: List<String>
    ) : JSONObject {
        return editTeam(
            teamId = team.id,
            title = title,
            logoPic = logoPic,
            description = description,
            members = members
        )
    }

    fun editTeam(
        teamId: String,
        title: String,
        logoPic: File,
        description: String,
        members: List<String>
    ) : JSONObject {
        val body = createTeamPayload(
            title = title,
            logoPic = logoPic,
            description = description,
            members = members
        )
        return execMultipartRequest(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = teamId
            ),
            body = body
        )
    }

    private fun createTeamPayload(
        title: String,
        logoPic: File,
        description: String,
        members: List<String>
    ) : MultipartBody {
        return MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                TITLE_KEY,
                title,
            )
            .addFormDataPart(
                LOGO_PIC_KEY,
                logoPic.name,
                logoPic.readBytes().toRequestBody("*/*".toMediaType())
            )
            .addFormDataPart(
                DESCRIPTION_KEY,
                description,
            )
            .addFormDataPart(
                MEMBERS_KEY,
                JSONArray(members).toString(),
            )
            .build()
    }

    fun manageTeamLinks(
        team: Team,
        links: List<String>
    ) : JSONObject {
        return manageTeamLinks(
            teamId = team.id,
            links = links
        )
    }

    fun manageTeamLinks(
        teamId: String,
        links: List<String>
    ) : JSONObject {
        val payload = Params()
        payload.addParam(LINKS_KEY, JSONArray(links))
        return execPut(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = "$teamId/$LINKS_KEY"
            ),
            payload = payload
        )
    }

    fun manageTeamCollections(
        team: Team,
        collections: List<String>
    ) : JSONObject {
        return manageTeamCollections(
            teamId = team.id,
            collections = collections
        )
    }

    fun manageTeamCollections(
        teamId: String,
        collections: List<String>
    ) : JSONObject {
        val payload = Params()
        payload.addParam(COLLECTIONS_KEY, JSONArray(collections))
        return execPut(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = "$teamId/$COLLECTIONS_KEY"
            ),
            payload = payload
        )
    }

    fun getTeam(
        team: Team
    ): JSONObject {
        return getTeam(
            teamId = team.id
        )
    }

    fun getTeam(
        teamId: String
    ): JSONObject {
        return execGet(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = teamId
            )
        )
    }

    fun updateMemberRole(
        team: Team,
        member: RefyTeamMember,
        role: TeamRole
    ): JSONObject {
        return updateMemberRole(
            teamId = team.id,
            memberId = member.id,
            role = role
        )
    }

    fun updateMemberRole(
        teamId: String,
        memberId: String,
        role: TeamRole
    ): JSONObject {
        val payload = Params()
        payload.addParam(TEAM_ROLE_KEY, role)
        return execPatch(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = "$teamId/$MEMBERS_KEY/$memberId$UPDATE_MEMBER_ROLE_ENDPOINT"
            ),
            payload = payload
        )
    }

    fun removeMember(
        team: Team,
        member: RefyTeamMember
    ): JSONObject {
        return removeMember(
            teamId = team.id,
            memberId = member.id
        )
    }

    fun removeMember(
        teamId: String,
        memberId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = "$teamId/$MEMBERS_KEY/$memberId"
            )
        )
    }

    fun leave(
        team: Team
    ): JSONObject {
        return leave(
            teamId = team.id
        )
    }

    fun leave(
        teamId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = "$teamId$LEAVE_ENDPOINT"
            )
        )
    }

    fun deleteTeam(
        team: Team
    ): JSONObject {
        return leave(
            teamId = team.id
        )
    }

    fun deleteTeam(
        teamId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleTeamsEndpointPath(
                subEndpoint = teamId
            )
        )
    }

    private fun assembleTeamsEndpointPath(
        subEndpoint: String = ""
    ): String {
        return assembleCustomEndpointPath(
            customEndpoint = TEAMS_KEY,
            subEndpoint = subEndpoint
        )
    }

    fun getCustomLinks() : JSONObject {
        return execGet(
            endpoint = assembleCustomLinksEndpointPath()
        )
    }

    fun createCustomLink(
        title: String,
        description: String,
        resources: Map<String, String>,
        fields: Map<String, String>,
        hasUniqueAccess: Boolean = false,
        expiredTime: ExpiredTime = ExpiredTime.NO_EXPIRATION
    ): JSONObject {
        val payload = createCustomLinkPayload(
            title = title,
            description = description,
            resources = resources,
            fields = fields,
            hasUniqueAccess = hasUniqueAccess,
            expiredTime = expiredTime
        )
        return execPost(
            endpoint = assembleCustomLinksEndpointPath(),
            payload = payload
        )
    }

    fun editCustomLink(
        link: CustomRefyLink,
        title: String,
        description: String,
        resources: Map<String, String>,
        fields: Map<String, String>,
        hasUniqueAccess: Boolean = false,
        expiredTime: ExpiredTime = ExpiredTime.NO_EXPIRATION
    ): JSONObject {
        return editCustomLink(
            linkId = link.id,
            title = title,
            description = description,
            resources = resources,
            fields = fields,
            hasUniqueAccess = hasUniqueAccess,
            expiredTime = expiredTime
        )
    }

    fun editCustomLink(
        linkId: String,
        title: String,
        description: String,
        resources: Map<String, String>,
        fields: Map<String, String>,
        hasUniqueAccess: Boolean = false,
        expiredTime: ExpiredTime = ExpiredTime.NO_EXPIRATION
    ): JSONObject {
        val payload = createCustomLinkPayload(
            title = title,
            description = description,
            resources = resources,
            fields = fields,
            hasUniqueAccess = hasUniqueAccess,
            expiredTime = expiredTime
        )
        return execPatch(
            endpoint = assembleCustomLinksEndpointPath(
                subEndpoint = linkId
            ),
            payload = payload
        )
    }

    private fun createCustomLinkPayload(
        title: String,
        description: String,
        resources: Map<String, String>,
        fields: Map<String, String>,
        hasUniqueAccess: Boolean,
        expiredTime: ExpiredTime
    ) : Params {
        val payload = Params()
        payload.addParam(TITLE_KEY, title)
        payload.addParam(DESCRIPTION_KEY, description)
        payload.addParam(RESOURCES_KEY, JSONObject(resources))
        payload.addParam(FIELDS_KEY, JSONObject(fields))
        payload.addParam(UNIQUE_ACCESS_KEY, hasUniqueAccess)
        payload.addParam(EXPIRED_TIME_KEY, expiredTime)
        return payload
    }

    fun getCustomLink(
        link: CustomRefyLink
    ): JSONObject {
        return getCustomLink(
            linkId = link.id
        )
    }

    fun getCustomLink(
        linkId: String
    ): JSONObject {
        return execGet(
            endpoint = assembleCustomLinksEndpointPath(
                subEndpoint = linkId
            )
        )
    }

    fun deleteCustomLink(
        link: CustomRefyLink
    ): JSONObject {
        return deleteCustomLink(
            linkId = link.id
        )
    }

    fun deleteCustomLink(
        linkId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleCustomLinksEndpointPath(
                subEndpoint = linkId
            )
        )
    }

    private fun assembleCustomLinksEndpointPath(
        subEndpoint: String = ""
    ): String {
        return assembleCustomEndpointPath(
            customEndpoint = CUSTOM_LINKS_ENDPOINT,
            subEndpoint = subEndpoint
        )
    }

    private fun assembleCustomEndpointPath(
        customEndpoint: String,
        subEndpoint: String = ""
    ): String {
        val subPath = if(subEndpoint.isNotBlank())
            "/$subEndpoint"
        else
            subEndpoint
        val requestUrl = "$customEndpoint$subPath"
        return assembleUsersEndpointPath(
            endpoint = if(customEndpoint.startsWith("/"))
                requestUrl
            else
                "/$requestUrl"
        )
    }

}