package de.vandalismdevelopment.vandalism.feature.impl.command.impl.development;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import net.minecraft.command.CommandSource;

public class TestCommand extends Command {

    public TestCommand() {
        super(
                "Test",
                "Just for development purposes.",
                FeatureCategory.DEVELOPMENT,
                true,
                "test"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.infoChatMessage("Executed Test Command.");
            return SINGLE_SUCCESS;
        });
    }

}
