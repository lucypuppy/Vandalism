package de.nekosarekawaii.foxglove.feature.impl.command.impl.development;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import de.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Test", aliases = {"test"}, description = "This is just a command for development purposes.", category = FeatureCategory.DEVELOPMENT, isExperimental = true)
public class TestCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.infoChatMessage("Executed Test Command.");
            return singleSuccess;
        });
    }

}
