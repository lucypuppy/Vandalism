package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;

public class CopyPositionCommand extends AbstractCommand {

    public CopyPositionCommand() {
        super(
                "Copies your current position into your clipboard.",
                Category.MISC,
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
