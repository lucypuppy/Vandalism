package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.util.minecraft.ChatUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

@CommandInfo(name = "Give", description = "Gives you any item.", aliases = {"give", "giveitem", "item", "i"}, category = FeatureCategory.MISC)
public class GiveCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("item", ItemStackArgumentType.itemStack(registryAccess)).executes(context -> {
            try {
                this.giveItem(ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false));
            } catch (final Throwable throwable) {
                ChatUtils.errorChatMessage("Failed to give item cause of: " + throwable);
            }
            return singleSuccess;
        }).then(argument("number", IntegerArgumentType.integer()).executes(context -> {
            try {
                this.giveItem(ItemStackArgumentType.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "number"), false));
            } catch (final Throwable throwable) {
                ChatUtils.errorChatMessage("Failed to give item cause of: " + throwable);
            }
            return singleSuccess;
        })));
    }

    private void giveItem(final ItemStack item) throws Throwable {
        final ClientPlayerEntity player = mc.player;
        final ClientPlayNetworkHandler handler = mc.getNetworkHandler();

        if (player != null && handler != null) {
            if (!player.getAbilities().creativeMode) throw notInCreativeMode.create();

            final int emptySlot = player.getInventory().getEmptySlot();
            if (emptySlot == -1 || emptySlot > 8) throw notSpaceInHotBar.create();

            handler.sendPacket(new CreativeInventoryActionC2SPacket(36 + emptySlot, item));
        }
    }

}