package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;

public class CopyInvisibleCharCommand extends Command {

    public CopyInvisibleCharCommand() {
        super(
                "Copy Invisible Char",
                "Copies an invisible character into your clipboard.",
                FeatureCategory.MISC,
                false,
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
