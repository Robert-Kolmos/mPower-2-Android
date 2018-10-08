package org.sagebionetworks.research.motor_control_module.inject;

import org.sagebionetworks.research.motor_control_module.step.CompletionStep;
import org.sagebionetworks.research.presentation.inject.TextToSpeechModule;
import org.sagebionetworks.research.presentation.speech.TextToSpeechService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module(includes = {InstructionStepModule.class, CompletionStepModule.class, HandSelectionStepModule.class, MPowerActiveStepModule.class,
        OverviewStepModule.class, TappingCompletionStepModule.class, TappingStepModule.class})
public abstract class MotorControlStepModule {
    @ContributesAndroidInjector
    abstract TextToSpeechService contributeTextToSpeechServiceInjector();
}
