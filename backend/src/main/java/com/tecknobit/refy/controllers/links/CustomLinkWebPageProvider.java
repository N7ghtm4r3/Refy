package com.tecknobit.refy.controllers.links;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.mantis.Mantis;
import com.tecknobit.refy.helpers.services.links.CustomLinksHelper;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Locale;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.refycore.records.RefyItem.TITLE_KEY;
import static com.tecknobit.refycore.records.links.CustomRefyLink.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

/**
 * The {@code CustomLinkWebPageProvider} class is useful to provide the dedicated web page of a {@link CustomRefyLink} and
 * manage the invalid accesses to the link and protect with a validation form the resources shared by that link
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxController
 *
 */
@Controller
@RequestMapping(BASE_EQUINOX_ENDPOINT + CUSTOM_LINKS_PATH)
public class CustomLinkWebPageProvider {

    /**
     * {@code mantis} the translations manager
     */
    protected final Mantis mantis;

    {
        try {
            mantis = new Mantis(Locale.ENGLISH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@code INVALID_CUSTOM_LINK_PAGE} the page when the link requested is not valid
     */
    private static final String INVALID_CUSTOM_LINK_PAGE = "invalid_link";

    /**
     * {@code MAIN_TEXT} the main text Thymeleaf tag
     */
    private static final String MAIN_TEXT = "main_text";

    /**
     * {@code SUB_TEXT} the sub text Thymeleaf tag
     */
    private static final String SUB_TEXT = "sub_text";

    /**
     * {@code IS_IN_PREVIEW_MODE} the Thymeleaf tag whether the page requested is in preview mode
     */
    private static final String IS_IN_PREVIEW_MODE = "is_in_preview_mode";

    /**
     * {@code VALIDATE_BUTTON_TEXT} the validate button text Thymeleaf tag
     */
    private static final String VALIDATE_BUTTON_TEXT = "validate_button_text";

    /**
     * {@code RESOURCES_TITLE_TEXT} the resources title text Thymeleaf tag
     */
    private static final String RESOURCES_TITLE_TEXT = "resources_title_text";

    /**
     * {@code LINK_UNIQUE_ACCESS_WARN_TEXT} the link unique access warn Thymeleaf tag
     */
    private static final String LINK_UNIQUE_ACCESS_WARN_TEXT = "link_unique_access_warn";

    /**
     * {@code customLinksHelper} helper to manage the {@link CustomRefyLink} database operations
     */
    @Autowired
    private CustomLinksHelper customLinksHelper;

    /**
     * Method to get a list of custom links
     *
     * @param model: the model used by Thymeleaf to format the web page
     * @param request: the http request useful to get whether language the user is using to format properly the page language
     * @param linkId: the custom link requested
     * @param previewToken: the token, if set, to access to the page in preview mode (owner's feature)
     *
     * @return the correct webpage to display title as {@link String}
     */
    @GetMapping(
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    @RequestPath(path = "/api/v1/customLinks/{link_id}", method = GET)
    public String loadCustomLinkWebPage(
            Model model,
            HttpServletRequest request,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestParam(
                    name = PREVIEW_TOKEN_KEY,
                    required = false
            ) String previewToken
    ) {
        mantis.changeCurrentLocale(request.getLocale());
        CustomRefyLink customLink = customLinksHelper.findById(linkId);
        if(customLink == null || customLink.isExpired())
            return linkNotExistsOrExpired(customLink, model);
        boolean isInPreviewMode = previewToken != null;
        if(isInPreviewMode && !previewToken.equals(customLink.getPreviewToken()))
            return wrongAttemptToPreviewMode(model);
        return customLinkPage(customLink, model, isInPreviewMode);
    }

    /**
     * Method to format and return the {@link #INVALID_CUSTOM_LINK_PAGE} due the link expiration or the not existing of
     * that link
     *
     * @param customLink: the custom link requested
     * @param model: the model used by Thymeleaf to format the web page
     * @return the title of the invalid page as {@link String}
     */
    private String linkNotExistsOrExpired(CustomRefyLink customLink, Model model) {
        model.addAttribute(MAIN_TEXT, mantis.getResource("invalid_link_key"));
        model.addAttribute(SUB_TEXT, mantis.getResource("invalid_link_subtext_key"));
        if(customLink != null)
            customLinksHelper.deleteLink(customLink.getId());
        return INVALID_CUSTOM_LINK_PAGE;
    }

    /**
     * Method to format and return the {@link #INVALID_CUSTOM_LINK_PAGE} due the wrong attempt to access in preview mode
     * to the web page
     *
     * @param model: the model used by Thymeleaf to format the web page
     * @return the title of the invalid page as {@link String}
     */
    private String wrongAttemptToPreviewMode(Model model) {
        model.addAttribute(MAIN_TEXT, mantis.getResource("wrong_attempt_key"));
        model.addAttribute(SUB_TEXT, mantis.getResource("you_are_not_authorized_key"));
        return INVALID_CUSTOM_LINK_PAGE;
    }

    /**
     * Method to format and return the {@link CustomRefyLink#CUSTOM_LINK_KEY} related page
     *
     * @param customLink: the custom link requested
     * @param model: the model used by Thymeleaf to format the web page
     * @param isPreviewMode: whether the page has been requested in preview mode or normal mode
     * @return the title of the correct page as {@link String}
     */
    private String customLinkPage(CustomRefyLink customLink, Model model, boolean isPreviewMode) {
        model.addAttribute(CUSTOM_LINK_KEY, customLink);
        model.addAttribute(TITLE_KEY, customLink.getTitle());
        model.addAttribute(IS_IN_PREVIEW_MODE, isPreviewMode);
        boolean hasUniqueAccess = customLink.hasUniqueAccess();
        model.addAttribute(UNIQUE_ACCESS_KEY, hasUniqueAccess);
        if(hasUniqueAccess)
            model.addAttribute(LINK_UNIQUE_ACCESS_WARN_TEXT, mantis.getResource("link_unique_access_warn_key"));
        if(customLink.mustValidateFields()) {
            model.addAttribute(MAIN_TEXT, mantis.getResource("fill_the_below_form_key"));
            model.addAttribute(VALIDATE_BUTTON_TEXT, mantis.getResource("validate_key"));
        }
        model.addAttribute(RESOURCES_TITLE_TEXT, mantis.getResource("copy_the_resources_key"));
        if(!isPreviewMode && hasUniqueAccess)
            customLinksHelper.deleteLink(customLink.getId());
        return CUSTOM_LINK_KEY;
    }

}
