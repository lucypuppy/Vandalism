package de.vandalismdevelopment.vandalism.base.clientsettings.impl;

import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.StringValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;

public class ChatSettings extends ValueGroup {

    public final StringValue commandPrefix = new StringValue(
            this,
            "Command Prefix",
            "Change the prefix to run the commands of the Mod.",
            "."
    );

    public final BooleanValue displayTypedChars = new BooleanValue(
            this,
            "Display Typed Chars",
            "Displays the current char count of the chat input field.",
            true
    );

    public final BooleanValue allowColorChar = new BooleanValue(
            this,
            "Allow Color Char",
            "Disables the color char restrictions of the Game.",
            true
    );

    public final BooleanValue dontClearChatHistory = new BooleanValue(
            this,
            "Dont Clear Chat History",
            "Prevents the Game from clearing your chat history.",
            true
    );

    public final BooleanValue customChatLength = new BooleanValue(
            this,
            "Custom Chat Length",
            "Allows you to enable or disable a custom chat length.",
            true
    );

    public final IntegerValue maxChatLength = new IntegerValue(
            this,
            "Max Chat Length",
            "Set the Max Chat Length",
            1000,
            1,
            10000
    ).visibleCondition(this.customChatLength::getValue);

    public ChatSettings(final ClientSettings parent) {
        super(parent, "Chat", "Chat related settings.");
    }
}
