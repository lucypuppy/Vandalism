package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import net.minecraft.command.CommandSource;

import java.util.Objects;

@CommandInfo(name = "ServerBrand", description = "Shows the server brand of the current server.", aliases = { "brand", "serverbrand", "showserverbrand" }, category = FeatureCategory.MISC)
public class ServerBrandCommand extends Command {

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.requires(commandSource -> !mc.isInSingleplayer()).executes(context -> {
            ChatUtils.infoChatMessage("Server brand: " + Objects.requireNonNull(mc.player).getServerBrand());
            return singleSuccess;
        });
    }
}
