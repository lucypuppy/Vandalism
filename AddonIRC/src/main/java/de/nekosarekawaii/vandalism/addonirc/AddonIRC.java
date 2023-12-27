package de.nekosarekawaii.vandalism.addonirc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.addonirc.clientmenu.IrcClientMenuWindow;
import de.nekosarekawaii.vandalism.addonirc.config.IRCConfig;

public class AddonIRC implements VandalismAddonLauncher {

    @Override
    public void onLaunch(Vandalism vandalism) {
        vandalism.getClientMenuManager().add(new IrcClientMenuWindow());
        vandalism.getConfigManager().add(new IRCConfig());
    }

}
