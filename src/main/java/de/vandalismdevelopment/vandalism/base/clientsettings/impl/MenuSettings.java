package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.awt.KeyBindValue;
import org.lwjgl.glfw.GLFW;

public class MenuSettings extends ValueGroup {
    
    public MenuSettings(final ClientSettings parent) {
        super(parent, "Menu", "Menu related configs.");
    }

    public final KeyBindValue menuKey = new KeyBindValue(
            this,
            "Menu Key",
            "Change the key to open the Menu.",
            GLFW.GLFW_KEY_RIGHT_SHIFT
    );

    public final Value<Boolean> moduleStateLogging = new BooleanValue(
            this,
            "Module State Logging",
            "Enables/Disables the logging for the module state.",
            true
    );

    public final Value<Boolean> scriptExecutionLogging = new BooleanValue(
            this,
            "Script Execution Logging",
            "Enables/Disables the logging for the script execution.",
            false
    );

    public final Value<Boolean> manageContainerButtons = new BooleanValue(
            this,
            "Manage Container Buttons",
            "Adds the store/steal buttons to containers.",
            true
    );

    public final Value<Boolean> alwaysDisplayCreativeTab = new BooleanValue(
            this,
            "Always Display Creative Tabs",
            "Always displays creative tabs (even if they are empty).",
            true
    );

    public final Value<Boolean> replaceGameMenuScreenButtons = new BooleanValue(
            this,
            "Replaces Game Menu Screen Buttons",
            "Adds a multiplayer and a reconnect button to the game menu screen.",
            true
    );

    public final Value<Boolean> moreResourcePackOptions = new BooleanValue(
            this,
            "More Server Resource Pack Options",
            "Shows more options in the Server Resource Pack Download Screen.",
            true
    );

    public final Value<Boolean> downloadingTerrainScreenEscaping = new BooleanValue(
            this,
            "Downloading Terrain Screen Escaping",
            "Allows you to escape the downloading terrain screen by pressing the escape key.",
            true
    );

    public final Value<Boolean> disconnectedScreenEscaping = new BooleanValue(
            this,
            "Disconnected Screen Escaping",
            "Allows you to escape the disconnected screen by pressing the escape key.",
            true
    );

    public final Value<Boolean> moreDisconnectedScreenButtons = new BooleanValue(
            this,
            "More Disconnected Buttons",
            "Adds more buttons to the disconnected screen.",
            true
    );
    
}
