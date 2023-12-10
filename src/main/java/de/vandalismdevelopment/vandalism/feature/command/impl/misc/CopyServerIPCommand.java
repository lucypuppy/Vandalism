package de.vandalismdevelopment.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.util.ServerUtil;
import net.minecraft.command.CommandSource;

public class CopyServerIPCommand extends AbstractCommand {

    public CopyServerIPCommand() {
        super("Copy Server IP", "Copies the ip address of the server your are currently connected to into your clipboard.", FeatureCategory.MISC, false, "copyserverip", "serveripcopy", "copyserveraddress", "serveraddresscopy", "copyip", "copyserverip");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.copyAddress(false);
            return SINGLE_SUCCESS;
        });

        builder.then(argument("entire-address", BoolArgumentType.bool())
                .executes(context -> {
                    this.copyAddress(BoolArgumentType.getBool(context, "entire-address"));
                    return SINGLE_SUCCESS;
                })
        );
    }

    private void copyAddress(final boolean entireAddress) {
        if (this.mc.isInSingleplayer()) {
            ChatUtil.errorChatMessage("You are in Single-player.");
            return;
        }

        this.mc.keyboard.setClipboard(ServerUtil.getLastServerInfo().address + (entireAddress ? " | " + this.mc.getNetworkHandler().getConnection().getAddress().toString() : ""));
        ChatUtil.infoChatMessage("Server IP copied into the Clipboard.");
    }

}
