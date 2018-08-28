package org.sagebionetworks.research.mpower.tracking.view_model;

import android.support.annotation.NonNull;

import org.sagebionetworks.research.mpower.tracking.model.TrackingItem;
import org.sagebionetworks.research.mpower.tracking.model.TrackingStepView;

public class SymptomTrackingTaskViewModel extends TrackingTaskViewModel<SimpleTrackingItemConfig, SymptomLog> {
    protected SymptomTrackingTaskViewModel(
            @NonNull final TrackingStepView stepView) {
        super(stepView);
    }

    @Override
    protected SimpleTrackingItemConfig instantiateConfigFromSelection(@NonNull final TrackingItem item) {
        return SimpleTrackingItemConfig.builder().setTrackingItem(item).build();
    }
}
