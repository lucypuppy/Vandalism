package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

public class GiveCommand extends Command {

    public GiveCommand() {
        super(
                "Give",
                "Gives you items when you are in creative mode.",
                FeatureCategory.MISC,
                false,
                "give",
                "giveitem",
                "itemgive",
                "getitem",
                "itemget",
                "i"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("item", ItemStackArgumentType.itemStack(REGISTRY_ACCESS)).executes(context -> {
            try {
                this.giveItem(ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false));
            } catch (final Throwable throwable) {
                ChatUtils.errorChatMessage("Failed to give item cause of: " + throwable);
            }
            return SINGLE_SUCCESS;
        }).then(argument("number", IntegerArgumentType.integer()).executes(context -> {
            try {
                this.giveItem(ItemStackArgumentType.getItemStackArgument(context, "item").createStack(
                        IntegerArgumentType.getInteger(context, "number"), false)
                );
            } catch (final Throwable throwable) {
                ChatUtils.errorChatMessage("Failed to give item cause of: " + throwable);
            }
            return SINGLE_SUCCESS;
        })));
    }

    private void giveItem(final ItemStack item) throws Throwable {
        if (player() != null && networkHandler() != null) {
            if (!player().getAbilities().creativeMode) throw NOT_IN_CREATIVE_MODE.create();
            final int emptySlot = player().getInventory().getEmptySlot();
            if (emptySlot == -1 || emptySlot > 8) throw NOT_SPACE_IN_HOT_BAR.create();
            networkHandler().sendPacket(new CreativeInventoryActionC2SPacket(36 + emptySlot, item));
        }
    }

}