package de.vandalismdevelopment.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;

public class CopyPositionCommand extends AbstractCommand {

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
            if (this.mc.player != null) {
                this.mc.keyboard.setClipboard(this.mc.player.getBlockPos().toShortString());
                ChatUtil.infoChatMessage("Position copied into the Clipboard.");
            }

            return SINGLE_SUCCESS;
        });
    }

}
