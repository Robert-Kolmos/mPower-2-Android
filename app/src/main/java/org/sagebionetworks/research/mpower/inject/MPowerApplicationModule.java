package org.sagebionetworks.research.mpower.inject;

import android.app.Application;
import android.content.Context;

import org.sagebionetworks.research.mpower.history.HistoryFragment;
import org.sagebionetworks.research.mpower.insights.InsightsFragment;
import org.sagebionetworks.research.mpower.tracking.TrackingFragment;
import org.sagebionetworks.research.mpower.MainActivity;
import org.sagebionetworks.research.mpower.profile.ProfileFragment;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public interface MPowerApplicationModule {
    @Provides
    static Context getApplicationContext(Application application) {
        return application.getApplicationContext();
    }

    @ContributesAndroidInjector
    HistoryFragment contributeHistoryFragmentInjector();

    @ContributesAndroidInjector
    InsightsFragment contributeInsightsFragmentInjector();

    @ContributesAndroidInjector
    TrackingFragment contributeTrackingFragmentInjector();

    @ContributesAndroidInjector
    MainActivity contributeMainActivityInjector();

    @ContributesAndroidInjector
    ProfileFragment contributeProfileFragmentInjector();
}
