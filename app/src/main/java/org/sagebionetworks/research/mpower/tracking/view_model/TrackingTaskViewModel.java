package org.sagebionetworks.research.mpower.tracking.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.mpower.tracking.model.TrackingItem;
import org.sagebionetworks.research.mpower.tracking.model.TrackingSection;
import org.sagebionetworks.research.mpower.tracking.model.TrackingStepView;
import org.sagebionetworks.research.mpower.tracking.view_model.configs.TrackingItemConfig;
import org.sagebionetworks.research.mpower.tracking.view_model.logs.TrackingItemLog;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class TrackingTaskViewModel<ConfigType extends TrackingItemConfig, LogType extends TrackingItemLog>
        extends ViewModel {
    protected LiveData<Boolean> selectionMade;
    protected MutableLiveData<Map<TrackingSection, Set<TrackingItem>>> availableElements;
    protected MutableLiveData<Set<ConfigType>> activeElements;
    protected LiveData<Set<ConfigType>> unconfiguredElements;
    protected MutableLiveData<Set<LogType>> loggedElements;
    protected TrackingStepView stepView;

    protected TrackingTaskViewModel(@NonNull final TrackingStepView stepView) {
        this.stepView = stepView;
        this.availableElements = new MutableLiveData<>();
        this.availableElements.setValue(stepView.getSelectionItems());
        this.activeElements = new MutableLiveData<>();
        this.activeElements.setValue(new HashSet<>());
        this.selectionMade = Transformations.map(this.activeElements, (elements) -> !elements.isEmpty());
        this.unconfiguredElements = Transformations.map(this.activeElements, (elements) -> {
            Set<ConfigType> result = new HashSet<>();
            for (ConfigType config : elements) {
                if (!config.isConfigured()) {
                    result.add(config);
                }
            }

            return result;
        });

        this.loggedElements = new MutableLiveData<>();
        this.loggedElements.setValue(new HashSet<>());
    }

    public LiveData<Boolean> getSelectionMade() {
        return this.selectionMade;
    }

    public LiveData<Map<TrackingSection, Set<TrackingItem>>> getAvailableElements() {
        return this.availableElements;
    }

    public LiveData<Set<ConfigType>> getActiveElements() {
        return this.activeElements;
    }

    public LiveData<Set<ConfigType>> getUnconfiguredElements() {
        return this.unconfiguredElements;
    }

    public LiveData<Set<LogType>> getLoggedElements() {
        return this.loggedElements;
    }

    public void itemSelected(@NonNull TrackingItem item) {
        Set<ConfigType> activeElements = this.activeElements.getValue();
        if (activeElements == null) {
            activeElements = new HashSet<>();
        }

        if (!TrackingTaskViewModel.containsMatchingTrackingItem(activeElements, item)) {
            activeElements.add(this.instantiateConfigFromSelection(item));
        }

        this.activeElements.setValue(activeElements);
    }

    public void itemDeselected(@NonNull TrackingItem item) {
        Set<ConfigType> result = new HashSet<>();
        Set<ConfigType> activeElements = this.activeElements.getValue();
        if (activeElements == null) {
            return;
        }

        // Filter the config for the given item out.
        for (ConfigType config : activeElements) {
            if (!config.getTrackingItem().equals(item)) {
                result.add(config);
            }
        }

        this.activeElements.setValue(result);
    }

    public boolean isSelected(@NonNull TrackingItem trackingItem) {
        Set<ConfigType> activeElements = this.activeElements.getValue();
        if (activeElements != null) {
            return TrackingTaskViewModel
                    .containsMatchingTrackingItem(activeElements, trackingItem);
        } else {
            return false;
        }
    }

    public boolean isLogged(@NonNull ConfigType config) {
        Set<LogType> loggedElements = this.loggedElements.getValue();
        if (loggedElements != null) {
            return TrackingTaskViewModel
                    .containsMatchingTrackingItem(loggedElements, config.getTrackingItem());
        } else {
            return false;
        }
    }

    public LogType getLog(@NonNull TrackingItem trackingItem) {
        return getMatchingTrackingItem(this.loggedElements.getValue(), trackingItem);
    }

    public ConfigType getConfig(@NonNull TrackingItem trackingItem) {
        return getMatchingTrackingItem(this.activeElements.getValue(), trackingItem);
    }

    private static <E extends HasTrackingItem> boolean containsMatchingTrackingItem(@NonNull Set<E> items,
            @NonNull TrackingItem item) {
        E reuslt = getMatchingTrackingItem(items, item);
        return reuslt != null;
    }

    @Nullable
    private static <E extends HasTrackingItem> E getMatchingTrackingItem(@NonNull Set<E> items, @NonNull TrackingItem item) {
        for (E hasTrackingItem : items) {
            if (hasTrackingItem.getTrackingItem().equals(item)) {
                return hasTrackingItem;
            }
        }

        return null;
    }

    public void addActiveElement(@NonNull ConfigType config) {
        Set<ConfigType> result = new HashSet<>();
        Set<ConfigType> activeElements = this.activeElements.getValue();
        if (activeElements != null) {
            for (ConfigType currentConfig : activeElements) {
                // Filter out the old Config if there is one.
                if (!currentConfig.getTrackingItem().equals(config.getTrackingItem())) {
                    result.add(currentConfig);
                }
            }
        }

        result.add(config);
        this.activeElements.setValue(result);
    }

    public void addLoggedElement(@NonNull LogType log) {
        Set<LogType> result = this.removeLoggedElementHelper(log.getTrackingItem().getIdentifier());
        result.add(log);
        this.loggedElements.setValue(result);
    }

    public void removeLoggedElement(@NonNull String identifier) {
        this.loggedElements.setValue(this.removeLoggedElementHelper(identifier));
    }

    public TrackingStepView getStepView() {
        return this.stepView;
    }

    private Set<LogType> removeLoggedElementHelper(@NonNull String identifier) {
        Set<LogType> result = new HashSet<>();
        Set<LogType> loggedElements = this.loggedElements.getValue();
        if (loggedElements != null) {
            for (LogType currentLog : loggedElements) {
                // Filter out the old log if there is one.
                if (!currentLog.getTrackingItem().getIdentifier().equals(identifier)) {
                    result.add(currentLog);
                }
            }
        }

        return result;
    }

    protected abstract ConfigType instantiateConfigFromSelection(@NonNull TrackingItem item);
}
