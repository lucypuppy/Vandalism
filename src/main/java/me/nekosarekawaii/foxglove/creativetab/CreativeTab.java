package me.nekosarekawaii.foxglove.creativetab;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.text.Text;

import java.util.Collection;

public abstract class CreativeTab {

    private ItemGroup itemGroup;

    public void setItemGroup(final ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    public abstract Text displayName();

    public ItemStack icon() {
        return new ItemStack(Items.DIRT);
    }

    public Collection<ItemStack> entries() {
        return ItemStackSet.create();
    }

    public ItemGroup generate(final Item dummyItem) {
        return FabricItemGroup.builder()
                .icon(this::icon)
                .displayName(this.displayName())
                .entries((displayContext, entries) -> entries.add(new ItemStack(dummyItem, 1)))
                .build();
    }

}
