package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;

public class CopyUsernameCommand extends Command {

    public CopyUsernameCommand() {
        super(
                "Copy Username",
                "Copies your username into your clipboard.",
                FeatureCategory.MISC,
                false,
                "copyusername",
                "usernamecopy"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.mc.keyboard.setClipboard(this.mc.player.getGameProfile().getName());
            ChatUtil.infoChatMessage("Username copied into the Clipboard.");
            return SINGLE_SUCCESS;
        });
    }

}
