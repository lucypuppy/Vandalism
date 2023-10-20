package de.vandalismdevelopment.vandalism.config.impl.main;

import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.StringValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderIntegerValue;

public class ChatCategory extends ValueCategory {
    
    public ChatCategory(final MainConfig parent) {
        super("Chat", "Chat related settings.", parent);
    }

    public final Value<String> commandPrefix = new StringValue(
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            this,
            "."
    );

    public final Value<Boolean> displayTypedChars = new BooleanValue(
            "Display Typed Chars",
            "Displays the current char count of from the chat input field.",
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
