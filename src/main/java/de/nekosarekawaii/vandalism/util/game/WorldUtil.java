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

package de.nekosarekawaii.vandalism.util.game;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.internal.TargetListener;
import de.nekosarekawaii.vandalism.injection.access.IGameRenderer;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class WorldUtil implements MinecraftWrapper {

    public enum Dimension {
        OVERWORLD, NETHER, END
    }

    //We don't need any other dimensions since this client isn't intended to be compatible with mod packs.
    public static Dimension getDimension() {
        return switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.NETHER;
            case "the_end" -> Dimension.END;
            default -> Dimension.OVERWORLD;
        };
    }

    public static HitResult raytrace(final Rotation rotation, final double range) {
        final float prevYaw = mc.player.getYaw();
        final float prevPitch = mc.player.getPitch();

        final HitResult prevCrosshairTarget = mc.crosshairTarget;
        final Entity prevTargetedEntity = mc.targetedEntity;

        mc.player.setYaw(rotation.getYaw());
        mc.player.setPitch(rotation.getPitch());

        final double prevRange = ((IGameRenderer) mc.gameRenderer).vandalism$getRange();
        ((IGameRenderer) mc.gameRenderer).vandalism$setRange(range);

        mc.gameRenderer.updateTargetedEntity(1.0f);
        final HitResult crosshairTarget = mc.crosshairTarget;

        ((IGameRenderer) mc.gameRenderer).vandalism$setRange(prevRange);

        mc.crosshairTarget = prevCrosshairTarget;
        mc.targetedEntity = prevTargetedEntity;
        mc.player.setYaw(prevYaw);
        mc.player.setPitch(prevPitch);

        return crosshairTarget;
    }

    public static boolean canHitEntity(final Entity target, final Rotation rotation, final double range) {
        final Vec3d eyePos = mc.player.getEyePos();
        final Vec3d rotationVector = rotation.getVector();

        final double rangeSquared = range * range;

        final Vec3d targetVec = eyePos.add(rotationVector.x * range, rotationVector.y * range, rotationVector.z * range);
        final Box box = mc.player.getBoundingBox().stretch(rotationVector.multiply(range)).expand(1.0, 1.0, 1.0);

        final EntityHitResult raycastEntity = ProjectileUtil.raycast(mc.player, eyePos, targetVec, box,
                entity -> !entity.isSpectator() && entity.canHit() && entity == target, rangeSquared);

        if (raycastEntity == null)
            return false;

        final BlockHitResult raycastBlocks = mc.world.raycast(new RaycastContext(eyePos, raycastEntity.getPos(),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));

        final double distance = eyePos.squaredDistanceTo(raycastEntity.getPos());
        return distance <= rangeSquared && (raycastBlocks == null || raycastBlocks.getType() == HitResult.Type.MISS);
    }

    public static boolean isTarget(final Entity entity) {
        final var event = new TargetListener.TargetEvent(entity);
        Vandalism.getInstance().getEventSystem().postInternal(TargetListener.TargetEvent.ID, event);
        return event.isTarget;
    }

}
