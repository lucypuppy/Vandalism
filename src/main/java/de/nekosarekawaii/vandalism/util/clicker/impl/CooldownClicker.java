package de.nekosarekawaii.vandalism.util.clicker.impl;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.minecraft.impl.clicker.Clicker;

public class CooldownClicker extends Clicker implements MinecraftWrapper {

    @Override
    public void update() {
        if (this.mc.player != null && this.mc.player.getAttackCooldownProgress(0.25f) >= 1.0f) {
            this.clickAction.run();
            this.mc.player.resetLastAttackedTicks();
        }
    }

}
