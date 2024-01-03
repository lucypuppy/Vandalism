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
import de.nekosarekawaii.vandalism.base.value.impl.awt.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import org.lwjgl.glfw.GLFW;

public class MenuSettings extends ValueGroup {

    public final KeyBindValue menuKey = new KeyBindValue(
            this,
            "Menu Key",
            "Change the key to open the Menu.",
            GLFW.GLFW_KEY_RIGHT_SHIFT
    );

    public final BooleanValue moduleStateLogging = new BooleanValue(
            this,
            "Module State Logging",
            "Activates/Deactivates the logging for the module state.",
            true
    );

    public final BooleanValue scriptExecutionLogging = new BooleanValue(
            this,
            "Script Execution Logging",
            "Activates/Deactivates the logging for the script execution.",
            false
    );

    public final BooleanValue manageContainerButtons = new BooleanValue(
            this,
            "Manage Container Buttons",
            "Adds the store/steal buttons to containers.",
            true
    );

    public final BooleanValue alwaysDisplayCreativeTab = new BooleanValue(
            this,
            "Always Display Creative Tabs",
            "Always displays creative tabs (even if they are empty).",
            true
    );

    public final BooleanValue replaceGameMenuScreenButtons = new BooleanValue(
            this,
            "Replace Game Menu Screen Buttons",
            "Adds a multiplayer and a reconnect button to the game menu screen.",
            true
    );

    public final BooleanValue downloadingTerrainScreenEscaping = new BooleanValue(
            this,
            "Downloading Terrain Screen Escaping",
            "Allows you to escape the downloading terrain screen by pressing the escape key.",
            true
    );

    public final BooleanValue progressScreenEscaping = new BooleanValue(
            this,
            "Progress Screen Escaping",
            "Allows you to escape the progress screen by pressing the escape key.",
            true
    );

    public final BooleanValue disconnectedScreenEscaping = new BooleanValue(
            this,
            "Disconnected Screen Escaping",
            "Allows you to escape the disconnected screen by pressing the escape key.",
            true
    );

    public final BooleanValue moreDisconnectedScreenButtons = new BooleanValue(
            this,
            "More Disconnected Buttons",
            "Adds more buttons to the disconnected screen.",
            true
    );

    public final BooleanValue forceEnableReconfiguartionDisconnectButton = new BooleanValue(
            this,
            "Force Enable Reconfiguration Disconnect Button",
            "Forces the disconnect button of the reconfiguration screen to be enabled.",
            true
    );

    public final BooleanValue forceEnableRespawnButton = new BooleanValue(
            this,
            "Force Enable Respawn Button",
            "Forces the respawn button to be enabled.",
            false
    );


    public MenuSettings(final ClientSettings parent) {
        super(parent, "Menu", "Menu related settings.");
    }

}
