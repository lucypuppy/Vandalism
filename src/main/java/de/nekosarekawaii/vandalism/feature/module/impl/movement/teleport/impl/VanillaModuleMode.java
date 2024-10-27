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
        if (!this.parent.teleport) return;
        final Vec3d target = this.parent.getSelectedPos();
        if (target == null) return;
        if (this.parent.isTeleportPositionValid()) {
            final Vec3d pos = mc.player.getPos();
            final Vec3d start = new Vec3d(pos.x, pos.y, pos.z);
            final double distance = start.distanceTo(target);
            final int packetsRequired = (int) Math.ceil(Math.abs(distance) / 10);
            if (packetsRequired > 1) {
                for (int i = 1; i <= packetsRequired; i++) {
                    double x = start.x + (target.x - start.x) / packetsRequired * i;
                    double y = start.y + (target.y - start.y) / packetsRequired * i;
                    double z = start.z + (target.z - start.z) / packetsRequired * i;
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
                }
            }
            mc.player.setPos(target.x, target.y, target.z);
            this.parent.reset();
        }
    }

}
