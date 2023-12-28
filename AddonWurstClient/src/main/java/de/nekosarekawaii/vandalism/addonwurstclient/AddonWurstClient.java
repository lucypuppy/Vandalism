package de.nekosarekawaii.vandalism.addonwurstclient;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.addonwurstclient.injection.access.IWurstClient;
import de.nekosarekawaii.vandalism.addonwurstclient.module.WurstClientModule;
import net.wurstclient.WurstClient;

import java.util.List;

public class AddonWurstClient implements VandalismAddonLauncher {

    // Temporary list to store the enabled hacks when the user disables the WurstClient module
    public static List<String> enabledHacks;

    private WurstClientModule module;

    @Override
    public void onLaunch(Vandalism vandalism) {
        WurstClient.INSTANCE.initialize(); // Initialize WurstClient, counterpart in MixinWurstInitializer.java

        vandalism.getModuleManager().add(module = new WurstClientModule());
    }

    @Override
    public void onLateLaunch(Vandalism vandalism) {
        ((IWurstClient) (Object) WurstClient.INSTANCE).vandalism$setSilentEnabled(module.isActive());
    }

}
