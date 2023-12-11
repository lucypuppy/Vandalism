package de.vandalismdevelopment.vandalism.feature.command.impl.development;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;

public class TestCommand extends AbstractCommand {

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
            ChatUtil.infoChatMessage("Executed Test Command.");
            return SINGLE_SUCCESS;
        });
    }

}
