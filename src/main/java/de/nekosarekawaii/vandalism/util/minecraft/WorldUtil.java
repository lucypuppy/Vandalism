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

import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.util.hit.HitResult;

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

        mc.player.setYaw(lastYaw);
        mc.player.setPitch(lastPitch);
        return crosshairTarget;
    }

}
