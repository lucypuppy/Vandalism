package de.nekosarekawaii.vandalism.addonwurstclient.module;

import de.nekosarekawaii.vandalism.addonwurstclient.injection.access.IWurstClient;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.wurstclient.WurstClient;

public class WurstClientModule extends AbstractModule {

    public WurstClientModule() {
        super("Wurst Client", "Implementation of the Wurst client.", Category.MISC);
    }

    @Override
    public void onActivate() {
        ((IWurstClient) (Object) WurstClient.INSTANCE).vandalism$setTrackedEnabled(true);
    }

    @Override
    public void onDeactivate() {
        ((IWurstClient) (Object) WurstClient.INSTANCE).vandalism$setTrackedEnabled(false);
    }

}
