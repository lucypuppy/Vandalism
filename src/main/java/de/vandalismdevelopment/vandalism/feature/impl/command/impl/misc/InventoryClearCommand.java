package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class InventoryClearCommand extends Command {

    public InventoryClearCommand() {
        super("Inventory Clear", "Clears your inventory.", FeatureCategory.MISC, false, "inventoryclear", "clearinventory", "invclear", "clearinv");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            final DefaultedList<ItemStack> mainInventory = this.mc.player.getInventory().main;
            for (int i = 0; i < mainInventory.size(); ++i) {
                if (mainInventory.get(i).isEmpty()) continue;
                this.clearSlot(i);
            }

            for (int i = 36; i < 46; i++) this.clearSlot(i);
            ChatUtil.infoChatMessage("Your inventory has been cleared.");
            return SINGLE_SUCCESS;
        });
    }

    private void clearSlot(final int id) {
        switch (this.mc.interactionManager.getCurrentGameMode()) {
            case CREATIVE ->
                    this.mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(
                            id,
                            ItemStack.EMPTY
                    ));
            case SURVIVAL, ADVENTURE ->
                    this.mc.interactionManager.clickSlot(
                            this.mc.player.currentScreenHandler.syncId,
                            id,
                            -999,
                            SlotActionType.THROW,
                            this.mc.player
                    );
            default -> {}
        }
    }

}
