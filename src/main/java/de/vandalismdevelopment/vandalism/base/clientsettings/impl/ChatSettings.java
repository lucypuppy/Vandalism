package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueCategory;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.StringValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.slider.SliderIntegerValue;

public class ChatSettings extends ValueCategory {
    
    public ChatSettings(final ClientSettings parent) {
        super("Chat", "Chat related configs.", parent);
    }

    public final Value<String> commandPrefix = new StringValue(
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            this,
            "."
    );

    public final Value<Boolean> displayTypedChars = new BooleanValue(
            "Display Typed Chars",
            "Displays the current char count of the chat input field.",
            this,
            true
    );

    public final Value<Boolean> allowColorChar = new BooleanValue(
            "Allow Color Char",
            "Disables the color char restrictions of the Game.",
            this,
            true
    );

    public final Value<Boolean> dontClearChatHistory = new BooleanValue(
            "Dont Clear Chat History",
            "Prevents the Game from clearing your chat history.",
            this,
            true
    );

    public final Value<Boolean> customChatLength = new BooleanValue(
            "Custom Chat Length",
            "Allows you to enable or disable a custom chat length.",
            this,
            true
    );

    public final Value<Integer> maxChatLength = new SliderIntegerValue(
            "Max Chat Length",
            "Set the Max Chat Length",
            this,
            1000,
            1,
            10000
    ).visibleConsumer(this.customChatLength::getValue);
    
}
