package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.Feature;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import de.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Copy Position", aliases = {"copyposition", "copypos"}, description = "This command copies the position from you into the clipboard.", category = FeatureCategory.MISC)
public class CopyPositionCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            final ClientPlayerEntity player = Feature.mc.player;
            if (player != null) {
                Feature.mc.keyboard.setClipboard(player.getBlockPos().toShortString());
                ChatUtils.infoChatMessage("Position copied into the Clipboard.");
            }
            return singleSuccess;
        });
    }

}
