package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.util.ServerUtil;
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
                    ServerUtil.getLastServerInfo().address + " | " +
                            networkHandler().getConnection().getAddress().toString()
            );
            ChatUtil.infoChatMessage("Server IP copied into the Clipboard.");
            return SINGLE_SUCCESS;
        });
    }

}
