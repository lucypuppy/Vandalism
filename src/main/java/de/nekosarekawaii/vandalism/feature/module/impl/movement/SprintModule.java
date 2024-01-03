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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.SprintListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;

public class SprintModule extends AbstractModule implements SprintListener {

    public SprintModule() {
        super("Sprint", "Automatically let's you sprint!", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(SprintEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(SprintEvent.ID, this);
    }

    @Override
    public void onSprint(final SprintEvent event) {
      /*  final RotationListener rotation = Vandalism.getInstance().getRotationListener();
        final boolean useSpoofedRotation = rotation.getRotation() != null;
        if (Math.abs(MathHelper.wrapDegrees((useSpoofedRotation ? rotation.getRotation().getYaw() : mc.player.getYaw()) - MovementUtil.getInputAngle(mc.player.getYaw()))) > 45D) {
            event.sprinting = false;
            event.force = true;
            return;
        }*/
        event.sprinting = MovementUtil.isMoving();
    }

}
