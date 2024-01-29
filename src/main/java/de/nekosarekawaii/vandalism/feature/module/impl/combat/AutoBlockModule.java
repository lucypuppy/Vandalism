/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.base.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.AttackListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Hand;

public class AutoBlockModule extends AbstractModule implements AttackListener, PlayerUpdateListener, OutgoingPacketListener {

    private long lastAttack;

    public AutoBlockModule() {
        super(
                "Auto Block",
                "Automatically blocks attacks.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        //Vandalism.getInstance().getEventSystem().subscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID);
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
       // Vandalism.getInstance().getEventSystem().unsubscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID);
        Vandalism.getInstance().getEventSystem().unsubscribe(this, OutgoingPacketEvent.ID);
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        if (event.target instanceof LivingEntity) {
            stopBlock();
            this.lastAttack = System.currentTimeMillis();
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (System.currentTimeMillis() - this.lastAttack > 500L) {
            stopBlock();
        }
    }

    @Override
    public void onPostPlayerUpdate(PlayerUpdateEvent event) {
        if (System.currentTimeMillis() - this.lastAttack > 500L) {
            return;
        }

        startBlock();
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        final Packet<?> packet = event.packet;

    }

    public void startBlock() {
        if (!this.mc.player.isBlocking())
            this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
    }

    public void stopBlock() {
        if (this.mc.player.isBlocking())
            this.mc.interactionManager.stopUsingItem(this.mc.player);
    }

}
