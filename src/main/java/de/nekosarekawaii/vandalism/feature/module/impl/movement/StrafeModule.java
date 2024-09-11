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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MovementUtil;

public class StrafeModule extends Module implements PlayerUpdateListener {

    private final BooleanValue onlyOnGround = new BooleanValue(
            this,
            "Only on ground",
            "Only changes direction if the player is on ground.",
            false
    );

    private final BooleanValue autoJump = new BooleanValue(
            this,
            "Auto Jump",
            "Jumps automatically if on ground.",
            false
    );

    public StrafeModule() {
        super("Strafe",
                "Improves the standard movement of minecraft.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (!MovementUtil.isMoving()) return;
        if (this.onlyOnGround.getValue() && !this.mc.player.isOnGround()) return;
        if (this.autoJump.getValue() && this.mc.player.isOnGround()) this.mc.player.jump();
        MovementUtil.setSpeed(MovementUtil.getSpeed());
    }
}
