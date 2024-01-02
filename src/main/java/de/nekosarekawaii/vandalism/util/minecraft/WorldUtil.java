package de.nekosarekawaii.vandalism.util.minecraft;

import de.florianmichael.rclasses.functional.tuple.Pair;
import de.florianmichael.rclasses.functional.tuple.immutable.ImmutablePair;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class WorldUtil implements MinecraftWrapper {

    public static double raytraceRange = -1.0;

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

    public static Pair<HitResult, Double> rayTrace(final Rotation rotation, final Vec3d cameraVec, final double range) {
        final Vec3d rotationVec = rotation.getVector();
        final Vec3d maxVec = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);

        final HitResult crosshairTarget = mc.player.getWorld().raycast(new RaycastContext(cameraVec, maxVec,
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
        final double croshairRange = crosshairTarget != null ? crosshairTarget.getPos().squaredDistanceTo(cameraVec) : range * range;

        final Box box = mc.player.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);
        final EntityHitResult entityHitResult = ProjectileUtil.raycast(
                mc.player,
                cameraVec,
                maxVec,
                box,
                entity -> !entity.isSpectator() && entity.canHit(),
                croshairRange
        );

        if (entityHitResult != null) {
            final Vec3d raytracePos = entityHitResult.getPos();
            final double raytraceRange = cameraVec.distanceTo(raytracePos) + 0.55;
            return new ImmutablePair<>(entityHitResult, raytraceRange);
        }

        return null;
    }

}
