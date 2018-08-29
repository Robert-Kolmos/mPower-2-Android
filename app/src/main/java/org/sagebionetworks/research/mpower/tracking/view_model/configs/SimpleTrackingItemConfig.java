package org.sagebionetworks.research.mpower.tracking.view_model.configs;


import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.sagebionetworks.research.mpower.tracking.model.TrackingItem;

@AutoValue
public abstract class SimpleTrackingItemConfig implements TrackingItemConfig {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract SimpleTrackingItemConfig build();

        @NonNull
        public abstract Builder setTrackingItem(@NonNull TrackingItem trackingItem);
    }

    public static Builder builder() {
        return new AutoValue_SimpleTrackingItemConfig.Builder();
    }

    @Override
    public boolean isConfigured() {
        return true;
    }
}
