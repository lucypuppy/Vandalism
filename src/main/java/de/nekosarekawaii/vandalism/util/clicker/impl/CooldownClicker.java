package de.nekosarekawaii.vandalism.util.clicker.impl;

import de.nekosarekawaii.vandalism.util.clicker.Clicker;

public class CooldownClicker extends Clicker {

    @Override
    public void onUpdate() {
        if (this.mc.player != null && this.mc.player.getAttackCooldownProgress(0.25f) >= 1.0f) {
            this.clickAction.run();
            this.mc.player.resetLastAttackedTicks();
        }
    }

}
