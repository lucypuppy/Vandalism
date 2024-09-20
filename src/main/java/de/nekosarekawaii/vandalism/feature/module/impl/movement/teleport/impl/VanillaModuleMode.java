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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.teleport.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.teleport.TeleportModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class VanillaModuleMode extends ModuleMulti<TeleportModule> implements PlayerUpdateListener {

    public VanillaModuleMode(final TeleportModule parent) {
        super("Vanilla", parent);
    }

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
        if (!this.parent.canTeleport()) return;
        final Vec3d target = this.parent.getBlockHitResult();
        if (target == null) return;
        final ClientPlayNetworkHandler networkHandler = this.mc.getNetworkHandler();
        if (networkHandler == null) return;
        final Vec3d finalPos = new Vec3d(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5);
        final double dis = this.mc.player.getPos().distanceTo(target);
        for (double d = 0.0D; d < dis; d += 2.0D) {
            final double x = this.mc.player.getX() + (finalPos.x - (double) this.mc.player.getHorizontalFacing().getOffsetX() - this.mc.player.getX()) * d / dis;
            final double y = this.mc.player.getY() + (finalPos.y - this.mc.player.getY()) * d / dis;
            final double z = this.mc.player.getZ() + (finalPos.z - (double) this.mc.player.getHorizontalFacing().getOffsetZ() - this.mc.player.getZ()) * d / dis;
            networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
            this.mc.player.setPosition(x, y, z);
        }
        final double x = finalPos.x;
        final double y = finalPos.y;
        final double z = finalPos.z;
        networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
        this.mc.player.setPosition(x, y, z);
    }

}
