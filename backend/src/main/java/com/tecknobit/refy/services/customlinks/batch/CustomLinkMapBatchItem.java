package com.tecknobit.refy.services.customlinks.batch;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@BatchQueryItem
public class CustomLinkMapBatchItem implements EquinoxItemsHelper.ComplexBatchItem {

    private final String linkId;

    private final String key;

    private final Object value;

    public CustomLinkMapBatchItem(String linkId, Object value, String key) {
        this.linkId = linkId;
        this.value = value;
        this.key = key;
    }

    public String getLinkId() {
        return linkId;
    }

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
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
