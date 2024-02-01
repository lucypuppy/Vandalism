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

package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;

public class ChatSettings extends ValueGroup {

    public final StringValue commandPrefix = new StringValue(
            this,
            "Command Prefix",
            "Change the client command prefix to run the commands with.",
            "."
    );

    public final BooleanValue displayTypedChars = new BooleanValue(
            this,
            "Display Typed Chars",
            "Displays the current char count of the chat input field.",
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
            "If enabled, you can increase or decrease the max chat input length.",
            true
    );

    public final IntegerValue moreChatInputMaxLength = new IntegerValue(
            this,
            "More Chat Input Max Length",
            "Allows you to change the max chat input length.",
            1000,
            1,
            10000
    ).visibleCondition(this.moreChatInput::getValue);

    public final BooleanValue moreChatHistory = new BooleanValue(
            this,
            "More Chat History",
            "If enabled, you can increase or decrease the max chat history.",
            true
    );

    public final IntegerValue moreChatHistoryMaxLength = new IntegerValue(
            this,
            "More Chat History Max Length",
            "Allows you to change the max chat history.",
            500,
            100,
            Short.MAX_VALUE / 2
    ).visibleCondition(this.moreChatHistory::getValue);

    public final BooleanValue moreChatInputSuggestions = new BooleanValue(
            this,
            "More Chat Input Suggestions",
            "If enabled, you can increase or decrease the max chat input suggestions.",
            true
    );

    public final IntegerValue moreChatInputSuggestionsMaxLength = new IntegerValue(
            this,
            "More Chat Input Suggestions Max Length",
            "Allows you to change the max chat input suggestions.",
            39,
            10,
            50
    ).visibleCondition(this.moreChatInputSuggestions::getValue);

    public ChatSettings(final ClientSettings parent) {
        super(parent, "Chat", "Chat related settings.");
    }

}
