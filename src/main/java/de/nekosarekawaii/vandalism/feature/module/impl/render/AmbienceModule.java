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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class AmbienceModule extends AbstractModule implements IncomingPacketListener, PlayerUpdateListener {

    private final BooleanValue overwriteWorldTime = new BooleanValue(
            this,
            "Overwrite World Time",
            "Whether to overwrite the world time.",
            true
    );

    private final ModeValue worldTime = new ModeValue(
            this,
            "World Time",
            "Time of the world.",
            "Day",
            "Noon",
            "Night",
            "Midnight"
    ).visibleCondition(this.overwriteWorldTime::getValue);

    private final BooleanValue overwriteWeather = new BooleanValue(
            this,
            "Overwrite Weather",
            "Whether to overwrite the weather.",
            true
    );

    private final ModeValue weather = new ModeValue(
            this,
            "Mode",
            "Mode of the ambience.",
            "Clear",
            "Rain",
            "Thunder"
    ).visibleCondition(this.overwriteWeather::getValue);

    public AmbienceModule() {
        super("Ambience", "Allows you to customize your ambience.", Category.RENDER);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, IncomingPacketEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.overwriteWorldTime.getValue()) {
            switch (this.worldTime.getValue()) {
                case "Day" -> {
                    this.mc.world.setTimeOfDay(-1000);
                }
                case "Noon" -> {
                    this.mc.world.setTimeOfDay(-6000);
                }
                case "Night" -> {
                    this.mc.world.setTimeOfDay(-13000);
                }
                case "Midnight" -> {
                    this.mc.world.setTimeOfDay(-18000);
                }
                default -> {
                }
            }
        }
        if (this.overwriteWeather.getValue()) {
            switch (this.weather.getValue()) {
                case "Clear" -> {
                    this.mc.world.setRainGradient(0.0F);
                    this.mc.world.setThunderGradient(0.0F);
                }
                case "Rain" -> {
                    this.mc.world.setRainGradient(1.0F);
                    this.mc.world.setThunderGradient(0.0F);
                }
                case "Thunder" -> {
                    this.mc.world.setRainGradient(1.0F);
                    this.mc.world.setThunderGradient(1.0F);
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;
        if (packet instanceof final WorldTimeUpdateS2CPacket timePacket) {
            if (this.overwriteWorldTime.getValue()) {
                timePacket.timeOfDay = switch (this.worldTime.getValue()) {
                    case "Day" -> -1000;
                    case "Noon" -> -6000;
                    case "Night" -> -13000;
                    case "Midnight" -> -18000;
                    default -> timePacket.timeOfDay;
                };
            }
        } else if (packet instanceof final GameStateChangeS2CPacket changePacket) {
            if (this.overwriteWeather.getValue()) {
                final GameStateChangeS2CPacket.Reason reason = changePacket.getReason();
                if (
                        reason.equals(GameStateChangeS2CPacket.RAIN_STARTED) ||
                                reason.equals(GameStateChangeS2CPacket.RAIN_STOPPED) ||
                                reason.equals(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) ||
                                reason.equals(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED)
                ) {
                    event.cancel();
                }
            }
        }
    }

}
