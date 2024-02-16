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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerList;
import org.lwjgl.glfw.GLFW;

public class EnhancedServerListSettings extends ValueGroup {

    public final BooleanValue enhancedServerList = new BooleanValue(
            this,
            "Enhanced Server List",
            "Enables/Disables the enhanced server list mode.",
            true).onValueChange((oldValue, newValue) -> {
        if (!newValue) {
            Vandalism.getInstance().getServerListManager().setSelectedServerList(ServerList.DEFAULT_SERVER_LIST_NAME);
        }
    });

    public final BooleanValue multiplayerScreenServerInformation = new BooleanValue(
            this,
            "Multiplayer Screen Server Information",
            "If activated the Game shows all necessary server information behind a server list entry.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final KeyBindValue pasteServerKey = new KeyBindValue(
            this,
            "Paste Server Key",
            "Change the key to paste a server from your clipboard.",
            GLFW.GLFW_KEY_INSERT,
            false
    ).visibleCondition(this.enhancedServerList::getValue);

    public final KeyBindValue copyServerKey = new KeyBindValue(
            this,
            "Copy Server Key",
            "Change the key to copy a server to your clipboard.",
            GLFW.GLFW_KEY_PAGE_DOWN,
            false
    ).visibleCondition(this.enhancedServerList::getValue);

    public final KeyBindValue deleteServerKey = new KeyBindValue(
            this,
            "Delete Server Key",
            "Change the key to delete a server from the server list.",
            GLFW.GLFW_KEY_DELETE,
            false
    ).visibleCondition(this.enhancedServerList::getValue);

    public final BooleanValue kickAllPlayers = new BooleanValue(
            this,
            "Kick All Players",
            "Adds a Button to the Multiplayer Screen that allows you to kick all Players from the Server.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    private final ValueGroup kickAllPlayersSettings = new ValueGroup(
            this,
            "Kick All Players Settings",
            "Kick All Players related settings."
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.kickAllPlayers.getValue());

    public final KeyBindValue kickAllPlayersKey = new KeyBindValue(
            this.kickAllPlayersSettings,
            "Kick All Players Key",
            "Change the key to kick all Players from the Server.",
            GLFW.GLFW_KEY_PAGE_UP,
            false
    ).visibleCondition(this.enhancedServerList::getValue);

    public final IntegerValue kickAllPlayersPingConnectionTimeout = new IntegerValue(
            this.kickAllPlayersSettings,
            "Kick All Players Ping Connection Timeout",
            "The Ping Connection Timeout that will be used to kick all Players.",
            5000,
            1000,
            10000
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.kickAllPlayers.getValue());

    public final IntegerValue kickAllPlayersPingReadTimeout = new IntegerValue(
            this.kickAllPlayersSettings,
            "Kick All Players Ping Read Timeout",
            "The Ping Read Timeout that will be used to kick all Players.",
            5000,
            1000,
            10000
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.kickAllPlayers.getValue());

    public final IntegerValue kickAllPlayersKickDelay = new IntegerValue(
            this.kickAllPlayersSettings,
            "Kick All Players Kick Delay",
            "The Kick Delay that will be used to kick a Player.",
            6000,
            100,
            10000
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.kickAllPlayers.getValue());

    public EnhancedServerListSettings(final ClientSettings parent) {
        super(parent, "Enhanced Server List", "Enhanced Server List related settings.");
    }

}
