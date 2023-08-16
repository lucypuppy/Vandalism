package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.phase;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.PhaseModule;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.network.ClientPlayerEntity;

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
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        final Block block = player.getBlockStateAtPos().getBlock();
        if (!(block instanceof AirBlock) && !(block instanceof FluidBlock)) {
            final double
                    yaw = Math.toRadians(player.headYaw),
                    horizontal = player.forwardSpeed > 0 ? 1 : player.forwardSpeed < 0 ? -1 : 0;
            double vertical = 0;
            if (mc.options.sneakKey.isPressed()) {
                vertical = -1;
            } else if (mc.options.jumpKey.isPressed() && player.fallDistance < 2.0f) {
                vertical = 1;
            }
            player.setPos(
                    player.getX() - Math.sin(yaw) * horizontal,
                    player.getY() + vertical,
                    player.getZ() + Math.cos(yaw) * horizontal
            );
        }
    }

}
