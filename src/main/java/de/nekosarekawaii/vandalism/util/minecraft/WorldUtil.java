/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.util.minecraft;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.internal.TargetListener;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class WorldUtil implements MinecraftWrapper {

    public static boolean doingRaytrace = false;
    public static double raytraceRange = -1;

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
    public static HitResult rayTrace(final Rotation rotation, final double range) {
        final HitResult lastCrosshairTarget = mc.crosshairTarget;
        final Entity lastTargetedEntity = mc.targetedEntity;
        final float lastYaw = mc.player.getYaw();
        final float lastPitch = mc.player.getPitch();
        mc.player.setYaw(rotation.getYaw());
        mc.player.setPitch(rotation.getPitch());

        raytraceRange = range;
        doingRaytrace = true;

        mc.gameRenderer.updateTargetedEntity(1.0F);
        final HitResult crosshairTarget = mc.crosshairTarget;

        doingRaytrace = false;
        raytraceRange = -1;

        mc.crosshairTarget = lastCrosshairTarget;
        mc.targetedEntity = lastTargetedEntity;
        mc.player.setYaw(lastYaw);
        mc.player.setPitch(lastPitch);
        return crosshairTarget;
    }

    public static BlockHitResult rayTraceBlock(final Rotation rotation, final double maxDistance) {
        final Vec3d playerPosition = mc.player.getEyePos();
        final Vec3d rotationVector = rotation.getVector();
        final Vec3d currentPos = playerPosition.add(
                rotationVector.x * maxDistance,
                rotationVector.y * maxDistance,
                rotationVector.z * maxDistance
        );

        return mc.world.raycast(new RaycastContext(
                playerPosition,
                currentPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));
    }

    public static BlockHitResult rayTraceBlock(final Vec3d targetPosition, final double maxDistance) {
        final Vec3d playerPosition = mc.player.getEyePos();
        final Vec3d lookDirection = targetPosition.subtract(playerPosition).normalize();
        final Vec3d currentPos = playerPosition.add(
                lookDirection.x * maxDistance,
                lookDirection.y * maxDistance,
                lookDirection.z * maxDistance
        );

        return mc.world.raycast(new RaycastContext(
                playerPosition,
                currentPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));
    }

    public static boolean isTarget(final Entity entity) {
        final TargetListener.TargetEvent event = new TargetListener.TargetEvent(entity);
        Vandalism.getInstance().getEventSystem().postInternal(TargetListener.TargetEvent.ID, event);
        return event.isTarget;
    }

}
