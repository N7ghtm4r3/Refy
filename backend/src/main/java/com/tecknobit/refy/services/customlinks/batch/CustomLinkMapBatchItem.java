package com.tecknobit.refy.services.customlinks.batch;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.refy.services.customlinks.entity.CustomRefyLink;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.ComplexBatchItem;

/**
 * The {@code CustomLinkMapBatchItem} is used during the batch synchronization to sync a {@link java.util.Map}'s value
 * of a {@link CustomRefyLink} like {@link CustomRefyLink}'s fields or {@link CustomRefyLink}'s resources
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see ComplexBatchItem
 */
@BatchQueryItem
public class CustomLinkMapBatchItem implements ComplexBatchItem {

    /**
     * {@code linkId} the identifier of the custom link
     */
    private final String linkId;

    /**
     * {@code key} the key of the value
     */
    private final String key;

    /**
     * {@code value} the value to insert
     */
    private final Object value;

    /**
     * Constructor to instantiate the item
     *
     * @param linkId The identifier of the custom link
     * @param key The key of the value
     * @param value The value to insert
     */
    public CustomLinkMapBatchItem(String linkId, String key, Object value) {
        this.linkId = linkId;
        this.value = value;
        this.key = key;
    }

    /**
     * Method to get the {@link #linkId} instance
     *
     * @return the {@link #linkId} instance as {@link String}
     */
    public String getLinkId() {
        return linkId;
    }

    /**
     * Method to get the {@link #key} instance
     *
     * @return the {@link #key} instance as {@link String}
     */
    public String getKey() {
        return key;
    }

    /**
     * Method to get the {@link #value} instance
     *
     * @return the {@link #value} instance as {@link Object}
     */
    public Object getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof CustomLinkMapBatchItem item))
            return false;
        return Objects.equals(linkId, item.linkId) && Objects.equals(key, item.key) && Objects.equals(value, item.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(linkId);
        result = 31 * result + Objects.hashCode(key);
        result = 31 * result + Objects.hashCode(value);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<?> mappedValues() {
        ArrayList<Object> mappedValues = new ArrayList<>();
        mappedValues.add(linkId);
        mappedValues.add(value);
        mappedValues.add(key);
        return mappedValues;
    }

}
