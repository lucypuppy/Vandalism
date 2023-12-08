package de.vandalismdevelopment.vandalism.feature.impl.command.impl.render;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

public class ClientsideInventoryClearCommand extends Command {

    public ClientsideInventoryClearCommand() {
        super(
                "Clientside Inventory Clear",
                "Clears your Clientside Inventory.",
                FeatureCategory.RENDER,
                false,
                "clientsideinventoryclear",
                "clientsideinvclear",
                "fakeinventoryclear",
                "fakeinvclear"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (this.player().isCreative()) {
                ChatUtil.errorChatMessage(Formatting.RED + "You can't clear your Clientside Inventory in Creative Mode.");
                return SINGLE_SUCCESS;
            }

            this.player().getInventory().clear();
            ChatUtil.infoChatMessage(Formatting.GREEN + "Your Clientside Inventory has been cleared.");
            return SINGLE_SUCCESS;
        });
    }

}
