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

package de.nekosarekawaii.vandalism.addonbaritone.modules;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.behavior.IPathingBehavior;
import baritone.api.pathing.calc.IPathingControlManager;
import baritone.api.process.IBaritoneProcess;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.injection.access.IRenderTickCounter;
import de.nekosarekawaii.vandalism.util.ChatUtil;

public class BaritoneAddonModule extends Module implements PlayerUpdateListener {

    private final IBaritone baritone;

    public BaritoneAddonModule() {
        super("Baritone", "This module is to access Baritone features.", Category.MISC);

        this.baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
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
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        final IPathingControlManager pathingControlManager = baritone.getPathingControlManager();
        final IBaritoneProcess process = pathingControlManager.mostRecentInControl().orElse(null);

        if (process == null) {
            return;
        }

        final IPathingBehavior pathingBehavior = baritone.getPathingBehavior();

        final double tps = ((IRenderTickCounter) mc.getRenderTickCounter()).vandalism$getTPS();
        final double ticksRemainingInSegment = pathingBehavior.ticksRemainingInSegment().orElse(0.0);
        final double ticksRemainingInGoal = pathingBehavior.estimatedTicksToGoal().orElse(0.0);
        final double remainingTimeInSegment = ticksRemainingInSegment / tps;
        final double remainingTimeInGoal = ticksRemainingInGoal / tps;

        ChatUtil.infoChatMessage(String.format(
                "Next segment: %.1fs Goal: %.1fs",
                remainingTimeInSegment,
                remainingTimeInGoal
        ));
    }

}
