package de.foxglovedevelopment.foxglove.feature.impl.command.impl.development;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.command.Command;
import de.foxglovedevelopment.foxglove.util.ChatUtils;
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
            return singleSuccess;
        });
    }

}
