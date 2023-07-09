package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class ClientKickerCreativeTab extends CreativeTab {

    public ClientKickerCreativeTab() {
        super(new ItemStack(Items.FIREWORK_ROCKET).setCustomName(Text.literal("Client Kicker")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();

        final ItemStack paperKickHead = new ItemStack(Items.PLAYER_HEAD);
        final NbtCompound paperKickHeadNBT = new NbtCompound();
        paperKickHeadNBT.putString("SkullOwner", " ");
        paperKickHead.setNbt(paperKickHeadNBT);
        this.putClientsideName(paperKickHead,
                Text.literal(
                        Formatting.RED + Formatting.BOLD.toString() + "Kick Head"
                ),
                Text.literal(
                        Formatting.GOLD + Formatting.BOLD.toString() + "Can crash older clients"
                )
        );
        current.add(paperKickHead);

        //TODO: XItemsRemover
        final ItemStack test = new ItemStack(Items.TNT);
        final NbtList lore = new NbtList();
        lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("[pdd]"))));
        test.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
        current.add(test);

        return current;
    }

}
