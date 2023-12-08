package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ChatUtil;
import net.minecraft.command.CommandSource;

public class ServerBrandCommand extends Command {

    public ServerBrandCommand() {
        super(
                "Server Brand",
                "Lets you view and copy the brand from the server you are currently connected to.",
                FeatureCategory.MISC,
                false,
                "serverbrand",
                "brand"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("view").executes(context -> {
            ChatUtil.infoChatMessage("Server Brand: " + this.networkHandler().getBrand());
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            this.keyboard().setClipboard(this.networkHandler().getBrand());
            ChatUtil.infoChatMessage("Server Brand copied into the Clipboard.");
            return SINGLE_SUCCESS;
        }));
    }

}
