package de.nekosarekawaii.foxglove.util.click.clickers;

import de.nekosarekawaii.foxglove.util.click.ClickGenerator;
import net.minecraft.client.MinecraftClient;

public class CooldownClicker extends ClickGenerator {

    public CooldownClicker(final Runnable clickAction) {
        super(clickAction);
    }

    @Override
    public void update() {
        if (MinecraftClient.getInstance().player != null &&
                MinecraftClient.getInstance().player.getAttackCooldownProgress(0.25f) >= 1.0f) {
            clickAction.run();
            MinecraftClient.getInstance().player.resetLastAttackedTicks();
        }
    }

}
