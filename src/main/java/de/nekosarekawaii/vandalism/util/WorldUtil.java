/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.injection.access.IGameRenderer;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

public class WorldUtil implements MinecraftWrapper {

    // Dimensions can be dynamically registered, so we need to uncover them from the registry sent by the server
    public static DimensionType uncoverDimensionType(final RegistryKey<DimensionType> key) {
        return mc.world.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).get(key);
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

        mc.gameRenderer.updateCrosshairTarget(1.0f);
        final HitResult crosshairTarget = mc.crosshairTarget;

        ((IGameRenderer) mc.gameRenderer).vandalism$setRange(prevRange);

        mc.crosshairTarget = prevCrosshairTarget;
        mc.targetedEntity = prevTargetedEntity;
        mc.player.setYaw(prevYaw);
        mc.player.setPitch(prevPitch);

        return crosshairTarget;
    }

    public static double calculateRange(final Vec3d from, final Vec3d to) {
        final double d = from.x - to.x;
        final double e = from.y - to.y;
        final double f = from.z - to.z;
        return Math.sqrt(d * d + e * e + f * f);
    }

    public static boolean canHitEntity(final Entity from, final Entity target, final Rotation rotation, final double range) {
        final Vec3d eyePos = from.getEyePos();
        final Vec3d rotationVector = rotation.getVec();

        final double rangeSquared = range * range;

        final Vec3d targetVec = eyePos.add(rotationVector.x * range, rotationVector.y * range, rotationVector.z * range);
        final Box box = from.getBoundingBox().stretch(rotationVector.multiply(range)).expand(1.0, 1.0, 1.0);

        final EntityHitResult raycastEntity = ProjectileUtil.raycast(from, eyePos, targetVec, box, entity -> {
            return !entity.isSpectator() && entity.canHit() && entity == target;
        }, rangeSquared);

        if (raycastEntity == null) {
            return false;
        }

        final BlockHitResult raycastBlocks = mc.world.raycast(new RaycastContext(
                eyePos,
                raycastEntity.getPos(),
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                from
        ));

        final double distance = eyePos.squaredDistanceTo(raycastEntity.getPos());
        return distance <= rangeSquared && (raycastBlocks == null || raycastBlocks.getType() == HitResult.Type.MISS);
    }

    public static BlockHitResult raytraceBlocks(final Rotation rotation, final double range) {
        final Vec3d eyePos = mc.player.getEyePos();
        final Vec3d rotationVector = rotation.getVec();
        final Vec3d targetVec = eyePos.add(rotationVector.x * range, rotationVector.y * range, rotationVector.z * range);

        return mc.world.raycast(new RaycastContext(
                eyePos,
                targetVec,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));
    }

    public static GameMode getGameMode(final UUID uuid) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler != null) {
            final PlayerListEntry playerEntry = networkHandler.getPlayerListEntry(uuid);
            if (playerEntry != null) {
                return playerEntry.getGameMode();
            }
        }
        return null;
    }

    public static PlayerListEntry getPlayerFromTab(String message) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();

        if (networkHandler == null) {
            return null;
        }

        message = Formatting.strip(message).trim();
        for (final String messageSnippet : message.split(" ")) {
            final String trimmedSnippet = messageSnippet.trim();

            for (final PlayerListEntry playerEntry : networkHandler.getPlayerList()) {
                final String playerName = playerEntry.getProfile().getName();

                if (StringUtils.contains(trimmedSnippet, playerName)) {
                    return playerEntry;
                }
            }
        }

        return null;
    }

}
