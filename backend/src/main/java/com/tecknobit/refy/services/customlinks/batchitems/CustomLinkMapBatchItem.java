package com.tecknobit.refy.services.customlinks.batchitems;

import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: 15/02/2025 ANNOTATE AS @BatchItem
public class CustomLinkMapBatchItem implements EquinoxItemsHelper.ComplexBatchItem {

    private final String linkId;

    private final Pair<String, Object> pair;

    public CustomLinkMapBatchItem(String linkId, String key, Object value) {
        this.linkId = linkId;
        pair = new Pair<>(key, value);
    }

    public String getLinkId() {
        return linkId;
    }

    public String getKey() {
        return pair.getFirst();
    }

    public Object getValue() {
        return pair.getSecond();
    }

    public Pair<String, Object> getPair() {
        return pair;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof CustomLinkMapBatchItem item)) return false;

        return Objects.equals(linkId, item.linkId) && pair.getFirst().equals(item.pair.getFirst()) &&
                pair.getSecond().equals(item.pair.getSecond());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(linkId);
        result = 31 * result + pair.getFirst().hashCode();
        result = 31 * result + pair.getSecond().hashCode();
        return result;
    }

    @Override
    public @NotNull List<?> mappedValues() {
        ArrayList<Object> mappedValues = new ArrayList<>();
        mappedValues.add(linkId);
        mappedValues.add(pair.getFirst());
        mappedValues.add(pair.getSecond());
        return mappedValues;
    }

}
