package de.nekosarekawaii.foxglove.util.minecraft.player;

import net.minecraft.client.MinecraftClient;

public class MovementUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static double getDirection() {
        final var player = mc.player;
        return (Math.atan2(player.forwardSpeed, player.sidewaysSpeed) /
                Math.PI * 180.0F + player.getYaw()) * Math.PI / 180.0F;
    }

    public static void setSpeed(final double speed) {
        final var player = mc.player;
        final var direction = getDirection();
        player.setVelocity(Math.cos(direction) * speed, player.getVelocity().getY(), Math.sin(direction) * speed);
    }

    public static void clip(final double vertical, final double horizontal) {
        final var player = mc.player;
        final var direction = getDirection();
        player.setPos(
                player.getX() - Math.sin(direction) * horizontal,
                player.getY() + vertical,
                player.getZ() + Math.cos(direction) * horizontal
        );
    }

}