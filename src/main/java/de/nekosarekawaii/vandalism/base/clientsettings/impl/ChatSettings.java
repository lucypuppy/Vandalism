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
            "Change the prefix to run the commands of the Mod.",
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

    public final BooleanValue customChatLength = new BooleanValue(
            this,
            "Custom Chat Length",
            "Allows you to enable or disable a custom chat length.",
            true
    );

    public final IntegerValue maxChatLength = new IntegerValue(
            this,
            "Max Chat Length",
            "Set the Max Chat Length",
            1000,
            1,
            10000
    ).visibleCondition(this.customChatLength::getValue);

    public ChatSettings(final ClientSettings parent) {
        super(parent, "Chat", "Chat related settings.");
    }
}
