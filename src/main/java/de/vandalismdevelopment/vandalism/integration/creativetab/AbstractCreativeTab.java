package de.vandalismdevelopment.vandalism.integration.creativetab;

import de.vandalismdevelopment.vandalism.base.FabricBootstrap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCreativeTab {

    private final List<ItemStack> TEMP_ITEMS = new ArrayList<>();

    private final Text name;
    private final ItemStack icon;

    public AbstractCreativeTab(final Text name, final Item icon) {
        this.name = name;
        this.icon = new ItemStack(icon);
    }

    public abstract void exposeItems(final List<ItemStack> items);

    public void publish() {
        final var itemGroup = FabricItemGroup.builder().icon(() -> icon).displayName(this.name).entries(((displayContext, entries) -> {
            if (this.TEMP_ITEMS.isEmpty()) {
                exposeItems(this.TEMP_ITEMS);
            }
            entries.addAll(this.TEMP_ITEMS);
        })).build();

        Registry.register(Registries.ITEM_GROUP, new Identifier(FabricBootstrap.MOD_ID, name.getString()), itemGroup);
    }

}
