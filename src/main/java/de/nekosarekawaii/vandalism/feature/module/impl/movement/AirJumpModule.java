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
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AirJumpModule extends Module implements PlayerUpdateListener {

    private final ModeValue mode = new ModeValue(
            this,
            "Mode",
            "The mode used to jump in air.",
            "On ground", "Jump"
    );

    private final BooleanValue sprintBoost = new BooleanValue(
            this,
            "Sprint boost",
            "Specifies wether or not the sprint boost is getting applied.",
            true
    ).visibleCondition(() -> this.mode.getValue().equals("Jump"));

    public AirJumpModule() {
        super(
                "Air Jump",
                "Allows the player to jump mid air.",
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
        if (this.mc.options.jumpKey.isPressed()) {
            switch (this.mode.getValue()) {
                case "On ground": {
                    this.mc.player.setOnGround(true);
                    break;
                }
                case "Jump": {
                    this.jump(this.sprintBoost.getValue());
                    break;
                }
            }
        }
    }

    private void jump(boolean sprintBoost) {
        Vec3d vec3d = this.mc.player.getVelocity();
        this.mc.player.setVelocity(vec3d.x, (double) this.mc.player.getJumpVelocity(), vec3d.z);
        if (this.mc.player.isSprinting() && sprintBoost) {
            float f = this.mc.player.getYaw() * (float) (Math.PI / 180.0);
            this.mc.player.setVelocity(this.mc.player.getVelocity().add((double) (-MathHelper.sin(f) * 0.2F), 0.0, (double) (MathHelper.cos(f) * 0.2F)));
        }

        this.mc.player.velocityDirty = true;
    }

}
