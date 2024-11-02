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

package de.nekosarekawaii.vandalism.event.player;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

public interface ChatReceiveListener {

    void onChatReceive(final ChatReceiveEvent event);

    class ChatReceiveEvent extends CancellableEvent<ChatReceiveListener> {

        public static final int ID = 18;

        public final Text text;
        public final MessageSignatureData signature;
        public final MessageIndicator indicator;

        public ChatReceiveEvent(final Text text, final MessageSignatureData signature, final MessageIndicator indicator) {
            this.text = text;
            this.signature = signature;
            this.indicator = indicator;
        }

        @Override
        public void call(final ChatReceiveListener listener) {
            listener.onChatReceive(this);
        }

    }

}
