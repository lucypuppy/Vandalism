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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.phase.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.event.game.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.phase.PhaseModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;

public class NoClipModuleMode extends ModuleMulti<PhaseModule> implements PlayerUpdateListener, BlockCollisionShapeListener {

    private final DoubleValue motionYOffset = new DoubleValue(
            this,
            "Motion Y Offset",
            "The motion y offset of the no clip phase.",
            0.5,
            0.1,
            2.0
    );

    private final KeyBindValue upwardsKey = new KeyBindValue(
            this,
            "Upwards Key",
            "The key to go upwards with the no clip phase.",
            GLFW.GLFW_KEY_SPACE
    );

    private final KeyBindValue downwardsKey = new KeyBindValue(
            this,
            "Downwards Key",
            "The key to go downwards with the no clip phase.",
            GLFW.GLFW_KEY_LEFT_SHIFT
    );

    public NoClipModuleMode() {
        super("No Clip");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(BlockCollisionShapeEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(BlockCollisionShapeEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.player.getBlockStateAtPos().isAir()) {
            return;
        }
        this.mc.player.setVelocity(
                0,
                this.upwardsKey.isPressed() ? this.motionYOffset.getValue() : this.downwardsKey.isPressed() ? -this.motionYOffset.getValue() : 0,
                0
        );
        if (MovementUtil.isMoving()) {
            final double combinedSpeed = Math.sqrt(mc.player.forwardSpeed * mc.player.forwardSpeed + mc.player.sidewaysSpeed * mc.player.sidewaysSpeed);
            final double direction = Math.toRadians(MovementUtil.getInputAngle(mc.player.getYaw()));
            MovementUtil.clip(direction, 0, combinedSpeed);
        }
    }

    @Override
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        event.shape = VoxelShapes.empty();
    }

}
