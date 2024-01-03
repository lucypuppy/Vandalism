package de.nekosarekawaii.vandalism.util.minecraft;

import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.util.hit.HitResult;

public class WorldUtil implements MinecraftWrapper {

    public static boolean doingRaytrace = false;

    public enum Dimension {
        OVERWORLD, NETHER, END
    }

    public static Dimension getDimension() {
        return switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.NETHER;
            case "the_end" -> Dimension.END;
            default -> Dimension.OVERWORLD;
        };
    }

    // This is a edited copy of net.minecraft.client.render.GameRenderer.updateTargetedEntity
    public static HitResult rayTrace(final Rotation rotation) {
        final float lastYaw = mc.player.getYaw();
        final float lastPitch = mc.player.getPitch();
        mc.player.setYaw(rotation.getYaw());
        mc.player.setPitch(rotation.getPitch());
        doingRaytrace = true;

        mc.gameRenderer.updateTargetedEntity(1.0F);
        final HitResult crosshairTarget = mc.crosshairTarget;

        doingRaytrace = false;
        mc.player.setYaw(lastYaw);
        mc.player.setPitch(lastPitch);
        return crosshairTarget;
    }

}
