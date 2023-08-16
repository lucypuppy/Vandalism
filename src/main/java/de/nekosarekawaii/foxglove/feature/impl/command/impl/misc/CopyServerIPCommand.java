package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.Feature;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import de.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import de.nekosarekawaii.foxglove.util.minecraft.ServerUtils;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Copy Server IP", aliases = {"copyserverip", "copyip", "ipcopy"}, description = "This command copies the ip of the server you are currently connected to into the clipboard.", category = FeatureCategory.MISC)
public class CopyServerIPCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Feature.mc.keyboard.setClipboard(ServerUtils.getLastServerInfo().address + " | " + Feature.mc.getNetworkHandler().getConnection().getAddress().toString());
            ChatUtils.infoChatMessage("Server IP copied into the Clipboard.");
            return singleSuccess;
        });
    }

}
