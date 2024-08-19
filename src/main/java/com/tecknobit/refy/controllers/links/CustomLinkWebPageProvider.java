package com.tecknobit.refy.controllers.links;

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

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.refy.controllers.links.CustomLinkWebPageProvider.CUSTOM_LINKS_PATH;
import static com.tecknobit.refycore.records.RefyItem.TITLE_KEY;
import static com.tecknobit.refycore.records.links.CustomRefyLink.*;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

@Controller
@RequestMapping(BASE_EQUINOX_ENDPOINT + CUSTOM_LINKS_PATH)
public class CustomLinkWebPageProvider {

    protected final Mantis mantis;

    {
        try {
            mantis = new Mantis(Locale.ENGLISH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String CUSTOM_LINKS_PATH = "customLinks";

    private static final String INVALID_CUSTOM_LINK_PAGE = "invalid_link";

    private static final String MAIN_TEXT = "main_text";

    private static final String SUB_TEXT = "sub_text";

    private static final String IS_IN_PREVIEW_MODE = "is_in_preview_mode";

    private static final String VALIDATE_BUTTON_TEXT = "validate_button_text";

    private static final String RESOURCES_TITLE_TEXT = "resources_title_text";

    private static final String LINK_UNIQUE_ACCESS_WARN_TEXT = "link_unique_access_warn";

    @Autowired
    private CustomLinksHelper customLinksHelper;

    @GetMapping(
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
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

    private String linkNotExistsOrExpired(CustomRefyLink customLink, Model model) {
        model.addAttribute(MAIN_TEXT, mantis.getResource("invalid_link_key"));
        model.addAttribute(SUB_TEXT, mantis.getResource("invalid_link_subtext_key"));
        if(customLink != null)
            customLinksHelper.deleteLink(customLink.getId());
        return INVALID_CUSTOM_LINK_PAGE;
    }

    private String wrongAttemptToPreviewMode(Model model) {
        model.addAttribute(MAIN_TEXT, mantis.getResource("wrong_attempt_key"));
        model.addAttribute(SUB_TEXT, mantis.getResource("you_are_not_authorized_key"));
        return INVALID_CUSTOM_LINK_PAGE;
    }

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
            model.addAttribute(RESOURCES_TITLE_TEXT, mantis.getResource("copy_the_resources_key"));
        }
        if(!isPreviewMode && hasUniqueAccess)
            customLinksHelper.deleteLink(customLink.getId());
        return CUSTOM_LINK_KEY;
    }

}
