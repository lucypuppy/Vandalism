package de.nekosarekawaii.vandalism.addonserverdiscovery;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.addonserverdiscovery.clientmenu.ServerDiscoveryClientMenuWindow;

public class AddonServerDiscovery implements VandalismAddonLauncher {

    @Override
    public void onLaunch(Vandalism vandalism) {
        vandalism.getClientMenuManager().add(new ServerDiscoveryClientMenuWindow());
    }

}
