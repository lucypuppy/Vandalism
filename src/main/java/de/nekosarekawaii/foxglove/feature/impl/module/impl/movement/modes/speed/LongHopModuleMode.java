package de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.speed;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleMode;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.movement.SpeedModule;
import net.minecraft.client.network.ClientPlayerEntity;

public class LongHopModuleMode extends ModuleMode<SpeedModule> implements TickListener {

    public LongHopModuleMode(final SpeedModule parent) {
        super("Long Hop", parent);
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
        if (player.forwardSpeed != 0 || player.sidewaysSpeed != 0) {
            if (player.isOnGround()) {
                player.jump();
            }
            final float yaw = (float) ((Math.atan2(player.forwardSpeed,
                    player.sidewaysSpeed) / Math.PI * 180.0F
                    + player.getYaw()
            ) * Math.PI / 180.0F);
            final double speed = 1.5;
            player.setVelocity(Math.cos(yaw) * speed, player.getVelocity().getY(), Math.sin(yaw) * speed);
        }
    }

}
