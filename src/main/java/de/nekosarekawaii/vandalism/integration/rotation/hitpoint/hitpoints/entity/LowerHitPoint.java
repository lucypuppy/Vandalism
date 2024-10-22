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
import net.minecraft.util.math.Vec3d;

public class LowerHitPoint extends EntityHitPoint {

    public LowerHitPoint() {
        super("Lower");
    }

    @Override
    public Vec3d generateHitPoint(Entity entity) {
        final float width = entity.getWidth() / 2f;
        final double localPlayerX = mc.player.getX();
        final double localPlayerY = mc.player.getY();
        final double localPlayerZ = mc.player.getZ();
        final double localPlayerEyeY = localPlayerY + mc.player.getEyeY();
        final double enemyX = entity.getX();
        final double enemyZ = entity.getZ();

        final double x = localPlayerX >= enemyX - width && localPlayerX <= enemyX + width ? localPlayerX : (enemyX - localPlayerX < 0 ? enemyX + width : enemyX - width);
        final double z = localPlayerZ >= enemyZ - width && localPlayerZ <= enemyZ + width ? localPlayerZ : (enemyZ - localPlayerZ < 0 ? enemyZ + width : enemyZ - width);

        final double eposY = entity.getY() - 0.1;
        double y = mc.player.getY() + mc.player.getEyeY();
        if (entity.getY() + entity.getHeight() < localPlayerEyeY) {
            y = eposY + (entity.getHeight() + 0.2f);
        }
        if (entity.getY() > localPlayerEyeY) {
            y = eposY;
        }

        // Aiming a bit lower will be improved
        y -= entity.getHeight() / 3f;

        return new Vec3d(x, y, z);
    }
}
