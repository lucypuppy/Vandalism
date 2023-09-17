package de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement.modes.phase;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.event.TickListener;
import de.foxglovedevelopment.foxglove.feature.impl.module.ModuleMode;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.movement.PhaseModule;

public class FallingBlockModuleMode extends ModuleMode<PhaseModule> implements TickListener {

    public FallingBlockModuleMode(final PhaseModule parent) {
        super("Falling Block", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (player() == null || world() == null) return;
        if (player().getBlockStateAtPos().isSolid() && world().getBlockState(player().getBlockPos().up(1)).isSolid()) {
            final double
                    yaw = Math.toRadians(player().headYaw),
                    horizontal = player().forwardSpeed > 0 ? 1 : player().forwardSpeed < 0 ? -1 : 0;
            double vertical = 0;
            if (options().sneakKey.isPressed()) {
                vertical = -1;
            } else if (options().jumpKey.isPressed() && player().fallDistance < 2.0f) {
                vertical = 1;
            }
            player().setPos(
                    player().getX() - Math.sin(yaw) * horizontal,
                    player().getY() + vertical,
                    player().getZ() + Math.cos(yaw) * horizontal
            );
        }
    }

}
