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

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.behavior.IPathingBehavior;
import baritone.api.pathing.calc.IPathingControlManager;
import baritone.api.process.IBaritoneProcess;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.event.player.ChatSendListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.injection.access.IRenderTickCounter;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.MSTimer;

public class BaritoneAddonModule extends Module implements PlayerUpdateListener, ChatSendListener {

    private final BooleanValue executeCommand = new BooleanValue(
            this,
            "Execute command",
            "Whether the defined command should be executed.",
            true
    );

    private final BooleanValue rememberCommand = new BooleanValue(
            this,
            "Update command",
            "Remembers the last command.",
            true
    ).visibleCondition(this.executeCommand::getValue);

    private final StringValue command = new StringValue(
            this,
            "Command",
            "The command to be executed.",
            ""
    ).visibleCondition(this.executeCommand::getValue);

    private final BooleanValue stopOnDeactivate = new BooleanValue(
            this,
            "Stop on deactivate",
            "Runs the baritone stop command on module dactivation.",
            true
    );

    private final BooleanValue showExecutionTime = new BooleanValue(
            this,
            "Show execution time",
            "Shows you the execution time in chat.",
            true
    );

    private final IntegerValue showExecutionTimeInterval = new IntegerValue(
            this,
            "Show execution time interval",
            "The interval for the execution time message.",
            1000,
            0,
            5000
    ).visibleCondition(this.showExecutionTime::getValue);

    private final IBaritone baritone;
    private final MSTimer timer = new MSTimer();

    public BaritoneAddonModule() {
        super("Baritone", "This module is to access Baritone features.", Category.MISC);
        this.baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        this.deactivateOnQuitDefault();
        this.deactivateOnShutdownDefault();
    }

    @Override
    public void onActivate() {
        if (this.executeCommand.getValue()) {
            if (!this.command.getValue().trim().isEmpty()) {
                this.baritone.getCommandManager().execute(this.command.getValue());
            } else {
                ChatUtil.errorChatMessage("No command set");
            }
        }
        Vandalism.getInstance().getEventSystem().subscribe(this, ChatSendEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onDeactivate() {
        if (this.stopOnDeactivate.getValue()) {
            this.baritone.getCommandManager().execute("stop");
        }
        Vandalism.getInstance().getEventSystem().unsubscribe(this, ChatSendEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onChatSend(ChatSendEvent event) {
        if (!this.rememberCommand.getValue()) return;
        if (event.message.startsWith(Baritone.settings().prefix.value) && !event.message.equals(Baritone.settings().prefix.value + "stop")) {
            this.command.setValue(event.message.substring(1));
            ChatUtil.chatMessage("Baritone command set to " + event.message);
        }
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (this.showExecutionTime.getValue() && this.timer.hasReached(this.showExecutionTimeInterval.getValue())) {
            final IPathingControlManager pathingControlManager = this.baritone.getPathingControlManager();
            final IBaritoneProcess process = pathingControlManager.mostRecentInControl().orElse(null);
            if (process == null) return;
            final IPathingBehavior pathingBehavior = this.baritone.getPathingBehavior();
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
            this.timer.reset();
        }
    }

}
