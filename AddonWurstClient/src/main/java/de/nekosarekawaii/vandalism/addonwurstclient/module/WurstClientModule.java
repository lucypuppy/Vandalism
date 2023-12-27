package de.nekosarekawaii.vandalism.addonwurstclient.module;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.wurstclient.WurstClient;

public class WurstClientModule extends AbstractModule {

    public WurstClientModule() {
        super("Wurst Client", "Implementation of the Wurst client.", Category.MISC);
    }

    @Override
    public void onActivate() {
        WurstClient.INSTANCE.setEnabled(true);
    }

    @Override
    public void onDeactivate() {
        WurstClient.INSTANCE.setEnabled(false);
    }

}
