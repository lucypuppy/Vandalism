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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.step.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.TickTimeListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.player.StepListener;
import de.nekosarekawaii.vandalism.event.player.StepSuccessListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.step.StepModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NCPModuleMode extends ModuleMulti<StepModule> implements
        StepListener, StepSuccessListener,
        TickTimeListener, PlayerUpdateListener {

    private boolean applyTimer = false;
    private boolean shouldResetTimer = false;

    private final MSTimer resetTimer = new MSTimer();

    public NCPModuleMode() {
        super("NCP");
    }

    @Override
    public void onActivate() {
        this.applyTimer = false;
        this.shouldResetTimer = false;
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                StepEvent.ID, StepSuccessEvent.ID,
                TickTimeEvent.ID, PlayerUpdateEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                StepEvent.ID, StepSuccessEvent.ID,
                TickTimeEvent.ID, PlayerUpdateEvent.ID
        );
        this.applyTimer = false;
        this.shouldResetTimer = false;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.shouldResetTimer) {
            if (this.resetTimer.hasReached(100, true)) {
                this.shouldResetTimer = false;
                this.applyTimer = false;
            }
        }
    }

    @Override
    public void onStep(final StepEvent event) {
        if (mc.player.age % 2 == 0) {
            event.stepHeight = 1f;
        }
    }

    @Override
    public void onStepSuccess(final StepSuccessEvent event) {
        if (event.adjustMovementForCollisions.y > 0.0625) {
            this.applyTimer = true;
            MovementUtil.setSpeed(-0.2);
            final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
            networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX(),
                    mc.player.getY() + 0.41999998688698,
                    mc.player.getZ(),
                    mc.player.isOnGround()
            ));
            networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX(),
                    mc.player.getY() + 0.7531999805212,
                    mc.player.getZ(),
                    mc.player.isOnGround()
            ));
        }
    }

    @Override
    public void onTickTimings(final TickTimeEvent event) {
        if (!this.applyTimer) return;
        this.shouldResetTimer = true;
        event.tickTime = 1000f / 16f;
    }

}
