package org.sagebionetworks.research.mpower.tracking.fragment;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sagebionetworks.research.mobile_ui.perform_task.PerformTaskFragment;
import org.sagebionetworks.research.mobile_ui.show_step.ShowStepFragment;
import org.sagebionetworks.research.mpower.tracking.model.TrackingStepView;
import org.sagebionetworks.research.mpower.tracking.view_model.configs.TrackingItemConfig;
import org.sagebionetworks.research.mpower.tracking.view_model.logs.TrackingItemLog;
import org.sagebionetworks.research.mpower.tracking.view_model.TrackingTaskViewModel;
import org.sagebionetworks.research.mpower.tracking.view_model.TrackingTaskViewModelFactory;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public abstract class TrackingFragment
        <ConfigType extends TrackingItemConfig, LogType extends TrackingItemLog, ViewModelType extends TrackingTaskViewModel<ConfigType, LogType>>
        extends ShowStepFragment {
    public static final String ARGUMENT_STEP_VIEW = "stepView";

    protected TrackingStepView stepView;
    protected ViewModelType viewModel;
    protected PerformTaskFragment performTaskFragment;
    protected Unbinder unbinder;

    @Inject
    protected TrackingTaskViewModelFactory trackingActiveTaskViewModelFactory;

    @NonNull
    public static Bundle createArguments(@NonNull StepView step) {
        checkNotNull(step);

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_STEP_VIEW, step);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackingStepView stepView = null;
        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                // noinspection unchecked
                stepView = (TrackingStepView)this.getArguments().getSerializable(ARGUMENT_STEP_VIEW);
            }
        } else {
            // noinspection unchecked
            stepView = (TrackingStepView)savedInstanceState.getSerializable(ARGUMENT_STEP_VIEW);
        }

        checkState(stepView != null, "stepView cannot be null");
        this.stepView = stepView;
        // noinspection unchecked
        this.viewModel =
                (ViewModelType) ViewModelProviders.of(this.getParentFragment(), this.trackingActiveTaskViewModelFactory.create(this.stepView))
                        .get(TrackingTaskViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(this.getLayoutId(), container, false);
        this.unbinder = ButterKnife.bind(this, result);
        return result;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
    }

    @Override
    public void setPerformTaskFragment(@NonNull PerformTaskFragment performTaskFragment) {
        this.performTaskFragment = performTaskFragment;
    }

    /**
     * Returns the id of the layout to inflate for this fragment.
     *
     * @return the id of the layout to inflate for this fragment.
     */
    @LayoutRes
    public abstract int getLayoutId();
}
