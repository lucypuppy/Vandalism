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
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.PlayerDamageUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TeleportModule extends AbstractModule implements PlayerUpdateListener, IncomingPacketListener {

    private final FloatValue maxDistance = new FloatValue(
            this,
            "Max Distance",
            "Max teleport distance.",
            10.0f,
            0.0f,
            100.0f
    );

    private boolean damaged, flagged, pressed, executed;
    private HitResult result;

    public TeleportModule() {
        super("Teleport", "Teleport to the block you click on.", Category.MOVEMENT);
    }

    private final ModeValue mode = new ModeValue(
            this,
            "Mode",
            "The mode of the teleport.",
            "Vanilla",
            "Vulcan"
    );

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        damaged = flagged = pressed = executed = false;
        result = null;
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        switch (mode.getValue().toLowerCase()) {
            case "vanilla" -> vanilla();
            case "vulcan" -> vulcan();
        }
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (mode.getValue().equalsIgnoreCase("Vulcan") && pressed && event.packet instanceof PlayerPositionLookS2CPacket) {
            flagged = true;
        }
    }

    private void vanilla() {
        if (this.mc.player.isUsingItem() || !this.mc.options.useKey.isPressed()) {
            return;
        }
        final HitResult result = this.mc.player.raycast(this.maxDistance.getValue(), mc.getRenderTickCounter().getTickDelta(false), false);
        if (!this.mc.player.isUsingItem() && this.mc.options.useKey.isPressed() && result.getType() == HitResult.Type.BLOCK) {
            pressed = true;
        }
        if (result.getType() == HitResult.Type.BLOCK) {
            final BlockPos pos = ((BlockHitResult) result).getBlockPos();
            final Vec3d finalPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            final double dis = mc.player.getPos().distanceTo(result.getPos());
            for (double d = 0.0D; d < dis; d += 2.0D) {
                final double x = this.mc.player.getX() + (finalPos.x - (double) this.mc.player.getHorizontalFacing().getOffsetX() - this.mc.player.getX()) * d / dis;
                final double y = this.mc.player.getY() + (finalPos.y - this.mc.player.getY()) * d / dis;
                final double z = this.mc.player.getZ() + (finalPos.z - (double) this.mc.player.getHorizontalFacing().getOffsetZ() - this.mc.player.getZ()) * d / dis;
                this.mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
                this.mc.player.setPosition(x, y, z);
            }
            final double x = finalPos.x;
            final double y = finalPos.y;
            final double z = finalPos.z;
            this.mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
            this.mc.player.setPosition(x, y, z);
            this.mc.options.useKey.setPressed(false);
        }
    }

    private void vulcan() {
        if (!this.mc.player.isUsingItem() && this.mc.options.useKey.isPressed()) {
            result = this.mc.player.raycast(this.maxDistance.getValue(), mc.getRenderTickCounter().getTickDelta(false) /* TODO CHECK EVERYWHERE*/, false);
            if (result.getType() == HitResult.Type.BLOCK) {
                this.mc.options.useKey.setPressed(false);
                pressed = true;
                executed = false;
            }
        }

        if (flagged && damaged && pressed && result != null) {
            final BlockPos pos = ((BlockHitResult) result).getBlockPos();
            final Vec3d finalPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            final double dis = mc.player.getPos().distanceTo(result.getPos());
            for (double d = 0.0D; d < dis; d += 9.0D) {
                final double x = this.mc.player.getX() + (finalPos.x - (double) this.mc.player.getHorizontalFacing().getOffsetX() - this.mc.player.getX()) * d / dis;
                final double y = this.mc.player.getY() + (finalPos.y - this.mc.player.getY()) * d / dis;
                final double z = this.mc.player.getZ() + (finalPos.z - (double) this.mc.player.getHorizontalFacing().getOffsetZ() - this.mc.player.getZ()) * d / dis;
                this.mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
            }
            final double x = finalPos.x;
            final double y = finalPos.y;
            final double z = finalPos.z;
            this.mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
            this.mc.player.setPosition(x, y, z);
            flagged = damaged = pressed = executed = false;
        }

        if (!damaged && pressed && mc.player.isOnGround() && result != null && !executed) {
            PlayerDamageUtil.damagePlayerVulcan();
            executed = true;
        }
        if (pressed && mc.player.hurtTime > 0) {
            damaged = true;
        }
    }
}
