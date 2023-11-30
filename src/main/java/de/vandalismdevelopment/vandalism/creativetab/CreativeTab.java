package de.vandalismdevelopment.vandalism.creativetab;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CreativeTab {

    private final ItemGroup itemGroup;
    private final List<ItemStack> items;

    public CreativeTab(final Text displayName, final ItemStack icon, final Consumer<List<ItemStack>> entryConsumer) {
        this.items = new ArrayList<>();
        this.itemGroup = FabricItemGroup.builder().
                icon(() -> icon).
                displayName(displayName).
                entries(((displayContext, entries) -> {
                    if (this.items.isEmpty()) {
                        entryConsumer.accept(items);
                    }
                    entries.addAll(items);
                })).
                build();
    }

    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

}
