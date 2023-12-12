package de.vandalismdevelopment.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;

public class CopyUsernameCommand extends AbstractCommand {

    public CopyUsernameCommand() {
        super(
                "Copies your username into your clipboard.",
                Category.MISC,
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
