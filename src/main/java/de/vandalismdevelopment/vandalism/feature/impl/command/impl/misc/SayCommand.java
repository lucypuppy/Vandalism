package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import net.minecraft.command.CommandSource;

public class SayCommand extends Command {

    public SayCommand() {
        super(
                "Say",
                "Allows you to send every message into the chat by skipping the command system of this mod.",
                FeatureCategory.MISC,
                false,
                "say"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            final String message = context.getArgument("message", String.class);
            if (message.startsWith("/")) networkHandler().sendChatCommand(message);
            else networkHandler().sendChatMessage(message);
            return SINGLE_SUCCESS;
        }));
    }

}
