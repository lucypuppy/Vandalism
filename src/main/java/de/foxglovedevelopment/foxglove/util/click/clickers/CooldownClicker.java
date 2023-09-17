package de.foxglovedevelopment.foxglove.util.click.clickers;

import de.foxglovedevelopment.foxglove.util.MinecraftWrapper;
import de.foxglovedevelopment.foxglove.util.click.ClickGenerator;

public class CooldownClicker extends ClickGenerator implements MinecraftWrapper {

    @Override
    public void update() {
        if (player() != null && player().getAttackCooldownProgress(0.25f) >= 1.0f) {
            this.clickAction.run();
            player().resetLastAttackedTicks();
        }
    }

}
