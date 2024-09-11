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
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.internal.TargetListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AntiBotsModule extends Module implements TargetListener, IncomingPacketListener, WorldListener {

    private final BooleanValue movement = new BooleanValue(this, "Movement", "Checks if the entities are moving", true);
    private final BooleanValue sound = new BooleanValue(this, "Sound", "Checks if the entities are playing sounds", false);
    private final BooleanValue playerList = new BooleanValue(this, "Player List", "Checks if the entities are in the player list (Only Players)", false);

    private final List<Entity> movedEntities = new ArrayList<>();
    private final List<Entity> soundEntities = new ArrayList<>();

    public AntiBotsModule() {
        super("Anti Bots", "Prevents bots from joining your server.", Category.COMBAT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TargetEvent.ID, IncomingPacketEvent.ID, WorldLoadEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TargetEvent.ID, IncomingPacketEvent.ID, WorldLoadEvent.ID);
        clearLists();
    }

    @Override
    public void onTarget(TargetEvent event) {
        if (!event.isTarget)
            return;

        if (this.movement.getValue() && !this.movedEntities.contains(event.entity)) {
            event.isTarget = false;
            return;
        }

        if (this.sound.getValue() && !this.soundEntities.contains(event.entity)) {
            event.isTarget = false;
            return;
        }

        //Only Player checks.
        if (event.entity instanceof final PlayerEntity player) {
            if (this.playerList.getValue() && mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null) {
                event.isTarget = false;
            }
        }
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (mc.world == null)
            return;

        if (this.movement.getValue() && event.packet instanceof final EntityPositionS2CPacket packet) {
            final Entity entity = mc.world.getEntityById(packet.getEntityId());
            if (entity == mc.player)
                return;

            if (entity != null && !this.movedEntities.contains(entity)) {
                if (packet.getX() != entity.getTrackedPosition().pos.x || packet.getY() != entity.getTrackedPosition().pos.y || packet.getZ() != entity.getTrackedPosition().pos.z) {
                    this.movedEntities.add(entity);
                }
            }
        }

        if (this.sound.getValue() && event.packet instanceof final PlaySoundS2CPacket packet) {
            if (packet.getCategory() == SoundCategory.PLAYERS || packet.getCategory() == SoundCategory.BLOCKS ||
                    packet.getCategory() == SoundCategory.HOSTILE || packet.getCategory() == SoundCategory.NEUTRAL) {
                for (final Entity entity : mc.world.getEntities()) {
                    if (entity == mc.player)
                        continue;

                    final double distance = entity.getPos().distanceTo(new Vec3d(packet.getX(), packet.getY(), packet.getZ()));

                    if (distance < 1.0 && !this.soundEntities.contains(entity)) {
                        this.soundEntities.add(entity);
                        break;
                    }
                }
            }
        }

        /*if (event.packet instanceof EntityS2CPacket packet) {
            Entity entity = packet.getEntity(mc.world);
            if (entity instanceof PlayerEntity && !movedEntities.contains(entity)) {
                if (packet.getDeltaX() != 0 || packet.getDeltaY() != 0 || packet.getDeltaZ() != 0) {
                    movedEntities.add(entity);
                }
            }
        }*/
    }


    @Override
    public void onPreWorldLoad() {
        clearLists();
    }

    private void clearLists() {
        this.movedEntities.clear();
        this.soundEntities.clear();
    }

}
