package me.nekosarekawaii.foxglove.feature.impl.command.impl.development;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Test", aliases = {"test"}, description = "This is just a command for development purposes.", category = FeatureCategory.DEVELOPMENT, isExperimental = true)
public final class TestCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.infoChatMessage("Executed Test Command.");
            return SINGLE_SUCCESS;
        });
    }

}
