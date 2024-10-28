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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.event.player.ChatSendListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import lombok.Getter;

public class AnticheatChecker extends Module implements PlayerUpdateListener, ChatSendListener {

    private final LongValue minDelay = new LongValue(this, "Min Delay", "Min delay between taking items.", 1000L, 0L, 10000L);
    private final LongValue maxDelay = new LongValue(this, "Max Delay", "Max delay between taking items.", 2000L, 0L, 10000L);

    private final MSTimer timer = new MSTimer();
    private long delay;
    private int currentAnticheat;
    private int currentMessage;

    public AnticheatChecker() {
        super("Anticheat Checker", "Tries most anticheat commands to check which anticheat is on the server.", Category.MISC);
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, ChatSendEvent.ID);
        generateRandomDelay();
        this.currentAnticheat = 0;
        this.currentMessage = 0;
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, ChatSendEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (this.timer.hasReached(this.delay, true)) {
            generateRandomDelay();

            final Anticheats anticheat = Anticheats.values()[this.currentAnticheat];
            ChatUtil.chatMessage("Testing for anticheat: " + anticheat.getName());
            ChatUtil.chatMessageToServer(anticheat.getMessages()[this.currentMessage]);

            if (this.currentMessage >= anticheat.getMessages().length - 1) {
                this.currentMessage = 0;

                if (this.currentAnticheat >= Anticheats.values().length - 1) {
                    deactivate();
                } else {
                    this.currentAnticheat++;
                }
            } else {
                this.currentMessage++;
            }
        }
    }

    @Override
    public void onChatSend(ChatSendEvent event) {
        this.timer.reset();
    }

    private void generateRandomDelay() {
        this.delay = RandomUtils.randomLong(this.minDelay.getValue(), this.maxDelay.getValue());
    }

    @Getter // Todo add more
    private enum Anticheats {
        AAC("/aac"),
        NCP("/ncp");

        private final String name;
        private final String[] messages;

        Anticheats(final String... messages) {
            this.name = StringUtils.normalizeEnumName(this.name());
            this.messages = messages;
        }
    }

}
