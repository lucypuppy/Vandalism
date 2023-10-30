package de.vandalismdevelopment.vandalism.util.clicker.impl;

import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.util.clicker.Clicker;

public class CooldownClicker extends Clicker implements MinecraftWrapper {

    @Override
    public void update() {
        if (player() != null && player().getAttackCooldownProgress(0.25f) >= 1.0f) {
            this.clickAction.run();
            player().resetLastAttackedTicks();
        }
    }

}
