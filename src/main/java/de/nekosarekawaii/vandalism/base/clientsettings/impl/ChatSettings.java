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

package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;

import java.awt.*;

public class ChatSettings extends ValueGroup {

    private final ValueGroup chatPrefix = new ValueGroup(this, "Chat Prefix", "Chat prefix related settings.");

    public final ColorValue chatPrefixColor = new ColorValue(
            this.chatPrefix,
            "Chat Prefix Color",
            "Change the color of the chat prefix.",
            Color.WHITE
    );

    public final StringValue startBracket = new StringValue(
            this.chatPrefix,
            "Start Bracket",
            "Change the start bracket of the chat prefix.",
            "("
    );

    public final StringValue endBracket = new StringValue(
            this.chatPrefix,
            "End Bracket",
            "Change the end bracket of the chat prefix.",
            ")"
    );

    public final StringValue commandPrefix = new StringValue(
            this,
            "Command Prefix",
            "Change the client command prefix to run the commands with.",
            "."
    );

    public final BooleanValue moduleStateLogging = new BooleanValue(
            this,
            "Module State Logging",
            "Activates/Deactivates the logging for the module state.",
            true
    );

    public final BooleanValue sameLineMessages = new BooleanValue(
            this,
            "Same Line Messages",
            "If enabled, some chat messages from the client will be displayed in the same line.",
            true
    );

    public final IntegerValue maxSameLineMessages = new IntegerValue(
            this,
            "Max Same Line Messages",
            "The max amount of chat messages that will be displayed in the same line.",
            2,
            1,
            20
    ).visibleCondition(this.sameLineMessages::getValue).onValueChange((oldValue, newValue) -> {
        if (newValue < 1) {
            this.maxSameLineMessages.setValue(1);
        }
    });

    public final BooleanValue fixChatFieldWidth = new BooleanValue(
            this,
            "Fix Chat Field Width",
            "Fixes the chat field width to the screen width.",
            true
    );

    public final BooleanValue displayTypedChars = new BooleanValue(
            this,
            "Display Typed Chars",
            "Displays the current char count of the chat input field.",
            true
    );

    public final BooleanValue displayAccountHead = new BooleanValue(
            this,
            "Display Account Head",
            "Displays the head of your current account at the bottom left.",
            true
    );

    public final BooleanValue modulateHead = new BooleanValue(
            this,
            "Modulate Head",
            "Modulates the head of your current account.",
            true
    );

    public final BooleanValue allowColorChar = new BooleanValue(
            this,
            "Allow Color Char",
            "Disables the color char restrictions of the Game.",
            true
    );

    public final BooleanValue dontClearChatHistory = new BooleanValue(
            this,
            "Dont Clear Chat History",
            "Prevents the Game from clearing your chat history.",
            true
    );

    public final BooleanValue moreChatInput = new BooleanValue(
            this,
            "More Chat Input",
            "Allows you to type as much as you want in the chat input field.",
            true
    );

    public final BooleanValue moreChatInputSuggestions = new BooleanValue(
            this,
            "More Chat Input Suggestions",
            "Shows so much chat input suggestions as possible.",
            true
    );

    public final BooleanValue moreChatHistory = new BooleanValue(
            this,
            "More Chat History",
            "If enabled, you can increase or decrease the max chat history.",
            true
    );

    public final IntegerValue moreChatHistoryMaxLength = new IntegerValue(
            this,
            "More Chat History Max Length",
            "Allows you to change the max chat history length.",
            500,
            100,
            Short.MAX_VALUE / 2
    ).visibleCondition(this.moreChatHistory::getValue);

    public final BooleanValue addTPQuickActionToShowEntityComponent = new BooleanValue(
            this,
            "Add TP Quick Action to Show Entity Component",
            "Makes messages in the chat which contains a show entity component clickable to teleport to the entity.",
            true
    );

    public final BooleanValue quickAccessOnRightClick = new BooleanValue(
            this,
            "Quick Access on Right Click",
            "Enables a quick access to the chat on right click.",
            true
    );

    public ChatSettings(final ClientSettings parent) {
        super(parent, "Chat", "Chat related settings.");
    }

}
