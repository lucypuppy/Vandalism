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

    public final BooleanValue directConnectInstantCrashButton = new BooleanValue(
            this,
            "Direct Connect Instant Crash Button",
            "Adds a button to the direct connect screen which allows you to instant crasher a server from 1.8.0 to 1.9.4",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final BooleanValue directConnectAddressFix = new BooleanValue(
            this,
            "Direct Connect Address Fix",
            "Fixes invalid addresses in the direct connect screen.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final BooleanValue saveScrollingAmount = new BooleanValue(
            this,
            "Save Scrolling Amount",
            "Saves the scrolling amount of the server list.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final BooleanValue saveSelectedEntry = new BooleanValue(
            this,
            "Save Selected Entry",
            "Saves the selected entry of the server list.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final BooleanValue cacheServerList = new BooleanValue(
            this,
            "Cache Server List",
            "If activated the Game caches the server list.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final BooleanValue serverPingerWidget = new BooleanValue(
            this,
            "Server Pinger Widget",
            "Activates/Deactivates the server pinger widget.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final IntegerValue serverPingerWidgetDelay = new IntegerValue(
            this,
            "Server Pinger Widget Delay",
            "The delay in milliseconds before the server pinger widget pings the server again.",
            5000,
            1000,
            30000
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.serverPingerWidget.getValue());

    public final BooleanValue morePingTooltipServerInformation = new BooleanValue(
            this,
            "More Ping Tooltip Server Information",
            "If activated the Game shows more server information in the ping tooltip.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

    public final BooleanValue forgePing = new BooleanValue(
            this,
            "Forge Ping",
            "If activated the client pings the server with a forge ping to get additional infos like the mods.",
            true
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.morePingTooltipServerInformation.getValue());

    public final BooleanValue queryPing = new BooleanValue(
            this,
            "Query Ping",
            "If activated the client pings the server with a query ping to get additional infos like the plugins.",
            true
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.morePingTooltipServerInformation.getValue());

    public final IntegerValue queryPingPort = new IntegerValue(
            this,
            "Query Ping Port",
            "The port used to query ping the server to get additional infos like the plugins.",
            25565,
            1,
            65535
    ).visibleCondition(() -> this.enhancedServerList.getValue() && this.morePingTooltipServerInformation.getValue() && this.queryPing.getValue());

    public final BooleanValue renderAddressAsDefaultServerName = new BooleanValue(
            this,
            "Render Address as Default Server Name",
            "If activated the Game renders the Server Address as the Default Server Name.",
            true
    ).visibleCondition(this.enhancedServerList::getValue);

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

    public final BooleanValue disableReversedDNSLookupForPureIPs = new BooleanValue(
            this,
            "Disable Reversed DNS Lookup for Pure IPs",
            "Disables the Reversed DNS Lookup for Pure IPs.",
            true
    );

    public final BooleanValue handleThreadPoolOverloads = new BooleanValue(
            this,
            "Handle Thread Pool Overloads",
            "Rescales the pinging thread pool if overhead gets to high (makes pinging faster)",
            true
    );

    public EnhancedServerListSettings(final ClientSettings parent) {
        super(parent, "Enhanced Server List", "Enhanced Server List related settings.");
    }

}
