package de.vandalismdevelopment.vandalism.config.impl.main;

import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.KeyInputValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;

public class MenuCategory extends ValueCategory {
    
    public MenuCategory(final MainConfig parent) {
        super("Menu", "Menu related configs.", parent);
    }

    public final Value<GlfwKeyName> menuKey = new KeyInputValue(
            "Menu Key",
            "Change the key to open the Menu.",
            this,
            GlfwKeyName.RIGHT_SHIFT
    );

    public final Value<Boolean> moduleStateLogging = new BooleanValue(
            "Module State Logging",
            "Enables/Disables the logging for the module state.",
            this,
            true
    );

    public final Value<Boolean> scriptExecutionLogging = new BooleanValue(
            "Script Execution Logging",
            "Enables/Disables the logging for the script execution.",
            this,
            false
    );

    public final Value<Boolean> alwaysDisplayCreativeTab = new BooleanValue(
            "Always Display Creative Tab",
            "Always displays the creative tab.",
            this,
            true
    );

    public final Value<Boolean> manageContainerButtons = new BooleanValue(
            "Manage Container Buttons",
            "Adds the store/steal buttons to containers.",
            this,
            true
    );

    public final Value<Boolean> replaceGameMenuScreenButtons = new BooleanValue(
            "Replaces Game Menu Screen Buttons",
            "Adds a multiplayer and a reconnect button to the game menu screen.",
            this,
            true
    );

    public final Value<Boolean> downloadingTerrainScreenEscaping = new BooleanValue(
            "Downloading Terrain Screen Escaping",
            "Allows you to escape the downloading terrain screen by pressing the escape key.",
            this,
            true
    );

    public final Value<Boolean> disconnectedScreenEscaping = new BooleanValue(
            "Disconnected Screen Escaping",
            "Allows you to escape the disconnected screen by pressing the escape key.",
            this,
            true
    );

    public final Value<Boolean> moreDisconnectedScreenButtons = new BooleanValue(
            "More Disconnected Buttons",
            "Adds more buttons to the disconnected screen.",
            this,
            true
    );

    public final Value<Boolean> multiplayerScreenServerInformation = new BooleanValue(
            "Multiplayer Screen Server Information",
            "If enabled the Game shows all necessary server information behind a server list entry.",
            this,
            true
    );

    public final Value<Integer> maxServerVersionLength = new SliderIntegerValue(
            "Max Server Version Length",
            "Sets the max display length of a server version that is being displayed in the multiplayer screen.",
            this,
            60,
            6,
            250
    ).visibleConsumer(this.multiplayerScreenServerInformation::getValue);

    public final Value<Boolean> moreResourcePackOptions = new BooleanValue(
            "More Server Resource Pack Options",
            "Shows more options in the Server Resource Pack Download Screen.",
            this,
            true
    );
    
}
