package com.tecknobit.refycore.helpers

import com.tecknobit.equinoxcore.helpers.InputsValidator
import com.tecknobit.refycore.AT_SYMBOL

/**
 * The `RefyInputValidator` class is useful to validate the inputs
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see InputsValidator
 */
object RefyInputsValidator : InputsValidator() {

    /**
     * `MAX_TAG_NAME_LENGTH` the max valid length for the tag name
     */
    const val MAX_TAG_NAME_LENGTH: Int = 16

    /**
     * `MAX_DESCRIPTION_LENGTH` the max valid length for the description field
     */
    const val MAX_DESCRIPTION_LENGTH: Int = 250

    /**
     * `MAX_TITLE_LENGTH` the max valid length for the title
     */
    const val MAX_TITLE_LENGTH: Int = 30

    /**
     * `HEX_COLOR_PATTERN` the patter for a valid hex code of a color
     */
    private const val HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$"

    /**
     * `colorRegex` the [.HEX_COLOR_PATTERN] validator
     */
    private val colorRegex: Regex = Regex(HEX_COLOR_PATTERN)

    /**
     * `WRONG_TAG_NAME` message to use when the tag name of the user is not valid
     */
    const val WRONG_TAG_NAME_MESSAGE: String = "wrong_tag_name_key"

    /**
     * Method to validate a tag name
     *
     * @param tagName Tag name to check the validity
     * @return whether the tag name is valid or not as `boolean`
     */
    fun isTagNameValid(
        tagName: String
    ): Boolean {
        return isInputValid(tagName) && tagName.startsWith(AT_SYMBOL) && tagName.length <= MAX_TAG_NAME_LENGTH
    }

    /**
     * Method to validate a payload of link
     *
     * @param description: description to check the validity
     * @param referenceLink: link resource to check the validity
     * @return whether payload is valid or not as `boolean`
     */
    fun isLinkPayloadValid(
        description: String?,
        referenceLink: String?,
    ): Boolean {
        return isDescriptionValid(description) && isLinkResourceValid(referenceLink)
    }

    /**
     * Method to validate a payload of collection
     *
     * @param color: color of the collection
     * @param title Title of the collection
     * @param description: description to check the validity
     * @param links: list of links shared in a collection
     * @return whether payload is valid or not as `boolean`
     */
    fun isCollectionPayloadValid(
        color: String,
        title: String,
        description: String?,
        links: List<String?>,
    ): Boolean {
        return isCollectionColorValid(color) && isTitleValid(title) && isDescriptionValid(description) && !links.isEmpty()
    }

    /**
     * Method to validate a payload of custom link
     *
     * @param title Title of the collection
     * @param description: description to check the validity
     * @param resources The resources shared by the link
     * @param fields The fields to use for the validation form to access to the resources
     * @return whether payload is valid or not as `boolean`
     */
    fun isCustomLinkPayloadValid(
        title: String,
        description: String?,
        resources: Map<String?, Any>,
        fields: Map<String?, Any>,
    ): Boolean {
        return isTitleValid(title) && isDescriptionValid(description) && resources.isNotEmpty() &&
                isCustomLinkMapValid(resources) && isCustomLinkMapValid(fields)
    }

    /**
     * Method to check whether a map instance is valid
     *
     * @param map: map to check
     * @return whether a map instance is valid as `boolean`
     */
    private fun isCustomLinkMapValid(
        map: Map<String?, Any>
    ): Boolean {
        if (map.isEmpty()) return true
        for (value in map.values) if (value.toString().isEmpty()) return false
        return true
    }

    /**
     * Method to validate a title
     *
     * @param title Title to check the validity
     * @return whether title is valid or not as `boolean`
     */
    fun isTitleValid(
        title: String
    ): Boolean {
        return isInputValid(title) && title.length <= MAX_TITLE_LENGTH
    }

    /**
     * Method to validate a link resource (url)
     *
     * @param linkResource: link resource to check the validity
     * @return whether link resource (url) is valid or not as `boolean`
     */
    fun isLinkResourceValid(
        linkResource: String?
    ): Boolean {
        return isInputValid(linkResource) && urlValidator.matches(linkResource!!)
    }

    /**
     * Method to validate a description
     *
     * @param description: description to check the validity
     * @return whether description is valid or not as `boolean`
     */
    fun isDescriptionValid(
        description: String?
    ): Boolean {
        return description != null && description.length <= MAX_DESCRIPTION_LENGTH
    }

    /**
     * Method to validate a collection color
     *
     * @param collectionColor: collection color to check the validity
     * @return whether collection color is valid or not as `boolean`
     */
    private fun isCollectionColorValid(
        collectionColor: String
    ): Boolean {
        if (!isInputValid(collectionColor))
            return false
        return colorRegex.matches(collectionColor)
    }

}
