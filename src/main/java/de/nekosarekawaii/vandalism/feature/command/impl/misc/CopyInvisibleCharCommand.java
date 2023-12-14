package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;

public class CopyInvisibleCharCommand extends AbstractCommand {

    public CopyInvisibleCharCommand() {
        super(
                "Copies an invisible character into your clipboard.",
                Category.MISC,
                "copyinvisiblechar",
                "copyinvchar"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.mc.keyboard.setClipboard("\uF802");
            ChatUtil.infoChatMessage("Invisible character copied into the Clipboard.");
            return SINGLE_SUCCESS;
        });
    }

}
