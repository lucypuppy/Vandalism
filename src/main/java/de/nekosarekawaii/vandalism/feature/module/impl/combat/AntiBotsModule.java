/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.internal.TargetListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;

import java.util.ArrayList;
import java.util.List;

public class AntiBotsModule extends AbstractModule implements TargetListener, IncomingPacketListener {

    public AntiBotsModule() {
        super("Anti Bots", "Prevents bots from joining your server.", Category.COMBAT);
    }

    private final List<Entity> movedEntities = new ArrayList<>();

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TargetEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TargetEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onTarget(TargetEvent event) {
        if (!event.isTarget) return;

        if (!movedEntities.contains(event.entity)) {
            event.isTarget = false;
        }
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if(mc.world != null) return;
        if (event.packet instanceof EntityPositionS2CPacket packet) {
            Entity entity = mc.world.getEntityById(packet.getId());
            if (entity instanceof PlayerEntity && !movedEntities.contains(entity)) {
                if (packet.getX() != entity.getTrackedPosition().pos.x || packet.getY() != entity.getTrackedPosition().pos.y || packet.getZ() != entity.getTrackedPosition().pos.z) {
                    movedEntities.add(entity);
                }
            }
        }

        if (event.packet instanceof EntityS2CPacket packet) {
            Entity entity = packet.getEntity(mc.world);
            if (entity instanceof PlayerEntity && !movedEntities.contains(entity)) {
                if (packet.getDeltaX() != 0 || packet.getDeltaY() != 0 || packet.getDeltaZ() != 0) {
                    movedEntities.add(entity);
                }
            }
        }
    }
}
