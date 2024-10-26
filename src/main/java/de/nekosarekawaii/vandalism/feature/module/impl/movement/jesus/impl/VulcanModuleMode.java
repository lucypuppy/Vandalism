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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.JesusModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class VulcanModuleMode extends ModuleMulti<JesusModule> implements BlockCollisionShapeListener, PlayerUpdateListener, OutgoingPacketListener {

    private boolean overLiquid = false;
    private long offGroundTicks = 0;

    public VulcanModuleMode() {
        super("Vulcan");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, BlockCollisionShapeEvent.ID, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, BlockCollisionShapeEvent.ID, PlayerUpdateEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        if (event.pos.getY() < mc.player.getY() && !event.state.getFluidState().isEmpty()) {
            event.shape = VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1);
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final boolean lastOverLiquid = this.overLiquid;
        this.overLiquid = overLiquid();

        if (!this.overLiquid && lastOverLiquid) {
            final Vec3d velocity = MovementUtil.setSpeed(0.2);
            mc.player.setVelocity(velocity.x, -0.42, velocity.z);
            return;
        }

        if (!this.overLiquid)
            return;

        if (mc.player.isOnGround()) {
            this.offGroundTicks = 0;
            mc.player.setVelocity(mc.player.getVelocity().x, 0.6, mc.player.getVelocity().z);
            mc.player.fallDistance = 0;
            return;
        } else if (mc.player.fallDistance > 0) {
            mc.player.setVelocity(mc.player.getVelocity().x, -0.1, mc.player.getVelocity().z);
        }

        this.offGroundTicks++;
        if (!MovementUtil.isMoving() || this.offGroundTicks < 5) {
            return;
        }

        MovementUtil.setSpeed(0.33);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketListener.OutgoingPacketEvent event) {
        if (event.packet instanceof PlayerMoveC2SPacket packet) {
            if (!overLiquid)
                return;

            if (mc.player.fallDistance > 0 && mc.player.age % 19 == 0) { // Fix fall damage
                packet.onGround = true;
                return;
            }

            // Disable JesusA check
            packet.onGround = false;
            packet.y += 0.1;
        }
    }

    public boolean overLiquid() {
        for (int i = 0; i < 5; i++) {
            final BlockState blockState = mc.world.getBlockState(mc.player.getBlockPos().add(0, -i, 0));

            if (blockState.getFluidState().isEmpty()) {
                if (!blockState.isAir()) {
                    return false; // Return if a block is in between the player and the water
                }
            } else {
                return true; // Return if player is over water
            }
        }

        return false; // Return if player is too high
    }

}
