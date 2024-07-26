package com.tecknobit.refycore.helpers;

import com.tecknobit.equinox.inputs.InputValidator;

public class RefyInputValidator extends InputValidator {

    public static int MAX_DESCRIPTION_LENGTH = 250;

    public static int MAX_TITLE_LENGTH = 30;

    public static boolean isLinkResourceValid(String linkResource) {
        return isInputValid(linkResource) && urlValidator.isValid(linkResource);
    }

    public static boolean isDescriptionValid(String description) {
        return description != null && description.length() <= MAX_DESCRIPTION_LENGTH;
    }

}
