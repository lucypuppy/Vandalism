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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class GroundSpoofModuleMode extends ModuleMulti<NoFallModule> implements OutgoingPacketListener {

    public GroundSpoofModuleMode() {
        super("Ground Spoof");
    }

    private final EnumModeValue<Mode> mode = new EnumModeValue<>(
            this,
            "Spoof Mode",
            "The current ground spoof mode.",
            Mode.ON_GROUND,
            Mode.values()
    );

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (mode.getValue() == Mode.ON_GROUND) {
            if (event.packet instanceof final PlayerMoveC2SPacket playerPacket && this.mc.player.fallDistance > 3.0f) {
                playerPacket.onGround = true;
            }
        } else {
            if (event.packet instanceof final PlayerMoveC2SPacket playerPacket) {
                playerPacket.onGround = false;
            }
        }
    }

    private enum Mode implements IName {
        ON_GROUND, NO_GROUND;

        private final String name;

        Mode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }
}
