package de.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MovementUtil implements MinecraftWrapper {

    public static double getDirection() {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;
        return (Math.atan2(player.forwardSpeed, player.sidewaysSpeed) /
                Math.PI * 180.0F + player.getYaw()) * Math.PI / 180.0F;
    }

    public static void setSpeed(final double speed) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        final double direction = getDirection();
        player.setVelocity(Math.cos(direction) * speed, player.getVelocity().getY(), Math.sin(direction) * speed);
    }

    public static void clip(final double vertical, final double horizontal) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        final double direction = getDirection();
        player.setPos(
                player.getX() - Math.sin(direction) * horizontal,
                player.getY() + vertical,
                player.getZ() + Math.cos(direction) * horizontal
        );
    }

}