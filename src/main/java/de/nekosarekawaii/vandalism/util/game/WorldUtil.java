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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
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

        mc.gameRenderer.updateTargetedEntity(1.0F);
        final HitResult crosshairTarget = mc.crosshairTarget;

        ((IGameRenderer) mc.gameRenderer).vandalism$setRange(prevRange);

        mc.crosshairTarget = prevCrosshairTarget;
        mc.targetedEntity = prevTargetedEntity;
        mc.player.setYaw(prevYaw);
        mc.player.setPitch(prevPitch);

        return crosshairTarget;
    }

    public static BlockHitResult rayTraceBlock(final Vec3d targetPosition, final double maxDistance) {
        final Vec3d eyePos = mc.player.getEyePos();
        final Vec3d lookVector = targetPosition.subtract(eyePos).normalize().multiply(maxDistance);
        final Vec3d currentPos = eyePos.add(lookVector);

        return mc.world.raycast(new RaycastContext(eyePos, currentPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
    }

    public static boolean isTarget(final Entity entity) {
        final var event = new TargetListener.TargetEvent(entity);
        Vandalism.getInstance().getEventSystem().postInternal(TargetListener.TargetEvent.ID, event);
        return event.isTarget;
    }

}
