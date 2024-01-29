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
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.awt.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

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

    public final BooleanValue customBackground = new BooleanValue(
            this,
            "Custom Background",
            "Activates/Deactivates the use of a custom background.",
            true
    );

    public final ColorValue customBackgroundColor = new ColorValue(
            this,
            "Custom Background Color",
            "The color of the custom background.",
            new Color(54, 54, 54, 255)
    ).visibleCondition(this.customBackground::getValue);

    public final BooleanValue customWidgets = new BooleanValue(
            this,
            "Custom Widgets",
            "Activates/Deactivates the use of our widgets.",
            true
    );

    private final ValueGroup customWidgetsSettings = new ValueGroup(
            this,
            "Custom Widgets Settings",
            "Settings for the custom widgets."
    ).visibleCondition(this.customWidgets::getValue);

    public final ColorValue customWidgetsMainColor = new ColorValue(
            this.customWidgetsSettings,
            "Custom Widgets Main Color",
            "The main color of the custom widgets.",
            new Color(217, 215, 215, 100)
    ).visibleCondition(this.customWidgets::getValue);

    public final ColorValue customWidgetsSecondaryColor = new ColorValue(
            this.customWidgetsSettings,
            "Custom Widgets Secondary Color",
            "The secondary color of the custom widgets.",
            new Color(25, 25, 25, 232)
    ).visibleCondition(this.customWidgets::getValue);

    public final IntegerValue customWidgetsOutlineWidth = new IntegerValue(
            this.customWidgetsSettings,
            "Custom Widgets Outline Width",
            "The outline width of the custom widgets.",
            1,
            1,
            3
    ).visibleCondition(this.customWidgets::getValue);

    public final IntegerValue customWidgetsSelectionOffset = new IntegerValue(
            this.customWidgetsSettings,
            "Custom Widgets Selection Offset",
            "The selection offset of the custom widgets.",
            1,
            0,
            4
    ).visibleCondition(this.customWidgets::getValue);

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
