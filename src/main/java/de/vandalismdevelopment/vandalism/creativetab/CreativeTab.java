package de.vandalismdevelopment.vandalism.creativetab;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.text.Text;

import java.util.Collection;

public abstract class CreativeTab {

    private final ItemStack icon;
    private final Text displayName;
    private ItemGroup itemGroup;

    public CreativeTab(final Text displayName, final ItemStack icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public Text getDisplayName() {
        return this.displayName;
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

}
