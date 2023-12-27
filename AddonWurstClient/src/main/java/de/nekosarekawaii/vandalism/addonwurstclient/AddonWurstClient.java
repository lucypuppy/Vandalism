package de.nekosarekawaii.vandalism.addonwurstclient;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.addonwurstclient.module.WurstClientModule;
import net.wurstclient.WurstClient;

public class AddonWurstClient implements VandalismAddonLauncher {

    @Override
    public void onLaunch(Vandalism vandalism) {
        WurstClient.INSTANCE.initialize(); // Initialize WurstClient, counterpart in MixinWurstInitializer.java

        vandalism.getModuleManager().add(new WurstClientModule());
    }

}
