package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

import java.util.Collection;

public class TestItemsCreativeTab extends CreativeTab {

    public TestItemsCreativeTab() {
        super(new ItemStack(Items.PAPER).setCustomName(Text.literal("Test Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();

        final ItemStack XItemsRemover = new ItemStack(Items.TNT);
        final NbtList lore = new NbtList();
        lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("[pdd]"))));
        XItemsRemover.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
        current.add(XItemsRemover);

        return current;
    }

}
