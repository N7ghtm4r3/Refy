package com.tecknobit.refycore.helpers;

import com.tecknobit.equinox.inputs.InputValidator;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code RefyInputValidator} class is useful to validate the inputs
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see InputValidator
 */
public class RefyInputValidator extends InputValidator {

    /**
     * {@code MAX_TAG_NAME_LENGTH} the max valid length for the tag name
     */
    public static int MAX_TAG_NAME_LENGTH = 15;

    /**
     * {@code MAX_DESCRIPTION_LENGTH} the max valid length for the description field
     */
    public static int MAX_DESCRIPTION_LENGTH = 250;

    /**
     * {@code MAX_TITLE_LENGTH} the max valid length for the title
     */
    public static int MAX_TITLE_LENGTH = 30;

    /**
     * {@code HEX_COLOR_PATTERN} the patter for a valid hex code of a color
     */
    private static final String HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";

    /**
     * {@code pattern} the {@link #HEX_COLOR_PATTERN} validator
     */
    private static final Pattern pattern = Pattern.compile(HEX_COLOR_PATTERN);

    /**
     * {@code WRONG_TAG_NAME} message to use when the tag name of the user is not valid
     */
    public static final String WRONG_TAG_NAME_MESSAGE = "wrong_tag_name_key";

    /**
     * Method to validate a tag name
     *
     * @param tagName: tag name to check the validity
     * @return whether the tag name is valid or not as {@code boolean}
     */
    public static boolean isTagNameValid(String tagName) {
        return isInputValid(tagName) && tagName.startsWith("@") && tagName.length() <= MAX_TAG_NAME_LENGTH;
    }

    /**
     * Method to validate a payload of link
     *
     * @param description: description to check the validity
     * @param referenceLink: link resource to check the validity
     * @return whether payload is valid or not as {@code boolean}
     */
    public static boolean isLinkPayloadValid(String description, String referenceLink) {
        return isDescriptionValid(description) && isLinkResourceValid(referenceLink);
    }

    /**
     * Method to validate a payload of collection
     *
     * @param color: color of the collection
     * @param title: title of the collection
     * @param description: description to check the validity
     * @param links: list of links shared in a collection
     * @return whether payload is valid or not as {@code boolean}
     */
    public static boolean isCollectionPayloadValid(String color, String title, String description, List<String> links) {
        return isCollectionColorValid(color) && isTitleValid(title) && isDescriptionValid(description) && !links.isEmpty();
    }

    /**
     * Method to validate a payload of custom link
     *
     * @param title: title of the collection
     * @param description: description to check the validity
     * @param resources: the resources shared by the link
     * @param fields: the fields to use for the validation form to access to the resources
     * @return whether payload is valid or not as {@code boolean}
     */
    public static boolean isCustomLinkPayloadValid(String title, String description, Map<String, Object> resources,
                                                   Map<String, Object> fields) {
        return isTitleValid(title) && isDescriptionValid(description) && !resources.isEmpty() &&
                isCustomLinkMapValid(resources) && isCustomLinkMapValid(fields);
    }

    /**
     * Method to check whether a map instance is valid
     *
     * @param map: map to check
     * @return whether a map instance is valid as {@code boolean}
     */
    private static boolean isCustomLinkMapValid(Map<String, Object> map) {
        if(map.isEmpty())
            return true;
        for (Object value : map.values())
            if(value.toString().isEmpty())
                return false;
        return true;
    }

    /**
     * Method to validate a title
     *
     * @param title: title to check the validity
     * @return whether title is valid or not as {@code boolean}
     */
    public static boolean isTitleValid(String title) {
        return isInputValid(title) && title.length() <= MAX_TITLE_LENGTH;
    }

    /**
     * Method to validate a link resource (url)
     *
     * @param linkResource: link resource to check the validity
     * @return whether link resource (url) is valid or not as {@code boolean}
     */
    public static boolean isLinkResourceValid(String linkResource) {
        return isInputValid(linkResource) && urlValidator.isValid(linkResource);
    }

    /**
     * Method to validate a description
     *
     * @param description: description to check the validity
     * @return whether description is valid or not as {@code boolean}
     */
    public static boolean isDescriptionValid(String description) {
        return description != null && description.length() <= MAX_DESCRIPTION_LENGTH;
    }

    /**
     * Method to validate a collection color
     *
     * @param collectionColor: collection color to check the validity
     * @return whether collection color is valid or not as {@code boolean}
     */
    private static boolean isCollectionColorValid(String collectionColor) {
        if(!isInputValid(collectionColor))
            return false;
        Matcher matcher = pattern.matcher(collectionColor);
        return matcher.matches();
    }

}
