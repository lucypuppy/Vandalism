package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.Feature;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import de.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import net.minecraft.command.CommandSource;

import java.util.Objects;

@CommandInfo(name = "ServerBrand", description = "Shows the server brand of the current server.", aliases = { "brand", "serverbrand", "showserverbrand" }, category = FeatureCategory.MISC)
public class ServerBrandCommand extends Command {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.requires(commandSource -> !Feature.mc.isInSingleplayer()).executes(context -> {
            ChatUtils.infoChatMessage("Server brand: " + Objects.requireNonNull(Feature.mc.player).getServerBrand());
            return singleSuccess;
        });
    }
}
