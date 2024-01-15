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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.velocity;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

public class BlocksMCModuleMode extends ModuleMulti<VelocityModule> implements PlayerUpdateListener, IncomingPacketListener {

    private boolean velocity = false;

    public BlocksMCModuleMode(final VelocityModule parent) {
        super("BlocksMC", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket && this.mc.player != null && velocityPacket.getId() == this.mc.player.getId()) {
            event.cancel();
            if (this.mc.player.isOnGround()) {
                this.mc.player.setVelocity(this.mc.player.getVelocity().add(0, velocityPacket.getVelocityY() / 8000.0, 0));
            }
            this.velocity = true;
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.velocity) {
            if (this.mc.player.hurtTime > 6) {
                final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
                final float yaw = (rotation != null ? rotation.getYaw() : this.mc.player.getYaw()) * 0.017453292F;
                this.mc.player.setVelocity(this.mc.player.getVelocity().add(-MathHelper.sin(yaw) * 0.04F, 0.0F, MathHelper.cos(yaw) * 0.04F));
            }
            else {
                this.velocity = false;
            }
        }
    }

}
