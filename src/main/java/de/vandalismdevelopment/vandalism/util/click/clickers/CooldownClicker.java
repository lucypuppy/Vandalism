package de.vandalismdevelopment.vandalism.util.click.clickers;

import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.util.click.ClickGenerator;

public class CooldownClicker extends ClickGenerator implements MinecraftWrapper {

    @Override
    public void update() {
        if (player() != null && player().getAttackCooldownProgress(0.25f) >= 1.0f) {
            this.clickAction.run();
            player().resetLastAttackedTicks();
        }
    }

}
