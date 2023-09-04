package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.command.CommandSource;

public class ServerBrandCommand extends Command {

    public ServerBrandCommand() {
        super(
                "Server Brand",
                "Lets you view and copy the brand from the server you are currently connected to.",
                FeatureCategory.MISC,
                false,
                "serverbrand",
                "brand"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("view").executes(context -> {
            ChatUtils.infoChatMessage("Server Brand: " + player().getServerBrand());
            return singleSuccess;
        }));
        builder.then(literal("copy").executes(context -> {
            keyboard().setClipboard(player().getServerBrand());
            ChatUtils.infoChatMessage("Server Brand copied into the Clipboard.");
            return singleSuccess;
        }));
    }

}
