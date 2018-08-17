/*
 * Copyright 2015 Apple Inc.
 * Ported to Android from ResearchKit/ResearchKit 1.5
 */
/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.motor_control_module.show_step_fragment.tapping;

import android.arch.lifecycle.Observer;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;

import org.sagebionetworks.research.domain.result.interfaces.CollectionResult;
import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.domain.task.Task;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowActiveUIStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.motor_control_module.R;
import org.sagebionetworks.research.motor_control_module.result.TappingResult;
import org.sagebionetworks.research.motor_control_module.show_step_fragment.HandStepUIHelper;
import org.sagebionetworks.research.motor_control_module.show_step_view_model.ShowTappingStepViewModel;
import org.sagebionetworks.research.motor_control_module.step.HandStepHelper;
import org.sagebionetworks.research.motor_control_module.step_view.TappingStepView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowActiveUIStepViewModel;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTappingStepFragment extends
        ShowActiveUIStepFragmentBase<TappingStepView, ShowTappingStepViewModel, TappingStepViewBinding> {
    private String nextButtonTitle;

    // region Fragment
    @NonNull
    public static ShowTappingStepFragment newInstance(@NonNull StepView stepView) {
        ShowTappingStepFragment fragment = new ShowTappingStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void update(TappingStepView stepView) {
        super.update(stepView);
        TaskResult taskResult = this.performTaskViewModel.getTaskResult().getValue();
        HandStepUIHelper.update(taskResult, stepView, this.stepViewBinding);
        // Underline the skip button
        ActionButton skipButton = this.stepViewBinding.getSkipButton();
        if (skipButton != null) {
            skipButton.setPaintFlags(skipButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mpower2_tapping_step;
    }

    @NonNull
    @Override
    protected TappingStepViewBinding instantiateAndBindBinding(View view) {
        return new TappingStepViewBinding(view);
    }
    // endregion

    // region Fragment Lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        // onCreateView registers the listeners for the tapping buttons, and overall view.
        View result = super.onCreateView(inflater, viewGroup, savedInstanceState);
        this.stepViewBinding.getLeftTapButton().setOnTouchListener((targetView, motionEvent) -> {
            targetView.performClick();
            this.handleMotionEvent(TappingButtonIdentifier.LEFT, motionEvent);
            return true;
        });

        this.stepViewBinding.getRightTapButton().setOnTouchListener((targetView, motionEvent) -> {
            targetView.performClick();
            this.handleMotionEvent(TappingButtonIdentifier.RIGHT, motionEvent);
            return true;
        });

        this.stepViewBinding.getUnitLabel().setText(R.string.tap_count_label);

        this.stepViewBinding.getRootView().setOnTouchListener((targetView, motionEvent) -> {
            targetView.performClick();
            this.handleMotionEvent(TappingButtonIdentifier.NONE, motionEvent);
            return true;
        });

        this.showStepViewModel.getHitButtonCount().observe(this, count -> {
                    count = count != null ? count : 0;
                    String countLabelText = count + "";
                    this.stepViewBinding.getCountLabel().setText(countLabelText);
                });
        // Hide the navigation action bar, so the user cannot press navigation buttons until the tapping
        // is finished.
        this.stepViewBinding.getNavigationActionBar().setVisibility(View.GONE);
        this.stepViewBinding.getNavigationActionBar().setAlpha(0f);
        // Add a PreDrawListener that updates the locations of the buttons for the tapping result.
        this.stepViewBinding.getRootView().getViewTreeObserver()
                .addOnPreDrawListener(this::updateButtonBounds);
        this.nextButtonTitle = this.getResources().getString(this.showStepViewModel.getNextButtonLabel());
        this.nextButtonTitle.replaceAll(HandStepHelper.JSON_PLACEHOLDER, HandStepHelper.whichHand(this.stepView.getIdentifier()).toString());
        this.showStepViewModel.isExpired().observe(this, expired -> {
            if (expired != null && expired) {
                this.tappingFinished();
            }
        });
        return result;
    }

    /**
     * Private helper that can function as a PreDrawListener that updates the positions of the buttons,
     * and root view on the screen for the tapping result. Always returns true so drawing proceeds as normal.
     * @return true so drawing always happens as normal when used as a PreDrawListener.
     */
    private boolean updateButtonBounds() {
        ActionButton leftButton = this.stepViewBinding.getLeftTapButton();
        if (leftButton != null) {
            Rect buttonRect1 = new Rect();
            buttonRect1.set(leftButton.getLeft(), leftButton.getTop(), leftButton.getRight(), leftButton.getBottom());
            this.showStepViewModel.getButtonRect1().setValue(buttonRect1);
        }

        ActionButton rightButton = this.stepViewBinding.getRightTapButton();
        if (rightButton != null) {
            Rect buttonRect2 = new Rect();
            buttonRect2.set(rightButton.getLeft(), rightButton.getTop(), rightButton.getRight(), rightButton.getBottom());
            this.showStepViewModel.getButtonRect2().setValue(buttonRect2);
        }

        View view = this.stepViewBinding.getRootView();
        if (view != null) {
            this.showStepViewModel.getViewSize().setValue(new Point(view.getWidth(), view.getHeight()));
        }

        // Always return true to proceed with drawing.
        return true;
    }

    /**
     * Called when this tapping step finishes.
     */
    private void tappingFinished() {
        Instant timestamp = Instant.now();
        this.showStepViewModel.updateLastSample(timestamp, TappingButtonIdentifier.LEFT);
        this.showStepViewModel.updateLastSample(timestamp, TappingButtonIdentifier.RIGHT);
        this.showStepViewModel.updateTappingResult();
        this.stepViewBinding.getNavigationActionBar().setVisibility(View.VISIBLE);
        this.stepViewBinding.getNextButton().setText(this.nextButtonTitle);
        this.stepViewBinding.getTappingButtonView().animate().alpha(0f).setDuration(300)
                .withEndAction(() ->  {
                    this.stepViewBinding.getLeftTapButton().setVisibility(View.GONE);
                    this.stepViewBinding.getRightTapButton().setVisibility(View.GONE);
                })
                .start();
        this.stepViewBinding.getNavigationActionBar().animate().alpha(1f).setDuration(300).start();
    }
    // endregion

    // region User Input Listeners
    /**
     * Called when a MotionEvent has occurred.
     * @param buttonIdentifier The identifier of the button that has been tapped.
     * @param motionEvent The MotionEvent that occurred.
     */
    private void handleMotionEvent(@TappingButtonIdentifier String buttonIdentifier,
                                   MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            this.buttonPressed(buttonIdentifier, motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            this.buttonReleased(buttonIdentifier, motionEvent);
        }
    }

    /**
     * Called when one of the tapping button's is pressed.
     * @param buttonIdentifier The identifer of the button that was pressed.
     * @param event The motion event corresponding to the button release.
     */
    private void buttonPressed(@TappingButtonIdentifier String buttonIdentifier, @NonNull MotionEvent event) {
        int actionType = event.getAction();
        if (actionType != MotionEvent.ACTION_DOWN) {
            return;
        }

        if (this.showStepViewModel.getTappingStart().getValue() == null) {
            this.startCountdown();
        }

        if (this.showStepViewModel.getLastTappedButtonIdentifier().getValue() != buttonIdentifier) {
            // TODO say the word tap if accessibility voice is turned on.
        }

        // Forward the button press to the view model.
        this.showStepViewModel.handleButtonPress(buttonIdentifier, event);
    }

    /**
     * Called when one of the tapping button's is released.
     * @param buttonIdentifier The identifier of the button that was released.
     * @param event The motion event corresponding to the button release.
     */
    private void buttonReleased(@TappingButtonIdentifier String buttonIdentifier, MotionEvent event) {
        int actionType = event.getAction();
        if (actionType != MotionEvent.ACTION_UP) {
            return;
        }

        if (this.showStepViewModel.userIsTapping()) {
            this.showStepViewModel.updateLastSample(Instant.ofEpochMilli(event.getEventTime()), buttonIdentifier);
        }
    }
    // endregion
}
