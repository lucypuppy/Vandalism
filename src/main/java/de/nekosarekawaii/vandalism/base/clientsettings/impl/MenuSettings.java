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

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class MenuSettings extends ValueGroup {

    public final KeyBindValue menuKey = new KeyBindValue(
            this,
            "Menu Key",
            "Change the key to open the Menu.",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            false
    );

    public final IntegerValue menuScale = new IntegerValue(
            this,
            "Menu Scale",
            "Changes the global scale of ImGui provided fonts. REQUIRES RESTART!",
            16,
            1,
            1_000
    );

    public final BooleanValue moduleStateSound = new BooleanValue(
            this,
            "Module State Sound",
            "Activates/Deactivates the sound for the module state.",
            false
    );

    public final BooleanValue hideExperimentalModules = new BooleanValue(
            this,
            "Hide Experimental Modules",
            "Hides experimental modules in module tabs.",
            true
    );

    public final FloatValue moduleTabMaxHeight = new FloatValue(
            this,
            "Module Tab Max Height",
            "The maximum height of the module tab.",
            415f,
            415f,
            915f
    );

    public final ColorValue activatedModuleColor = new ColorValue(
            this,
            "Activated Module Color",
            "The color of activated modules.",
            new Color(28, 204, 28, 120)
    );

    public final ColorValue multiModeSelectionColor = new ColorValue(
            this,
            "Multi Mode Selection Color",
            "The color of selected multi mode options.",
            new Color(28, 204, 28, 120)
    );

    private final ValueGroup backgroundSettings = new ValueGroup(
            this,
            "Background Settings",
            "Settings for the background."
    );

    public final EnumModeValue<BackgroundMode> backgroundMode = new EnumModeValue<>(
            this.backgroundSettings,
            "Background Mode",
            "The mode of the background.",
            BackgroundMode.DEFAULT,
            BackgroundMode.values()
    );

    public final ColorValue customBackgroundColor = new ColorValue(
            this.backgroundSettings,
            "Custom Background Color",
            "The color of the custom background.",
            new Color(69, 17, 89, 255)
    ).visibleCondition(() -> this.backgroundMode.getValue() == BackgroundMode.COLOR);

    private final ValueGroup inGameBackgroundSettings = new ValueGroup(
            this,
            "In-Game Background Settings",
            "Settings for the in-game background."
    );

    public final EnumModeValue<InGameBackgroundMode> inGameBackgroundMode = new EnumModeValue<>(
            this.inGameBackgroundSettings,
            "In Game Background Mode",
            "The mode of the background.",
            InGameBackgroundMode.DEFAULT,
            InGameBackgroundMode.values()
    );

    public final BooleanValue inGameBackgroundBlur = new BooleanValue(
            this.inGameBackgroundSettings,
            "In-Game Background Blur",
            "Activates/Deactivates the blur for the in-game background.",
            true
    );

    public final ColorValue shaderColorSpark = new ColorValue(
            this.inGameBackgroundSettings,
            "Shader Color Spark",
            "The color of the spark for the shader background.",
            new Color(168, 10, 225, 150)
    ).visibleCondition(() -> this.inGameBackgroundMode.getValue() == InGameBackgroundMode.SHADER);

    public final ColorValue shaderColorBloom = new ColorValue(
            this.inGameBackgroundSettings,
            "Shader Color Bloom",
            "The color of the bloom for the shader background.",
            new Color(168, 10, 225, 150)
    ).visibleCondition(() -> this.inGameBackgroundMode.getValue() == InGameBackgroundMode.SHADER);

    public final ColorValue shaderColorSmoke = new ColorValue(
            this.inGameBackgroundSettings,
            "Shader Color Smoke",
            "The color of the smoke for the shader background.",
            new Color(168, 10, 225, 150)
    ).visibleCondition(() -> this.inGameBackgroundMode.getValue() == InGameBackgroundMode.SHADER);

    public final ColorValue inGameCustomBackgroundColorTop = new ColorValue(
            this.inGameBackgroundSettings,
            "In-Game Custom Background Color Top",
            "The color of the custom background in-game at the top.",
            new Color(0, 0, 0, 94)
    ).visibleCondition(() -> this.inGameBackgroundMode.getValue() == InGameBackgroundMode.COLOR_FADE);

    public final ColorValue inGameCustomBackgroundColorBottom = new ColorValue(
            this.inGameBackgroundSettings,
            "In-Game Custom Background Color Bottom",
            "The color of the custom background in-game at the bottom.",
            new Color(168, 10, 225, 150)
    ).visibleCondition(() -> this.inGameBackgroundMode.getValue() == InGameBackgroundMode.COLOR_FADE);

    private final ValueGroup customWidgetsSettings = new ValueGroup(
            this,
            "Custom Widgets Settings",
            "Settings for the custom widgets."
    );

    public final BooleanValue customWidgets = new BooleanValue(
            this.customWidgetsSettings,
            "Custom Widgets",
            "Activates/Deactivates the use of our widgets.",
            true
    );

    public final ColorValue customWidgetsMainColor = new ColorValue(
            this.customWidgetsSettings,
            "Custom Widgets Main Color",
            "The main color of the custom widgets.",
            new Color(175, 26, 189, 98)
    ).visibleCondition(this.customWidgets::getValue);

    public final ColorValue customWidgetsSecondaryColor = new ColorValue(
            this.customWidgetsSettings,
            "Custom Widgets Secondary Color",
            "The secondary color of the custom widgets.",
            new Color(255, 255, 255, 212)
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

    public final BooleanValue addMoreButtonsToGameMenuScreen = new BooleanValue(
            this,
            "Add more buttons to Game Menu Screen",
            "Adds more buttons to the game menu screen like the multiplayer and reconnect button.",
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

    public final BooleanValue disconnectedScreenCopyData = new BooleanValue(
            this,
            "Disconnected Screen Copy Data",
            "Allows you to copy the disconnect data by pressing the ctrl and c.",
            true
    );

    public final BooleanValue moreDisconnectedScreenButtons = new BooleanValue(
            this,
            "More Disconnected Buttons",
            "Adds more buttons to the disconnected screen.",
            true
    );

    public final BooleanValue autoReconnect = new BooleanValue(
            this,
            "Auto Reconnect",
            "Automatically reconnects to the last server when you get disconnected.",
            true
    ).visibleCondition(this.moreDisconnectedScreenButtons::getValue);

    public final IntegerValue autoReconnectDelay = new IntegerValue(
            this,
            "Auto Reconnect Delay",
            "The delay in seconds before the auto reconnect does its job.",
            5,
            1,
            60
    ).visibleCondition(this.autoReconnect::getValue);

    public final BooleanValue forceEnableReconfigurationDisconnectButton = new BooleanValue(
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

    public final BooleanValue changeScreenCloseButton = new BooleanValue(
            this,
            "Change Screen Close Button",
            "Changes the close button for gui screens.",
            false
    );

    public final KeyBindValue changeScreenCloseButtonKey = new KeyBindValue(
            this,
            "Change Screen Close Button Key",
            "Change the key to close the gui screens.",
            GLFW.GLFW_KEY_ESCAPE,
            false
    ).visibleCondition(this.changeScreenCloseButton::getValue);

    public final BooleanValue imguiGlowOutline = new BooleanValue(
            this,
            "ImGui Glow Outline",
            "Activates/Deactivates the glow outline for ImGui.",
            false
    );

    public final ColorValue imguiGlowOutlineColor = new ColorValue(
            this,
            "ImGui Glow Outline Color",
            "The color of the glow outline for ImGui.",
            Color.lightGray
    ).visibleCondition(this.imguiGlowOutline::getValue);

    public final FloatValue imguiGlowOutlineWidth = new FloatValue(
            this,
            "ImGui Glow Outline Width",
            "The width of the glow outline for ImGui.",
            6.0f,
            1.0f,
            20.0f
    ).visibleCondition(this.imguiGlowOutline::getValue);

    public final FloatValue imguiGlowOutlineAccuracy = new FloatValue(
            this,
            "ImGui Glow Outline Accuracy",
            "The accuracy of the glow outline for ImGui.",
            1.0f,
            1.0f,
            8.0f
    ).visibleCondition(this.imguiGlowOutline::getValue);

    public final FloatValue imguiGlowOutlineExponent = new FloatValue(
            this,
            "ImGui Glow Outline Exponent",
            "The exponent of the glow outline for ImGui.",
            0.22f,
            0.01f,
            4.0f
    ).visibleCondition(this.imguiGlowOutline::getValue);

    public final BooleanValue runDirectoryButton = new BooleanValue(
            this,
            "Run Directory Button",
            "Adds a button to open the run directory inside the options screen.",
            true
    );

    public final BooleanValue skinsDirectoryButton = new BooleanValue(
            this,
            "Skins Directory Button",
            "Adds a button to open the skins directory inside the skin options screen.",
            true
    );

    public MenuSettings(final ClientSettings parent) {
        super(parent, "Menu", "Menu related settings.");
    }

    public enum BackgroundMode implements IName {
        DEFAULT,
        SHADER,
        COLOR;

        private final String name;

        BackgroundMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public enum InGameBackgroundMode implements IName {
        DEFAULT,
        SHADER,
        COLOR_FADE;

        private final String name;

        InGameBackgroundMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
