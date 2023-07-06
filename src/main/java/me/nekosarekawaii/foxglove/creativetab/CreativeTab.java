package me.nekosarekawaii.foxglove.creativetab;

import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.nbt.NbtCompound;
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

    public ItemStack putClientsideName(final ItemStack itemStack, final Text name) {
        NbtCompound nbtCompound = itemStack.getNbt();
        if (nbtCompound == null) nbtCompound = new NbtCompound();
        nbtCompound.put("clientsideName", new NbtCompound());
        itemStack.setCustomName(name);
        return itemStack;
    }

    public ItemStack putClientsideGlint(final ItemStack itemStack) {
        NbtCompound nbtCompound = itemStack.getNbt();
        if (nbtCompound == null) nbtCompound = new NbtCompound();
        nbtCompound.put("clientsideGlint", new NbtCompound());
        itemStack.setNbt(nbtCompound);
        return itemStack;
    }

}
