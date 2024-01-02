package de.nekosarekawaii.vandalism.addonwurstclient.hack;

import net.wurstclient.Category;
import net.wurstclient.hack.Hack;
import net.wurstclient.options.WurstOptionsScreen;

public class WurstClientOptionsHack extends Hack {

    public WurstClientOptionsHack() {
        super("Options");
        this.setCategory(Category.OTHER);
    }

    @Override
    protected void onEnable() {
        this.setEnabled(false);
        MC.setScreen(new WurstOptionsScreen(MC.currentScreen));
    }

}
