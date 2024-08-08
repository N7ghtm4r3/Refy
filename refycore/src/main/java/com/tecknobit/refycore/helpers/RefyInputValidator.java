package com.tecknobit.refycore.helpers;

import com.tecknobit.equinox.inputs.InputValidator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefyInputValidator extends InputValidator {

    public static int MAX_TAG_NAME_LENGTH = 15;

    public static int MAX_DESCRIPTION_LENGTH = 250;

    public static int MAX_TITLE_LENGTH = 30;

    private static final String HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";

    private static final Pattern pattern = Pattern.compile(HEX_COLOR_PATTERN);

    /**
     * {@code WRONG_TAG_NAME} message to use when the tag name of the user is not valid
     */
    public static final String WRONG_TAG_NAME_MESSAGE = "wrong_tag_name_key";

    public static boolean isTagNameValid(String tagName) {
        return isInputValid(tagName) && tagName.startsWith("@") && tagName.length() <= MAX_TAG_NAME_LENGTH;
    }

    public static boolean isLinkPayloadValid(String description, String referenceLink) {
        return isDescriptionValid(description) && isLinkResourceValid(referenceLink);
    }

    public static boolean isCollectionPayloadValid(String color, String title, String description, List<String> links) {
        return isCollectionColorValid(color) && isTitleValid(title) && isDescriptionValid(description) && !links.isEmpty();
    }

    public static boolean isTitleValid(String title) {
        return isInputValid(title) && title.length() <= MAX_TITLE_LENGTH;
    }

    public static boolean isLinkResourceValid(String linkResource) {
        return isInputValid(linkResource) && urlValidator.isValid(linkResource);
    }

    public static boolean isDescriptionValid(String description) {
        return description != null && description.length() <= MAX_DESCRIPTION_LENGTH;
    }

    private static boolean isCollectionColorValid(String collectionColor) {
        if(!isInputValid(collectionColor))
            return false;
        Matcher matcher = pattern.matcher(collectionColor);
        return matcher.matches();
    }

}
