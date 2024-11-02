/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.game.TimeTravelListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.lwjgl.glfw.GLFW;

public class TickBaseModule extends Module implements TimeTravelListener, IncomingPacketListener {

    private boolean shouldCharge, unCharge;
    private long prevTime;
    private long shifted;

    private final ValueGroup generalGroup = new ValueGroup(this, "General", "General settings for tick base.");

    private final IntegerValue maxCharge = new IntegerValue(generalGroup, "Max Charge", "The maximum amount of ticks you can charge.", 100, 0, 1000);
    private final KeyBindValue chargeKey = new KeyBindValue(generalGroup, "Charge Key", "The key to charge the tick base.", GLFW.GLFW_KEY_UP);
    private final KeyBindValue unChargeKey = new KeyBindValue(generalGroup, "Un Charge Key", "The key to uncharge the tick base.", GLFW.GLFW_KEY_DOWN);

    public TickBaseModule() {
        super("Tick Base", "Allows you to manipulate how minecraft handles ticks and speedup the game.", Category.MISC);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TimeTravelEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TimeTravelEvent.ID, IncomingPacketEvent.ID);
        this.shouldCharge = false;
        this.unCharge = false;
        this.prevTime = 0;
        this.shifted = 0;
    }

    @Override
    public void onTimeTravel(final TimeTravelEvent event) {
        if (mc.player == null) {
            this.shifted = 0;
            return;
        }

        this.shouldCharge = this.chargeKey.isPressed() && !this.unChargeKey.isPressed();
        this.unCharge = this.unChargeKey.isPressed() && !this.chargeKey.isPressed();

        if (shouldCharge && this.getCharge() < this.maxCharge.getValue()) {
            this.shifted += event.time - this.prevTime;
            for (Entity entity : mc.world.getEntities()) {
                if (entity == null || entity == mc.player) continue;
                mc.world.tickEntity(entity);
            }
        } else if (this.unCharge && this.shifted > 0) {
            this.shifted = 0;
        }
        prevTime = event.time;
        event.time -= this.shifted;
    }

    public int getCharge() {
        return (int) (shifted / ((RenderTickCounter.Dynamic) mc.getRenderTickCounter()).tickTime);
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (event.packet instanceof PlayerRespawnS2CPacket) {
            shifted = 0;
        }
    }
}