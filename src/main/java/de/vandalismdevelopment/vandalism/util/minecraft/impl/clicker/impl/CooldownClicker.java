package de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.impl;

import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.Clicker;

public class CooldownClicker extends Clicker implements MinecraftWrapper {

    @Override
    public void update() {
        if (this.player() != null && this.player().getAttackCooldownProgress(0.25f) >= 1.0f) {
            this.clickAction.run();
            this.player().resetLastAttackedTicks();
        }
    }

}
