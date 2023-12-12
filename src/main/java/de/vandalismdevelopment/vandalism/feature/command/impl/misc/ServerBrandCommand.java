package de.vandalismdevelopment.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;

public class ServerBrandCommand extends AbstractCommand {

    public ServerBrandCommand() {
        super(
                "Lets you view and copy the brand from the server you are currently connected to.",
                Category.MISC,
                "serverbrand",
                "brand"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("view").executes(context -> {
            ChatUtil.infoChatMessage("Server Brand: " + this.mc.getNetworkHandler().getBrand());
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            this.mc.keyboard.setClipboard(this.mc.getNetworkHandler().getBrand());
            ChatUtil.infoChatMessage("Server Brand copied into the Clipboard.");
            return SINGLE_SUCCESS;
        }));
    }

}
