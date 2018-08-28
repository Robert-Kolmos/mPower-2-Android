package org.sagebionetworks.research.mpower.tracking.view_model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.sagebionetworks.research.mpower.tracking.model.TrackingItem;
import org.threeten.bp.Instant;

@AutoValue
public abstract class SymptomLog implements TrackingItemLog {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract SymptomLog build();

        @NonNull
        public abstract Builder setTrackingItem(@NonNull TrackingItem trackingItem);

        @NonNull
        public abstract Builder setTimestamp(@NonNull Instant timestamp);

        @NonNull
        public abstract Builder setSeverity(@Nullable Integer severity);

        // TODO add time and duration.

        @NonNull
        public abstract Builder setMedicationTiming(@Nullable String medicationTiming);
    }

    @Nullable
    public abstract Integer getSeverity();

    @Nullable
    public abstract String getMedicationTiming();

    public static Builder builder() {
        return new AutoValue_SymptomLog.Builder();
    }

    public static TypeAdapter<SymptomLog> typeAdapter(Gson gson) {
        return new AutoValue_SymptomLog.GsonTypeAdapter(gson);
    }

    public abstract Builder toBuilder();
}
