package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import me.nekosarekawaii.foxglove.util.ServerUtils;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Copy Server IP", aliases = {"copyserverip", "copyip", "ipcopy"}, description = "This command copies the ip of the server you are currently connected to into the clipboard.", category = FeatureCategory.MISC)
public class CopyServerIPCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            mc.keyboard.setClipboard(ServerUtils.getLastServerInfo().address);
            ChatUtils.infoChatMessage("Server IP copied into the Clipboard.");
            return SINGLE_SUCCESS;
        });
    }

}
