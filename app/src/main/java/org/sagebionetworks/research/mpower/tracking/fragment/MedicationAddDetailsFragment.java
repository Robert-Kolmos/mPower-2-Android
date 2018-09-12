package org.sagebionetworks.research.mpower.tracking.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker.OnValueChangeListener;

import org.sagebionetworks.research.mpower.R;
import org.sagebionetworks.research.mpower.tracking.recycler_view.MedicationAddDetailsAdapter;
import org.sagebionetworks.research.mpower.tracking.recycler_view.MedicationAddDetailsViewHolder.MedicationAddDetailsListener;
import org.sagebionetworks.research.mpower.tracking.view_model.MedicationTrackingTaskViewModel;
import org.sagebionetworks.research.mpower.tracking.view_model.SimpleTrackingTaskViewModel;
import org.sagebionetworks.research.mpower.tracking.view_model.configs.MedicationConfig;
import org.sagebionetworks.research.mpower.tracking.view_model.configs.SimpleTrackingItemConfig;
import org.sagebionetworks.research.mpower.tracking.view_model.logs.SimpleTrackingItemLog;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MedicationAddDetailsFragment extends
        RecyclerViewTrackingFragment<MedicationConfig, SimpleTrackingItemLog, MedicationTrackingTaskViewModel,
                MedicationAddDetailsAdapter> {
    @NonNull
    public static MedicationAddDetailsFragment newInstance(@NonNull StepView step) {
        MedicationAddDetailsFragment fragment = new MedicationAddDetailsFragment();
        Bundle args = TrackingFragment.createArguments(step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View result = super.onCreateView(inflater, container, savedInstanceState);
       title.setText(R.string.medication_add_details_title);
       detail.setText(R.string.medication_add_details_detail);
       addMore.setText(R.string.medication_add_more);
       addMore.setOnClickListener(view -> {
           getFragmentManager().beginTransaction()
                   .replace(((ViewGroup)this.getView().getParent()).getId(), MedicationSelectionFragment.newInstance(stepView))
                   .commit();
       });

       return result;
    }

    @Override
    public int getLayoutId() {
        return R.layout.mpower2_logging_step;
    }

    @NonNull
    @Override
    public MedicationAddDetailsAdapter initializeAdapter() {
        MedicationAddDetailsListener medicationAddDetailsListener = (config, position) -> {
            // TODO launch the scheduling fragment for the given config.
            adapter.itemConfigured(position);
            adapter.notifyDataSetChanged();
        };

        List<MedicationConfig> unconfiguredElements = getUnconfiguredElements();
        return new MedicationAddDetailsAdapter(unconfiguredElements, medicationAddDetailsListener);
    }

    private List<MedicationConfig> getUnconfiguredElements() {
        List<MedicationConfig> result = new ArrayList<>();
        for (MedicationConfig config : viewModel.getActiveElementsById().getValue().values()) {
            if (!config.isConfigured()) {
                result.add(config);
            }
        }

        return result;
    }
}
