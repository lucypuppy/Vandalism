package me.nekosarekawaii.foxglove.creativetab;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

import java.util.Collection;

public abstract class CreativeTab implements MinecraftWrapper {

    private final ItemStack icon;
    private ItemGroup itemGroup;

    public CreativeTab(final ItemStack icon) {
        this.icon = icon;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public void setItemGroup(final ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    public Collection<ItemStack> entries() {
        return ItemStackSet.create();
    }

    public ItemStack putClientsideName(final ItemStack itemStack, final Text name, final Text... description) {
        final NbtCompound nbtCompound = itemStack.getOrCreateNbt();
        nbtCompound.put(Foxglove.getInstance().getCreativeTabRegistry().getClientsideName(), new NbtCompound());
        itemStack.setCustomName(name);
        if (description != null && description.length > 0) {
            final NbtList lore = new NbtList();
            for (final Text text : description) {
                if (text != null) {
                    lore.add(NbtString.of(Text.Serializer.toJson(text)));
                }
            }
            itemStack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
        }
        return itemStack;
    }

    public ItemStack putClientsideGlint(final ItemStack itemStack) {
        final NbtCompound nbtCompound = itemStack.getOrCreateNbt();
        nbtCompound.put(Foxglove.getInstance().getCreativeTabRegistry().getClientsideGlint(), new NbtCompound());
        itemStack.setNbt(nbtCompound);
        return itemStack;
    }

}
