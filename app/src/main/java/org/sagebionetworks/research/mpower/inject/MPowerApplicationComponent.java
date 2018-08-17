package org.sagebionetworks.research.mpower.inject;

import android.app.Application;

import org.sagebionetworks.bridge.android.di.BridgeServiceModule;
import org.sagebionetworks.bridge.android.di.S3Module;
import org.sagebionetworks.research.domain.form.interfaces.InputField;
import org.sagebionetworks.research.domain.inject.ActionModule;
import org.sagebionetworks.research.domain.inject.InputFieldsModule;
import org.sagebionetworks.research.mpower.MPowerApplication;
import org.sagebionetworks.research.mpower.researchstack.inject.MPowerResearchStackModule;
import org.sagebionetworks.research.mpower.sageresearch.inject.MPowerSageResearchModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {MPowerApplicationModule.class,
        MPowerResearchStackModule.class, MPowerSageResearchModule.class,
        S3Module.class, BridgeServiceModule.class})
public interface MPowerApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        MPowerApplicationComponent build();
    }

    void inject(MPowerApplication application);
}
