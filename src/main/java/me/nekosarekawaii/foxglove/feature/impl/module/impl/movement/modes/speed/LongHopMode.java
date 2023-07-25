package me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.modes.speed;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.impl.module.Mode;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.SpeedModule;
import net.minecraft.client.network.ClientPlayerEntity;

public class LongHopMode extends Mode<SpeedModule> implements TickListener {

    public LongHopMode(final SpeedModule parent) {
        super("Long Hop", parent);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    public void onTick() {
        final ClientPlayerEntity player = mc.player;
        if (player == null) return;
        if (player.isOnGround() && (player.forwardSpeed != 0 || player.sidewaysSpeed != 0)) {
            player.jump();
            final float yaw = (float) ((Math.atan2(player.forwardSpeed,
                    player.sidewaysSpeed) / Math.PI * 180.0F
                    + player.getYaw()
            ) * Math.PI / 180.0F);
            final double speed = 1.5;
            player.setVelocity(Math.cos(yaw) * speed, player.getVelocity().getY(), Math.sin(yaw) * speed);
        }
    }

}
