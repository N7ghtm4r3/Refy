package com.tecknobit.refy.helpers;

import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;

@Deprecated(since = "USE THE EQUINOX BUILT-IN ONE")
public abstract class JoinTableSyncBatchItem<T> implements EquinoxItemsHelper.ComplexBatchItem {

    protected final T owner;

    protected final T owned;

    public JoinTableSyncBatchItem(T owner, T owned) {
        this.owner = owner;
        this.owned = owned;
    }

    public T getOwner() {
        return owner;
    }

    public T getOwned() {
        return owned;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof JoinTableSyncBatchItem<?> that))
            return false;
        return owner.equals(that.owner) && owned.equals(that.owned);
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + owned.hashCode();
        return result;
    }

}
