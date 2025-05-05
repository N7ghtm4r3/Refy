package com.tecknobit.refy.services.shared.batch.procedures;

import com.tecknobit.equinoxcore.annotations.FutureEquinoxApi;

import java.util.Collection;

@FutureEquinoxApi(
        releaseVersion = "1.1.2",
        additionalNotes = """
                - At the moment is a raw behavior will be improved, check for example for a better name
                - Document its usage properly
                - Will replace the BatchSynchronizationProcedure.loadDataList method
                """
)
public interface RawCollectionConverter<D, V> {

    Collection<V> convert(Collection<D> rawData);

}
