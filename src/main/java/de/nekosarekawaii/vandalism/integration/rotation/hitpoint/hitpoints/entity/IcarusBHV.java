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

package de.nekosarekawaii.vandalism.integration.rotation.hitpoint.hitpoints.entity;

import de.nekosarekawaii.vandalism.integration.rotation.hitpoint.EntityHitPoint;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class IcarusBHV extends EntityHitPoint {

    public IcarusBHV() {
        super("Best Hit Vector");
    }

    @Override
    public Vec3d generateHitPoint(Entity entity) {
        final float width = entity.getWidth() / 2f;
        final double localPlayerX = mc.player.getX();
        final double localPlayerY = mc.player.getY();
        final double localPlayerZ = mc.player.getZ();
        final double localPlayerEyeHeight = mc.player.getEyeHeight(mc.player.getPose());

        final double enemyX = entity.getX();
        final double enemyZ = entity.getZ();
        final double x = localPlayerX >= enemyX - width && localPlayerX <= enemyX + width ? localPlayerX : (enemyX - localPlayerX < 0 ? enemyX + width : enemyX - width);
        final double z = localPlayerZ >= enemyZ - width && localPlayerZ <= enemyZ + width ? localPlayerZ : (enemyZ - localPlayerZ < 0 ? enemyZ + width : enemyZ - width);

        final double yDIff = MathHelper.clamp(entity.getY() - localPlayerY, -1, 1);
        final double y = (entity.getY() + localPlayerEyeHeight) - yDIff;

        return new Vec3d(x, y, z);
    }

}