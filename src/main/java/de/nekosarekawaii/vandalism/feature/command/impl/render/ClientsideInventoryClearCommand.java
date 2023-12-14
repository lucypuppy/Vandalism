package de.nekosarekawaii.vandalism.feature.command.impl.render;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

public class ClientsideInventoryClearCommand extends AbstractCommand {

    public ClientsideInventoryClearCommand() {
        super(
                "Clears your Clientside Inventory.",
                Category.RENDER,
                "clientsideinventoryclear",
                "clientsideinvclear",
                "fakeinventoryclear",
                "fakeinvclear"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (this.mc.player.isCreative()) {
                ChatUtil.errorChatMessage(Formatting.RED + "You can't clear your Clientside Inventory in Creative Mode.");
                return SINGLE_SUCCESS;
            }

            this.mc.player.getInventory().clear();
            ChatUtil.infoChatMessage(Formatting.GREEN + "Your Clientside Inventory has been cleared.");
            return SINGLE_SUCCESS;
        });
    }

}
