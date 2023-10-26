package de.vandalismdevelopment.vandalism.config.impl.main;

import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.KeyInputValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderIntegerValue;
import it.unimi.dsi.fastutil.Pair;
import org.lwjgl.glfw.GLFW;

public class MenuCategory extends ValueCategory {
    
    public MenuCategory(final MainConfig parent) {
        super("Menu", "Menu related settings.", parent);
    }

    public final Value<Pair<Integer, String>> menuKey = new KeyInputValue(
            "Menu Key",
            "Change the key to open the Menu.",
            this,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "rshift"
    );

    public final Value<Boolean> scriptExecutionLogging = new BooleanValue(
            "Script Execution Logging",
            "Enables/Disables the logging for the script execution.",
            this,
            false
    );

    public final Value<Boolean> manageContainerButtons = new BooleanValue(
            "Manage Container Buttons",
            "Adds the store/steal buttons to containers.",
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
