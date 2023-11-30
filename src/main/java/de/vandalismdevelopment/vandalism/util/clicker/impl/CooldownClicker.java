package de.vandalismdevelopment.vandalism.util.clicker.impl;

import de.vandalismdevelopment.vandalism.util.clicker.Clicker;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;

public class CooldownClicker extends Clicker implements MinecraftWrapper {

    @Override
    public void update() {
        if (this.player() != null && this.player().getAttackCooldownProgress(0.25f) >= 1.0f) {
            this.clickAction.run();
            this.player().resetLastAttackedTicks();
        }
    }

}
