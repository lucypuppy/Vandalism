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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.elytraflight;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.ElytraFlightModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class CreativeModuleMode extends ModuleMulti<ElytraFlightModule> implements TickGameListener {

    public CreativeModuleMode(final ElytraFlightModule parent) {
        super("Creative", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
        if (this.mc.player == null) return;
        this.mc.player.getAbilities().flying = false;
        this.mc.player.getAbilities().allowFlying = this.mc.player.getAbilities().creativeMode;
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) return;
        if (!this.mc.player.isFallFlying()) {
            this.mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }
        this.mc.player.getAbilities().flying = true;
        this.mc.player.getAbilities().allowFlying = true;
    }

}
