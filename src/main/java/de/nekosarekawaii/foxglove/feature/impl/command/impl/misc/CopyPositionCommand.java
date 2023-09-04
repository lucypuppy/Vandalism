package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.command.CommandSource;

public class CopyPositionCommand extends Command {

    public CopyPositionCommand() {
        super(
                "Copy Position",
                "Copies your current position into your clipboard.",
                FeatureCategory.MISC,
                false,
                "copyposition",
                "copypos"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (player() != null) {
                keyboard().setClipboard(player().getBlockPos().toShortString());
                ChatUtils.infoChatMessage("Position copied into the Clipboard.");
            }
            return singleSuccess;
        });
    }

}
