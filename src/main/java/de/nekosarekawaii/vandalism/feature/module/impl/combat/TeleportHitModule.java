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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TeleportHitModule extends Module implements PlayerUpdateListener {

    public TeleportHitModule() {
        super("Teleport Hit", "Allows you to attack enemies from further distances.", Category.COMBAT);
    }

    private final FloatValue maxDistance = new FloatValue(
            this,
            "Max Distance",
            "Max teleport distance.",
            100.0f,
            0.0f,
            300.0f
    );

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!mc.options.attackKey.isPressed()) return;
        final PrioritizedRotation playerRotation = new PrioritizedRotation(this.mc.player.getYaw(), this.mc.player.getPitch(), RotationPriority.NORMAL);
        final HitResult result = WorldUtil.raytrace(playerRotation, maxDistance.getValue());

        if (result.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) result).getEntity();
            final BlockPos pos = target.getBlockPos();
            final Vec3d startPos = mc.player.getPos();
            final Vec3d finalPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            teleport(finalPos);
            this.mc.getNetworkHandler().getConnection().channel.writeAndFlush(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            teleport(startPos);
        }
    }

    private void teleport(final Vec3d pos) {
        final double dis = mc.player.getPos().distanceTo(pos);
        for (double d = 0.0D; d < dis; d += 2.0D) {
            final double x = this.mc.player.getX() + (pos.x - (double) this.mc.player.getHorizontalFacing().getOffsetX() - this.mc.player.getX()) * d / dis;
            final double y = this.mc.player.getY() + (pos.y - this.mc.player.getY()) * d / dis;
            final double z = this.mc.player.getZ() + (pos.z - (double) this.mc.player.getHorizontalFacing().getOffsetZ() - this.mc.player.getZ()) * d / dis;
            this.mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
        }
        final double x = pos.x;
        final double y = pos.y;
        final double z = pos.z;
        this.mc.getNetworkHandler().getConnection().channel.writeAndFlush(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
    }
}
