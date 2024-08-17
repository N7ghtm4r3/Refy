package com.tecknobit.refy.controllers.links;

import com.tecknobit.refy.helpers.services.links.CustomLinksHelper;
import com.tecknobit.refycore.records.links.CustomRefyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.tecknobit.equinox.environment.helpers.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.refy.controllers.links.CustomLinkWebPageProvider.CUSTOM_LINKS_PATH;
import static com.tecknobit.refycore.records.RefyItem.OWNER_KEY;
import static com.tecknobit.refycore.records.links.CustomRefyLink.CUSTOM_LINK_KEY;
import static com.tecknobit.refycore.records.links.RefyLink.LINK_IDENTIFIER_KEY;

@Controller
@RequestMapping(BASE_EQUINOX_ENDPOINT + CUSTOM_LINKS_PATH)
public class CustomLinkWebPageProvider {

    public static final String CUSTOM_LINKS_PATH = "customLinks";

    private static final String MAIN_TEXT_KEY = "main_text";

    private static final String SUB_TEXT_KEY = "sub_text";

    private static final String INVALID_CUSTOM_LINK_PAGE = "invalid_link";

    @Autowired
    private CustomLinksHelper customLinksHelper;

    @GetMapping(
            path = "/{" + LINK_IDENTIFIER_KEY + "}"
    )
    public String loadCustomLinkWebPage(
            Model model,
            @PathVariable(LINK_IDENTIFIER_KEY) String linkId,
            @RequestParam(
                    name = OWNER_KEY,
                    required = false
            ) String owner
    ) {
        //TODO: TO USE MANTIS TO TRANSLATE BY THE USER LANGUAGE
        CustomRefyLink customLink = customLinksHelper.findById(linkId);
        if(customLink == null || customLink.isExpired()) {
            model.addAttribute(MAIN_TEXT_KEY, "Invalid link...");
            model.addAttribute(SUB_TEXT_KEY, "The link requested not exists or has been expired.");
            return INVALID_CUSTOM_LINK_PAGE;
        }
        if(!owner.equals(customLink.getOwner().getId())) {
            model.addAttribute(MAIN_TEXT_KEY, "Wrong attempt");
            model.addAttribute(SUB_TEXT_KEY, "You are not authorized");
            return INVALID_CUSTOM_LINK_PAGE;
        }
        return CUSTOM_LINK_KEY;
    }

}
