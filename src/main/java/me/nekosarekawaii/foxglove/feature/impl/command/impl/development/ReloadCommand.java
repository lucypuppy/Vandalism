package me.nekosarekawaii.foxglove.feature.impl.command.impl.development;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Reload", aliases = {"reload"}, description = "This command reloads all the mod features.", category = FeatureCategory.DEVELOPMENT)
public class ReloadCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.warningChatMessage("Reloading features...");
            Foxglove.getInstance().getFeatures().reload();
            ChatUtils.infoChatMessage("Features reloaded!");
            return SINGLE_SUCCESS;
        });
    }

}
