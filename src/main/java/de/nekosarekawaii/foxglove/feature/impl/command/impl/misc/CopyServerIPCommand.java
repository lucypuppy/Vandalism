package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.util.ChatUtils;
import de.nekosarekawaii.foxglove.util.ServerUtils;
import net.minecraft.command.CommandSource;

public class CopyServerIPCommand extends Command {

    public CopyServerIPCommand() {
        super(
                "Copy Server IP",
                "Copies the ip address of the server your are currently connected to into your clipboard.",
                FeatureCategory.MISC,
                false,
                "copyserverip",
                "serveripcopy",
                "copyserveraddress",
                "serveraddresscopy"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            keyboard().setClipboard(
                    ServerUtils.getLastServerInfo().address + " | " +
                            networkHandler().getConnection().getAddress().toString()
            );
            ChatUtils.infoChatMessage("Server IP copied into the Clipboard.");
            return singleSuccess;
        });
    }

}
